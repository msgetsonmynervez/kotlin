package com.sterlingsworld.feature.game.suites.relaxation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
private val CwBg        = Color(0xFFFFF8E1)
private val CwTeal      = Color(0xFF00897B)
private val CwTealLight = Color(0xFFE0F2F1)
private val CwSkyBlue   = Color(0xFFB3E5FC)   // active word highlight
private val CwSelected  = Color(0xFF80CBC4)   // selected cell
private val CwBlocked   = Color(0xFF1A2A1F)
private val CwError     = Color(0xFFFFCDD2)
private val CwBorder    = Color(0xFFB2DFDB)

// ── Data ───────────────────────────────────────────────────────────────────────
data class CrosswordWord(
    val word: String, val row: Int, val col: Int,
    val dir: String, val num: Int, val clue: String,
)
data class CrosswordPuzzle(val grid: List<List<Char>>, val words: List<CrosswordWord>)

data class MsCrosswordUiState(
    val puzzle: CrosswordPuzzle        = ALL_CROSSWORD_PUZZLES[0],
    val userGrid: List<List<Char>>     = emptyUserGrid(ALL_CROSSWORD_PUZZLES[0].grid),
    val selectedCell: Pair<Int, Int>?  = null,
    val selectedDir: String            = "across",
    val activeWordCells: Set<Pair<Int, Int>> = emptySet(),
    val activeClue: String             = "",
    val errorCells: Set<Pair<Int, Int>> = emptySet(),
    val isComplete: Boolean            = false,
    val message: String                = "",
)

private fun emptyUserGrid(grid: List<List<Char>>) = grid.map { row -> row.map { ' ' } }

// ── ViewModel ──────────────────────────────────────────────────────────────────
class MsCrosswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MsCrosswordUiState())
    val uiState: StateFlow<MsCrosswordUiState> = _uiState.asStateFlow()

    fun newPuzzle() {
        val puz = ALL_CROSSWORD_PUZZLES.random()
        _uiState.value = MsCrosswordUiState(puzzle = puz, userGrid = emptyUserGrid(puz.grid))
    }

    fun selectCell(row: Int, col: Int) {
        val s = _uiState.value
        if (s.puzzle.grid[row][col] == '#') return

        val sameCell = s.selectedCell == row to col
        val newDir = when {
            sameCell -> toggleDir(s.selectedDir, row, col, s.puzzle.words)
            else     -> preferredDir(row, col, s.puzzle.words)
        }
        val word = findWord(row, col, newDir, s.puzzle.words)
        _uiState.update {
            it.copy(
                selectedCell     = row to col,
                selectedDir      = newDir,
                activeWordCells  = word?.let { w -> wordCells(w).toSet() } ?: emptySet(),
                activeClue       = word?.let { w -> "${w.num} ${w.dir.uppercase()}: ${w.clue}" } ?: "",
                errorCells       = emptySet(),
                message          = "",
            )
        }
    }

    fun typeKey(key: Char) {
        val s = _uiState.value
        val cell = s.selectedCell ?: return
        val (r, c) = cell
        if (s.puzzle.grid[r][c] == '#') return
        val newGrid = s.userGrid.map { it.toMutableList() }
        newGrid[r][c] = key.uppercaseChar()
        val updated = _uiState.value.copy(userGrid = newGrid.map { it.toList() }, errorCells = emptySet())
        _uiState.value = updated

        // Auto-advance to next empty cell in the active word
        val word = findWord(r, c, s.selectedDir, s.puzzle.words) ?: return
        val cells = wordCells(word)
        val idx   = cells.indexOf(r to c)
        val next  = (idx + 1 until cells.size).firstOrNull { updated.userGrid[cells[it].first][cells[it].second] == ' ' }
            ?: (0 until idx).firstOrNull { updated.userGrid[cells[it].first][cells[it].second] == ' ' }
        if (next != null) {
            val nc = cells[next]
            _uiState.update { it.copy(selectedCell = nc) }
        }
    }

    fun deleteKey() {
        val s = _uiState.value
        val cell = s.selectedCell ?: return
        val (r, c) = cell
        if (s.puzzle.grid[r][c] == '#') return

        if (s.userGrid[r][c] != ' ') {
            // Delete current cell
            val newGrid = s.userGrid.map { it.toMutableList() }
            newGrid[r][c] = ' '
            _uiState.update { it.copy(userGrid = newGrid.map { it.toList() }, errorCells = emptySet()) }
        } else {
            // Move back to previous cell
            val word  = findWord(r, c, s.selectedDir, s.puzzle.words) ?: return
            val cells = wordCells(word)
            val idx   = cells.indexOf(r to c)
            if (idx > 0) {
                val prev = cells[idx - 1]
                val ng   = s.userGrid.map { it.toMutableList() }
                ng[prev.first][prev.second] = ' '
                _uiState.update {
                    it.copy(
                        selectedCell = prev,
                        userGrid     = ng.map { it.toList() },
                        errorCells   = emptySet(),
                    )
                }
            }
        }
    }

    fun checkPuzzle() {
        val s = _uiState.value
        val errors = buildSet<Pair<Int, Int>> {
            s.puzzle.grid.forEachIndexed { r, row ->
                row.forEachIndexed { c, ch ->
                    if (ch != '#') {
                        val user = s.userGrid[r][c]
                        if (user != ' ' && user != ch) add(r to c)
                    }
                }
            }
        }
        val allFilled = s.puzzle.grid.indices.all { r ->
            s.puzzle.grid[r].indices.all { c ->
                s.puzzle.grid[r][c] == '#' || s.userGrid[r][c] != ' '
            }
        }
        _uiState.update {
            it.copy(
                errorCells = errors,
                isComplete = errors.isEmpty() && allFilled,
                message    = when {
                    errors.isEmpty() && allFilled -> "All correct!"
                    !allFilled                    -> "Some cells are still empty."
                    else                          -> "Some letters are wrong."
                },
            )
        }
    }

    private fun toggleDir(dir: String, row: Int, col: Int, words: List<CrosswordWord>): String {
        val other = if (dir == "across") "down" else "across"
        return if (findWord(row, col, other, words) != null) other else dir
    }

    private fun preferredDir(row: Int, col: Int, words: List<CrosswordWord>): String =
        if (findWord(row, col, "across", words) != null) "across" else "down"

    private fun findWord(row: Int, col: Int, dir: String, words: List<CrosswordWord>): CrosswordWord? =
        words.firstOrNull { w ->
            w.dir == dir && when (dir) {
                "across" -> w.row == row && col in w.col until (w.col + w.word.length)
                else     -> w.col == col && row in w.row until (w.row + w.word.length)
            }
        }

    private fun wordCells(w: CrosswordWord): List<Pair<Int, Int>> =
        (0 until w.word.length).map { i ->
            if (w.dir == "across") w.row to (w.col + i) else (w.row + i) to w.col
        }
}

// ── Root Composable ────────────────────────────────────────────────────────────
@Composable
fun MsCrosswordGame(vm: MsCrosswordViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CwBg),
    ) {
        AnimatedVisibility(
            visible = uiState.message.isNotEmpty(),
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (uiState.isComplete) CwTeal else Color(0xFF78909C))
                    .padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(uiState.message, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }

        // Grid area — scrollable for taller puzzles
        Column(
            modifier            = Modifier.weight(1f).padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CrosswordGrid(uiState = uiState, onSelectCell = vm::selectCell)

            Row(
                modifier              = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(onClick = vm::checkPuzzle, modifier = Modifier.weight(1f)) {
                    Text("Check", color = CwTeal, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick  = vm::newPuzzle,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.buttonColors(containerColor = CwTeal),
                    shape    = RoundedCornerShape(12.dp),
                ) {
                    Text("New Puzzle", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Clue bar — slides active clue in as user navigates
        AnimatedContent(
            targetState = uiState.activeClue,
            label       = "clue_bar",
        ) { clue ->
            if (clue.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CwTealLight)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text       = clue,
                        color      = Color(0xFF004D40),
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        // Custom keyboard — same QWERTY layout as Wordle, no key-state coloring
        CrosswordKeyboard(onKey = vm::typeKey, onDelete = vm::deleteKey)
    }
}

@Composable
private fun CrosswordGrid(
    uiState:      MsCrosswordUiState,
    onSelectCell: (Int, Int) -> Unit,
) {
    val puzzle      = uiState.puzzle
    val rows        = puzzle.grid.size
    val cols        = puzzle.grid[0].size
    val cellNumbers = puzzle.words.associate { (it.row to it.col) to it.num }

    BoxWithConstraints(modifier = Modifier.padding(horizontal = 8.dp)) {
        val cellSize: Dp = (maxWidth / cols)
        Column {
            for (r in 0 until rows) {
                Row {
                    for (c in 0 until cols) {
                        val ch         = puzzle.grid[r][c]
                        val userLetter = uiState.userGrid[r][c]
                        val key        = r to c
                        val isBlocked  = ch == '#'
                        val isSelected = uiState.selectedCell == key
                        val isActive   = key in uiState.activeWordCells
                        val isError    = key in uiState.errorCells

                        val bg = when {
                            isBlocked  -> CwBlocked
                            isSelected -> CwSelected
                            isError    -> CwError
                            isActive   -> CwSkyBlue
                            else       -> Color.White
                        }

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .background(bg)
                                .then(if (!isBlocked) Modifier.border(0.5.dp, CwBorder) else Modifier)
                                .then(if (!isBlocked) Modifier.clickable { onSelectCell(r, c) } else Modifier),
                        ) {
                            // Word number label
                            cellNumbers[key]?.let { num ->
                                Text(
                                    text     = num.toString(),
                                    fontSize = (cellSize.value * 0.22f).sp,
                                    color    = if (isBlocked) Color.Transparent else CwTeal,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.offset(x = 1.dp, y = 0.dp).align(Alignment.TopStart),
                                    lineHeight = (cellSize.value * 0.22f).sp,
                                )
                            }
                            // User-entered letter
                            if (!isBlocked && userLetter != ' ') {
                                Text(
                                    text       = userLetter.toString(),
                                    fontSize   = (cellSize.value * 0.48f).sp,
                                    color      = if (isError) Color(0xFFB71C1C) else Color(0xFF1A2A1F),
                                    fontWeight = FontWeight.Bold,
                                    modifier   = Modifier.align(Alignment.Center),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private val CW_KB_ROWS = listOf(
    listOf("Q","W","E","R","T","Y","U","I","O","P"),
    listOf("A","S","D","F","G","H","J","K","L"),
    listOf("Z","X","C","V","B","N","M","⌫"),
)

@Composable
private fun CrosswordKeyboard(onKey: (Char) -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CwTealLight)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CW_KB_ROWS.forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                row.forEach { key ->
                    val isDelete = key == "⌫"
                    Box(
                        modifier = Modifier
                            .weight(if (isDelete) 1.5f else 1f)
                            .height(44.dp)
                            .background(CwTeal.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, CwTeal.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable {
                                if (isDelete) onDelete() else onKey(key[0])
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = key,
                            color      = Color(0xFF004D40),
                            fontSize   = if (isDelete) 15.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// ── Puzzle Bank (ported from HTML) ─────────────────────────────────────────────
internal val ALL_CROSSWORD_PUZZLES = listOf(
    CrosswordPuzzle(
        grid = listOf(
            listOf('M','Y','E','L','I','N','#'),
            listOf('#','#','#','E','#','E','#'),
            listOf('F','L','A','S','H','U','#'),
            listOf('#','#','X','I','#','R','#'),
            listOf('#','#','O','O','#','O','#'),
            listOf('#','#','N','N','#','N','#'),
            listOf('#','#','#','S','#','#','#'),
        ),
        words = listOf(
            CrosswordWord("MYELIN",  0,0,"across",1,"Protective nerve coating damaged in MS"),
            CrosswordWord("FLASH",   2,0,"across",3,"Brief symptom recurrence (hot ___)"),
            CrosswordWord("LESIONS", 0,3,"down",  2,"Damage areas visible on MRI"),
            CrosswordWord("AXON",    2,2,"down",  4,"Thread-like nerve cell projection"),
            CrosswordWord("NEURON",  0,5,"down",  5,"Basic nerve system cell"),
        ),
    ),
    CrosswordPuzzle(
        grid = listOf(
            listOf('#','R','E','L','A','P','S','E'),
            listOf('#','#','#','#','#','L','#','#'),
            listOf('N','E','R','V','E','A','#','#'),
            listOf('#','#','#','#','#','Q','#','#'),
            listOf('B','R','A','I','N','U','#','#'),
            listOf('#','#','#','#','#','E','#','#'),
            listOf('S','C','A','N','#','S','#','#'),
        ),
        words = listOf(
            CrosswordWord("RELAPSE", 0,1,"across",1,"MS symptoms return after stability"),
            CrosswordWord("NERVE",   2,0,"across",3,"Carries signals through body"),
            CrosswordWord("BRAIN",   4,0,"across",5,"Central organ MS affects"),
            CrosswordWord("SCAN",    6,0,"across",6,"MRI ___ detects MS damage"),
            CrosswordWord("PLAQUES", 0,5,"down",  2,"Hardened damaged myelin patches"),
        ),
    ),
    CrosswordPuzzle(
        grid = listOf(
            listOf('F','A','T','I','G','U','E'),
            listOf('#','#','#','#','#','#','#'),
            listOf('#','O','P','T','I','C','#'),
            listOf('#','#','#','R','#','#','#'),
            listOf('#','#','#','E','#','#','#'),
            listOf('#','#','#','M','#','#','#'),
            listOf('#','#','#','O','#','#','#'),
            listOf('#','#','#','R','#','#','#'),
        ),
        words = listOf(
            CrosswordWord("FATIGUE", 0,0,"across",1,"Overwhelming tiredness in MS"),
            CrosswordWord("OPTIC",   2,1,"across",3,"___ neuritis: eye nerve swelling"),
            CrosswordWord("TREMOR",  2,3,"down",  4,"Involuntary limb shaking"),
        ),
    ),
    CrosswordPuzzle(
        grid = listOf(
            listOf('G','L','I','A','L','#'),
            listOf('#','#','#','#','#','#'),
            listOf('S','H','E','A','T','H'),
            listOf('#','#','#','#','#','#'),
            listOf('S','P','I','N','A','L'),
            listOf('#','#','#','#','#','#'),
            listOf('C','O','R','T','E','X'),
        ),
        words = listOf(
            CrosswordWord("GLIAL",  0,0,"across",1,"Nerve support cell type"),
            CrosswordWord("SHEATH", 2,0,"across",2,"Myelin ___: nerve covering"),
            CrosswordWord("SPINAL", 4,0,"across",3,"___ cord: nerve bundle in back"),
            CrosswordWord("CORTEX", 6,0,"across",4,"Outer brain layer"),
        ),
    ),
)
