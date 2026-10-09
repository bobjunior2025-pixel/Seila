package com.example.data

import android.content.Context
import com.example.engine.DefaultDataGenerator
import com.example.engine.SimulationEngine
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max

class GameRepository(private val context: Context) {

    private val _kingdoms = MutableStateFlow<List<Kingdom>>(emptyList())
    val kingdoms: StateFlow<List<Kingdom>> = _kingdoms.asStateFlow()

    private val _userKingdomId = MutableStateFlow<String?>(null)
    val userKingdomId: StateFlow<String?> = _userKingdomId.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _currentSeason = MutableStateFlow(1)
    val currentSeason: StateFlow<Int> = _currentSeason.asStateFlow()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _marketWarriors = MutableStateFlow<List<Warrior>>(emptyList())
    val marketWarriors: StateFlow<List<Warrior>> = _marketWarriors.asStateFlow()

    private val _latestNews = MutableStateFlow<List<String>>(listOf("Bem-vindo ao ReinoFoot! A temporada 1 das Grandes Guerras e Torneios começou!"))
    val latestNews: StateFlow<List<String>> = _latestNews.asStateFlow()

    init {
        // Inicializa com dados padrão
        val initialKingdoms = DefaultDataGenerator.generateInitialKingdoms()
        _kingdoms.value = initialKingdoms
        _userKingdomId.value = "paladinos"
        _matches.value = DefaultDataGenerator.generateLeagueFixtures(initialKingdoms, 1) +
                DefaultDataGenerator.generateLeagueFixtures(initialKingdoms, 2)
        _marketWarriors.value = DefaultDataGenerator.generateTransferMarketWarriors(initialKingdoms)
    }

    val userKingdom: Kingdom?
        get() = _kingdoms.value.firstOrNull { it.id == _userKingdomId.value }

    fun startNewGame(selectedKingdomId: String) {
        val initialKingdoms = DefaultDataGenerator.generateInitialKingdoms()
        _kingdoms.value = initialKingdoms
        _userKingdomId.value = selectedKingdomId
        _currentRound.value = 1
        _currentSeason.value = 1
        _matches.value = DefaultDataGenerator.generateLeagueFixtures(initialKingdoms, 1) +
                DefaultDataGenerator.generateLeagueFixtures(initialKingdoms, 2)
        _marketWarriors.value = DefaultDataGenerator.generateTransferMarketWarriors(initialKingdoms)
        val kName = initialKingdoms.firstOrNull { it.id == selectedKingdomId }?.name ?: "Seu Reino"
        _latestNews.value = listOf(
            "👑 Você assumiu o comando de $kName!",
            "⚔️ Organize sua formação tática e prepare seus guerreiros para a 1ª rodada."
        )
    }

    fun getStandings(division: Int): List<Standing> {
        val divKingdoms = _kingdoms.value.filter { it.division == division }
        val divMatches = _matches.value.filter { it.division == division && it.isPlayed }

        val standingsMap = divKingdoms.associate { it.id to Standing(kingdomId = it.id) }.toMutableMap()

        for (m in divMatches) {
            val home = standingsMap[m.homeKingdomId]
            val away = standingsMap[m.awayKingdomId]

            if (home != null && away != null) {
                home.played++
                away.played++
                home.goalsFor += m.homeScore
                home.goalsAgainst += m.awayScore
                away.goalsFor += m.awayScore
                away.goalsAgainst += m.homeScore

                when {
                    m.homeScore > m.awayScore -> {
                        home.points += 3
                        home.wins++
                        away.losses++
                    }
                    m.homeScore < m.awayScore -> {
                        away.points += 3
                        away.wins++
                        home.losses++
                    }
                    else -> {
                        home.points += 1
                        away.points += 1
                        home.draws++
                        away.draws++
                    }
                }
            }
        }

        return standingsMap.values.sortedWith(
            compareByDescending<Standing> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsFor }
        )
    }

    fun getNextMatchForUser(): Match? {
        val userK = userKingdom ?: return null
        return _matches.value.firstOrNull {
            it.round == _currentRound.value &&
                    (it.homeKingdomId == userK.id || it.awayKingdomId == userK.id) &&
                    !it.isPlayed
        }
    }

    /**
     * Simula todas as partidas da rodada que não sejam a do usuário,
     * atualiza economia, salários e avanços de rodada
     */
    fun finishRoundAndAdvance() {
        val round = _currentRound.value
        val allK = _kingdoms.value.associateBy { it.id }

        // Simula partidas restantes desta rodada
        _matches.value.filter { it.round == round && !it.isPlayed }.forEach { match ->
            val homeK = allK[match.homeKingdomId]
            val awayK = allK[match.awayKingdomId]
            if (homeK != null && awayK != null) {
                SimulationEngine.simulateQuickMatch(match, homeK, awayK)
            }
        }

        // Economia do reino do jogador: Bilheteria se jogou em casa + custos de salário
        userKingdom?.let { userK ->
            val playedHome = _matches.value.any { it.round == round && it.homeKingdomId == userK.id }
            var revenue = 0
            if (playedHome) {
                val fans = (userK.coliseumCapacity * (0.65f + (userK.prestige / 300f))).toInt().coerceIn(1000, userK.coliseumCapacity)
                revenue = fans * userK.ticketPrice
                userK.gold += revenue
            }

            val totalWages = userK.warriors.sumOf { it.wageGold }
            userK.gold = max(0, userK.gold - totalWages)

            // Atualiza condição dos guerreiros
            userK.warriors.forEach { w ->
                if (w.injuredRounds > 0) w.injuredRounds--
                if (w.suspendedRounds > 0) w.suspendedRounds--
            }

            val news = mutableListOf<String>()
            if (playedHome) {
                news.add("💰 Bilheteria da Arena: +$revenue moedas de ouro.")
            }
            news.add("🛡️ Folha de Soldos paga aos guerreiros: -$totalWages moedas de ouro.")
            _latestNews.value = news + _latestNews.value.take(4)
        }

        if (round >= 14) {
            // Fim de temporada: promoções, rebaixamentos e novo ano
            handleSeasonEnd()
        } else {
            _currentRound.value = round + 1
        }
    }

    private fun handleSeasonEnd() {
        val div1Standings = getStandings(1)
        val div2Standings = getStandings(2)

        val championDiv1 = _kingdoms.value.firstOrNull { it.id == div1Standings.firstOrNull()?.kingdomId }
        val relegated = div1Standings.takeLast(2).mapNotNull { s -> _kingdoms.value.firstOrNull { it.id == s.kingdomId } }
        val promoted = div2Standings.take(2).mapNotNull { s -> _kingdoms.value.firstOrNull { it.id == s.kingdomId } }

        // Premiação do Campeão
        championDiv1?.let {
            it.gold += 300000
            it.prestige += 10
        }

        relegated.forEach {
            it.prestige = max(10, it.prestige - 10)
        }
        promoted.forEach {
            it.prestige += 15
        }

        // Criar novo conjunto de reinos com divisões ajustadas
        val updatedKingdoms = _kingdoms.value.map { k ->
            when {
                relegated.any { it.id == k.id } -> k.copy(division = 2)
                promoted.any { it.id == k.id } -> k.copy(division = 1)
                else -> k
            }
        }
        _kingdoms.value = updatedKingdoms

        _currentSeason.value += 1
        _currentRound.value = 1
        _matches.value = DefaultDataGenerator.generateLeagueFixtures(updatedKingdoms, 1) +
                DefaultDataGenerator.generateLeagueFixtures(updatedKingdoms, 2)

        val champName = championDiv1?.name ?: "Ordem dos Paladinos"
        _latestNews.value = listOf(
            "🏆 TEMPORADA ENCERRADA! $champName é o grande Campeão do Reino!",
            "⬆️ Reinos que ascenderam à 1ª Divisão: ${promoted.joinToString { it.name }}",
            "⬇️ Reinos rebaixados para a 2ª Divisão: ${relegated.joinToString { it.name }}",
            "⚔️ Uma nova temporada se inicia! O tesouro e contratos foram renovados."
        )
    }

    fun substituteWarrior(outWarriorId: String, inWarriorId: String) {
        val userK = userKingdom ?: return
        val outW = userK.warriors.firstOrNull { it.id == outWarriorId }
        val inW = userK.warriors.firstOrNull { it.id == inWarriorId }
        if (outW != null && inW != null) {
            outW.isStarter = false
            inW.isStarter = true
        }
    }

    fun changeFormation(formation: Formation) {
        val userK = userKingdom ?: return
        userK.formation = formation
        DefaultDataGenerator.setupDefaultStarters(userK)
    }

    fun changeStrategy(strategy: Strategy) {
        userKingdom?.strategy = strategy
    }

    fun changeCombatFocus(focus: CombatFocus) {
        userKingdom?.combatFocus = focus
    }

    fun buyWarrior(warrior: Warrior): Boolean {
        val userK = userKingdom ?: return false
        if (userK.gold >= warrior.valueGold && userK.warriors.size < 24) {
            userK.gold -= warrior.valueGold
            val signedWarrior = warrior.copy(kingdomId = userK.id, isStarter = false)
            userK.warriors.add(signedWarrior)
            _marketWarriors.value = _marketWarriors.value.filter { it.id != warrior.id }
            _latestNews.value = listOf("✍️ Contratação! O guerreiro ${warrior.name} jurou fidelidade ao ${userK.name}!") + _latestNews.value
            return true
        }
        return false
    }

    fun sellWarrior(warriorId: String): Boolean {
        val userK = userKingdom ?: return false
        val warrior = userK.warriors.firstOrNull { it.id == warriorId } ?: return false
        if (userK.warriors.size <= 12) return false // Manter elenco mínimo de 12

        val sellPrice = (warrior.valueGold * 0.75f).toInt()
        userK.gold += sellPrice
        userK.warriors.remove(warrior)
        if (warrior.isStarter) {
            DefaultDataGenerator.setupDefaultStarters(userK)
        }
        _latestNews.value = listOf("💰 Negociação! ${warrior.name} foi dispensado por $sellPrice moedas de ouro.") + _latestNews.value
        return true
    }

    fun upgradeColiseum(): Boolean {
        val userK = userKingdom ?: return false
        val cost = 60000
        if (userK.gold >= cost) {
            userK.gold -= cost
            userK.coliseumCapacity += 4000
            userK.prestige += 5
            return true
        }
        return false
    }

    fun upgradeInfirmary(): Boolean {
        val userK = userKingdom ?: return false
        val cost = 45000
        if (userK.gold >= cost && userK.infirmaryLevel < 5) {
            userK.gold -= cost
            userK.infirmaryLevel++
            return true
        }
        return false
    }

    fun upgradeTraining(): Boolean {
        val userK = userKingdom ?: return false
        val cost = 50000
        if (userK.gold >= cost && userK.trainingLevel < 5) {
            userK.gold -= cost
            userK.trainingLevel++
            // Treino aumenta força e estamina dos jovens
            userK.warriors.forEach {
                if (it.age <= 24) {
                    it.stamina = 100
                }
            }
            return true
        }
        return false
    }
}
