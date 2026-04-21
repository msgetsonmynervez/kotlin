package com.example.spoonsandstairs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawImage
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlin.math.floor

/**
 * Top level composable that renders the entire game scene.  It draws
 * the scrolling background, heads up display (spoon count and score),
 * accepts tap gestures to move the player between lanes and renders
 * both the player and all active objects.  It also starts the
 * underlying [GameEngine] when the game is in play mode.
 *
 * @param viewModel The state holder controlling game variables.
 * @param useRug Whether to use the rug or road background.  Calling
 *   composables can switch this based on theme or other settings.
 */
@Composable
fun GameScreen(viewModel: GameViewModel, useRug: Boolean = true) {
    // Load all sprites once per composition.
    val assets = rememberGameAssets()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = maxWidth.value
        val screenHeightPx = maxHeight.value

        // Draw the background.  It fills the entire space.  Scroll
        // effects could be added by adjusting the y offset over time.
        val background = if (useRug) assets.backgroundRug else assets.backgroundRoad
        Image(
            bitmap = background,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Heads up display: spoons on the left and score on the right.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(viewModel.spoons) {
                    Image(
                        bitmap = assets.spoonIcon,
                        contentDescription = "Spoon",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                }
            }
            Text(
                text = "Score: ${viewModel.score}",
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
        }

        // Main play area captures taps to change lanes and draws objects.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val laneWidth = size.width / 3f
                        val tappedLane = floor(offset.x / laneWidth).toInt()
                        when {
                            tappedLane < viewModel.currentLane.index -> viewModel.moveLeft()
                            tappedLane > viewModel.currentLane.index -> viewModel.moveRight()
                            else -> Unit
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val laneWidth = size.width / 3f

                // Draw active objects.
                viewModel.activeObjects.forEach { obj ->
                    val sprite = assets.spriteFor(obj.type)
                    val x = laneWidth * obj.lane.index + laneWidth / 2f
                    val y = obj.yPosition
                    drawImage(
                        image = sprite,
                        topLeft = Offset(
                            x - sprite.width / 2f,
                            y - sprite.height / 2f
                        )
                    )
                }

                // Draw the player.  The player sits at a fixed vertical
                // position near the bottom of the screen.
                val playerSprite = assets.player
                val playerX = laneWidth * viewModel.currentLane.index + laneWidth / 2f
                val playerY = size.height * 0.9f
                drawImage(
                    image = playerSprite,
                    topLeft = Offset(
                        playerX - playerSprite.width / 2f,
                        playerY - playerSprite.height / 2f
                    )
                )
            }
        }

        // Start the engine only when playing.  The engine will cancel
        // itself automatically when the status changes.
        if (viewModel.gameStatus == GameStatus.PLAYING) {
            GameEngine(viewModel, playAreaHeight = screenHeightPx)
        }
    }
}