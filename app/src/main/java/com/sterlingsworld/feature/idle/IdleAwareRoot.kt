package com.sterlingsworld.feature.idle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

private const val IDLE_TIMEOUT_MS = 12 * 60 * 1000L  // 12 minutes, matches RN source

/**
 * Wraps the app in a touch-intercepting root that:
 *  - resets the inactivity timer on any touch
 *  - shows IdleOverlay after IDLE_TIMEOUT_MS of inactivity
 *  - dismisses the overlay and resets the timer on overlay tap
 */
@Composable
fun IdleAwareRoot(content: @Composable () -> Unit) {
    var idleVisible by remember { mutableStateOf(false) }
    var lastTouchTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Countdown coroutine — re-launches whenever lastTouchTime changes
    LaunchedEffect(lastTouchTime) {
        delay(IDLE_TIMEOUT_MS)
        idleVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent(PointerEventPass.Initial)
                        lastTouchTime = System.currentTimeMillis()
                        if (idleVisible) idleVisible = false
                    }
                }
            },
    ) {
        content()
        if (idleVisible) {
            IdleOverlay(
                onResume = {
                    idleVisible = false
                    lastTouchTime = System.currentTimeMillis()
                },
            )
        }
    }
}
