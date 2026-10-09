package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Position
import com.example.model.Warrior
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MarketScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentRound by viewModel.repository.currentRound.collectAsState()
    val currentSeason by viewModel.repository.currentSeason.collectAsState()
    val marketWarriors by viewModel.repository.marketWarriors.collectAsState()
    val userKingdom = viewModel.repository.userKingdom

    var selectedPosFilter by remember { mutableStateOf<Position?>(null) }

    val filteredWarriors = remember(marketWarriors, selectedPosFilter) {
        if (selectedPosFilter == null) marketWarriors else marketWarriors.filter { it.position == selectedPosFilter }
    }

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
                currentScreen = AppScreen.MARKET,
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
            // Header da Taverna com Tesouro
            Surface(
                color = DarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🍺 Taverna de Mercenários",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = RoyalGold
                        )
                        Text(
                            text = "Contrate guerreiros livres para reforçar o clã",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    if (userKingdom != null) {
                        GoldDisplay(gold = userKingdom.gold)
                    }
                }
            }

            // Filtros de Posição
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedPosFilter == null,
                    onClick = { selectedPosFilter = null },
                    label = { Text("Todos", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalGold,
                        selectedLabelColor = OnGold
                    ),
                    modifier = Modifier.testTag("filter_all")
                )
                Position.values().forEach { pos ->
                    FilterChip(
                        selected = selectedPosFilter == pos,
                        onClick = { selectedPosFilter = if (selectedPosFilter == pos) null else pos },
                        label = { Text(pos.code, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalGold,
                            selectedLabelColor = OnGold
                        ),
                        modifier = Modifier.testTag("filter_${pos.code.lowercase()}")
                    )
                }
            }

            // Lista de Mercenários
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredWarriors, key = { it.id }) { warrior ->
                    MarketWarriorCard(
                        warrior = warrior,
                        userGold = userKingdom?.gold ?: 0,
                        onBuy = { viewModel.buyWarrior(warrior) }
                    )
                }
            }
        }
    }
}

@Composable
fun MarketWarriorCard(
    warrior: Warrior,
    userGold: Int,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canAfford = userGold >= warrior.valueGold

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Idade: ${warrior.age} anos • Soldo semanal: ${warrior.wageGold} Ouro",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = RoyalGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold)
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

            Spacer(modifier = Modifier.height(10.dp))

            // Atributos de combate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttributeChip(label = "ATQ", value = warrior.attack, color = CrimsonRuby)
                    AttributeChip(label = "DEF", value = warrior.defense, color = EmeraldGuard)
                    AttributeChip(label = "MAG", value = warrior.magic, color = AmethystPurple)
                }
                Text("Vigor: ${warrior.stamina}%", fontSize = 11.sp, color = EmeraldGuard)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preço e Botão de Contratação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%,d Ouro".format(warrior.valueGold).replace(',', '.'),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = RoyalGold
                    )
                }

                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalGold,
                        contentColor = OnGold,
                        disabledContainerColor = DarkSurfaceVariant,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_buy_${warrior.id}")
                ) {
                    Text(
                        text = if (canAfford) "CONTRATAR" else "OURO INSUFICIENTE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
