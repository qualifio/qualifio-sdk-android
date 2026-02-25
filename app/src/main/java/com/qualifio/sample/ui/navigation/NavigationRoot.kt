package com.qualifio.sample.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.qualifio.sample.ui.launch.LaunchScreen
import com.qualifio.sample.ui.screens.configuration.ConfigurationScreen
import com.qualifio.sample.ui.screens.home.HomeScreen
import com.qualifio.sample.ui.screens.secure_token_fields.SecureTokenFieldsScreen
import com.qualifio.sample.ui.screens.settings.SettingsScreen

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    viewModel: RootViewModel = hiltViewModel(),
) {
    val isConfigurationFinished by viewModel.isConfigurationFinished.collectAsState()

    val navigationState = rememberNavigationState(
        startRoute = Route.Home,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys,
    )
    val navigator = remember {
        Navigator(navigationState)
    }

    when (isConfigurationFinished) {
        null -> {
            LaunchScreen()
        }

        false -> {
            ConfigurationScreen()
        }

        true -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopBar(
                        currentRoute = navigationState.currentRoute,
                        onBackClick = { navigator.goBack() },
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        selectedKey = navigationState.topLevelRoute,
                        onSelectKey = { navigator.navigate(it) },
                    )
                },
            ) { innerPadding ->
                NavDisplay(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onBack = navigator::goBack,
                    entries = navigationState.toEntries(
                        entryProvider {
                            entry<Route.Home> {
                                HomeScreen()
                            }
                            entry<Route.Settings> {
                                SettingsScreen {
                                    navigator.navigate(Route.SecureTokenFields)
                                }
                            }
                            entry<Route.SecureTokenFields> {
                                SecureTokenFieldsScreen(innerPadding = innerPadding)
                            }
                        },
                    ),
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                    },
                    predictivePopTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                    },
                )
            }
        }
    }
}
