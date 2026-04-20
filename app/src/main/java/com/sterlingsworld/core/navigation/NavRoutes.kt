package com.sterlingsworld.core.navigation

import android.net.Uri

/** Typed route registry for top-level, nested, and parameterized destinations. */
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Settings : Screen("settings")

    object GamePlayer : Screen("game_player/{gameId}") {
        fun withId(gameId: String) = "game_player/${encodeArg(gameId)}"
    }

    object VideoPlayer : Screen("video_player/{videoId}/{source}") {
        fun withId(videoId: String, source: String) =
            "video_player/${encodeArg(videoId)}/${encodeArg(source)}"
    }

    object Completion : Screen("completion/{gameId}") {
        fun withId(gameId: String) = "completion/${encodeArg(gameId)}"
    }

    object Arcade : Screen("arcade")
    object Cinema : Screen("cinema")
    object CinemaMenu : Screen("cinema_menu")
    object Studio : Screen("studio")
    object Kidz : Screen("kidz")
    object Map : Screen("map")

    object GrandArcadeIndoor : Screen("grand_arcade_indoor")
    object LuckyPaws : Screen("lucky_paws")
    object Gauntlet : Screen("gauntlet")
    object SymptomStriker : Screen("symptom_striker")
    object Creamery : Screen("creamery")
    object RelaxationRetreat : Screen("relaxation_retreat")

    object KidzHub : Screen("kidz_hub")
    object KidzGames : Screen("kidz_games")
    object KidzArcadeMenu : Screen("kidz_arcade_menu")
    object KidzCinema : Screen("kidz_cinema/{videoId}") {
        fun withId(videoId: String) = "kidz_cinema/${encodeArg(videoId)}"
    }
    object StorybookLand : Screen("storybook_land")
    object Doodle : Screen("doodle")
    object Linebreaker : Screen("linebreaker")
    object LumiStarQuest : Screen("lumi_star_quest")
    object Nostalgia : Screen("nostalgia")
    object SnailsJourney : Screen("snails_journey")

    object Aol : Screen("aol")

    object StudioPlayer : Screen("studio_player")

    object TechnicalDifficulties : Screen("technical_difficulties")

    companion object {
        fun decodeArg(value: String): String = Uri.decode(value)

        private fun encodeArg(value: String): String = Uri.encode(value)
    }
}
