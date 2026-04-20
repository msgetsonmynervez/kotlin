package com.sterlingsworld.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sterlingsworld.core.ui.components.BathroomMapButton
import com.sterlingsworld.core.ui.theme.Accent

private data class AppTab(val label: String, val icon: ImageVector, val route: String)

private val appTabs = listOf(
    AppTab("Arcade", Icons.Filled.SportsEsports, Screen.Arcade.route),
    AppTab("Cinema", Icons.Filled.Movie, Screen.Cinema.route),
    AppTab("Studio", Icons.Filled.MusicNote, Screen.Studio.route),
    AppTab("Kidz", Icons.Filled.ChildCare, Screen.Kidz.route),
    AppTab("Map", Icons.Filled.Map, Screen.Map.route),
)

private val BottomBarContainer = Color(0xFF141414)
private val BottomBarSelected = Accent
private val BottomBarUnselected = Color(0xFFB8873A)
private val BottomBarTopBorder = Color(0x33F4B942)

private fun routeMatches(actualRoute: String?, pattern: String): Boolean {
    if (actualRoute == null) return false
    if (!pattern.contains("{")) return actualRoute == pattern
    val prefix = pattern.substringBefore("/{")
    return actualRoute == prefix || actualRoute.startsWith("$prefix/")
}

private fun sectionRouteFor(route: String?): String? = when {
    routeMatches(route, Screen.Arcade.route) ||
        routeMatches(route, Screen.GrandArcadeIndoor.route) ||
        routeMatches(route, Screen.LuckyPaws.route) ||
        routeMatches(route, Screen.Gauntlet.route) ||
        routeMatches(route, Screen.SymptomStriker.route) ||
        routeMatches(route, Screen.Creamery.route) ||
        routeMatches(route, Screen.RelaxationRetreat.route) ||
        routeMatches(route, Screen.Aol.route) -> Screen.Arcade.route

    routeMatches(route, Screen.Cinema.route) ||
        routeMatches(route, Screen.CinemaMenu.route) -> Screen.Cinema.route

    routeMatches(route, Screen.Studio.route) ||
        routeMatches(route, Screen.StudioPlayer.route) -> Screen.Studio.route

    routeMatches(route, Screen.Kidz.route) ||
        routeMatches(route, Screen.KidzHub.route) ||
        routeMatches(route, Screen.KidzArcadeMenu.route) ||
        routeMatches(route, Screen.StorybookLand.route) ||
        routeMatches(route, Screen.KidzCinema.route) ||
        routeMatches(route, Screen.Doodle.route) ||
        routeMatches(route, Screen.Linebreaker.route) ||
        routeMatches(route, Screen.LumiStarQuest.route) ||
        routeMatches(route, Screen.Nostalgia.route) -> Screen.Kidz.route

    routeMatches(route, Screen.Map.route) -> Screen.Map.route
    else -> null
}

private fun shouldShowScaffoldBathroomButton(route: String?): Boolean = when {
    routeMatches(route, Screen.Arcade.route) -> true
    routeMatches(route, Screen.Cinema.route) -> true
    routeMatches(route, Screen.Studio.route) -> true
    routeMatches(route, Screen.Map.route) -> true
    else -> false
}

fun shouldShowBottomNav(route: String?): Boolean =
    sectionRouteFor(route) != null || routeMatches(route, Screen.Settings.route)

@Composable
fun AppScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomNav = shouldShowBottomNav(currentRoute)
    val currentSectionRoute = sectionRouteFor(currentRoute)
    val showScaffoldBathroomButton = shouldShowScaffoldBathroomButton(currentRoute)

    val currentTab = appTabs.firstOrNull { it.route == currentSectionRoute }

    Scaffold(
        topBar = {
            if (currentTab != null) {
                Row(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentTab.label,
                        color = BottomBarSelected,
                        fontWeight = FontWeight.SemiBold,
                        modifier = androidx.compose.ui.Modifier.weight(1f),
                    )
                    if (showScaffoldBathroomButton) {
                        BathroomMapButton(
                            modifier = androidx.compose.ui.Modifier.widthIn(min = 152.dp),
                            label = "Bathroom Finder",
                        )
                        Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = BottomBarSelected,
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(containerColor = BottomBarContainer) {
                    appTabs.forEach { tab ->
                        val selected = currentSectionRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BottomBarSelected,
                                selectedTextColor = BottomBarSelected,
                                indicatorColor = BottomBarTopBorder,
                                unselectedIconColor = BottomBarUnselected,
                                unselectedTextColor = BottomBarUnselected,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}
