package com.sterlingsworld.feature.game.suites.relaxation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ── Colors ─────────────────────────────────────────────────────────────────────
private val SudokuBg         = Color(0xFFFFF8E1)
private val TropicalTeal     = Color(0xFF00897B)
private val TropicalTealLight= Color(0xFFE0F2F1)
private val DeepPalm         = Color(0xFF1B5E20)
private val HibiscusRed      = Color(0xFFEF5350)
private val HighlightBg      = Color(0xFFB2DFDB)
private val SelectedBg       = Color(0xFF80CBC4)
private val BlockLight       = Color(0xFFFFF9EF)
private val BlockDark        = Color(0xFFF0EDD8)
private val GridLine         = Color(0xFF80CBC4)
private val GridLineBold     = TropicalTeal
private val InvalidBg        = Color(0xFFFFCDD2)

// ── Data ───────────────────────────────────────────────────────────────────────
data class SudokuUiState(
    val puzzle:          List<List<Int>>           = List(9) { List(9) { 0 } },
    val solution:        List<List<Int>>           = List(9) { List(9) { 0 } },
    val userGrid:        List<List<Int>>           = List(9) { List(9) { 0 } },
    val givenCells:      Set<Pair<Int, Int>>       = emptySet(),
    val selectedCell:    Pair<Int, Int>?           = null,
    val highlightedCells:Set<Pair<Int, Int>>       = emptySet(),
    val invalidCells:    Set<Pair<Int, Int>>       = emptySet(),
    val isComplete:      Boolean                   = false,
    val message:         String                    = "",
)

// ── ViewModel ──────────────────────────────────────────────────────────────────
class IslandSudokuViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SudokuUiState())
    val uiState: StateFlow<SudokuUiState> = _uiState.asStateFlow()

    init { newGame() }

    fun newGame() {
        val sol  = generateSolution()
        val puz  = createPuzzle(sol, clues = 36)
        val given = buildSet {
            for (r in 0..8) for (c in 0..8) if (puz[r][c] != 0) add(r to c)
        }
        _uiState.value = SudokuUiState(
            puzzle     = puz,
            solution   = sol,
            userGrid   = puz.map { it.toList() },
            givenCells = given,
        )
    }

    fun selectCell(row: Int, col: Int) {
        val cur = _uiState.value.selectedCell
        if (cur == row to col) {
            _uiState.update { it.copy(selectedCell = null, highlightedCells = emptySet()) }
            return
        }
        _uiState.update {
            it.copy(
                selectedCell     = row to col,
                highlightedCells = computeHighlights(row, col),
                invalidCells     = emptySet(),
            )
        }
    }

    fun enterNumber(n: Int) {
        val s    = _uiState.value
        val cell = s.selectedCell ?: return
        val (r, c) = cell
        if (cell in s.givenCells) return

        val newGrid = s.userGrid.map { it.toMutableList() }
        newGrid[r][c] = n
        _uiState.update { it.copy(userGrid = newGrid.map { row -> row.toList() }, invalidCells = emptySet()) }
    }

    fun clearCell() {
        val s    = _uiState.value
        val cell = s.selectedCell ?: return
        if (cell in s.givenCells) return
        val (r, c) = cell
        val newGrid = s.userGrid.map { it.toMutableList() }
        newGrid[r][c] = 0
        _uiState.update { it.copy(userGrid = newGrid.map { row -> row.toList() }, invalidCells = emptySet()) }
    }

    fun checkSolution() {
        val s      = _uiState.value
        val errors = buildSet {
            for (r in 0..8) for (c in 0..8) {
                val v = s.userGrid[r][c]
                if (v == 0 || v != s.solution[r][c]) add(r to c)
            }
        }
        val allFilled = (0..8).all { r -> (0..8).all { c -> s.userGrid[r][c] != 0 } }
        _uiState.update {
            it.copy(
                invalidCells = errors,
                isComplete   = errors.isEmpty() && allFilled,
                message      = when {
                    errors.isEmpty() && allFilled -> "Perfect! Mahalo!"
                    !allFilled                    -> "Some cells are still empty."
                    else                          -> "Some numbers are wrong — check highlighted cells."
                },
            )
        }
    }

    private fun computeHighlights(row: Int, col: Int): Set<Pair<Int, Int>> =
        buildSet {
            for (i in 0..8) { add(row to i); add(i to col) }
            val br = (row / 3) * 3; val bc = (col / 3) * 3
            for (r in br until br + 3) for (c in bc until bc + 3) add(r to c)
            remove(row to col)
        }

    // ── Puzzle generation (ported from JS backtracking) ─────────────────────────
    private fun generateSolution(): List<List<Int>> {
        val grid = Array(9) { IntArray(9) }
        fillGrid(grid)
        return grid.map { it.toList() }
    }

    private fun fillGrid(grid: Array<IntArray>): Boolean {
        val (row, col) = findEmpty(grid) ?: return true
        for (n in (1..9).shuffled()) {
            if (isPlacementValid(grid, row, col, n)) {
                grid[row][col] = n
                if (fillGrid(grid)) return true
                grid[row][col] = 0
            }
        }
        return false
    }

    private fun findEmpty(grid: Array<IntArray>): Pair<Int, Int>? {
        for (r in 0..8) for (c in 0..8) if (grid[r][c] == 0) return r to c
        return null
    }

    private fun isPlacementValid(grid: Array<IntArray>, row: Int, col: Int, n: Int): Boolean {
        for (i in 0..8) {
            if (grid[row][i] == n || grid[i][col] == n) return false
        }
        val br = (row / 3) * 3; val bc = (col / 3) * 3
        for (r in br until br + 3) for (c in bc until bc + 3) {
            if (grid[r][c] == n) return false
        }
        return true
    }

    private fun createPuzzle(solution: List<List<Int>>, clues: Int): List<List<Int>> {
        val puzzle = solution.map { it.toMutableList() }
        val cells  = (0..8).flatMap { r -> (0..8).map { c -> r to c } }.shuffled()
        cells.take(81 - clues).forEach { (r, c) -> puzzle[r][c] = 0 }
        return puzzle.map { it.toList() }
    }
}

// ── Root Composable ────────────────────────────────────────────────────────────
@Composable
fun IslandSudokuGame(vm: IslandSudokuViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val haptic   = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SudokuBg),
    ) {
        // Result banner (no overlay)
        AnimatedVisibility(
            visible = uiState.message.isNotEmpty(),
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (uiState.isComplete) TropicalTeal else HibiscusRed)
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = uiState.message,
                    color      = Color.White,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center,
                )
            }
        }

        // Grid + action buttons — scrollable so the sand pad doesn't clip content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SudokuGrid(uiState = uiState, onSelectCell = vm::selectCell)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = vm::checkSolution,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Check", color = TropicalTeal, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = vm::newGame,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(containerColor = TropicalTeal),
                    shape    = RoundedCornerShape(12.dp),
                ) {
                    Text("New Puzzle", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Sand Pad — slides in from bottom when a cell is selected
        AnimatedVisibility(
            visible = uiState.selectedCell != null,
            enter   = slideInVertically { it } + fadeIn(),
            exit    = slideOutVertically { it } + fadeOut(),
        ) {
            SandPad(
                onNumber = { n ->
                    vm.enterNumber(n)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onClear = {
                    vm.clearCell()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
            )
        }
    }
}

// ── 9×9 Grid ───────────────────────────────────────────────────────────────────
@Composable
private fun SudokuGrid(
    uiState:      SudokuUiState,
    onSelectCell: (Int, Int) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        // Reserve ~7dp for thick dividers and ~6dp for thin dividers between cells
        val cellSize: Dp = ((maxWidth - 14.dp) / 9f)

        Column(
            modifier = Modifier.border(2.dp, GridLineBold, RoundedCornerShape(6.dp)),
        ) {
            for (row in 0..8) {
                if (row > 0) {
                    val lineH = if (row % 3 == 0) 2.dp else 0.5.dp
                    val lineC = if (row % 3 == 0) GridLineBold else GridLine
                    Spacer(Modifier.height(lineH).fillMaxWidth().background(lineC))
                }
                Row {
                    for (col in 0..8) {
                        if (col > 0) {
                            val lineW = if (col % 3 == 0) 2.dp else 0.5.dp
                            val lineC = if (col % 3 == 0) GridLineBold else GridLine
                            Spacer(Modifier.width(lineW).height(cellSize).background(lineC))
                        }
                        SudokuCell(
                            value       = uiState.userGrid[row][col],
                            isGiven     = (row to col) in uiState.givenCells,
                            isSelected  = uiState.selectedCell == (row to col),
                            isHighlight = (row to col) in uiState.highlightedCells,
                            isInvalid   = (row to col) in uiState.invalidCells,
                            blockRow    = row / 3,
                            blockCol    = col / 3,
                            cellSize    = cellSize,
                            onClick     = { onSelectCell(row, col) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SudokuCell(
    value:       Int,
    isGiven:     Boolean,
    isSelected:  Boolean,
    isHighlight: Boolean,
    isInvalid:   Boolean,
    blockRow:    Int,
    blockCol:    Int,
    cellSize:    Dp,
    onClick:     () -> Unit,
) {
    val bgColor = when {
        isSelected  -> SelectedBg
        isInvalid   -> InvalidBg
        isHighlight -> HighlightBg
        (blockRow + blockCol) % 2 == 0 -> BlockLight
        else        -> BlockDark
    }
    val textColor = when {
        isInvalid -> HibiscusRed
        isGiven   -> DeepPalm
        else      -> TropicalTeal
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (value != 0) {
            Text(
                text       = value.toString(),
                color      = textColor,
                fontSize   = (cellSize.value * 0.45f).sp,
                fontWeight = if (isGiven) FontWeight.Bold else FontWeight.SemiBold,
                textAlign  = TextAlign.Center,
            )
        }
    }
}

// ── Sand Pad (1–9 + Clear) ────────────────────────────────────────────────────
@Composable
private fun SandPad(
    onNumber: (Int) -> Unit,
    onClear:  () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Three rows of 1–3, 4–6, 7–9
        for (rowIdx in 0..2) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (colIdx in 0..2) {
                    val n = rowIdx * 3 + colIdx + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(TropicalTealLight, RoundedCornerShape(10.dp))
                            .clickable { onNumber(n) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = n.toString(),
                            color      = DeepPalm,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // Clear button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFFFFE0B2), RoundedCornerShape(10.dp))
                .clickable { onClear() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = "Clear",
                color      = Color(0xFF5D4037),
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
