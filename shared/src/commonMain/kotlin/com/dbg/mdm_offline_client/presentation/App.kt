package com.dbg.mdm_offline_client.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dbg.mdm_offline_client.domain.isJvmPlatform
import com.dbg.mdm_offline_client.presentation.i18n.Strings
import com.dbg.mdm_offline_client.presentation.navigation.AppRoute
import com.dbg.mdm_offline_client.presentation.navigation.appNavSavedStateConfiguration
import com.dbg.mdm_offline_client.presentation.ui.DesktopCommandBar
import com.dbg.mdm_offline_client.presentation.ui.HomeScreen
import com.dbg.mdm_offline_client.presentation.ui.NavigationPane
import com.dbg.mdm_offline_client.presentation.ui.TutorialScreen
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentCard
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentLayerDefault
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentOnAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentStroke
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
        val current = backStack.lastOrNull()
        val showChrome = if (isJvmPlatform()) {
            current != AppRoute.Tutorial
        } else {
            current != AppRoute.Tutorial || viewModel.tutorialCompleted
        }

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

        if (isJvmPlatform()) {
            DesktopShell(
                strings = state.strings,
                current = current,
                showChrome = showChrome,
                onSelect = { route ->
                    backStack.clear()
                    backStack.add(route)
                },
            ) {
                AppScreens(
                    viewModel = viewModel,
                    backStack = backStack,
                    showScreenHeader = !showChrome,
                )
            }
        } else {
            AndroidShell(
                strings = state.strings,
                current = current,
                showChrome = showChrome,
                onSelect = { route ->
                    backStack.clear()
                    backStack.add(route)
                },
            ) {
                AppScreens(viewModel = viewModel, backStack = backStack)
            }
        }
    }
}

@Composable
private fun DesktopShell(
    strings: Strings,
    current: NavKey?,
    showChrome: Boolean,
    onSelect: (AppRoute) -> Unit,
    content: @Composable () -> Unit,
) {
    if (!showChrome) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FluentLayerDefault),
        ) {
            content()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FluentLayerDefault),
    ) {
        DesktopCommandBar(strings = strings)
        HorizontalDivider(color = FluentStroke, thickness = 1.dp)
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            NavigationPane(
                strings = strings,
                current = current as? AppRoute,
                onSelect = onSelect,
            )
            VerticalDivider(color = FluentStroke, thickness = 1.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(FluentCard),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun AndroidShell(
    strings: Strings,
    current: NavKey?,
    showChrome: Boolean,
    onSelect: (AppRoute) -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        containerColor = FluentLayerDefault,
        bottomBar = {
            if (showChrome) {
                NavigationBar(
                    containerColor = FluentOnAccent
                ) {
                    NavigationBarItem(
                        selected = current == AppRoute.Home,
                        onClick = { onSelect(AppRoute.Home) },
                        icon = {},
                        label = { Text(strings.home) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = FluentAccent,
                        )
                    )

                    NavigationBarItem(
                        selected = current == AppRoute.Tutorial,
                        onClick = { onSelect(AppRoute.Tutorial) },
                        icon = { },
                        label = { Text(strings.tutorial) },
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
            content()
        }
    }
}

@Composable
private fun AppScreens(
    viewModel: MdmClientViewModel,
    backStack: NavBackStack<NavKey>,
    showScreenHeader: Boolean = true,
) {
    val state by viewModel.state.collectAsState()
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppRoute.Tutorial> {
                TutorialScreen(
                    strings = state.strings,
                    showHeader = showScreenHeader,
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
                    showHeader = showScreenHeader,
                    onToggleAgent = viewModel::toggleAgent,
                )
            }
        },
    )
}
