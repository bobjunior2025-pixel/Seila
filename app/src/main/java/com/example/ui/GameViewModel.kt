package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.engine.SimulationEngine
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    MAIN_MENU,
    CHOOSE_KINGDOM,
    DASHBOARD,
    SQUAD,
    TACTICS,
    STANDINGS,
    MARKET,
    CASTLE,
    LIVE_MATCH,
    ROUND_SUMMARY,
    GITHUB_APK_INFO
}

data class LiveMatchState(
    val match: Match,
    val homeKingdom: Kingdom,
    val awayKingdom: Kingdom,
    val currentMinute: Int = 0,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val speedMultiplier: Int = 1, // 1x, 2x, 5x
    val events: List<MatchEvent> = emptyList(),
    val homePossessionPercent: Int = 50
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val repository = GameRepository(application.applicationContext)

    private val _currentScreen = MutableStateFlow(AppScreen.MAIN_MENU)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _liveMatchState = MutableStateFlow<LiveMatchState?>(null)
    val liveMatchState: StateFlow<LiveMatchState?> = _liveMatchState.asStateFlow()

    private val _selectedWarriorForSub = MutableStateFlow<Warrior?>(null)
    val selectedWarriorForSub: StateFlow<Warrior?> = _selectedWarriorForSub.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    private var simulationJob: Job? = null

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun showMessage(msg: String) {
        _snackMessage.value = msg
    }

    fun clearMessage() {
        _snackMessage.value = null
    }

    fun startNewGame(kingdomId: String) {
        repository.startNewGame(kingdomId)
        _currentScreen.value = AppScreen.DASHBOARD
    }

    fun startUserMatch() {
        val nextMatch = repository.getNextMatchForUser()
        if (nextMatch == null) {
            showMessage("Nenhum confronto pendente nesta rodada!")
            return
        }

        val allKingdoms = repository.kingdoms.value.associateBy { it.id }
        val homeK = allKingdoms[nextMatch.homeKingdomId]
        val awayK = allKingdoms[nextMatch.awayKingdomId]

        if (homeK == null || awayK == null) {
            showMessage("Erro ao carregar os reinos da partida.")
            return
        }

        // Garante que ambos tenham 11 titulares
        if (homeK.starters.size < 11) {
            homeK.warriors.take(11).forEach { it.isStarter = true }
        }
        if (awayK.starters.size < 11) {
            awayK.warriors.take(11).forEach { it.isStarter = true }
        }

        nextMatch.events.clear()
        nextMatch.homeScore = 0
        nextMatch.awayScore = 0

        _liveMatchState.value = LiveMatchState(
            match = nextMatch,
            homeKingdom = homeK,
            awayKingdom = awayK,
            currentMinute = 0,
            isRunning = true,
            isFinished = false,
            speedMultiplier = 2,
            events = emptyList()
        )

        _currentScreen.value = AppScreen.LIVE_MATCH
        startMatchSimulationLoop()
    }

    private fun startMatchSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            val state = _liveMatchState.value ?: return@launch
            var min = state.currentMinute

            while (min < 90) {
                val currentState = _liveMatchState.value ?: break
                if (!currentState.isRunning) {
                    delay(250)
                    continue
                }

                // Delay baseado no speedMultiplier (1x = 400ms, 2x = 150ms, 5x = 50ms)
                val tickDelay = when (currentState.speedMultiplier) {
                    1 -> 350L
                    2 -> 130L
                    5 -> 40L
                    else -> 100L
                }
                delay(tickDelay)

                min++
                val newEvent = SimulationEngine.simulateMinuteStep(
                    currentMinute = min,
                    match = currentState.match,
                    homeKingdom = currentState.homeKingdom,
                    awayKingdom = currentState.awayKingdom
                )

                val updatedEvents = if (newEvent != null) {
                    listOf(newEvent) + currentState.events
                } else {
                    currentState.events
                }

                val homeP = SimulationEngine.calculateTeamPower(currentState.homeKingdom, true).totalPower
                val awayP = SimulationEngine.calculateTeamPower(currentState.awayKingdom, false).totalPower
                val posPercent = ((homeP / (homeP + awayP)) * 100).toInt().coerceIn(25, 75)

                _liveMatchState.value = currentState.copy(
                    currentMinute = min,
                    events = updatedEvents,
                    homePossessionPercent = posPercent
                )
            }

            // Fim de partida aos 90'
            _liveMatchState.value?.let { endState ->
                endState.match.isPlayed = true
                _liveMatchState.value = endState.copy(
                    currentMinute = 90,
                    isRunning = false,
                    isFinished = true
                )
            }
        }
    }

    fun toggleMatchPause() {
        val cur = _liveMatchState.value ?: return
        if (!cur.isFinished) {
            _liveMatchState.value = cur.copy(isRunning = !cur.isRunning)
        }
    }

    fun setMatchSpeed(speed: Int) {
        val cur = _liveMatchState.value ?: return
        _liveMatchState.value = cur.copy(speedMultiplier = speed)
    }

    fun instantFinishMatch() {
        simulationJob?.cancel()
        val cur = _liveMatchState.value ?: return
        if (cur.isFinished) return

        var min = cur.currentMinute
        val events = cur.events.toMutableList()

        while (min < 90) {
            min++
            val ev = SimulationEngine.simulateMinuteStep(
                currentMinute = min,
                match = cur.match,
                homeKingdom = cur.homeKingdom,
                awayKingdom = cur.awayKingdom
            )
            if (ev != null) {
                events.add(0, ev)
            }
        }

        cur.match.isPlayed = true
        _liveMatchState.value = cur.copy(
            currentMinute = 90,
            isRunning = false,
            isFinished = true,
            events = events
        )
    }

    fun finishMatchAndGoToSummary() {
        simulationJob?.cancel()
        repository.finishRoundAndAdvance()
        _currentScreen.value = AppScreen.ROUND_SUMMARY
    }

    fun selectWarriorForSub(warrior: Warrior?) {
        _selectedWarriorForSub.value = warrior
    }

    fun executeSubstitution(starter: Warrior, reserve: Warrior) {
        repository.substituteWarrior(starter.id, reserve.id)
        _selectedWarriorForSub.value = null
        showMessage("${reserve.name} entrou no lugar de ${starter.name}!")
    }

    fun changeFormation(formation: Formation) {
        repository.changeFormation(formation)
        showMessage("Formação alterada para ${formation.title}!")
    }

    fun changeStrategy(strategy: Strategy) {
        repository.changeStrategy(strategy)
        showMessage("Postura ajustada para ${strategy.label}!")
    }

    fun changeFocus(focus: CombatFocus) {
        repository.changeCombatFocus(focus)
        showMessage("Foco de combate: ${focus.label}!")
    }

    fun buyWarrior(warrior: Warrior) {
        val success = repository.buyWarrior(warrior)
        if (success) {
            showMessage("Guerreiro contratado com sucesso!")
        } else {
            showMessage("Ouro insuficiente no tesouro real!")
        }
    }

    fun sellWarrior(warriorId: String) {
        val success = repository.sellWarrior(warriorId)
        if (success) {
            showMessage("Guerreiro dispensado e moedas adicionadas ao tesouro.")
        } else {
            showMessage("Não é possível vender: tamanho mínimo de elenco atingido.")
        }
    }

    fun upgradeColiseum() {
        val success = repository.upgradeColiseum()
        if (success) {
            showMessage("Coliseu expandido em +4.000 lugares!")
        } else {
            showMessage("Ouro insuficiente para expandir o Coliseu (60.000 moedas necessárias).")
        }
    }

    fun upgradeInfirmary() {
        val success = repository.upgradeInfirmary()
        if (success) {
            showMessage("Enfermaria Real melhorada!")
        } else {
            showMessage("Ouro insuficiente para melhorar Enfermaria (45.000 moedas necessárias).")
        }
    }

    fun upgradeTraining() {
        val success = repository.upgradeTraining()
        if (success) {
            showMessage("Campo de Treinamento aprimorado! Jovens revigorados.")
        } else {
            showMessage("Ouro insuficiente para o Centro de Treinamento (50.000 moedas necessárias).")
        }
    }
}
