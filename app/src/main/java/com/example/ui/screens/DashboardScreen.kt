package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.model.Kingdom
import com.example.model.Match
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.KingdomBadge
import com.example.ui.components.MedievalBottomNav
import com.example.ui.components.TopKingdomBar
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val kingdoms by viewModel.repository.kingdoms.collectAsState()
    val currentRound by viewModel.repository.currentRound.collectAsState()
    val currentSeason by viewModel.repository.currentSeason.collectAsState()
    val newsList by viewModel.repository.latestNews.collectAsState()

    val userKingdom = viewModel.repository.userKingdom
    val nextMatch = viewModel.repository.getNextMatchForUser()
    val allKingdomsMap = remember(kingdoms) { kingdoms.associateBy { it.id } }

    val standings = remember(kingdoms, currentRound) {
        if (userKingdom != null) viewModel.repository.getStandings(userKingdom.division) else emptyList()
    }
    val userPosition = standings.indexOfFirst { it.kingdomId == userKingdom?.id } + 1
    val userStanding = standings.firstOrNull { it.kingdomId == userKingdom?.id }

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
                currentScreen = AppScreen.DASHBOARD,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HERO CARD: PRÓXIMA BATALHA
            if (nextMatch != null && userKingdom != null) {
                val isHome = nextMatch.homeKingdomId == userKingdom.id
                val rivalId = if (isHome) nextMatch.awayKingdomId else nextMatch.homeKingdomId
                val rival = allKingdomsMap[rivalId]

                NextMatchHeroCard(
                    userKingdom = userKingdom,
                    rival = rival,
                    isHome = isHome,
                    round = currentRound,
                    onStartBattle = { viewModel.startUserMatch() }
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Todas as batalhas da temporada foram disputadas!",
                            fontWeight = FontWeight.Bold,
                            color = RoyalGold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.STANDINGS) },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = OnGold)
                        ) {
                            Text("Ver Tabela Final da Temporada")
                        }
                    }
                }
            }

            // GRID DE STATUS RÁPIDO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Posição na Liga
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(AppScreen.STANDINGS) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Posição", fontSize = 11.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (userPosition > 0) "${userPosition}º Lugar" else "-",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${userStanding?.points ?: 0} pts • Divisão ${userKingdom?.division ?: 1}",
                            fontSize = 11.sp,
                            color = EmeraldGuard
                        )
                    }
                }

                // Tática & Força
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(AppScreen.TACTICS) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = ArcaneCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tática", fontSize = 11.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = userKingdom?.formation?.code ?: "4-4-2",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Força Média: ${userKingdom?.squadOverallAverage ?: 70}",
                            fontSize = 11.sp,
                            color = LightGold
                        )
                    }
                }
            }

            // ATALHOS RÁPIDOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.SQUAD) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Escalar", fontSize = 12.sp, color = TextPrimary)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.MARKET) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = EmeraldGuard, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mercado", fontSize = 12.sp, color = TextPrimary)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.CASTLE) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Icon(Icons.Default.Castle, contentDescription = null, tint = ArcaneCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Castelo", fontSize = 12.sp, color = TextPrimary)
                }
            }

            // CRÔNICAS E NOTÍCIAS DO REINO
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crônicas e Avisos da Coroa", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    newsList.take(4).forEach { newsItem ->
                        Text(
                            text = newsItem,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NextMatchHeroCard(
    userKingdom: Kingdom,
    rival: Kingdom?,
    isHome: Boolean,
    round: Int,
    onStartBattle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, RoyalGold.copy(alpha = 0.7f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "⚔️ CONFRONTO DA RODADA $round ⚔️",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalGold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seu Reino
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KingdomBadge(kingdom = userKingdom, size = 56)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = userKingdom.shortName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isHome) "Mandante" else "Visitante",
                        fontSize = 10.sp,
                        color = if (isHome) EmeraldGuard else TextSecondary
                    )
                }

                // VS
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "VS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = CrimsonRuby
                    )
                    Text(
                        text = if (isHome) "Arena do ${userKingdom.shortName}" else "Arena Rival",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Rival
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (rival != null) {
                        KingdomBadge(kingdom = rival, size = 56)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rival.shortName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (!isHome) "Mandante" else "Visitante",
                            fontSize = 10.sp,
                            color = if (!isHome) EmeraldGuard else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartBattle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalGold,
                    contentColor = OnGold
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_start_match")
            ) {
                Icon(Icons.Default.SportsMartialArts, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "INICIAR BATALHA!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
