package com.sterlingsworld.core.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sterlingsworld.feature.arcade.ArcadeScreen
import com.sterlingsworld.feature.cinema.CinemaScreen
import com.sterlingsworld.feature.game.completion.CompletionScreen
import com.sterlingsworld.feature.game.shell.GameShellScreen
import com.sterlingsworld.feature.kidz.KidzScreen
import com.sterlingsworld.feature.map.MapScreen
import com.sterlingsworld.feature.settings.SettingsScreen
import com.sterlingsworld.feature.studio.StudioScreen
import com.sterlingsworld.feature.video.VideoPlayerScreen
import com.sterlingsworld.feature.welcome.WelcomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetSterlingNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onEnterPark = {
                    navController.navigate(Screen.Park.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Park.route) {
            ParkScaffold(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToGame = { gameId ->
                    navController.navigate(Screen.GamePlayer.withId(gameId))
                },
                onNavigateToVideo = { videoId, source ->
                    navController.navigate(Screen.VideoPlayer.withId(videoId, source))
                },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.GamePlayer.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: return@composable
            GameShellScreen(
                gameId = gameId,
                onExit = { navController.popBackStack() },
                onComplete = { result ->
                    navController.navigate(Screen.Completion.withId(gameId)) {
                        popUpTo(Screen.GamePlayer.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Screen.VideoPlayer.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.StringType },
                navArgument("source") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable
            val source  = backStackEntry.arguments?.getString("source") ?: return@composable
            VideoPlayerScreen(
                videoId = videoId,
                source = source,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.Completion.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: return@composable
            CompletionScreen(
                gameId = gameId,
                onReturnToPark = {
                    navController.navigate(Screen.Park.route) {
                        popUpTo(Screen.Park.route) { inclusive = false }
                    }
                },
                onReplay = {
                    navController.navigate(Screen.GamePlayer.withId(gameId)) {
                        popUpTo(Screen.Completion.route) { inclusive = true }
                    }
                },
            )
        }
    }
}
