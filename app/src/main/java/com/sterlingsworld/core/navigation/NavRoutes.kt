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
}
