package com.myelin.game.android

import com.accessquest.AccessQuestGame
import com.accessracer.game.AccessRacerGame
import com.badlogic.gdx.ApplicationListener
import com.example.snailsjourney.MyGdxGame
import com.luckypaws.platform.NativePlatformInterface
import game.GameApp
import com.myelin.game.MyelinProtocolGame

object NativeGameRegistry {
    const val EXTRA_GAME_ID = "GAME_ID"
    const val GAME_ID_ACCESS_QUEST = "access-quest"
    const val GAME_ID_ACCESS_RACER = "access-racer"
    const val GAME_ID_SNAILS_JOURNEY = "snails-journey"
    const val GAME_ID_SPOON_GAUNTLET = "spoon-gauntlet"
    const val GAME_ID_WHEELIE_SPOON_RUSH = "wheelie-spoon-rush"

    val supportedGameIds: Set<String> = setOf(
        GAME_ID_ACCESS_QUEST,
        GAME_ID_ACCESS_RACER,
        GAME_ID_SNAILS_JOURNEY,
        GAME_ID_SPOON_GAUNTLET,
        GAME_ID_WHEELIE_SPOON_RUSH,
    )

    fun create(gameId: String?, platform: NativePlatformInterface): ApplicationListener = when (gameId) {
        GAME_ID_ACCESS_QUEST -> AccessQuestGame(platform)
        GAME_ID_ACCESS_RACER -> AccessRacerGame(platform)
        GAME_ID_SNAILS_JOURNEY -> MyGdxGame(platform)
        GAME_ID_SPOON_GAUNTLET,
        GAME_ID_WHEELIE_SPOON_RUSH -> GameApp(platform)
        else -> MyelinProtocolGame()
    }
}
