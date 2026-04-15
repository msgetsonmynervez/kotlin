package com.sterlingsworld.core.navigation

/** All navigation destinations as typed sealed class to avoid stringly-typed route errors. */
sealed class Screen(val route: String) {
    // Top-level destinations
    object Welcome    : Screen("welcome")
    object Park       : Screen("park")          // host for bottom tab scaffold
    object Settings   : Screen("settings")

    // Full-screen routes launched from within the park
    object GamePlayer : Screen("game_player/{gameId}") {
        fun withId(gameId: String) = "game_player/$gameId"
    }
    object VideoPlayer : Screen("video_player/{videoId}/{source}") {
        fun withId(videoId: String, source: String) = "video_player/$videoId/$source"
    }
    object Completion : Screen("completion/{gameId}") {
        fun withId(gameId: String) = "completion/$gameId"
    }

    // Park tab destinations (nested inside ParkScaffold)
    object Arcade : Screen("arcade")
    object Cinema : Screen("cinema")
    object Studio : Screen("studio")
    object Kidz   : Screen("kidz")
    object Map    : Screen("map")

    // Arcade sub-screens
    object GrandArcadeIndoor  : Screen("grand_arcade_indoor")
    object LuckyPaws          : Screen("lucky_paws")
    object Gauntlet           : Screen("gauntlet")
    object SymptomStriker     : Screen("symptom_striker")
    object Creamery           : Screen("creamery")
    object RelaxationRetreat  : Screen("relaxation_retreat")

    // Kidz sub-screens
    object KidzHub            : Screen("kidz_hub")
    object KidzGames          : Screen("kidz_games")
    object KidzCinema         : Screen("kidz_cinema")
    object StorybookLand      : Screen("storybook_land")
    object Doodle             : Screen("doodle")
    object Linebreaker        : Screen("linebreaker")
    object LumiStarQuest      : Screen("lumi_star_quest")
    object Nostalgia          : Screen("nostalgia")

    // Global
    object TechnicalDifficulties : Screen("technical_difficulties")
}
