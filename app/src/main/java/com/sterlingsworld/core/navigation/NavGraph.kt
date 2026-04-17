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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.myelin.game.android.MyelinProtocolActivity
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.data.catalog.GameLaunchCatalog
import com.sterlingsworld.data.catalog.GameLaunchSpec
import com.sterlingsworld.domain.model.GameDefinition
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.feature.accessquest.AccessQuestScreen
import com.sterlingsworld.feature.aol.AolScreen
import com.sterlingsworld.feature.powerglide.PowerGlideGrandPrixScreen
import com.sterlingsworld.feature.snailsjourney.SnailsJourneyScreen
import com.sterlingsworld.feature.wheeliespoonrush.WheelieSpoonRushScreen
import com.sterlingsworld.feature.arcade.GrandArcadeIndoorScreen
import com.sterlingsworld.feature.game.games.webview.WebViewGame
import com.sterlingsworld.feature.creamery.CreameryScreen
import com.sterlingsworld.feature.doodle.DoodleScreen
import com.sterlingsworld.feature.error.TechnicalDifficultiesScreen
import com.sterlingsworld.feature.game.completion.CompletionScreen
import com.sterlingsworld.feature.game.EmbeddedGameRegistry
import com.sterlingsworld.feature.game.shell.GameShellScreen
import com.sterlingsworld.feature.kidz.KidzGamesScreen
import com.sterlingsworld.feature.kidz.KidzGameshellScreen
import com.sterlingsworld.feature.kidz.StorybookLandScreen
import com.sterlingsworld.feature.kidzarcade.KidzArcadeMenuScreen
import com.sterlingsworld.feature.kidzcinema.KidzCinemaScreen
import com.sterlingsworld.feature.linebreaker.LinebreakerScreen
import com.sterlingsworld.feature.luckypaws.LuckyPawsScreen
import com.sterlingsworld.feature.lumistarquest.LumisStarQuestScreen
import com.sterlingsworld.feature.nostalgia.NostalgiaScreen
import com.sterlingsworld.feature.game.suites.relaxation.RelaxationSuiteHost
import com.sterlingsworld.feature.relaxationretreat.RelaxationRetreatScreen
import com.sterlingsworld.feature.settings.SettingsScreen
import com.sterlingsworld.feature.spoongauntlet.GauntletScreen
import com.sterlingsworld.feature.studio.StudioScreen
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
                onStartMyelinProtocol = { context ->
                    context.startActivity(MyelinProtocolActivity.intent(context))
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
                onNavigateToStudio = {
                    navController.navigate(Screen.StudioPlayer.route)
                },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
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
                when (val launchSpec = GameLaunchCatalog.forGame(gameId)) {
                    is GameLaunchSpec.Native -> {
                        { _ ->
                            NativeGameLauncher(
                                gameId = launchSpec.gameId,
                                onLaunched = { navController.popBackStack() },
                            )
                        }
                    }
                    is GameLaunchSpec.EmbeddedCompose -> { onComplete ->
                        EmbeddedGameRegistry.Render(gameId = gameId, onDone = onComplete)
                    }
                    is GameLaunchSpec.WebView -> { onComplete ->
                        WebViewGame(assetFolder = launchSpec.assetFolder, onDone = onComplete)
                    }
                    GameLaunchSpec.RelaxationSuite -> { _ -> RelaxationSuiteHost() }
                    GameLaunchSpec.Unsupported -> { _ -> InertGameContent(game = game) }
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
                        launchSingleTop = true
                    }
                },
                onReplay = {
                    navController.navigate(Screen.GamePlayer.withId(gameId)) {
                        popUpTo(Screen.Completion.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.GrandArcadeIndoor.route) {
            GrandArcadeIndoorScreen(
                onGameSelected = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.LuckyPaws.route) {
            LuckyPawsScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("lucky-paws")) })
        }

        composable(Screen.Gauntlet.route) {
            GauntletScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("spoon-gauntlet")) })
        }

        composable(Screen.SymptomStriker.route) {
            SymptomStrikerScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("symptom-striker")) })
        }

        composable(Screen.Creamery.route) {
            CreameryScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("cognitive-creamery")) })
        }

        composable(Screen.RelaxationRetreat.route) {
            RelaxationRetreatScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("relaxation-retreat")) })
        }

        composable(Screen.AccessQuest.route) {
            AccessQuestScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("access-quest")) })
        }

        composable(Screen.PowerGlideGrandPrix.route) {
            PowerGlideGrandPrixScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("access-racer")) })
        }

        composable(Screen.SnailsJourney.route) {
            SnailsJourneyScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("snails-journey")) })
        }

        composable(Screen.WheelieSpoonRush.route) {
            WheelieSpoonRushScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("wheelie-spoon-rush")) })
        }

        composable(Screen.KidzHub.route) {
            KidzGameshellScreen(
                onGamesLand = { navController.navigate(Screen.KidzArcadeMenu.route) },
                onStorybookLand = { navController.navigate(Screen.StorybookLand.route) },
            )
        }

        composable(Screen.KidzGames.route) {
            KidzGamesScreen(onGameSelected = { route -> navController.navigate(route) })
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
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.StorybookLand.route) {
            StorybookLandScreen(
                onVideoSelected = { videoId ->
                    navController.navigate(Screen.KidzCinema.withId(videoId))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.KidzCinema.route,
            arguments = listOf(navArgument("videoId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments
                ?.getString("videoId")
                ?.let(Screen::decodeArg)
                ?: "kids-video-01"

            KidzCinemaScreen(
                videoId = videoId,
                onPlayVideo = {
                    navController.navigate(Screen.VideoPlayer.withId(videoId, "kidz"))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Doodle.route) {
            DoodleScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("kidz-doodle-land")) })
        }

        composable(Screen.Linebreaker.route) {
            LinebreakerScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("kidz-linebreaker")) })
        }

        composable(Screen.LumiStarQuest.route) {
            LumisStarQuestScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("lumis-star-quest")) })
        }

        composable(Screen.Nostalgia.route) {
            NostalgiaScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("nostalgia")) })
        }

        composable(Screen.Aol.route) {
            AolScreen(onPlay = { navController.navigate(Screen.GamePlayer.withId("aol")) })
        }

        composable(Screen.StudioPlayer.route) {
            StudioScreen()
        }

        composable(Screen.TechnicalDifficulties.route) {
            TechnicalDifficultiesScreen()
        }

    }
}

@Composable
private fun NativeGameLauncher(
    gameId: String,
    onLaunched: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(gameId, context) {
        context.startActivity(MyelinProtocolActivity.intent(context, gameId))
        onLaunched()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Launching game...",
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
        )
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
