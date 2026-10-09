package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.*
import com.example.ui.GameViewModel
import com.example.ui.components.KingdomBadge
import com.example.ui.components.PositionBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMatchScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val liveState by viewModel.liveMatchState.collectAsState()
    var showSubSheet by remember { mutableStateOf(false) }

    if (liveState == null) {
        Box(modifier = modifier.fillMaxSize().background(DarkBg), contentAlignment = Alignment.Center) {
            Text("Nenhuma batalha em andamento.", color = TextSecondary)
        }
        return
    }

    val state = liveState!!
    val homeK = state.homeKingdom
    val awayK = state.awayKingdom
    val userK = viewModel.repository.userKingdom

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            // Controles de Jogo Inferiores
            Surface(
                color = DarkSurface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                if (!state.isFinished) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play / Pause
                        IconButton(
                            onClick = { viewModel.toggleMatchPause() },
                            modifier = Modifier.testTag("btn_toggle_match_pause")
                        ) {
                            Icon(
                                imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (state.isRunning) "Pausar" else "Continuar",
                                tint = RoyalGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Velocidade
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1, 2, 5).forEach { speed ->
                                FilterChip(
                                    selected = state.speedMultiplier == speed,
                                    onClick = { viewModel.setMatchSpeed(speed) },
                                    label = { Text("${speed}x", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalGold,
                                        selectedLabelColor = OnGold
                                    ),
                                    modifier = Modifier.testTag("btn_speed_${speed}x")
                                )
                            }
                        }

                        // Substituição
                        OutlinedButton(
                            onClick = {
                                viewModel.toggleMatchPause() // Pausa para trocar
                                showSubSheet = true
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, ArcaneCyan),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ArcaneCyan),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_live_sub")
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Substituir", fontSize = 11.sp)
                        }

                        // Pular para o Fim
                        IconButton(
                            onClick = { viewModel.instantFinishMatch() },
                            modifier = Modifier.testTag("btn_instant_finish")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "Pular para o Fim",
                                tint = TextSecondary
                            )
                        }
                    }
                } else {
                    // Botão de Avançar quando a partida terminar
                    Button(
                        onClick = { viewModel.finishMatchAndGoToSummary() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalGold,
                            contentColor = OnGold
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .height(48.dp)
                            .testTag("btn_finish_match_summary")
                    ) {
                        Text(
                            text = "CONTINUAR PARA RESULTADOS DA RODADA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // PLACAR PRINCIPAL MEDIEVAL
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Cronômetro Central
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Batalha do Reino",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Surface(
                            color = if (state.isFinished) EmeraldGuard.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (state.isFinished) EmeraldGuard else RoyalGold)
                        ) {
                            Text(
                                text = if (state.isFinished) "FIM DE JOGO (90')" else "${state.currentMinute}'",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (state.isFinished) EmeraldGuard else RoyalGold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = if (state.isRunning) "EM COMBATE" else if (state.isFinished) "ENCERRADO" else "PAUSADO",
                            fontSize = 11.sp,
                            color = if (state.isRunning) EmeraldGuard else RoyalGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Linha dos Times e Placar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Mandante
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            KingdomBadge(kingdom = homeK, size = 44)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = homeK.shortName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text("Mandante", fontSize = 10.sp, color = TextMuted)
                            }
                        }

                        // Placar Gigante
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = "${state.match.homeScore}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 34.sp,
                                color = RoyalGold
                            )
                            Text(
                                text = " : ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${state.match.awayScore}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 34.sp,
                                color = RoyalGold
                            )
                        }

                        // Visitante
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = awayK.shortName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text("Visitante", fontSize = 10.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            KingdomBadge(kingdom = awayK, size = 44)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Barra de Domínio da Arena / Posse
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${state.homePossessionPercent}% Domínio", fontSize = 10.sp, color = Color(homeK.primaryColorHex), fontWeight = FontWeight.Bold)
                        Text("${100 - state.homePossessionPercent}% Domínio", fontSize = 10.sp, color = Color(awayK.primaryColorHex), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { state.homePossessionPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(homeK.primaryColorHex),
                        trackColor = Color(awayK.primaryColorHex)
                    )
                }
            }

            // FEED DE LANCES DA PARTIDA AO VIVO (ESTILO BRASFOOT)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.events.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Os guerreiros entram na arena sob o rugido da multidão...",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(state.events) { ev ->
                        LiveEventItemCard(event = ev, homeKingdomId = homeK.id)
                    }
                }
            }
        }
    }

    // Modal de Substituição Durante a Partida
    if (showSubSheet && userK != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showSubSheet = false
                viewModel.toggleMatchPause() // Retoma
            },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Substituição Tática do ${userK.shortName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = RoyalGold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Titulares em campo:", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))

                var selectedStarter by remember { mutableStateOf<Warrior?>(null) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (selectedStarter == null) {
                        items(userK.starters) { w ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedStarter = w }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PositionBadge(position = w.position)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(w.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                    Text("Vigor: ${w.stamina}%", fontSize = 11.sp, color = EmeraldGuard)
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Substituir: ${selectedStarter!!.name}. Escolha o reserva:",
                                fontSize = 12.sp,
                                color = ArcaneCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(userK.reserves) { r ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.executeSubstitution(selectedStarter!!, r)
                                        showSubSheet = false
                                        viewModel.toggleMatchPause()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PositionBadge(position = r.position)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(r.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                    Text("FOR ${r.overall}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalGold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveEventItemCard(
    event: MatchEvent,
    homeKingdomId: String,
    modifier: Modifier = Modifier
) {
    val (icon, tintColor, bgContainer) = when (event.type) {
        MatchEventType.GOAL -> Triple(Icons.Default.SportsSoccer, RoyalGold, RoyalGold.copy(alpha = 0.15f))
        MatchEventType.SAVE -> Triple(Icons.Default.Shield, EmeraldGuard, EmeraldGuard.copy(alpha = 0.12f))
        MatchEventType.YELLOW_CARD -> Triple(Icons.Default.Style, Color(0xFFFBBF24), Color(0xFFFBBF24).copy(alpha = 0.15f))
        MatchEventType.RED_CARD -> Triple(Icons.Default.Style, CrimsonRuby, CrimsonRuby.copy(alpha = 0.2f))
        MatchEventType.FOUL -> Triple(Icons.Default.Warning, Color(0xFFFB923C), Color(0xFFFB923C).copy(alpha = 0.1f))
        MatchEventType.INJURY -> Triple(Icons.Default.LocalHospital, CrimsonRuby, CrimsonRuby.copy(alpha = 0.15f))
        else -> Triple(Icons.Default.SportsMartialArts, TextSecondary, DarkSurfaceVariant)
    }

    Surface(
        color = bgContainer,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, tintColor.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = tintColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${event.minute}'",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = tintColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (event.type) {
                            MatchEventType.GOAL -> "GOL!"
                            MatchEventType.SAVE -> "DEFESA!"
                            MatchEventType.YELLOW_CARD -> "CARTÃO AMARELO"
                            MatchEventType.RED_CARD -> "EXPULSÃO"
                            MatchEventType.FOUL -> "FALTA"
                            MatchEventType.INJURY -> "LESÃO"
                            else -> "LANCE"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tintColor
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
