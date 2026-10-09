package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.components.MedievalBottomNav
import com.example.ui.components.TopKingdomBar
import com.example.ui.theme.*

@Composable
fun CastleScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentRound by viewModel.repository.currentRound.collectAsState()
    val currentSeason by viewModel.repository.currentSeason.collectAsState()
    val userKingdom = viewModel.repository.userKingdom

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
                currentScreen = AppScreen.CASTLE,
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
            Text(
                text = "🏰 Gestão da Fortaleza & Arena",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalGold
            )

            if (userKingdom != null) {
                // 1. COLISEU / ARENA
                CastleUpgradeCard(
                    icon = Icons.Default.Stadium,
                    title = "Coliseu da Batalha",
                    currentLevelDesc = "Capacidade: %,d lugares".format(userKingdom.coliseumCapacity).replace(',', '.'),
                    benefitDesc = "Gera renda com ingressos nas batalhas em casa (~%,d ouro/batalha)".format(
                        (userKingdom.coliseumCapacity * 0.7f * userKingdom.ticketPrice).toInt()
                    ).replace(',', '.'),
                    cost = 60000,
                    userGold = userKingdom.gold,
                    buttonLabel = "EXPANDIR (+4.000 LUGARES)",
                    onUpgrade = { viewModel.upgradeColiseum() },
                    testTag = "btn_upgrade_coliseum"
                )

                // 2. ENFERMARIA REAL
                CastleUpgradeCard(
                    icon = Icons.Default.LocalHospital,
                    title = "Enfermaria Real",
                    currentLevelDesc = "Nível atual: ${userKingdom.infirmaryLevel}/5",
                    benefitDesc = "Cura guerreiros feridos em combate com maior rapidez.",
                    cost = 45000,
                    userGold = userKingdom.gold,
                    buttonLabel = if (userKingdom.infirmaryLevel < 5) "MELHORAR ENFERMARIA" else "NÍVEL MÁXIMO",
                    isMax = userKingdom.infirmaryLevel >= 5,
                    onUpgrade = { viewModel.upgradeInfirmary() },
                    testTag = "btn_upgrade_infirmary"
                )

                // 3. CENTRO DE TREINAMENTO
                CastleUpgradeCard(
                    icon = Icons.Default.FitnessCenter,
                    title = "Campos de Treinamento",
                    currentLevelDesc = "Nível atual: ${userKingdom.trainingLevel}/5",
                    benefitDesc = "Acelera a recuperação de vigor e evolução de jovens soldados.",
                    cost = 50000,
                    userGold = userKingdom.gold,
                    buttonLabel = if (userKingdom.trainingLevel < 5) "APRIMORAR TREINAMENTO" else "NÍVEL MÁXIMO",
                    isMax = userKingdom.trainingLevel >= 5,
                    onUpgrade = { viewModel.upgradeTraining() },
                    testTag = "btn_upgrade_training"
                )

                // 4. BALANÇO FINANCEIRO
                val totalWages = userKingdom.warriors.sumOf { it.wageGold }
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Balanço Financeiro da Coroa", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        FinanceRow(label = "Tesouro Atual", value = "+ %,d Ouro".format(userKingdom.gold).replace(',', '.'), color = RoyalGold)
                        FinanceRow(label = "Folha de Pagamento Semanal", value = "- %,d Ouro".format(totalWages).replace(',', '.'), color = CrimsonRuby)
                        FinanceRow(label = "Tamanho do Exército", value = "${userKingdom.warriors.size} guerreiros", color = TextPrimary)
                        FinanceRow(label = "Prestígio do Reino", value = "${userKingdom.prestige} pts", color = LightGold)
                    }
                }
            }
        }
    }
}

@Composable
fun CastleUpgradeCard(
    icon: ImageVector,
    title: String,
    currentLevelDesc: String,
    benefitDesc: String,
    cost: Int,
    userGold: Int,
    buttonLabel: String,
    isMax: Boolean = false,
    onUpgrade: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val canAfford = userGold >= cost && !isMax

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
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Text(currentLevelDesc, fontSize = 12.sp, color = EmeraldGuard, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(benefitDesc, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isMax) {
                    Text("Custo: %,d Ouro".format(cost).replace(',', '.'), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalGold)
                } else {
                    Text("Aprimoramento Completo", fontSize = 12.sp, color = EmeraldGuard)
                }

                Button(
                    onClick = onUpgrade,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalGold,
                        contentColor = OnGold,
                        disabledContainerColor = DarkSurfaceVariant,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag(testTag)
                ) {
                    Text(buttonLabel, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun FinanceRow(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
