package com.sterlingsworld.feature.game

import androidx.compose.runtime.Composable
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.feature.game.games.cognitivecreamery.CognitiveCreameryGame
import com.sterlingsworld.feature.game.games.ghost.GhostGame
import com.sterlingsworld.feature.game.games.luckypaws.LuckyPawsGame
import com.sterlingsworld.feature.game.games.symptomstriker.SymptomStrikerGame

object EmbeddedGameRegistry {
    @Composable
    fun Render(gameId: String, onDone: (GameResult) -> Unit) {
        when (gameId) {
            "cognitive-creamery" -> CognitiveCreameryGame(onDone = onDone)
            "ghost" -> GhostGame(onDone = onDone)
            "lucky-paws" -> LuckyPawsGame(onDone = onDone)
            "symptom-striker" -> SymptomStrikerGame(onDone = onDone)
            else -> Unit
        }
    }
}
