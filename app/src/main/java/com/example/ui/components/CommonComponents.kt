package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Kingdom
import com.example.model.Position
import com.example.ui.AppScreen
import com.example.ui.theme.*

@Composable
fun KingdomBadge(
    kingdom: Kingdom,
    size: Int = 40,
    modifier: Modifier = Modifier
) {
    val primary = Color(kingdom.primaryColorHex)
    val secondary = Color(kingdom.secondaryColorHex)

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(primary)
            .border(2.dp, secondary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = kingdom.shortName.take(3),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.35f).sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PositionBadge(
    position: Position,
    modifier: Modifier = Modifier
) {
    val (bg, label) = when (position) {
        Position.GUARD -> Pair(PositionGuardColor, "GDR")
        Position.VANGUARD -> Pair(PositionVanguardColor, "VAN")
        Position.TACTICIAN -> Pair(PositionTacticianColor, "TAC")
        Position.STRIKER -> Pair(PositionStrikerColor, "ATQ")
    }

    Surface(
        color = bg.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, bg),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = bg,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StaminaBar(
    stamina: Int,
    modifier: Modifier = Modifier
) {
    val progress = (stamina / 100f).coerceIn(0f, 1f)
    val color = when {
        stamina >= 75 -> EmeraldGuard
        stamina >= 50 -> RoyalGold
        else -> CrimsonRuby
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Vigor", fontSize = 10.sp, color = TextMuted)
            Text("$stamina%", fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = DarkSurfaceVariant
        )
    }
}

@Composable
fun GoldDisplay(
    gold: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = "Ouro",
            tint = RoyalGold,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "%,d".format(gold).replace(',', '.'),
            color = RoyalGold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun TopKingdomBar(
    kingdom: Kingdom?,
    round: Int,
    season: Int,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        tonalElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (kingdom != null) {
                    KingdomBadge(kingdom = kingdom, size = 36)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = kingdom.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "Temp. $season • Rodada $round/14",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    Text(
                        text = "ReinoFoot",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = RoyalGold
                    )
                }
            }

            if (kingdom != null) {
                GoldDisplay(gold = kingdom.gold)
            }

            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_top_menu")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Principal",
                    tint = TextPrimary
                )
            }
        }
    }
}

@Composable
fun MedievalBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp,
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val items = listOf(
            Triple(AppScreen.DASHBOARD, Icons.Default.Home, "Painel"),
            Triple(AppScreen.SQUAD, Icons.Default.Shield, "Elenco"),
            Triple(AppScreen.STANDINGS, Icons.Default.Leaderboard, "Ligas"),
            Triple(AppScreen.MARKET, Icons.Default.Storefront, "Taverna"),
            Triple(AppScreen.CASTLE, Icons.Default.Castle, "Castelo")
        )

        items.forEach { (screen, icon, label) ->
            val selected = currentScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) RoyalGold else TextSecondary
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (selected) RoyalGold else TextSecondary,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = RoyalGold.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
            )
        }
    }
}
