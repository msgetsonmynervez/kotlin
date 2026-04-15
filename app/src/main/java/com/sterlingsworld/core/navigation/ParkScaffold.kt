package com.sterlingsworld.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.feature.arcade.ArcadeScreen
import com.sterlingsworld.feature.cinema.CinemaScreen
import com.sterlingsworld.feature.kidz.KidzScreen
import com.sterlingsworld.feature.map.MapScreen
import com.sterlingsworld.feature.studio.MusicLandScreen

private data class ParkTab(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

private val parkTabs = listOf(
    ParkTab("Arcade", Icons.Filled.SportsEsports, Screen.Arcade.route),
    ParkTab("Cinema", Icons.Filled.Movie, Screen.Cinema.route),
    ParkTab("Studio", Icons.Filled.MusicNote, Screen.Studio.route),
    ParkTab("Kidz",   Icons.Filled.ChildCare, Screen.Kidz.route),
    ParkTab("Map",    Icons.Filled.Map, Screen.Map.route),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkScaffold(
    onNavigateToSettings: () -> Unit,
    onNavigateToGame: (gameId: String) -> Unit,
    onNavigateToVideo: (videoId: String, source: String) -> Unit,
    onNavigateToGrandArcade: () -> Unit = {},
    onNavigateToKidzHub: () -> Unit = {},
    onNavigateToStudio: () -> Unit = {},
) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentTab = parkTabs.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentTab?.label ?: "Sterling Park") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = Primary,
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Primary,
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Background) {
                parkTabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            tabNavController.navigate(tab.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = Screen.Arcade.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Arcade.route) {
                ArcadeScreen(onEnterArcade = onNavigateToGrandArcade)
            }
            composable(Screen.Cinema.route) {
                CinemaScreen(
                    onWatchVideo = { videoId ->
                        onNavigateToVideo(videoId, "cinema")
                    },
                )
            }
            composable(Screen.Studio.route) {
                MusicLandScreen(onListen = onNavigateToStudio)
            }
            composable(Screen.Kidz.route) {
                KidzScreen(
                    onGameSelected = onNavigateToGame,
                    onVideoSelected = { videoId ->
                        onNavigateToVideo(videoId, "kidz")
                    },
                    onEnterKidz = onNavigateToKidzHub,
                )
            }
            composable(Screen.Map.route) {
                MapScreen(onNavigateToZone = { zone ->
                    tabNavController.navigate(zone) {
                        popUpTo(tabNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
        }
    }
}
