package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Standing
import com.example.model.Warrior
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.KingdomBadge
import com.example.ui.components.MedievalBottomNav
import com.example.ui.components.PositionBadge
import com.example.ui.components.TopKingdomBar
import com.example.ui.theme.*

@Composable
fun StandingsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val kingdoms by viewModel.repository.kingdoms.collectAsState()
    val currentRound by viewModel.repository.currentRound.collectAsState()
    val currentSeason by viewModel.repository.currentSeason.collectAsState()
    val userKingdom = viewModel.repository.userKingdom

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Div 1, 1: Div 2, 2: Artilharia

    val div1Standings = remember(kingdoms, currentRound) { viewModel.repository.getStandings(1) }
    val div2Standings = remember(kingdoms, currentRound) { viewModel.repository.getStandings(2) }

    val allWarriors = remember(kingdoms) {
        kingdoms.flatMap { it.warriors }.sortedByDescending { it.goals }.take(15)
    }

    val kingdomsMap = remember(kingdoms) { kingdoms.associateBy { it.id } }

    Scaffold(
        topBar = {
            TopKingdomBar(
                kingdom = userKingdom,
                round = currentRound,
                season = currentSeason,
                onMenuClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
            )
        },
        bottomBar = {
            MedievalBottomNav(
                currentScreen = AppScreen.STANDINGS,
                onNavigate = { viewModel.navigateTo(it) }
            )
        },
        containerColor = DarkBg,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceVariant,
                contentColor = RoyalGold,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(it[selectedTab]),
                        color = RoyalGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("1ª DIVISÃO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedTab == 0) RoyalGold else TextSecondary) },
                    modifier = Modifier.testTag("tab_standings_div1")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("2ª DIVISÃO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedTab == 1) RoyalGold else TextSecondary) },
                    modifier = Modifier.testTag("tab_standings_div2")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("ARTILHARIA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedTab == 2) RoyalGold else TextSecondary) },
                    modifier = Modifier.testTag("tab_standings_scorers")
                )
            }

            when (selectedTab) {
                0 -> LeagueTable(standings = div1Standings, kingdomsMap = kingdomsMap, userKingdomId = userKingdom?.id, division = 1)
                1 -> LeagueTable(standings = div2Standings, kingdomsMap = kingdomsMap, userKingdomId = userKingdom?.id, division = 2)
                2 -> TopScorersList(warriors = allWarriors, kingdomsMap = kingdomsMap)
            }
        }
    }
}

@Composable
fun LeagueTable(
    standings: List<Standing>,
    kingdomsMap: Map<String, com.example.model.Kingdom>,
    userKingdomId: String?,
    division: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp)) {
        // Cabeçalho da Tabela
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(24.dp))
            Text("Reino", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.weight(1f))
            Text("P", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = RoyalGold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
            Text("J", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            Text("V", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            Text("E", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            Text("D", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            Text("SG", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(standings) { index, standing ->
                val pos = index + 1
                val kingdom = kingdomsMap[standing.kingdomId]
                val isUser = standing.kingdomId == userKingdomId

                val posColor = when {
                    division == 1 && pos == 1 -> RoyalGold // Campeão
                    division == 1 && pos >= 7 -> CrimsonRuby // Rebaixamento
                    division == 2 && pos <= 2 -> EmeraldGuard // Acesso
                    else -> TextSecondary
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isUser) RoyalGold.copy(alpha = 0.12f) else DarkSurface)
                        .border(
                            1.dp,
                            if (isUser) RoyalGold.copy(alpha = 0.5f) else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$pos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = posColor,
                        modifier = Modifier.width(24.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (kingdom != null) {
                            KingdomBadge(kingdom = kingdom, size = 22)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = kingdom?.shortName ?: standing.kingdomId,
                            fontWeight = if (isUser) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isUser) RoyalGold else TextPrimary,
                            maxLines = 1
                        )
                    }

                    Text("${standing.points}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalGold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                    Text("${standing.played}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text("${standing.wins}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text("${standing.draws}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text("${standing.losses}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text("${standing.goalDifference}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    if (division == 1) {
                        LegendaItem(color = RoyalGold, label = "Campeão da Liga")
                        LegendaItem(color = CrimsonRuby, label = "Descenso (2ª Div)")
                    } else {
                        LegendaItem(color = EmeraldGuard, label = "Acesso (1ª Div)")
                    }
                }
            }
        }
    }
}

@Composable
fun LegendaItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
fun TopScorersList(
    warriors: List<Warrior>,
    kingdomsMap: Map<String, com.example.model.Kingdom>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(warriors) { index, warrior ->
            val kingdom = kingdomsMap[warrior.kingdomId]
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}º",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (index == 0) RoyalGold else TextSecondary,
                        modifier = Modifier.width(30.dp)
                    )
                    PositionBadge(position = warrior.position)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(warrior.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                        Text(kingdom?.name ?: warrior.kingdomId, fontSize = 11.sp, color = TextSecondary)
                    }
                    Surface(
                        color = CrimsonRuby.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRuby)
                    ) {
                        Text(
                            text = "⚔️ ${warrior.goals} GOLS",
                            fontWeight = FontWeight.Bold,
                            color = CrimsonRuby,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
