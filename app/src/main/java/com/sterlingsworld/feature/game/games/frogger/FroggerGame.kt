package com.sterlingsworld.feature.game.games.frogger

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.feature.game.games.frogger.model.Direction
import com.sterlingsworld.feature.game.games.frogger.model.GameStatus
import com.sterlingsworld.feature.game.games.frogger.model.RowState
import com.sterlingsworld.feature.game.games.frogger.model.RowType

private val FroggerRoad = Brush.verticalGradient(listOf(Color(0xFF191C23), Color(0xFF2D3340)))
private val FroggerRiver = Brush.verticalGradient(listOf(Color(0xFF0D3B66), Color(0xFF1B6CA8)))
private val FroggerSafe = Brush.verticalGradient(listOf(Color(0xFF355E3B), Color(0xFF27492E)))
private val FroggerHome = Brush.verticalGradient(listOf(Color(0xFF183D25), Color(0xFF0D2617)))

@Composable
fun FroggerGame(
    vm: FroggerViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val gameState by vm.state.collectAsStateWithLifecycle()
    val animatedFrogX by animateFloatAsState(
        targetValue = gameState.frogX,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "frogX",
    )
    val animatedFrogRow by animateFloatAsState(
        targetValue = gameState.frogRow.toFloat(),
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "frogRow",
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Busy Streets", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Score ${gameState.score}  |  Crossings ${gameState.crossings}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            OutlinedButton(onClick = { onDone(vm.buildResult()) }) {
                Text("End Run")
            }
        }

        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131A).copy(alpha = 0.9f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Level ${gameState.currentLevelIndex + 1}", color = Color.White)
                    Text("Lives ${gameState.lives}", color = Color(0xFFFFD166))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        gameState.homes.forEach { occupied ->
                            Box(
                                modifier = Modifier
                                    .size(width = 28.dp, height = 14.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (occupied) Color(0xFFFFD166) else Color(0xFF3E4554))
                                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(5.dp)),
                            )
                        }
                    }
                    Text(
                        gameState.statusMessage,
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            val rowHeight = maxHeight / gameState.rows.size
            val frogWidthDp = maxWidth * vm.frogWidthNormalized

            Column(modifier = Modifier.fillMaxSize()) {
                gameState.rows.forEach { row ->
                    FroggerRow(row = row, rowHeight = rowHeight)
                }
            }

            gameState.rows.forEachIndexed { index, row ->
                val yOffset = rowHeight * index
                row.objects.forEachIndexed { objectIndex, obj ->
                    FroggerObject(
                        row = row,
                        objectIndex = objectIndex,
                        modifier = Modifier
                            .offset(x = maxWidth * obj.x, y = yOffset + rowHeight * 0.12f)
                            .width(maxWidth * obj.width)
                            .height(rowHeight * 0.76f),
                    )
                }
            }

            FroggerFrog(
                modifier = Modifier
                    .offset(
                        x = maxWidth * animatedFrogX,
                        y = rowHeight * animatedFrogRow + rowHeight * 0.1f,
                    )
                    .width(frogWidthDp)
                    .height(rowHeight * 0.8f),
            )

            when (gameState.status) {
                GameStatus.START_MENU -> FroggerOverlay(
                    title = "Busy Streets",
                    body = gameState.statusMessage,
                    primaryLabel = "Start Crossing",
                    onPrimary = vm::startGame,
                    secondaryLabel = "Exit",
                    onSecondary = { onDone(vm.buildResult()) },
                )

                GameStatus.LIFE_LOST -> FroggerOverlay(
                    title = "Life Lost",
                    body = gameState.statusMessage,
                    primaryLabel = "Hop Back In",
                    onPrimary = vm::resumeAfterLifeLost,
                    secondaryLabel = "Finish Run",
                    onSecondary = { onDone(vm.buildResult()) },
                )

                GameStatus.LEVEL_COMPLETE -> FroggerOverlay(
                    title = "Crossing Clear",
                    body = gameState.statusMessage,
                    primaryLabel = "Next Level",
                    onPrimary = vm::nextLevel,
                    secondaryLabel = "Finish Run",
                    onSecondary = { onDone(vm.buildResult()) },
                )

                GameStatus.GAME_OVER -> FroggerOverlay(
                    title = "Traffic Won",
                    body = gameState.statusMessage,
                    primaryLabel = "Restart Run",
                    onPrimary = vm::restartRun,
                    secondaryLabel = "Finish Run",
                    onSecondary = { onDone(vm.buildResult()) },
                )

                GameStatus.GAME_WIN -> FroggerOverlay(
                    title = "Route Complete",
                    body = gameState.statusMessage,
                    primaryLabel = "Finish Run",
                    onPrimary = { onDone(vm.buildResult()) },
                    secondaryLabel = "Play Again",
                    onSecondary = vm::restartRun,
                )

                GameStatus.PLAYING -> Unit
            }
        }

        if (gameState.status == GameStatus.PLAYING) {
            FroggerControls(onMove = vm::moveFrog)
        }
    }
}

@Composable
private fun FroggerRow(
    row: RowState,
    rowHeight: androidx.compose.ui.unit.Dp,
) {
    val brush = when (row.type) {
        RowType.ROAD -> FroggerRoad
        RowType.RIVER -> FroggerRiver
        RowType.SAFE, RowType.START -> FroggerSafe
        RowType.HOME -> FroggerHome
    }
    val accent = when (row.type) {
        RowType.ROAD -> Color.White.copy(alpha = 0.16f)
        RowType.RIVER -> Color(0xFF7FDBFF).copy(alpha = 0.16f)
        RowType.SAFE, RowType.START -> Color(0xFF9BE564).copy(alpha = 0.12f)
        RowType.HOME -> Color(0xFFFFD166).copy(alpha = 0.16f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(brush),
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (index * 78).dp)
                    .width(if (row.type == RowType.ROAD) 32.dp else 20.dp)
                    .height(4.dp)
                    .background(accent, RoundedCornerShape(8.dp)),
            )
        }
    }
}

@Composable
private fun FroggerObject(
    row: RowState,
    objectIndex: Int,
    modifier: Modifier,
) {
    val spriteRes = when (row.type) {
        RowType.ROAD -> if (row.goingLeft) R.drawable.frogger_car_left else R.drawable.frogger_car_right
        RowType.RIVER -> R.drawable.frogger_raft
        else -> null
    }
    val fallbackBackground = when (row.type) {
        RowType.ROAD -> Brush.horizontalGradient(listOf(Color(0xFFFF7B54), Color(0xFFE63946)))
        RowType.RIVER -> Brush.horizontalGradient(listOf(Color(0xFF9B6B43), Color(0xFF6B4226)))
        else -> Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }
    val label = when (row.type) {
        RowType.ROAD -> if (row.goingLeft) "<< car" else "car >>"
        RowType.RIVER -> if (objectIndex % 2 == 0) "raft" else "log"
        else -> ""
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(fallbackBackground)
            .border(1.dp, Color.Black.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
    ) {
        if (spriteRes != null) {
            Image(
                painter = painterResource(id = spriteRes),
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        } else {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun FroggerFrog(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFA5E65A), Color(0xFF4CAF50))))
            .border(2.dp, Color(0xFF183A1D), RoundedCornerShape(14.dp)),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, CircleShape),
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, CircleShape),
            )
        }
        Text(
            text = "hop",
            color = Color(0xFF173517),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun FroggerControls(
    onMove: (Direction) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF10161C).copy(alpha = 0.92f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = { onMove(Direction.UP) },
                modifier = Modifier.width(132.dp),
            ) {
                Text("Hop Up")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { onMove(Direction.LEFT) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Left")
                }
                Button(
                    onClick = { onMove(Direction.DOWN) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Down")
                }
                Button(
                    onClick = { onMove(Direction.RIGHT) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Right")
                }
            }
        }
    }
}

@Composable
private fun FroggerOverlay(
    title: String,
    body: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xBF05070A)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F141B)),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text(
                    body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onSecondary) {
                        Text(secondaryLabel)
                    }
                    Button(onClick = onPrimary) {
                        Text(primaryLabel)
                    }
                }
            }
        }
    }
}
