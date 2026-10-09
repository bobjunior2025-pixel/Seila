package com.example.model

enum class Position(val code: String, val label: String, val roleDesc: String) {
    GUARD("GDR", "Guardião", "Goleiro / Barreira de Escudos"),
    VANGUARD("VAN", "Vanguarda", "Defensor / Linha de Frente"),
    TACTICIAN("TAC", "Tático", "Mago / Bardo / Meio-Campo"),
    STRIKER("ATQ", "Atirador", "Algoz / Finalizador Letal")
}

enum class Formation(
    val code: String,
    val title: String,
    val guards: Int,
    val vanguards: Int,
    val tacticians: Int,
    val strikers: Int
) {
    FALANGE_442("4-4-2", "Falange Imperial (4-4-2)", 1, 4, 4, 2),
    CERCO_433("4-3-3", "Cerco do Dragão (4-3-3)", 1, 4, 3, 3),
    AVANCO_343("3-4-3", "Lanças Aladas (3-4-3)", 1, 3, 4, 3),
    MURALHA_532("5-3-2", "Muralha de Aço (5-3-2)", 1, 5, 3, 2),
    TEMPESTADE_451("4-5-1", "Tempestade Arcana (4-5-1)", 1, 4, 5, 1);

    val totalStarters: Int = 11
}

enum class Strategy(val label: String, val description: String, val attackMod: Float, val defenseMod: Float) {
    BALANCED("Equilibrada", "Balanço entre ataque e defesa militar", 1.0f, 1.0f),
    ALL_OUT_ATTACK("Carga Total", "Ataque frenético; alta chance de gols e contra-ataques", 1.35f, 0.75f),
    COUNTER_ATTACK("Emboscada", "Defesa sólida com saídas letais", 0.95f, 1.20f),
    FORTRESS_DEFENSE("Muralha Impenetrável", "Foco total em não sofrer gols", 0.70f, 1.45f)
}

enum class CombatFocus(val label: String, val desc: String) {
    PHYSICAL("Aço e Armadura", "Combate corpo a corpo vigoroso"),
    ARCANE("Feitiços e Encantamentos", "Magias de suporte e bolas de fogo"),
    TACTICAL("Velocidade e Precisão", "Movimentação rápida e flechas precisas")
}

data class Warrior(
    val id: String,
    val name: String,
    val kingdomId: String,
    val position: Position,
    val overall: Int,
    val attack: Int,
    val defense: Int,
    val magic: Int,
    var stamina: Int = 100, // 0..100
    var morale: Int = 4, // 1..5
    val age: Int = 23,
    val valueGold: Int,
    val wageGold: Int,
    var injuredRounds: Int = 0,
    var suspendedRounds: Int = 0,
    var yellowCards: Int = 0,
    var isStarter: Boolean = false,
    var goals: Int = 0,
    var assists: Int = 0,
    var matchesPlayed: Int = 0
) {
    val isAvailable: Boolean get() = injuredRounds == 0 && suspendedRounds == 0
}

data class Kingdom(
    val id: String,
    val name: String,
    val shortName: String,
    val division: Int, // 1 ou 2
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val coatIconName: String,
    var gold: Int,
    var coliseumCapacity: Int,
    var ticketPrice: Int = 20,
    var trainingLevel: Int = 1, // 1..5
    var infirmaryLevel: Int = 1, // 1..5
    var prestige: Int = 50,
    val warriors: MutableList<Warrior> = mutableListOf(),
    var formation: Formation = Formation.FALANGE_442,
    var strategy: Strategy = Strategy.BALANCED,
    var combatFocus: CombatFocus = CombatFocus.PHYSICAL
) {
    val starters: List<Warrior> get() = warriors.filter { it.isStarter && it.isAvailable }
    val reserves: List<Warrior> get() = warriors.filter { !it.isStarter && it.isAvailable }
    val squadOverallAverage: Int
        get() {
            val list = starters.ifEmpty { warriors.take(11) }
            return if (list.isNotEmpty()) list.map { it.overall }.average().toInt() else 60
        }
}

enum class MatchEventType {
    GOAL,
    SAVE,
    MISS,
    FOUL,
    YELLOW_CARD,
    RED_CARD,
    INJURY,
    INFO
}

data class MatchEvent(
    val minute: Int,
    val type: MatchEventType,
    val description: String,
    val kingdomId: String? = null,
    val warriorName: String? = null,
    val isHomeEvent: Boolean = true
)

data class Match(
    val id: String,
    val round: Int,
    val division: Int,
    val homeKingdomId: String,
    val awayKingdomId: String,
    var homeScore: Int = 0,
    var awayScore: Int = 0,
    var isPlayed: Boolean = false,
    val isCup: Boolean = false,
    val cupStage: String = "",
    val events: MutableList<MatchEvent> = mutableListOf()
)

data class Standing(
    val kingdomId: String,
    var points: Int = 0,
    var played: Int = 0,
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}
