package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val snackMessage by viewModel.snackMessage.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                // Mostrar mensagens do ViewModel no Snackbar
                LaunchedEffect(snackMessage) {
                    snackMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearMessage()
                    }
                }

                // Gerenciamento de BackHandler para navegação customizada
                BackHandler(enabled = currentScreen != AppScreen.MAIN_MENU) {
                    when (currentScreen) {
                        AppScreen.MAIN_MENU -> {
                            // Deixa sair
                        }
                        AppScreen.CHOOSE_KINGDOM, AppScreen.GITHUB_APK_INFO -> {
                            viewModel.navigateTo(AppScreen.MAIN_MENU)
                        }
                        AppScreen.LIVE_MATCH -> {
                            // Não sai acidentalmente durante a partida; se desejar pode pausar
                            viewModel.toggleMatchPause()
                        }
                        AppScreen.ROUND_SUMMARY -> {
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        }
                        else -> {
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        }
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when (currentScreen) {
                        AppScreen.MAIN_MENU -> {
                            MainMenuScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.CHOOSE_KINGDOM -> {
                            ChooseKingdomScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.DASHBOARD -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.SQUAD -> {
                            SquadAndTacticsScreen(
                                viewModel = viewModel,
                                initialTab = 0,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.TACTICS -> {
                            SquadAndTacticsScreen(
                                viewModel = viewModel,
                                initialTab = 2,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.STANDINGS -> {
                            StandingsScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.MARKET -> {
                            MarketScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.CASTLE -> {
                            CastleScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.LIVE_MATCH -> {
                            LiveMatchScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.ROUND_SUMMARY -> {
                            RoundSummaryScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.GITHUB_APK_INFO -> {
                            GitHubApkScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
