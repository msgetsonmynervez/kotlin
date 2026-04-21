package com.sterlingsworld.feature.game.games.spoonsandstairs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

@Composable
fun SpoonsAndStairsEngine(
    viewModel: SpoonsAndStairsViewModel,
    playAreaHeightPx: Float,
) {
    LaunchedEffect(viewModel.gameStatus, playAreaHeightPx) {
        if (viewModel.gameStatus !in setOf(SpoonsAndStairsStatus.COUNTDOWN, SpoonsAndStairsStatus.PLAYING) || playAreaHeightPx <= 0f) {
            return@LaunchedEffect
        }
        var lastFrameNanos = 0L

        while (isActive && viewModel.gameStatus in setOf(SpoonsAndStairsStatus.COUNTDOWN, SpoonsAndStairsStatus.PLAYING)) {
            val frameNanos = withFrameNanos { it }
            if (lastFrameNanos == 0L) {
                lastFrameNanos = frameNanos
                continue
            }
            val deltaSeconds = ((frameNanos - lastFrameNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
            lastFrameNanos = frameNanos
            viewModel.tick(deltaSeconds = deltaSeconds, playAreaHeightPx = playAreaHeightPx)
        }
    }
}
