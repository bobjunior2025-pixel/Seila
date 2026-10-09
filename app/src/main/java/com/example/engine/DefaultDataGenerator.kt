package com.example.engine

import com.example.model.*
import java.util.UUID

object DefaultDataGenerator {

    fun generateInitialKingdoms(): List<Kingdom> {
        val kingdoms = mutableListOf<Kingdom>()

        // 1ª Divisão
        kingdoms.add(
            createKingdom(
                id = "paladinos",
                name = "Ordem dos Paladinos",
                shortName = "PAL",
                division = 1,
                primaryColor = 0xFFF59E0B, // Ouro
                secondaryColor = 0xFF1E3A8A, // Azul Nobre
                coat = "shield_cross",
                gold = 150000,
                coliseum = 22000,
                basePower = 76
            )
        )
        kingdoms.add(
            createKingdom(
                id = "valiria",
                name = "Império Valíria",
                shortName = "VAL",
                division = 1,
                primaryColor = 0xFFDC2626, // Carmesim
                secondaryColor = 0xFFFBBF24, // Dourado
                coat = "dragon",
                gold = 180000,
                coliseum = 25000,
                basePower = 78
            )
        )
        kingdoms.add(
            createKingdom(
                id = "tempestade",
                name = "Guarda da Tempestade",
                shortName = "TEM",
                division = 1,
                primaryColor = 0xFF0284C7, // Azul Céu
                secondaryColor = 0xFF38BDF8, // Ciano
                coat = "lightning",
                gold = 135000,
                coliseum = 18000,
                basePower = 75
            )
        )
        kingdoms.add(
            createKingdom(
                id = "lobos",
                name = "Clã dos Lobos do Norte",
                shortName = "LOB",
                division = 1,
                primaryColor = 0xFF475569, // Ardósia
                secondaryColor = 0xFF94A3B8, // Prata
                coat = "wolf",
                gold = 120000,
                coliseum = 19000,
                basePower = 74
            )
        )
        kingdoms.add(
            createKingdom(
                id = "sombras",
                name = "Corte das Sombras",
                shortName = "SOM",
                division = 1,
                primaryColor = 0xFF7C3AED, // Violeta
                secondaryColor = 0xFF0F172A, // Preto
                coat = "dagger",
                gold = 140000,
                coliseum = 16000,
                basePower = 75
            )
        )
        kingdoms.add(
            createKingdom(
                id = "pedra_alta",
                name = "Feudo de Pedra Alta",
                shortName = "PED",
                division = 1,
                primaryColor = 0xFFD97706, // Bronze
                secondaryColor = 0xFF15803D, // Verde Floresta
                coat = "hammer",
                gold = 110000,
                coliseum = 17500,
                basePower = 72
            )
        )
        kingdoms.add(
            createKingdom(
                id = "crepusculo",
                name = "Legião do Crepúsculo",
                shortName = "CRE",
                division = 1,
                primaryColor = 0xFF9333EA, // Roxo Escuro
                secondaryColor = 0xFF991B1B, // Vinho
                coat = "skull",
                gold = 125000,
                coliseum = 16500,
                basePower = 73
            )
        )
        kingdoms.add(
            createKingdom(
                id = "silvestre",
                name = "Aliança Silvestre",
                shortName = "SIL",
                division = 1,
                primaryColor = 0xFF16A34A, // Verde Esmeralda
                secondaryColor = 0xFFF59E0B, // Ouro
                coat = "bow",
                gold = 130000,
                coliseum = 17000,
                basePower = 73
            )
        )

        // 2ª Divisão
        kingdoms.add(
            createKingdom(
                id = "costa_negra",
                name = "Corsários da Costa Negra",
                shortName = "COR",
                division = 2,
                primaryColor = 0xFF334155,
                secondaryColor = 0xFFEAB308,
                coat = "anchor",
                gold = 85000,
                coliseum = 12000,
                basePower = 64
            )
        )
        kingdoms.add(
            createKingdom(
                id = "falcao_rubro",
                name = "Ordem do Falcão Rubro",
                shortName = "FAL",
                division = 2,
                primaryColor = 0xFFB91C1C,
                secondaryColor = 0xFFF1F5F9,
                coat = "falcon",
                gold = 90000,
                coliseum = 13000,
                basePower = 66
            )
        )
        kingdoms.add(
            createKingdom(
                id = "geada",
                name = "Sentinelas da Geada",
                shortName = "GEO",
                division = 2,
                primaryColor = 0xFF0284C7,
                secondaryColor = 0xFFE0F2FE,
                coat = "snowflake",
                gold = 80000,
                coliseum = 11000,
                basePower = 63
            )
        )
        kingdoms.add(
            createKingdom(
                id = "ladinos",
                name = "Irmandade dos Ladinos",
                shortName = "LAD",
                division = 2,
                primaryColor = 0xFF78350F,
                secondaryColor = 0xFFD97706,
                coat = "hood",
                gold = 75000,
                coliseum = 10000,
                basePower = 62
            )
        )
        kingdoms.add(
            createKingdom(
                id = "ferro",
                name = "Vanguarda de Ferro",
                shortName = "FER",
                division = 2,
                primaryColor = 0xFF64748B,
                secondaryColor = 0xFFEA580C,
                coat = "anvil",
                gold = 88000,
                coliseum = 12500,
                basePower = 65
            )
        )
        kingdoms.add(
            createKingdom(
                id = "sol_nascente",
                name = "Feudo do Sol Nascente",
                shortName = "SOL",
                division = 2,
                primaryColor = 0xFFEA580C,
                secondaryColor = 0xFFFDE047,
                coat = "sun",
                gold = 82000,
                coliseum = 11500,
                basePower = 63
            )
        )
        kingdoms.add(
            createKingdom(
                id = "serpente",
                name = "Presas da Serpente",
                shortName = "SER",
                division = 2,
                primaryColor = 0xFF047857,
                secondaryColor = 0xFF022C22,
                coat = "snake",
                gold = 79000,
                coliseum = 10500,
                basePower = 62
            )
        )
        kingdoms.add(
            createKingdom(
                id = "rosa_branca",
                name = "Cavaleiros da Rosa Branca",
                shortName = "ROS",
                division = 2,
                primaryColor = 0xFFE11D48,
                secondaryColor = 0xFFFFFFFF,
                coat = "rose",
                gold = 95000,
                coliseum = 14000,
                basePower = 67
            )
        )

        return kingdoms
    }

    private fun createKingdom(
        id: String,
        name: String,
        shortName: String,
        division: Int,
        primaryColor: Long,
        secondaryColor: Long,
        coat: String,
        gold: Int,
        coliseum: Int,
        basePower: Int
    ): Kingdom {
        val kingdom = Kingdom(
            id = id,
            name = name,
            shortName = shortName,
            division = division,
            primaryColorHex = primaryColor,
            secondaryColorHex = secondaryColor,
            coatIconName = coat,
            gold = gold,
            coliseumCapacity = coliseum,
            ticketPrice = if (division == 1) 25 else 15,
            prestige = if (division == 1) 65 else 45
        )

        val warriors = generateWarriorsForKingdom(id, basePower)
        kingdom.warriors.addAll(warriors)
        setupDefaultStarters(kingdom)
        return kingdom
    }

    private val warriorNamesByPosition = mapOf(
        Position.GUARD to listOf(
            "Sir Geoffrey", "Gromm Barreira", "Muralha Donald", "Garrick Escudo",
            "Balthazar Titã", "Olaf Firme", "Krag Rocha", "Eldrin Sentinela",
            "Mestre Thorne", "Duncan Bastion", "Aldous O Guardião", "Roderick Escudeiro"
        ),
        Position.VANGUARD to listOf(
            "Sir Lancelot", "Ragnar Punho-de-Ferro", "Galahad Nobre", "Torstein Machado",
            "Percival Valente", "Gawain Lâmina", "Boromir Guerreiro", "Thorin Martelo",
            "Gregor O Montanha", "Dain Pés-de-Ferro", "Sandor Destemido", "Cassian Lança",
            "Roland Paladino", "Bors O Forte", "Einar Fúria", "Thorgal Machado"
        ),
        Position.TACTICIAN to listOf(
            "Mago Aldous", "Morgana Feiticeira", "Merlin O Sábio", "Bardo Liam",
            "Radagast Arcano", "Lyanna Cantora", "Theron Mente-Lúcida", "Faelar Élfico",
            "Cedric Estrategista", "Kaelin Chamas", "Morrigan Feitiço", "Alvo Místico",
            "Gideon Runas", "Vaelen Vento", "Dorian Tempestade", "Zephyr Relâmpago"
        ),
        Position.STRIKER to listOf(
            "Arthur Matador", "Geralt Lobo", "Lyra Flecha-Veloz", "Legolas Mirador",
            "Robin Justiceiro", "Dragan Fera", "Sylas O Algoz", "Valerius Espada-do-Sol",
            "Aric Falcão", "Bjorn O Sanguinário", "Kallan Flecha-de-Fogo", "Malakor Ceifador",
            "Kael Tiro-Certo", "Corvin Sombra", "Rowan Caçador", "Titus Conquistador"
        )
    )

    private fun generateWarriorsForKingdom(kingdomId: String, basePower: Int): List<Warrior> {
        val list = mutableListOf<Warrior>()
        val rand = kotlin.random.Random(kingdomId.hashCode())

        // 2 Guards
        for (i in 1..2) {
            val name = warriorNamesByPosition[Position.GUARD]!!.random(rand) + " $kingdomId".takeLast(3).uppercase()
            val overall = (basePower - 3 + rand.nextInt(7)).coerceIn(50, 95)
            list.add(createWarrior(kingdomId, name, Position.GUARD, overall, rand))
        }

        // 5 Vanguards
        for (i in 1..5) {
            val name = warriorNamesByPosition[Position.VANGUARD]!!.random(rand) + " " + ('A' + rand.nextInt(26))
            val overall = (basePower - 4 + rand.nextInt(9)).coerceIn(50, 95)
            list.add(createWarrior(kingdomId, name, Position.VANGUARD, overall, rand))
        }

        // 5 Tacticians
        for (i in 1..5) {
            val name = warriorNamesByPosition[Position.TACTICIAN]!!.random(rand) + " " + ('A' + rand.nextInt(26))
            val overall = (basePower - 4 + rand.nextInt(9)).coerceIn(50, 95)
            list.add(createWarrior(kingdomId, name, Position.TACTICIAN, overall, rand))
        }

        // 4 Strikers
        for (i in 1..4) {
            val name = warriorNamesByPosition[Position.STRIKER]!!.random(rand) + " " + ('A' + rand.nextInt(26))
            val overall = (basePower - 3 + rand.nextInt(9)).coerceIn(50, 96)
            list.add(createWarrior(kingdomId, name, Position.STRIKER, overall, rand))
        }

        return list
    }

    private fun createWarrior(
        kingdomId: String,
        name: String,
        pos: Position,
        overall: Int,
        rand: kotlin.random.Random
    ): Warrior {
        val atk = when (pos) {
            Position.STRIKER -> (overall + rand.nextInt(6) - 1).coerceIn(40, 99)
            Position.TACTICIAN -> (overall - 5 + rand.nextInt(8)).coerceIn(35, 95)
            Position.VANGUARD -> (overall - 18 + rand.nextInt(10)).coerceIn(30, 85)
            Position.GUARD -> (overall - 35 + rand.nextInt(10)).coerceIn(15, 60)
        }
        val def = when (pos) {
            Position.GUARD -> (overall + rand.nextInt(6)).coerceIn(45, 99)
            Position.VANGUARD -> (overall + rand.nextInt(5)).coerceIn(40, 98)
            Position.TACTICIAN -> (overall - 8 + rand.nextInt(8)).coerceIn(30, 90)
            Position.STRIKER -> (overall - 25 + rand.nextInt(10)).coerceIn(20, 75)
        }
        val mag = when (pos) {
            Position.TACTICIAN -> (overall + rand.nextInt(6)).coerceIn(40, 99)
            Position.GUARD -> (overall - 10 + rand.nextInt(10)).coerceIn(25, 85)
            Position.STRIKER -> (overall - 12 + rand.nextInt(12)).coerceIn(25, 88)
            Position.VANGUARD -> (overall - 15 + rand.nextInt(10)).coerceIn(20, 80)
        }
        val age = 18 + rand.nextInt(16)
        val wage = (overall * 75) + (atk + def + mag) * 10
        val value = overall * overall * 110 + (35 - age) * 1500

        return Warrior(
            id = UUID.randomUUID().toString(),
            name = name,
            kingdomId = kingdomId,
            position = pos,
            overall = overall,
            attack = atk,
            defense = def,
            magic = mag,
            stamina = 95 + rand.nextInt(6),
            morale = 3 + rand.nextInt(3),
            age = age,
            valueGold = value,
            wageGold = wage
        )
    }

    fun setupDefaultStarters(kingdom: Kingdom) {
        val form = kingdom.formation
        kingdom.warriors.forEach { it.isStarter = false }

        val guards = kingdom.warriors.filter { it.position == Position.GUARD && it.isAvailable }
            .sortedByDescending { it.overall }
        val vanguards = kingdom.warriors.filter { it.position == Position.VANGUARD && it.isAvailable }
            .sortedByDescending { it.overall }
        val tacticians = kingdom.warriors.filter { it.position == Position.TACTICIAN && it.isAvailable }
            .sortedByDescending { it.overall }
        val strikers = kingdom.warriors.filter { it.position == Position.STRIKER && it.isAvailable }
            .sortedByDescending { it.overall }

        guards.take(form.guards).forEach { it.isStarter = true }
        vanguards.take(form.vanguards).forEach { it.isStarter = true }
        tacticians.take(form.tacticians).forEach { it.isStarter = true }
        strikers.take(form.strikers).forEach { it.isStarter = true }
    }

    /**
     * Gera tabela de 14 rodadas (turno e returno) para liga de 8 times usando algoritmo round-robin
     */
    fun generateLeagueFixtures(kingdoms: List<Kingdom>, division: Int): List<Match> {
        val divKingdoms = kingdoms.filter { it.division == division }
        val n = divKingdoms.size // 8
        val rounds = (n - 1) * 2 // 14
        val matches = mutableListOf<Match>()

        val teamIds = divKingdoms.map { it.id }.toMutableList()

        for (round in 1..(n - 1)) {
            for (i in 0 until (n / 2)) {
                val t1 = teamIds[i]
                val t2 = teamIds[n - 1 - i]
                // Alterna mando de campo
                val (home, away) = if (round % 2 == 1) Pair(t1, t2) else Pair(t2, t1)
                matches.add(
                    Match(
                        id = "div_${division}_r${round}_m${matches.size}",
                        round = round,
                        division = division,
                        homeKingdomId = home,
                        awayKingdomId = away
                    )
                )
            }
            // Rotaciona mantendo o primeiro time fixo
            val last = teamIds.removeAt(teamIds.size - 1)
            teamIds.add(1, last)
        }

        // Returno (inverte mando)
        val firstHalfMatches = matches.toList()
        for (m in firstHalfMatches) {
            val returnRound = m.round + (n - 1)
            matches.add(
                Match(
                    id = "div_${division}_r${returnRound}_m${matches.size}",
                    round = returnRound,
                    division = division,
                    homeKingdomId = m.awayKingdomId,
                    awayKingdomId = m.homeKingdomId
                )
            )
        }

        return matches
    }

    fun generateCupFixtures(allKingdoms: List<Kingdom>): List<Match> {
        // Copa da Coroa: 16 times -> Oitavas de Final (ou Quartas diretas com os 8 da 1ª div e convidados da 2ª)
        // Para agilidade e emoção de Brasfoot: Quartas de final com os 8 melhores da temporada anterior
        val selected = allKingdoms.filter { it.division == 1 }.shuffled()
        val cupMatches = mutableListOf<Match>()

        for (i in 0 until 4) {
            cupMatches.add(
                Match(
                    id = "cup_qf_$i",
                    round = 4, // Ocorre na rodada 4
                    division = 0,
                    homeKingdomId = selected[i * 2].id,
                    awayKingdomId = selected[i * 2 + 1].id,
                    isCup = true,
                    cupStage = "Quartas de Final"
                )
            )
        }
        return cupMatches
    }

    fun generateTransferMarketWarriors(allKingdoms: List<Kingdom>): List<Warrior> {
        val rand = kotlin.random.Random(42)
        val freeAgents = mutableListOf<Warrior>()
        val names = listOf(
            "Sir Tristan O Errante", "Kharim Lâmina-do-Deserto", "Aethelgard O Renegado",
            "Valen Arqueiro-Negro", "Brunhilda Valquíria", "Fenris Garra-de-Ferro",
            "Mestre Ignatius Mago-de-Fogo", "Kaelen Elfo-da-Noite", "Barão Von Krieger",
            "Siegfried Caçador-de-Dragões", "Gunnar Machado-Duplo", "Sylvia Sacerdotisa"
        )
        val positions = listOf(Position.GUARD, Position.VANGUARD, Position.TACTICIAN, Position.STRIKER)

        names.forEachIndexed { index, name ->
            val pos = positions[index % positions.size]
            val overall = 62 + rand.nextInt(26)
            val w = createWarrior("free_agents", name, pos, overall, rand)
            freeAgents.add(w)
        }
        return freeAgents
    }
}
