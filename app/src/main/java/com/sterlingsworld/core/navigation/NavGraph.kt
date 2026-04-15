package com.sterlingsworld.core.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sterlingsworld.feature.arcade.GrandArcadeIndoorScreen
import com.sterlingsworld.feature.creamery.CreameryScreen
import com.sterlingsworld.feature.doodle.DoodleScreen
import com.sterlingsworld.feature.error.TechnicalDifficultiesScreen
import com.sterlingsworld.feature.game.completion.CompletionScreen
import com.sterlingsworld.feature.game.shell.GameShellScreen
import com.sterlingsworld.feature.kidz.KidzGameshellScreen
import com.sterlingsworld.feature.kidz.KidzGamesScreen
import com.sterlingsworld.feature.kidz.StorybookLandScreen
import com.sterlingsworld.feature.kidzarcade.KidzArcadeMenuScreen
import com.sterlingsworld.feature.kidzcinema.KidzCinemaScreen
import com.sterlingsworld.feature.linebreaker.LinebreakerScreen
import com.sterlingsworld.feature.luckypaws.LuckyPawsScreen
import com.sterlingsworld.feature.lumistarquest.LumisStarQuestScreen
import com.sterlingsworld.feature.nostalgia.NostalgiaScreen
import com.sterlingsworld.feature.relaxationretreat.RelaxationRetreatScreen
import com.sterlingsworld.feature.settings.SettingsScreen
import com.sterlingsworld.feature.spoongauntlet.GauntletScreen
import com.sterlingsworld.feature.symptomstriker.SymptomStrikerScreen
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
                onNavigateToGrandArcade = {
                    navController.navigate(Screen.GrandArcadeIndoor.route)
                },
                onNavigateToKidzHub = {
                    navController.navigate(Screen.KidzHub.route)
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
                onComplete = { _ ->
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

        // ── Arcade sub-screens ────────────────────────────────────────────────
        composable(Screen.GrandArcadeIndoor.route) {
            GrandArcadeIndoorScreen(
                onGameSelected = { route -> navController.navigate(route) },
            )
        }

        composable(Screen.LuckyPaws.route) {
            LuckyPawsScreen()
        }

        composable(Screen.Gauntlet.route) {
            GauntletScreen()
        }

        composable(Screen.SymptomStriker.route) {
            SymptomStrikerScreen()
        }

        composable(Screen.Creamery.route) {
            CreameryScreen()
        }

        composable(Screen.RelaxationRetreat.route) {
            RelaxationRetreatScreen()
        }

        // ── Kidz sub-screens ─────────────────────────────────────────────────
        composable(Screen.KidzHub.route) {
            KidzGameshellScreen(
                onGamesLand = { navController.navigate(Screen.KidzGames.route) },
                onStorybookLand = { navController.navigate(Screen.StorybookLand.route) },
            )
        }

        composable(Screen.KidzGames.route) {
            KidzGamesScreen(
                onGameSelected = { route -> navController.navigate(route) },
            )
        }

        composable(Screen.KidzArcadeMenu.route) {
            val kidzGameRoutes = listOf(
                Screen.LumiStarQuest.route,
                Screen.Doodle.route,
                Screen.Linebreaker.route,
                Screen.Nostalgia.route,
            )
            KidzArcadeMenuScreen(
                onMenuItemClick = { index ->
                    kidzGameRoutes.getOrNull(index)?.let { navController.navigate(it) }
                },
            )
        }

        composable(Screen.StorybookLand.route) {
            StorybookLandScreen()
        }

        composable(Screen.KidzCinema.route) {
            KidzCinemaScreen(
                onPlayVideo = {
                    navController.navigate(Screen.VideoPlayer.withId("kids-video-01", "kidz"))
                },
            )
        }

        composable(Screen.Doodle.route) {
            DoodleScreen()
        }

        composable(Screen.Linebreaker.route) {
            LinebreakerScreen()
        }

        composable(Screen.LumiStarQuest.route) {
            LumisStarQuestScreen()
        }

        composable(Screen.Nostalgia.route) {
            NostalgiaScreen()
        }

        // ── Global ────────────────────────────────────────────────────────────
        composable(Screen.TechnicalDifficulties.route) {
            TechnicalDifficultiesScreen()
        }
    }
}
