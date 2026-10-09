package com.example.engine

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object SimulationEngine {

    data class TeamCombatPower(
        val guardPower: Float,
        val defensePower: Float,
        val midfieldPower: Float,
        val attackPower: Float,
        val totalPower: Float
    )

    fun calculateTeamPower(kingdom: Kingdom, isHome: Boolean): TeamCombatPower {
        val starters = kingdom.starters.ifEmpty { kingdom.warriors.take(11) }

        val guards = starters.filter { it.position == Position.GUARD }
        val vanguards = starters.filter { it.position == Position.VANGUARD }
        val tacticians = starters.filter { it.position == Position.TACTICIAN }
        val strikers = starters.filter { it.position == Position.STRIKER }

        val avgGuard = if (guards.isNotEmpty()) guards.map { it.defense * (it.stamina / 100f) }.average().toFloat() else 50f
        val avgDef = if (vanguards.isNotEmpty()) vanguards.map { it.defense * (it.stamina / 100f) }.average().toFloat() else 50f
        val avgMid = if (tacticians.isNotEmpty()) tacticians.map { ((it.magic + it.attack) / 2f) * (it.stamina / 100f) }.average().toFloat() else 50f
        val avgAtk = if (strikers.isNotEmpty()) strikers.map { it.attack * (it.stamina / 100f) }.average().toFloat() else 50f

        val strat = kingdom.strategy
        val homeBonus = if (isHome) 1.08f else 1.0f

        val adjustedGuard = avgGuard * strat.defenseMod * homeBonus
        val adjustedDef = avgDef * strat.defenseMod * homeBonus
        val adjustedMid = avgMid * homeBonus
        val adjustedAtk = avgAtk * strat.attackMod * homeBonus

        val total = (adjustedGuard * 0.2f + adjustedDef * 0.3f + adjustedMid * 0.25f + adjustedAtk * 0.25f)

        return TeamCombatPower(
            guardPower = adjustedGuard,
            defensePower = adjustedDef,
            midfieldPower = adjustedMid,
            attackPower = adjustedAtk,
            totalPower = total
        )
    }

    /**
     * Simula instantaneamente uma partida entre dois times controlados por IA
     */
    fun simulateQuickMatch(match: Match, homeKingdom: Kingdom, awayKingdom: Kingdom) {
        val homePower = calculateTeamPower(homeKingdom, isHome = true)
        val awayPower = calculateTeamPower(awayKingdom, isHome = false)

        val powerDiff = homePower.totalPower - awayPower.totalPower

        // Gols baseados no equilíbrio de forças estilo Brasfoot
        val homeChance = max(0.5f, 1.4f + (powerDiff / 35f))
        val awayChance = max(0.4f, 1.1f - (powerDiff / 35f))

        var homeGoals = 0
        var awayGoals = 0

        val homeStrikers = homeKingdom.starters.filter { it.position == Position.STRIKER || it.position == Position.TACTICIAN }
        val awayStrikers = awayKingdom.starters.filter { it.position == Position.STRIKER || it.position == Position.TACTICIAN }

        for (min in 1..90) {
            if (min % 5 == 0 && Random.nextFloat() < (homeChance / 18f)) {
                homeGoals++
                val scorer = homeStrikers.randomOrNull()?.name ?: "Guerreiro do ${homeKingdom.shortName}"
                match.events.add(
                    MatchEvent(
                        minute = min,
                        type = MatchEventType.GOAL,
                        description = "GOLPE DECISIVO! $scorer finalizou com maestria contra o ${awayKingdom.name}!",
                        kingdomId = homeKingdom.id,
                        warriorName = scorer,
                        isHomeEvent = true
                    )
                )
            }
            if (min % 5 == 0 && Random.nextFloat() < (awayChance / 19f)) {
                awayGoals++
                val scorer = awayStrikers.randomOrNull()?.name ?: "Guerreiro do ${awayKingdom.shortName}"
                match.events.add(
                    MatchEvent(
                        minute = min,
                        type = MatchEventType.GOAL,
                        description = "GOLPE DE MISERICÓRDIA! $scorer balançou as redes do coliseu!",
                        kingdomId = awayKingdom.id,
                        warriorName = scorer,
                        isHomeEvent = false
                    )
                )
            }
        }

        match.homeScore = homeGoals
        match.awayScore = awayGoals
        match.isPlayed = true

        // Desgaste físico dos guerreiros
        consumeStamina(homeKingdom)
        consumeStamina(awayKingdom)
    }

    private fun consumeStamina(kingdom: Kingdom) {
        kingdom.starters.forEach { warrior ->
            val drain = Random.nextInt(4, 9)
            warrior.stamina = (warrior.stamina - drain).coerceIn(40, 100)
            warrior.matchesPlayed++
        }
        // Reservas recuperam energia
        kingdom.reserves.forEach { warrior ->
            warrior.stamina = min(100, warrior.stamina + 10)
        }
    }

    /**
     * Executa 1 minuto da partida interativa ao vivo
     */
    fun simulateMinuteStep(
        currentMinute: Int,
        match: Match,
        homeKingdom: Kingdom,
        awayKingdom: Kingdom
    ): MatchEvent? {
        val homePower = calculateTeamPower(homeKingdom, isHome = true)
        val awayPower = calculateTeamPower(awayKingdom, isHome = false)

        val totalPower = homePower.totalPower + awayPower.totalPower
        val homePossession = if (totalPower > 0) (homePower.totalPower / totalPower) else 0.5f

        // Possibilidade de lance perigoso neste minuto
        val eventRoll = Random.nextFloat()

        if (eventRoll < 0.16f) { // ~14 lances narrados por partida
            val isHomeAttacking = Random.nextFloat() < homePossession
            val attackingTeam = if (isHomeAttacking) homeKingdom else awayKingdom
            val defendingTeam = if (isHomeAttacking) awayKingdom else homeKingdom
            val atkPower = if (isHomeAttacking) homePower.attackPower else awayPower.attackPower
            val defPower = if (isHomeAttacking) awayPower.defensePower else homePower.defensePower
            val guardPower = if (isHomeAttacking) awayPower.guardPower else homePower.guardPower

            val attackers = attackingTeam.starters.filter { it.position == Position.STRIKER || it.position == Position.TACTICIAN }
                .ifEmpty { attackingTeam.starters }
            val attacker = attackers.randomOrNull()
            val defenders = defendingTeam.starters.filter { it.position == Position.VANGUARD }
            val defender = defenders.randomOrNull()
            val guard = defendingTeam.starters.firstOrNull { it.position == Position.GUARD }

            val goalChance = (atkPower / (atkPower + defPower + guardPower * 0.8f)) * 0.55f

            val outcomeRoll = Random.nextFloat()
            return when {
                outcomeRoll < goalChance -> {
                    // GOL!
                    if (isHomeAttacking) match.homeScore++ else match.awayScore++
                    attacker?.let { it.goals++ }
                    val desc = generateGoalDescription(attacker?.name ?: "Herói", attackingTeam.name)
                    val ev = MatchEvent(
                        minute = currentMinute,
                        type = MatchEventType.GOAL,
                        description = desc,
                        kingdomId = attackingTeam.id,
                        warriorName = attacker?.name,
                        isHomeEvent = isHomeAttacking
                    )
                    match.events.add(ev)
                    ev
                }
                outcomeRoll < (goalChance + 0.35f) -> {
                    // DEFESA DO GUARDIÃO!
                    val desc = generateSaveDescription(guard?.name ?: "Guardião", attacker?.name ?: "Atacante")
                    val ev = MatchEvent(
                        minute = currentMinute,
                        type = MatchEventType.SAVE,
                        description = desc,
                        kingdomId = defendingTeam.id,
                        warriorName = guard?.name,
                        isHomeEvent = !isHomeAttacking
                    )
                    match.events.add(ev)
                    ev
                }
                outcomeRoll < (goalChance + 0.65f) -> {
                    // FALTA OU CARTÃO
                    if (Random.nextFloat() < 0.30f) {
                        defender?.yellowCards = (defender?.yellowCards ?: 0) + 1
                        val desc = "CARTÃO DE DESONRA! ${defender?.name ?: "Defensor"} cometeu falta desleal e foi advertido!"
                        val ev = MatchEvent(
                            minute = currentMinute,
                            type = MatchEventType.YELLOW_CARD,
                            description = desc,
                            kingdomId = defendingTeam.id,
                            warriorName = defender?.name,
                            isHomeEvent = !isHomeAttacking
                        )
                        match.events.add(ev)
                        ev
                    } else {
                        val desc = "FALTA TÁTICA! ${defender?.name ?: "Vanguarda"} travou o avanço de ${attacker?.name ?: "Atacante"}."
                        val ev = MatchEvent(
                            minute = currentMinute,
                            type = MatchEventType.FOUL,
                            description = desc,
                            kingdomId = defendingTeam.id,
                            warriorName = defender?.name,
                            isHomeEvent = !isHomeAttacking
                        )
                        match.events.add(ev)
                        ev
                    }
                }
                else -> {
                    // CHANCE DESPERDIÇADA
                    val desc = "${attacker?.name ?: "Atacante"} arriscou o disparo de longa distância, mas a bola passou raspando o travessão de pedra!"
                    val ev = MatchEvent(
                        minute = currentMinute,
                        type = MatchEventType.MISS,
                        description = desc,
                        kingdomId = attackingTeam.id,
                        warriorName = attacker?.name,
                        isHomeEvent = isHomeAttacking
                    )
                    match.events.add(ev)
                    ev
                }
            }
        }
        return null
    }

    private fun generateGoalDescription(attacker: String, team: String): String {
        val quotes = listOf(
            "GOL HISTÓRICO! $attacker acertou uma bomba indefensável no ângulo das redes!",
            "GOLPE DE GLÓRIA! $attacker driblou a vanguarda e tocou com categoria para o delírio da torcida!",
            "EXPLOSÃO NO COLISEU! $attacker aproveitou cruzamento perfeito e finalizou de cabeça!",
            "CONQUISTA! $attacker recebeu na entrada da área e estufou as redes com força descomunal!",
            "GOL DO $team! $attacker mostrou instinto de predador e mandou para o fundo das redes!"
        )
        return quotes.random()
    }

    private fun generateSaveDescription(guard: String, attacker: String): String {
        val quotes = listOf(
            "DEFESA MONUMENTAL! $guard voou de ponta a ponta e espalmou a finalização perigosa de $attacker!",
            "MILAGRE DO GUARDIÃO! $guard fechou o ângulo e barrou o chute cara a cara com $attacker!",
            "PAREDE INABALÁVEL! $guard segurou firme o disparo violento vindo de fora da área!"
        )
        return quotes.random()
    }
}
