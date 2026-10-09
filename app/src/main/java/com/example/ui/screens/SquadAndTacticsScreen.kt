package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.model.*
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun SquadAndTacticsScreen(
    viewModel: GameViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    val currentRound by viewModel.repository.currentRound.collectAsState()
    val currentSeason by viewModel.repository.currentSeason.collectAsState()
    val userKingdom = viewModel.repository.userKingdom
    val selectedForSub by viewModel.selectedWarriorForSub.collectAsState()

    var activeTab by remember { mutableIntStateOf(initialTab) }

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
                currentScreen = AppScreen.SQUAD,
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
            // Seletor de Abas: Titulares, Reservas, Táticas
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSurfaceVariant,
                contentColor = RoyalGold,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(it[activeTab]),
                        color = RoyalGold
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Text(
                            "TITULARES (${userKingdom?.starters?.size ?: 0}/11)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (activeTab == 0) RoyalGold else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_starters")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            "RESERVAS (${userKingdom?.reserves?.size ?: 0})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (activeTab == 1) RoyalGold else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_reserves")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = {
                        Text(
                            "TÁTICAS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (activeTab == 2) RoyalGold else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_tactics")
                )
            }

            // Banner informativo se houver guerreiro selecionado para substituição
            if (selectedForSub != null) {
                Surface(
                    color = ArcaneCyan.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArcaneCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🔄 Substituindo: ${selectedForSub?.name}. Escolha o substituto!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ArcaneCyan
                        )
                        TextButton(onClick = { viewModel.selectWarriorForSub(null) }) {
                            Text("Cancelar", color = CrimsonRuby, fontSize = 12.sp)
                        }
                    }
                }
            }

            when (activeTab) {
                0 -> {
                    // Lista de Titulares
                    val starters = userKingdom?.starters.orEmpty()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(starters, key = { it.id }) { warrior ->
                            WarriorItemCard(
                                warrior = warrior,
                                isSelected = selectedForSub?.id == warrior.id,
                                isStarterList = true,
                                onClick = {
                                    if (selectedForSub == null) {
                                        viewModel.selectWarriorForSub(warrior)
                                    } else {
                                        viewModel.selectWarriorForSub(null)
                                    }
                                },
                                onSell = null
                            )
                        }
                    }
                }
                1 -> {
                    // Lista de Reservas
                    val reserves = userKingdom?.reserves.orEmpty()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reserves, key = { it.id }) { warrior ->
                            WarriorItemCard(
                                warrior = warrior,
                                isSelected = false,
                                isStarterList = false,
                                onClick = {
                                    if (selectedForSub != null) {
                                        viewModel.executeSubstitution(selectedForSub!!, warrior)
                                    }
                                },
                                onSell = {
                                    viewModel.sellWarrior(warrior.id)
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Tela de Táticas
                    TacticsView(
                        kingdom = userKingdom,
                        onFormationChange = { viewModel.changeFormation(it) },
                        onStrategyChange = { viewModel.changeStrategy(it) },
                        onFocusChange = { viewModel.changeFocus(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun WarriorItemCard(
    warrior: Warrior,
    isSelected: Boolean,
    isStarterList: Boolean,
    onClick: () -> Unit,
    onSell: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) ArcaneCyan else DarkCardBorder

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PositionBadge(position = warrior.position)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = warrior.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Idade: ${warrior.age} • Soldo: ${warrior.wageGold} Ouro • Gols: ${warrior.goals}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Overall Rating Badge
                Surface(
                    color = RoyalGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("FOR", fontSize = 9.sp, color = RoyalGold)
                        Text(
                            text = "${warrior.overall}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = RoyalGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Atributos secundários: ATQ, DEF, MAG + Vigor
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttributeChip(label = "ATQ", value = warrior.attack, color = CrimsonRuby)
                    AttributeChip(label = "DEF", value = warrior.defense, color = EmeraldGuard)
                    AttributeChip(label = "MAG", value = warrior.magic, color = AmethystPurple)
                }

                StaminaBar(
                    stamina = warrior.stamina,
                    modifier = Modifier.width(110.dp)
                )
            }

            if (!isStarterList && onSell != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onSell,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRuby),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRuby.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Dispensar (${(warrior.valueGold * 0.75f).toInt()} Ouro)", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AttributeChip(
    label: String,
    value: Int,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.width(3.dp))
            Text("$value", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun TacticsView(
    kingdom: Kingdom?,
    onFormationChange: (Formation) -> Unit,
    onStrategyChange: (Strategy) -> Unit,
    onFocusChange: (CombatFocus) -> Unit,
    modifier: Modifier = Modifier
) {
    if (kingdom == null) return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Formação Tática", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RoyalGold)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Formation.values().forEach { form ->
                    val isSelected = kingdom.formation == form
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) RoyalGold else DarkCardBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFormationChange(form) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(form.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text(
                                    "1 Guardião • ${form.vanguards} Vanguardas • ${form.tacticians} Táticos • ${form.strikers} Atiradores",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RoyalGold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Postura de Combate", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RoyalGold)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Strategy.values().forEach { strat ->
                    val isSelected = kingdom.strategy == strat
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) EmeraldGuard else DarkCardBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStrategyChange(strat) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(strat.label, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text(strat.description, fontSize = 11.sp, color = TextSecondary)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGuard)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Foco do Reino", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RoyalGold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CombatFocus.values().forEach { focus ->
                    val isSelected = kingdom.combatFocus == focus
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DarkSurfaceVariant else DarkSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) ArcaneCyan else DarkCardBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onFocusChange(focus) }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(focus.label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
