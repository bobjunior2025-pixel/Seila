package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Match
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.KingdomBadge
import com.example.ui.components.TopKingdomBar
import com.example.ui.theme.*

@Composable
fun RoundSummaryScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val kingdoms by viewModel.repository.kingdoms.collectAsState()
    val currentRound by viewModel.repository.currentRound.collectAsState()
    val currentSeason by viewModel.repository.currentSeason.collectAsState()
    val matches by viewModel.repository.matches.collectAsState()
    val userKingdom = viewModel.repository.userKingdom

    val kingdomsMap = remember(kingdoms) { kingdoms.associateBy { it.id } }

    // Rodada anterior que acabou de ser concluída
    val completedRound = if (currentRound > 1) currentRound - 1 else 14
    val roundMatches = remember(matches, completedRound, userKingdom) {
        matches.filter { it.round == completedRound && it.division == (userKingdom?.division ?: 1) }
    }

    Scaffold(
        topBar = {
            TopKingdomBar(
                kingdom = userKingdom,
                round = completedRound,
                season = currentSeason,
                onMenuClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
            )
        },
        containerColor = DarkBg,
        bottomBar = {
            Surface(
                color = DarkSurface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = OnGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .height(48.dp)
                        .testTag("btn_continue_to_dashboard")
                ) {
                    Text("AVANÇAR PARA A RODADA $currentRound", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "📜 Resumo dos Resultados - Rodada $completedRound",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = RoyalGold
            )
            Text(
                text = "Confira o desfecho de todos os confrontos da divisão",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(roundMatches) { match ->
                    val homeK = kingdomsMap[match.homeKingdomId]
                    val awayK = kingdomsMap[match.awayKingdomId]
                    val isUserMatch = match.homeKingdomId == userKingdom?.id || match.awayKingdomId == userKingdom?.id

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUserMatch) RoyalGold.copy(alpha = 0.12f) else DarkSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isUserMatch) RoyalGold else DarkCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Home
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (homeK != null) KingdomBadge(kingdom = homeK, size = 30)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = homeK?.shortName ?: match.homeKingdomId,
                                    fontWeight = if (isUserMatch && match.homeKingdomId == userKingdom?.id) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (isUserMatch && match.homeKingdomId == userKingdom?.id) RoyalGold else TextPrimary,
                                    maxLines = 1
                                )
                            }

                            // Placar
                            Surface(
                                color = DarkSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "${match.homeScore} x ${match.awayScore}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = RoyalGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            // Away
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = awayK?.shortName ?: match.awayKingdomId,
                                    fontWeight = if (isUserMatch && match.awayKingdomId == userKingdom?.id) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (isUserMatch && match.awayKingdomId == userKingdom?.id) RoyalGold else TextPrimary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (awayK != null) KingdomBadge(kingdom = awayK, size = 30)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.STANDINGS) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver Classificação Atualizada da Liga", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
