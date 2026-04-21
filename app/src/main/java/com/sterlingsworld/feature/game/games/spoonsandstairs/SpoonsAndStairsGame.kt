package com.sterlingsworld.feature.game.games.spoonsandstairs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.domain.model.GameResult
import kotlin.math.floor

@Composable
fun SpoonsAndStairsGame(
    vm: SpoonsAndStairsViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val assets = rememberSpoonsAndStairsAssets()
    val animatedLaneIndex by animateFloatAsState(
        targetValue = vm.currentLane.index.toFloat(),
        animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing),
        label = "playerLane",
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val playAreaHeightPx = constraints.maxHeight.toFloat()

        Image(
            bitmap = assets.backgroundRoad,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xAA0C0A08), Color(0x330C0A08), Color(0xCC0C0A08)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Spoons and Stairs", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Score ${vm.score}  |  Combo ${vm.comboStreak}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                OutlinedButton(onClick = { onDone(vm.buildResult()) }) {
                    Text("End Run")
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171310).copy(alpha = 0.92f)),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(vm.spoons) {
                                Image(
                                    bitmap = assets.spoonIcon,
                                    contentDescription = "Spoon",
                                    modifier = Modifier
                                        .size(22.dp)
                                        .padding(end = 4.dp),
                                )
                            }
                        }
                        Text(
                            "Dodges ${vm.hazardsDodged}  |  Pickups ${vm.pickupsCollected}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                        )
                    }
                    Text(
                        text = vm.statusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(vm.gameStatus) {
                        detectTapGestures { offset ->
                            if (vm.gameStatus != SpoonsAndStairsStatus.PLAYING) return@detectTapGestures
                            val laneWidth = size.width / 3f
                            val tappedLane = floor(offset.x / laneWidth).toInt().coerceIn(0, 2)
                            when {
                                tappedLane < vm.currentLane.index -> vm.moveLeft()
                                tappedLane > vm.currentLane.index -> vm.moveRight()
                            }
                        }
                    },
            ) {
                Image(
                    bitmap = assets.backgroundRug,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val laneWidth = size.width / 3f
                    drawRect(Color.Black.copy(alpha = 0.22f))

                    repeat(3) { laneIndex ->
                        val startX = laneWidth * laneIndex
                        val laneTint = if (laneIndex == vm.currentLane.index) {
                            Color(0x33FFD166)
                        } else {
                            Color(0x11000000)
                        }
                        drawRect(
                            color = laneTint,
                            topLeft = Offset(startX, 0f),
                            size = androidx.compose.ui.geometry.Size(laneWidth, size.height),
                        )
                    }

                    repeat(2) { dividerIndex ->
                        val x = laneWidth * (dividerIndex + 1)
                        drawLine(
                            color = Color.White.copy(alpha = 0.18f),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 6f,
                        )
                    }

                    val playerTop = size.height * 0.78f
                    drawRect(
                        color = Color(0x22FFD166),
                        topLeft = Offset(0f, playerTop),
                        size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.16f),
                    )

                    vm.activeObjects.forEach { obj ->
                        val sprite = assets.spriteFor(obj.type)
                        val laneStart = laneWidth * obj.lane.index
                        val spriteWidth = laneWidth * obj.widthFraction
                        val spriteHeight = obj.heightPx
                        val centerX = laneStart + laneWidth / 2f
                        drawImage(
                            image = sprite,
                            dstOffset = androidx.compose.ui.unit.IntOffset(
                                x = (centerX - spriteWidth / 2f).toInt(),
                                y = (obj.yPosition - spriteHeight / 2f).toInt(),
                            ),
                            dstSize = androidx.compose.ui.unit.IntSize(
                                width = spriteWidth.toInt(),
                                height = spriteHeight.toInt(),
                            ),
                        )
                    }

                    val playerX = laneWidth * animatedLaneIndex + laneWidth / 2f
                    val playerY = size.height * 0.86f
                    val playerWidth = laneWidth * 0.54f
                    val playerHeight = size.height * 0.14f
                    drawImage(
                        image = assets.player,
                        dstOffset = androidx.compose.ui.unit.IntOffset(
                            x = (playerX - playerWidth / 2f).toInt(),
                            y = (playerY - playerHeight / 2f).toInt(),
                        ),
                        dstSize = androidx.compose.ui.unit.IntSize(
                            width = playerWidth.toInt(),
                            height = playerHeight.toInt(),
                        ),
                    )
                }

                if (vm.gameStatus == SpoonsAndStairsStatus.COUNTDOWN) {
                    SpoonsAndStairsOverlay(
                        title = "Get Set",
                        body = "Tap any lane to move once the countdown ends.",
                        primaryLabel = "Wait",
                        onPrimary = {},
                        secondaryLabel = vm.countdownValue.toString(),
                        onSecondary = {},
                        primaryEnabled = false,
                        secondaryEnabled = false,
                    )
                }

                if (vm.gameStatus == SpoonsAndStairsStatus.START_MENU) {
                    SpoonsAndStairsOverlay(
                        title = "Spoons and Stairs",
                        body = "Dodge hazards, catch water and lightning, and keep your spoon meter alive long enough to build a real combo.",
                        primaryLabel = "Start Run",
                        onPrimary = vm::startGame,
                        secondaryLabel = "Exit",
                        onSecondary = { onDone(vm.buildResult()) },
                    )
                }

                if (vm.gameStatus == SpoonsAndStairsStatus.GAME_OVER) {
                    SpoonsAndStairsOverlay(
                        title = "Run Over",
                        body = "Score ${vm.score}. Dodges ${vm.hazardsDodged}. Pickups ${vm.pickupsCollected}. Best combo ${vm.bestCombo}.",
                        primaryLabel = "Play Again",
                        onPrimary = vm::startGame,
                        secondaryLabel = "Finish Run",
                        onSecondary = { onDone(vm.buildResult()) },
                    )
                }
            }
        }

        if (vm.gameStatus == SpoonsAndStairsStatus.COUNTDOWN || vm.gameStatus == SpoonsAndStairsStatus.PLAYING) {
            SpoonsAndStairsEngine(
                viewModel = vm,
                playAreaHeightPx = playAreaHeightPx,
            )
        }
    }
}

@Composable
private fun SpoonsAndStairsOverlay(
    title: String,
    body: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.48f)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .border(1.dp, Color(0x55FFD166), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B18).copy(alpha = 0.96f)),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onSecondary,
                        modifier = Modifier.weight(1f),
                        enabled = secondaryEnabled,
                    ) {
                        Text(secondaryLabel)
                    }
                    Button(
                        onClick = onPrimary,
                        modifier = Modifier.weight(1f),
                        enabled = primaryEnabled,
                    ) {
                        Text(primaryLabel)
                    }
                }
            }
        }
    }
}
