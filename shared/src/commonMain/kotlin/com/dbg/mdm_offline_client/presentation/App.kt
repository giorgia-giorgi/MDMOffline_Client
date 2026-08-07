package com.dbg.mdm_offline_client.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dbg.mdm_offline_client.presentation.navigation.AppRoute
import com.dbg.mdm_offline_client.presentation.navigation.appNavSavedStateConfiguration
import com.dbg.mdm_offline_client.presentation.ui.HomeScreen
import com.dbg.mdm_offline_client.presentation.ui.TutorialScreen
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentLayerDefault
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentOnAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.MdmOfflineTheme
import com.dbg.mdm_offline_client.presentation.viewmodel.MdmClientViewModel

@Composable
@Preview
fun App(viewModel: MdmClientViewModel = remember { MdmClientViewModel() }) {
    MdmOfflineTheme {
        val state by viewModel.state.collectAsState()
        val lifecycleOwner = LocalLifecycleOwner.current
        val startRoute = if (viewModel.tutorialCompleted) AppRoute.Home else AppRoute.Tutorial
        val backStack = rememberNavBackStack(appNavSavedStateConfiguration, startRoute)

        DisposableEffect(lifecycleOwner, viewModel, backStack) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME &&
                    backStack.lastOrNull() == AppRoute.Home
                ) {
                    viewModel.onAppForeground()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        Scaffold(
            containerColor = FluentLayerDefault,
            bottomBar = {
                if (backStack.lastOrNull() != AppRoute.Tutorial || viewModel.tutorialCompleted) {
                    NavigationBar(
                        containerColor = FluentOnAccent
                    ) {
                        NavigationBarItem(
                            selected = backStack.lastOrNull() == AppRoute.Home,
                            onClick = {
                                backStack.clear()
                                backStack.add(AppRoute.Home)
                            },
                            icon = {},
                            label = { Text(state.strings.home) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = FluentAccent,
                            )
                        )

                        NavigationBarItem(
                            selected = backStack.lastOrNull() == AppRoute.Tutorial,
                            onClick = {
                                backStack.clear()
                                backStack.add(AppRoute.Tutorial)
                            },
                            icon = { },
                            label = { Text("Tutorial") },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = FluentAccent
                            )
                        )
                    }
                }
            }
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(color = FluentLayerDefault),
            ) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<AppRoute.Tutorial> {
                            TutorialScreen(
                                strings = state.strings,
                                onFinished = {
                                    viewModel.completeTutorial()
                                    backStack.clear()
                                    backStack.add(AppRoute.Home)
                                },
                            )
                        }
                        entry<AppRoute.Home> {
                            HomeScreen(
                                state = state,
                                onToggleAgent = viewModel::toggleAgent,
                            )
                        }
                    },
                )
            }
        }
    }
}
