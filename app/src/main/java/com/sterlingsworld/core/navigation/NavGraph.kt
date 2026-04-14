package com.sterlingsworld.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.domain.model.GameDefinition
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.feature.arcade.ArcadeScreen
import com.sterlingsworld.feature.cinema.CinemaScreen
import com.sterlingsworld.feature.game.completion.CompletionScreen
import com.sterlingsworld.feature.game.games.cognitivecreamery.CognitiveCreameryGame
import com.sterlingsworld.feature.game.games.ghost.GhostGame
import com.sterlingsworld.feature.game.games.luckypaws.LuckyPawsGame
import com.sterlingsworld.feature.game.games.symptomstriker.SymptomStrikerGame
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
            val gameId = backStackEntry.arguments
                ?.getString("gameId")
                ?.let(Screen::decodeArg)
                ?: return@composable
            val game = GameCatalog.byId(gameId)

            val gameContent: @Composable (onComplete: (GameResult) -> Unit) -> Unit =
                when (game?.id) {
                    "cognitive-creamery" -> { onComplete -> CognitiveCreameryGame(onDone = onComplete) }
                    "ghost" -> { onComplete -> GhostGame(onDone = onComplete) }
                    "lucky-paws" -> { onComplete -> LuckyPawsGame(onDone = onComplete) }
                    "symptom-striker" -> { onComplete -> SymptomStrikerGame(onDone = onComplete) }
                    else -> { _ -> InertGameContent(game = game) }
                }

            GameShellScreen(
                gameId = gameId,
                onExit = { navController.popBackStack() },
                onRestart = {
                    navController.navigate(Screen.GamePlayer.withId(gameId)) {
                        popUpTo(Screen.GamePlayer.route) { inclusive = true }
                    }
                },
                onComplete = { _ ->
                    navController.navigate(Screen.Completion.withId(gameId)) {
                        popUpTo(Screen.GamePlayer.route) { inclusive = true }
                    }
                },
                gameContent = gameContent,
            )
        }

        composable(
            route = Screen.VideoPlayer.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.StringType },
                navArgument("source") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments
                ?.getString("videoId")
                ?.let(Screen::decodeArg)
                ?: return@composable
            val source = backStackEntry.arguments
                ?.getString("source")
                ?.let(Screen::decodeArg)
                ?: return@composable
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
            val gameId = backStackEntry.arguments
                ?.getString("gameId")
                ?.let(Screen::decodeArg)
                ?: return@composable
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

@Composable
private fun InertGameContent(game: GameDefinition?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = game?.title ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
            Text(
                text = game?.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Not part of the current playable build",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
            )
        }
    }
}
