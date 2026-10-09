package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Kingdom
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.KingdomBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseKingdomScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val kingdoms by viewModel.repository.kingdoms.collectAsState()
    var selectedDivision by remember { mutableIntStateOf(1) }

    val filteredKingdoms = kingdoms.filter { it.division == selectedDivision }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Escolha seu Reino / Ordem",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                        modifier = Modifier.testTag("btn_back_choose_kingdom")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
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
            // Tabs 1ª Divisão vs 2ª Divisão
            TabRow(
                selectedTabIndex = selectedDivision - 1,
                containerColor = DarkSurfaceVariant,
                contentColor = RoyalGold,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(it[selectedDivision - 1]),
                        color = RoyalGold
                    )
                }
            ) {
                Tab(
                    selected = selectedDivision == 1,
                    onClick = { selectedDivision = 1 },
                    text = {
                        Text(
                            "1ª DIVISÃO (ELITE)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedDivision == 1) RoyalGold else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_div_1")
                )
                Tab(
                    selected = selectedDivision == 2,
                    onClick = { selectedDivision = 2 },
                    text = {
                        Text(
                            "2ª DIVISÃO (BARÕES)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedDivision == 2) RoyalGold else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_div_2")
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredKingdoms, key = { it.id }) { kingdom ->
                    KingdomSelectionCard(
                        kingdom = kingdom,
                        onSelect = {
                            viewModel.startNewGame(kingdom.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun KingdomSelectionCard(
    kingdom: Kingdom,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                KingdomBadge(kingdom = kingdom, size = 48)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kingdom.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Força Média: ${kingdom.squadOverallAverage} • Prestígio: ${kingdom.prestige}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detalhes de tesouro e arena
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Tesouro Real", fontSize = 11.sp, color = TextMuted)
                    Text("%,d Ouro".format(kingdom.gold).replace(',', '.'), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalGold)
                }
                Column {
                    Text("Capacidade Coliseu", fontSize = 11.sp, color = TextMuted)
                    Text("%,d lugares".format(kingdom.coliseumCapacity).replace(',', '.'), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text("Elenco", fontSize = 11.sp, color = TextMuted)
                    Text("${kingdom.warriors.size} Soldados", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGuard)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(kingdom.primaryColorHex),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_select_kingdom_${kingdom.id}")
            ) {
                Text(
                    text = "LIDERAR O ${kingdom.shortName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
