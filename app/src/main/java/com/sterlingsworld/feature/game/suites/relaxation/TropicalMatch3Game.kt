package com.sterlingsworld.feature.game.suites.relaxation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

// ── Tiles ──────────────────────────────────────────────────────────────────────
private val TILE_EMOJIS  = listOf("🌺","🌴","🐚","🌊","☀️","🍍")
private val TILE_COLORS  = listOf(
    Color(0xFFFCE4EC), Color(0xFFE8F5E9), Color(0xFFFFF3E0),
    Color(0xFFE3F2FD), Color(0xFFFFFDE7), Color(0xFFF3E5F5),
)
private val TILE_BORDERS = listOf(
    Color(0xFFF06292), Color(0xFF66BB6A), Color(0xFFFFB74D),
    Color(0xFF64B5F6), Color(0xFFFFD54F), Color(0xFFBA68C8),
)

private const val ROWS = 6
private const val COLS = 6

// ── Colors ─────────────────────────────────────────────────────────────────────
private val M3Bg   = Color(0xFFFFF8E1)
private val M3Teal = Color(0xFF00897B)

// ── Data ───────────────────────────────────────────────────────────────────────
data class Match3UiState(
    val grid:        List<List<Int>>          = buildInitialGrid(),
    val score:       Int                      = 0,
    val selected:    Pair<Int, Int>?          = null,
    val removedCells:Set<Pair<Int, Int>>      = emptySet(),  // cells mid-pop animation
    val isProcessing:Boolean                  = false,
    val message:     String                   = "",
)

private fun buildInitialGrid(): List<List<Int>> {
    var grid: List<List<Int>>
    do {
        grid = List(ROWS) { List(COLS) { (0 until 6).random() } }
    } while (findAllMatches(grid).isNotEmpty())
    return grid
}

private fun findAllMatches(grid: List<List<Int>>): Set<Pair<Int, Int>> {
    val matched = mutableSetOf<Pair<Int, Int>>()
    // Horizontal
    for (r in 0 until ROWS) {
        var c = 0
        while (c < COLS - 2) {
            val v = grid[r][c]
            if (v != -1 && grid[r][c+1] == v && grid[r][c+2] == v) {
                var end = c + 2
                while (end + 1 < COLS && grid[r][end+1] == v) end++
                for (i in c..end) matched.add(r to i)
                c = end + 1
            } else c++
        }
    }
    // Vertical
    for (c in 0 until COLS) {
        var r = 0
        while (r < ROWS - 2) {
            val v = grid[r][c]
            if (v != -1 && grid[r+1][c] == v && grid[r+2][c] == v) {
                var end = r + 2
                while (end + 1 < ROWS && grid[end+1][c] == v) end++
                for (i in r..end) matched.add(i to c)
                r = end + 1
            } else r++
        }
    }
    return matched
}

private fun applyGravity(grid: List<List<Int>>): List<List<Int>> {
    val mut = grid.map { it.toMutableList() }
    for (c in 0 until COLS) {
        var write = ROWS - 1
        for (r in ROWS - 1 downTo 0) {
            if (mut[r][c] != -1) { mut[write][c] = mut[r][c]; if (write != r) mut[r][c] = -1; write-- }
        }
        for (r in write downTo 0) mut[r][c] = (0 until 6).random()
    }
    return mut.map { it.toList() }
}

// ── ViewModel ──────────────────────────────────────────────────────────────────
class TropicalMatch3ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Match3UiState())
    val uiState: StateFlow<Match3UiState> = _uiState.asStateFlow()

    fun newGame() { _uiState.value = Match3UiState() }

    fun tapTile(row: Int, col: Int) {
        val s = _uiState.value
        if (s.isProcessing) return

        if (s.selected == null) {
            _uiState.update { it.copy(selected = row to col) }
            return
        }

        val selected = s.selected ?: return
        val (sr, sc) = selected
        if (sr == row && sc == col) {
            _uiState.update { it.copy(selected = null) }
            return
        }

        // Only adjacent cells can swap
        if (abs(sr - row) + abs(sc - col) != 1) {
            _uiState.update { it.copy(selected = row to col) }
            return
        }

        // Perform swap and check for matches
        val swapped = swap(s.grid, sr, sc, row, col)
        val matches = findAllMatches(swapped)

        if (matches.isEmpty()) {
            // No match — signal rebound (swap back), clear selection
            _uiState.update { it.copy(selected = null, message = "No match!") }
            return
        }

        // Apply matches and gravity
        processMatches(swapped, matches)
    }

    private fun processMatches(grid: List<List<Int>>, matches: Set<Pair<Int, Int>>) {
        val points = matches.size * 10
        // Mark removed cells for animation, then resolve
        val withRemoved = grid.map { it.toMutableList() }
        matches.forEach { (r, c) -> withRemoved[r][c] = -1 }

        _uiState.update {
            it.copy(
                isProcessing = true,
                selected     = null,
                removedCells = matches,
                score        = it.score + points,
                message      = "",
            )
        }
    }

    // Called by composable after pop animation completes
    fun onPopComplete() {
        val s = _uiState.value
        if (!s.isProcessing) return

        val withRemoved = s.grid.map { it.toMutableList() }
        s.removedCells.forEach { (r, c) -> withRemoved[r][c] = -1 }

        val afterGravity = applyGravity(withRemoved.map { it.toList() })
        val newMatches   = findAllMatches(afterGravity)

        if (newMatches.isEmpty()) {
            _uiState.update { it.copy(grid = afterGravity, removedCells = emptySet(), isProcessing = false) }
        } else {
            // Chain reaction
            val points = newMatches.size * 10
            _uiState.update {
                it.copy(
                    grid         = afterGravity,
                    removedCells = newMatches,
                    score        = it.score + points,
                )
            }
        }
    }

    private fun swap(grid: List<List<Int>>, r1: Int, c1: Int, r2: Int, c2: Int): List<List<Int>> {
        val mut = grid.map { it.toMutableList() }
        val tmp = mut[r1][c1]; mut[r1][c1] = mut[r2][c2]; mut[r2][c2] = tmp
        return mut.map { it.toList() }
    }
}

// ── Root Composable ────────────────────────────────────────────────────────────
@Composable
fun TropicalMatch3Game(vm: TropicalMatch3ViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // Trigger pop-complete callback after pop animation duration
    LaunchedEffect(uiState.removedCells) {
        if (uiState.removedCells.isEmpty()) return@LaunchedEffect
        delay(400L) // match pop animation duration
        vm.onPopComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(M3Bg),
    ) {
        // Score bar
        ScoreBar(score = uiState.score, message = uiState.message, onNewGame = vm::newGame)

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                val cellSize: Dp = (maxWidth - 10.dp) / COLS
                Match3Grid(
                    uiState  = uiState,
                    cellSize = cellSize,
                    onTap    = vm::tapTile,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ScoreBar(score: Int, message: String, onNewGame: () -> Unit) {
    val scoreScale by animateFloatAsState(
        targetValue    = 1f,
        animationSpec  = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label          = "score_scale",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(M3Teal)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text       = "Score: $score",
            color      = Color.White,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        if (message.isNotEmpty()) {
            Text(text = message, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .clickable(onClick = onNewGame)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text("New", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun Match3Grid(
    uiState:  Match3UiState,
    cellSize: Dp,
    onTap:    (Int, Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        for (r in 0 until ROWS) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (c in 0 until COLS) {
                    val tileVal = uiState.grid[r][c]
                    val isSelected = uiState.selected == r to c
                    val isRemoving = (r to c) in uiState.removedCells
                    TropicalTile(
                        tileVal    = tileVal,
                        isSelected = isSelected,
                        isRemoving = isRemoving,
                        cellSize   = cellSize,
                        onClick    = { onTap(r, c) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TropicalTile(
    tileVal: Int, isSelected: Boolean, isRemoving: Boolean,
    cellSize: Dp, onClick: () -> Unit,
) {
    val alpha by animateFloatAsState(
        targetValue   = if (isRemoving) 0f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "tile_alpha",
    )
    val scale by animateFloatAsState(
        targetValue   = when {
            isRemoving -> 1.4f
            isSelected -> 0.88f
            else       -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label         = "tile_scale",
    )

    // Particle sparkle on removal
    if (isRemoving) {
        SparkleEffect(cellSize = cellSize, color = if (tileVal >= 0) TILE_BORDERS[tileVal] else M3Teal)
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .then(
                if (alpha > 0f) Modifier
                    .clip(RoundedCornerShape((cellSize.value * 0.2f).dp))
                    .background(
                        if (tileVal >= 0) TILE_COLORS[tileVal].copy(alpha = alpha) else Color.Transparent,
                        RoundedCornerShape((cellSize.value * 0.2f).dp),
                    )
                    .border(
                        2.dp,
                        if (isSelected && tileVal >= 0) Color(0xFFFFB300)
                        else if (tileVal >= 0) TILE_BORDERS[tileVal].copy(alpha = alpha)
                        else Color.Transparent,
                        RoundedCornerShape((cellSize.value * 0.2f).dp),
                    )
                    .clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (tileVal >= 0 && alpha > 0f) {
            Text(
                text     = TILE_EMOJIS[tileVal],
                fontSize = (cellSize.value * 0.5f * scale).sp,
            )
        }
    }
}

// ── Simple sparkle / particle burst ────────────────────────────────────────────
@Composable
private fun SparkleEffect(cellSize: Dp, color: Color) {
    val radius = remember { Animatable(0f) }
    val alpha  = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        radius.animateTo(cellSize.value * 0.7f, spring(stiffness = Spring.StiffnessMediumLow))
        alpha.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
    }

    Canvas(modifier = Modifier.size(cellSize)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r  = radius.value
        val a  = alpha.value
        // Draw 6 small circles bursting outward
        for (i in 0..5) {
            val angle = Math.toRadians((i * 60.0))
            val px = cx + r * Math.cos(angle).toFloat()
            val py = cy + r * Math.sin(angle).toFloat()
            drawCircle(color = color.copy(alpha = a), radius = size.width * 0.08f, center = Offset(px, py))
        }
    }
}
