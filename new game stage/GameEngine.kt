package com.example.spoonsandstairs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.isActive
import kotlin.random.Random

/**
 * Implements the game loop.  This effect is started when the game
 * transitions into the playing state and runs continuously until the
 * composable leaves composition or the coroutine is cancelled.  It
 * advances all active objects, spawns new ones when appropriate,
 * detects collisions and updates the score.  Haptic feedback is
 * triggered for collisions with hazards and power ups.
 *
 * @param viewModel The view model owning all mutable state.
 * @param playAreaHeight The height of the play area in pixels.  Used to
 *   determine when objects leave the screen and where the player sits.
 */
@Composable
fun GameEngine(viewModel: GameViewModel, playAreaHeight: Float) {
    val haptic = LocalHapticFeedback.current

    /**
     * Only start the engine when the game is in the playing state.  If
     * [GameViewModel.gameStatus] changes (e.g. to GAME_OVER) the
     * LaunchedEffect will cancel and stop processing frames.
     */
    LaunchedEffect(viewModel.gameStatus) {
        if (viewModel.gameStatus != GameStatus.PLAYING) return@LaunchedEffect

        // Base scroll speed in pixels per frame.  This is multiplied
        // by the ViewModel's speedMultiplier to adjust difficulty over time.
        val baseSpeedPerFrame = 4f

        // Used to decide how far apart new objects should appear.  If the
        // lowest object on screen has moved past this threshold a new one
        // is spawned.
        val spawnThreshold = playAreaHeight / 3f

        while (isActive && viewModel.gameStatus == GameStatus.PLAYING) {
            // Advance all active objects.  Filter out those that have
            // completely scrolled off the bottom of the screen.
            val newObjects = mutableListOf<GameObject>()
            for (obj in viewModel.activeObjects) {
                obj.yPosition += baseSpeedPerFrame * viewModel.speedMultiplier
                if (obj.yPosition < playAreaHeight + 200f) {
                    // Keep objects until they're far enough below the bottom
                    newObjects.add(obj)
                }
            }
            viewModel.activeObjects = newObjects

            // Determine if it's time to spawn a new object.  If there are no
            // objects we should spawn one immediately.  Otherwise look at
            // the object that is closest to the top of the screen (smallest
            // y) and spawn when it has travelled beyond the threshold.
            val shouldSpawn = viewModel.activeObjects.isEmpty() ||
                    (viewModel.activeObjects.minOf { it.yPosition } > spawnThreshold)
            if (shouldSpawn) {
                // Weighted random selection: 70% chance to spawn a hazard, 30% a power up.
                val spawnHazard = Random.nextFloat() < 0.7f
                val type = if (spawnHazard) {
                    listOf(
                        GameObjectType.TOY,
                        GameObjectType.LAUNDRY,
                        GameObjectType.STAIRS
                    ).random()
                } else {
                    listOf(
                        GameObjectType.WATER,
                        GameObjectType.LIGHTNING
                    ).random()
                }
                val lane = Lane.values().random()
                val newObject = GameObject(type = type, lane = lane, yPosition = 0f)
                viewModel.addGameObject(newObject)
            }

            // Collision detection.  The player occupies a fixed vertical band
            // near the bottom of the play area.  If any object intersects
            // this band and shares the same lane as the player, resolve
            // accordingly.
            val playerLane = viewModel.currentLane
            val playerTop = playAreaHeight * 0.8f
            val playerBottom = playAreaHeight * 0.9f
            val collisions = viewModel.activeObjects.filter { obj ->
                obj.lane == playerLane && obj.yPosition in playerTop..playerBottom
            }
            for (obj in collisions) {
                when (obj.type) {
                    GameObjectType.TOY,
                    GameObjectType.LAUNDRY,
                    GameObjectType.STAIRS -> {
                        // Hazard: lose a spoon and trigger heavy haptic.
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.decrementSpoons()
                    }
                    GameObjectType.WATER,
                    GameObjectType.LIGHTNING -> {
                        // Power up: gain a spoon and trigger a light tick.
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.incrementSpoons()
                    }
                }
                viewModel.removeGameObject(obj)
            }

            // Increase score each frame the player survives.  This simple
            // metric can be expanded later (e.g. based on distance).
            viewModel.incrementScore()

            // Suspend until the next frame.  Without this delay the loop
            // would run as fast as possible and overwhelm the UI thread.
            // Compose injects an appropriate delay on its own.
            kotlinx.coroutines.yield()
        }
    }
}