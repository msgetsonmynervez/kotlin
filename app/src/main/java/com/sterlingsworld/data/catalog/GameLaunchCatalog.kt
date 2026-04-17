package com.sterlingsworld.data.catalog

import android.util.Log
import com.myelin.game.android.NativeGameRegistry

sealed interface GameLaunchSpec {
    data class Native(val gameId: String) : GameLaunchSpec
    data class WebView(val assetFolder: String) : GameLaunchSpec
    data object EmbeddedCompose : GameLaunchSpec
    data object RelaxationSuite : GameLaunchSpec
    data object Unsupported : GameLaunchSpec
}

object GameLaunchCatalog {
    private const val TAG = "GameLaunchCatalog"

    fun forGame(gameId: String): GameLaunchSpec = when (gameId) {
        "cognitive-creamery",
        "ghost",
        "lucky-paws",
        "symptom-striker",
            -> GameLaunchSpec.EmbeddedCompose

        "relaxation-retreat" -> GameLaunchSpec.RelaxationSuite

        NativeGameRegistry.GAME_ID_ACCESS_QUEST,
        NativeGameRegistry.GAME_ID_ACCESS_RACER,
        NativeGameRegistry.GAME_ID_SNAILS_JOURNEY,
        NativeGameRegistry.GAME_ID_SPOON_GAUNTLET,
            -> GameLaunchSpec.Native(gameId)

        "kidz-doodle-land" -> GameLaunchSpec.WebView("Kidz-doodle_land")
        "kidz-linebreaker" -> GameLaunchSpec.WebView("Kidz-linebreaker")
        "lumis-star-quest" -> GameLaunchSpec.WebView("Lumis_star_quest")
        "nostalgia" -> GameLaunchSpec.WebView("Nostalgia")
        "aol" -> GameLaunchSpec.WebView("AOL")
        else -> {
            Log.w(TAG, "Unsupported gameId: '$gameId'")
            GameLaunchSpec.Unsupported
        }
    }
}
