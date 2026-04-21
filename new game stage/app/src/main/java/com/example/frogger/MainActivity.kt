package com.example.frogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frogger.model.Direction
import com.example.frogger.model.GameStatus
import com.example.frogger.model.RowType

/**
 * Main entry point for the Frogger Compose application. It sets up
 * the [GameViewModel] and delegates rendering to [GameScreen]. The
 * activity is kept minimal since all UI is implemented with
 * Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: GameViewModel by viewModels()
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameScreen(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * Top‑level composable for rendering the game state. It reads
 * game state from the [GameViewModel] and draws the rows, moving
 * objects, frog and control buttons. When the game reaches a
 * terminal state it overlays an appropriate message and offers
 * actions to proceed or restart.
 */
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val gameState by viewModel.state.collectAsState()
    val rows = gameState.rows
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Compute dimensions using the available constraints. maxWidth and maxHeight
        // are Dp values corresponding to the full size of the BoxWithConstraints.
        val rowCount = rows.size
        val rowHeight: androidx.compose.ui.unit.Dp = maxHeight / rowCount
        val frogWidthDp = maxWidth * viewModel.frogWidthNormalized

        // Render background lanes. Each row occupies an equal vertical slice
        // of the screen. Color coding matches the row type.
        Column(modifier = Modifier.fillMaxSize()) {
            rows.forEach { row ->
                val backgroundColor = when (row.type) {
                    RowType.ROAD -> Color.DarkGray
                    RowType.RIVER -> Color(0xFF1565C0) // deep blue
                    RowType.SAFE -> Color(0xFF2E7D32) // green
                    RowType.HOME -> Color(0xFF1B5E20) // darker green
                    RowType.START -> Color(0xFF2E7D32)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .background(backgroundColor)
                )
            }
        }

        // Render moving objects on top of the background lanes. Each object's
        // position and size are scaled relative to the full width and row height.
        rows.forEachIndexed { index, row ->
            // Vertical offset for this row
            val yOffset: androidx.compose.ui.unit.Dp = rowHeight * index
            row.objects.forEach { obj ->
                val objectColor = when (row.type) {
                    RowType.ROAD -> Color.Red
                    RowType.RIVER -> Color(0xFF795548) // brown logs
                    else -> Color.Transparent
                }
                Box(
                    modifier = Modifier
                        .offset(x = maxWidth * obj.x, y = yOffset)
                        .width(maxWidth * obj.width)
                        .height(rowHeight)
                        .background(objectColor)
                )
            }
        }

        // Draw the frog on top of everything. The frog's position is based
        // on the current game state's frogX and frogRow. Its width is
        // defined by the view model, and its height matches a row.
        Box(
            modifier = Modifier
                .offset(x = maxWidth * gameState.frogX, y = rowHeight * gameState.frogRow)
                .width(frogWidthDp)
                .height(rowHeight)
                .background(Color.Green)
        )

        // Draw top status bar with lives and homes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lives display
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Lives: ${gameState.lives}")
            }
            // Homes display
            Row(verticalAlignment = Alignment.CenterVertically) {
                gameState.homes.forEach { occupied ->
                    val color = if (occupied) Color.Yellow else Color.LightGray
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(horizontal = 2.dp)
                            .background(color)
                    )
                }
            }
        }

        // Overlay messages for level complete, game over and win
        when (gameState.status) {
            GameStatus.LEVEL_COMPLETE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Level complete!", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.nextLevel() }) {
                            Text("Next Level")
                        }
                    }
                }
            }
            GameStatus.GAME_OVER -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Game Over", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.restartCurrentLevel() }) {
                            Text("Restart")
                        }
                    }
                }
            }
            GameStatus.GAME_WIN -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "You win!", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.restartCurrentLevel() }) {
                            Text("Play Again")
                        }
                    }
                }
            }
            else -> Unit
        }

        // Draw control buttons at the bottom of the screen if the game is
        // actively being played. Buttons are disabled otherwise.
        if (gameState.status == GameStatus.PLAYING) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Up button row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(48.dp))
                    Button(onClick = { viewModel.moveFrog(Direction.UP) }) {
                        Text("↑")
                    }
                    Spacer(modifier = Modifier.width(48.dp))
                }
                // Left, Down, Right buttons row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { viewModel.moveFrog(Direction.LEFT) }) {
                        Text("←")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { viewModel.moveFrog(Direction.DOWN) }) {
                        Text("↓")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { viewModel.moveFrog(Direction.RIGHT) }) {
                        Text("→")
                    }
                }
            }
        }
    }
}