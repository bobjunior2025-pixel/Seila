package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubApkScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Integração GitHub Actions (APK)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                        modifier = Modifier.testTag("btn_back_github_apk")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
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
            // Cartão de Destaque
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ArcaneCyan.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = ArcaneCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Android, contentDescription = null, tint = ArcaneCyan, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Workflow do GitHub Actions Configurado!",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                "Arquivo: .github/workflows/build-apk.yml",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = ArcaneCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "O workflow foi configurado para compilar o APK automaticamente sempre que você fizer um 'git push' para as branches main/master ou quando acionar manualmente via GitHub Web.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Passo a passo para baixar o APK no GitHub
            Text("Como gerar e baixar o APK:", fontWeight = FontWeight.Bold, color = RoyalGold, fontSize = 15.sp)

            StepCard(
                step = "1",
                title = "Envie para o GitHub",
                desc = "Faça o commit e envie seu repositório para o GitHub usando o comando git push."
            )

            StepCard(
                step = "2",
                title = "Acesse a aba 'Actions'",
                desc = "No seu repositório no GitHub pelo navegador, clique na aba 'Actions' no topo."
            )

            StepCard(
                step = "3",
                title = "Selecione o Workflow",
                desc = "Clique em 'Build Android APK'. Você pode aguardar o build automático ou clicar em 'Run workflow' para disparar manualmente."
            )

            StepCard(
                step = "4",
                title = "Baixe o APK nos Artefatos (Artifacts)",
                desc = "Quando a execução terminar com sucesso (ícone verde ✅), role até a seção 'Artifacts' no final da página da execução e baixe o zip com o APK (ex: ReinoFoot-debug-apk)."
            )

            StepCard(
                step = "5",
                title = "Instale no Celular Android",
                desc = "Transfira o arquivo .apk para o seu smartphone Android, toque nele e permita a instalação de fontes desconhecidas para jogar ReinoFoot direto no celular!"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = OnGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("VOLTAR AO MENU PRINCIPAL", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StepCard(step: String, title: String, desc: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = RoyalGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(step, fontWeight = FontWeight.Bold, color = RoyalGold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text(desc, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}
