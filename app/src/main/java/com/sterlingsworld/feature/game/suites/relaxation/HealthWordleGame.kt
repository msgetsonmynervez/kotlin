package com.sterlingsworld.feature.game.suites.relaxation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── Colors ─────────────────────────────────────────────────────────────────────
private val WordleBg       = Color(0xFFFFF8E1)
private val SeafoamGreen   = Color(0xFF43A047)
private val SunbeamYellow  = Color(0xFFFFB300)
private val AbsentGray     = Color(0xFF78909C)
private val KeyBg          = Color(0xFFE0F2F1)
private val KeyText        = Color(0xFF004D40)
private val TileFilledBorder = Color(0xFF00897B)
private val TileEmptyBorder  = Color(0xFFB2DFDB)
private val TileText         = Color(0xFF1A2A1F)
private val TealCta          = Color(0xFF00897B)

// ── Data ───────────────────────────────────────────────────────────────────────
enum class TileState { EMPTY, FILLED, CORRECT, PRESENT, ABSENT }

data class GuessRow(val letters: List<Char>, val states: List<TileState>)

data class HealthWordleUiState(
    val targetWord: String = "",
    val guesses: List<GuessRow> = emptyList(),
    val currentGuess: String = "",
    val keyStates: Map<Char, TileState> = emptyMap(),
    val isGameOver: Boolean = false,
    val isWon: Boolean = false,
    val message: String = "",
    val shake: Boolean = false,
)

// ── ViewModel ──────────────────────────────────────────────────────────────────
class HealthWordleViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HealthWordleUiState())
    val uiState: StateFlow<HealthWordleUiState> = _uiState.asStateFlow()

    init { newGame() }

    fun newGame() {
        _uiState.value = HealthWordleUiState(targetWord = WORDLE_WORDS.random())
    }

    fun onKey(key: Char) {
        val s = _uiState.value
        if (s.isGameOver || s.currentGuess.length >= 5) return
        _uiState.update { it.copy(currentGuess = it.currentGuess + key.lowercaseChar()) }
    }

    fun onDelete() {
        if (_uiState.value.isGameOver) return
        _uiState.update { it.copy(currentGuess = it.currentGuess.dropLast(1)) }
    }

    fun onEnter() {
        val s = _uiState.value
        if (s.isGameOver) return
        if (s.currentGuess.length < 5) {
            _uiState.update { it.copy(shake = true) }
            return
        }
        val states = evaluate(s.currentGuess, s.targetWord)
        val newGuesses = s.guesses + GuessRow(s.currentGuess.toList(), states)

        val newKeys = s.keyStates.toMutableMap()
        s.currentGuess.forEachIndexed { i, c ->
            val cur = newKeys[c]; val next = states[i]
            if (cur == null
                || (cur != TileState.CORRECT && next == TileState.CORRECT)
                || (cur == TileState.ABSENT  && next == TileState.PRESENT)
            ) newKeys[c] = next
        }

        val won  = s.currentGuess == s.targetWord
        val lost = !won && newGuesses.size >= 6
        _uiState.update {
            it.copy(
                guesses      = newGuesses,
                currentGuess = "",
                keyStates    = newKeys,
                isGameOver   = won || lost,
                isWon        = won,
                message      = when {
                    won  -> "Correct! \"${s.targetWord.uppercase()}\""
                    lost -> "The word was \"${s.targetWord.uppercase()}\""
                    else -> ""
                },
            )
        }
    }

    fun onShakeComplete() { _uiState.update { it.copy(shake = false) } }

    private fun evaluate(guess: String, target: String): List<TileState> {
        val result    = MutableList(5) { TileState.ABSENT }
        val targetMut = target.toMutableList()
        val guessMut  = guess.toMutableList()
        val used      = BooleanArray(5)

        for (i in 0..4) {
            if (guessMut[i] == targetMut[i]) {
                result[i] = TileState.CORRECT; used[i] = true; guessMut[i] = '\u0000'
            }
        }
        for (j in 0..4) {
            if (guessMut[j] == '\u0000') continue
            for (k in 0..4) {
                if (!used[k] && guessMut[j] == targetMut[k]) {
                    result[j] = TileState.PRESENT; used[k] = true; break
                }
            }
        }
        return result
    }
}

// ── Root Composable ────────────────────────────────────────────────────────────
@Composable
fun HealthWordleGame(vm: HealthWordleViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val haptic   = LocalHapticFeedback.current

    // 6×5 flip Animatables — read inside WordleTileView, not here
    val flipAnims = remember { List(6) { List(5) { Animatable(0f) } } }
    // Shake Animatable — read inside graphicsLayer lambda in WordleRow, not here
    val shakeAnim = remember { Animatable(0f) }

    // Instantly reveal rows that were already submitted on first composition
    LaunchedEffect(Unit) {
        uiState.guesses.indices.forEach { row ->
            repeat(5) { col -> flipAnims[row][col].snapTo(1f) }
        }
    }

    // Stagger-flip the newest row when a guess is submitted
    LaunchedEffect(uiState.guesses.size) {
        val row = uiState.guesses.size - 1
        if (row < 0 || flipAnims[row][0].value == 1f) return@LaunchedEffect
        coroutineScope {
            for (col in 0..4) {
                launch {
                    delay(col * 200L)
                    flipAnims[row][col].animateTo(1f, tween(350))
                }
            }
        }
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Bounce-shake current guess row when word is too short
    LaunchedEffect(uiState.shake) {
        if (!uiState.shake) return@LaunchedEffect
        repeat(3) {
            shakeAnim.animateTo( 10f, spring(stiffness = 2000f))
            shakeAnim.animateTo(-10f, spring(stiffness = 2000f))
        }
        shakeAnim.animateTo(0f, spring())
        vm.onShakeComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WordleBg),
    ) {
        // Slide-in result banner — no pop-up overlay
        AnimatedVisibility(
            visible = uiState.message.isNotEmpty(),
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (uiState.isWon) SeafoamGreen else AbsentGray)
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

        // Board
        Column(
            modifier            = Modifier.weight(1f).padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            for (row in 0..5) {
                WordleRow(
                    rowIndex  = row,
                    uiState   = uiState,
                    flipAnims = flipAnims,
                    // Pass shakeAnim only for the row currently being typed
                    shakeAnim = if (row == uiState.guesses.size && !uiState.isGameOver) shakeAnim else null,
                )
                if (row < 5) Spacer(Modifier.height(4.dp))
            }

            if (uiState.isGameOver) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = vm::newGame,
                    colors  = ButtonDefaults.buttonColors(containerColor = TealCta),
                    shape   = RoundedCornerShape(14.dp),
                ) {
                    Text("New Word", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        WordleKeyboard(
            keyStates = uiState.keyStates,
            onKey     = vm::onKey,
            onDelete  = vm::onDelete,
            onEnter   = vm::onEnter,
        )
    }
}

// ── Board row — shake reads shakeAnim.value inside graphicsLayer (no recompose) ─
@Composable
private fun WordleRow(
    rowIndex:  Int,
    uiState:   HealthWordleUiState,
    flipAnims: List<List<Animatable<Float, AnimationVector1D>>>,
    shakeAnim: Animatable<Float, AnimationVector1D>?,
) {
    Row(
        modifier = Modifier.graphicsLayer {
            // State read inside graphicsLayer → layer-only invalidation, no recomposition
            if (shakeAnim != null) translationX = shakeAnim.value
        },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (col in 0..4) {
            val (letter, state) = when {
                rowIndex < uiState.guesses.size -> {
                    val g = uiState.guesses[rowIndex]
                    g.letters[col] to g.states[col]
                }
                rowIndex == uiState.guesses.size && col < uiState.currentGuess.length ->
                    uiState.currentGuess[col] to TileState.FILLED
                else -> ' ' to TileState.EMPTY
            }
            WordleTileView(
                letter   = letter,
                state    = state,
                flipAnim = flipAnims[rowIndex][col],
            )
        }
    }
}

// ── Tile — reads flipAnim.value here, so only this composable recomposes per frame ─
@Composable
private fun WordleTileView(
    letter:   Char,
    state:    TileState,
    flipAnim: Animatable<Float, AnimationVector1D>,
) {
    val prog  = flipAnim.value  // state read → scopes recomposition to this composable
    val rotX  = if (prog < 0.5f) prog * 180f else (1f - prog) * 180f
    val showResult = prog >= 0.5f

    val bgColor = when {
        showResult && state == TileState.CORRECT -> SeafoamGreen
        showResult && state == TileState.PRESENT -> SunbeamYellow
        showResult && state == TileState.ABSENT  -> AbsentGray
        else -> Color.White
    }
    val borderColor = if (state == TileState.EMPTY) TileEmptyBorder else TileFilledBorder
    val textColor = if (showResult && state in listOf(TileState.CORRECT, TileState.PRESENT, TileState.ABSENT)) {
        Color.White
    } else TileText

    Box(
        modifier = Modifier
            .size(52.dp)
            .graphicsLayer { rotationX = rotX }
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(bgColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (letter != ' ') {
            Text(
                text       = letter.uppercaseChar().toString(),
                color      = textColor,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ── Custom keyboard — themed, no system IME ────────────────────────────────────
private val KB_ROWS = listOf(
    listOf("Q","W","E","R","T","Y","U","I","O","P"),
    listOf("A","S","D","F","G","H","J","K","L"),
    listOf("ENTER","Z","X","C","V","B","N","M","⌫"),
)

@Composable
private fun WordleKeyboard(
    keyStates: Map<Char, TileState>,
    onKey:     (Char) -> Unit,
    onDelete:  () -> Unit,
    onEnter:   () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0F2F1))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalArrangement   = Arrangement.spacedBy(6.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
    ) {
        KB_ROWS.forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                row.forEach { key ->
                    val isWide   = key == "ENTER" || key == "⌫"
                    val charKey  = if (key.length == 1) key[0].lowercaseChar() else null
                    val keyState = charKey?.let { keyStates[it] }

                    val bg   = when (keyState) {
                        TileState.CORRECT -> SeafoamGreen
                        TileState.PRESENT -> SunbeamYellow
                        TileState.ABSENT  -> AbsentGray
                        else              -> KeyBg
                    }
                    val fg = when (keyState) {
                        TileState.CORRECT, TileState.ABSENT -> Color.White
                        TileState.PRESENT                   -> Color(0xFF3E2723)
                        else                                -> KeyText
                    }

                    Box(
                        modifier = Modifier
                            .weight(if (isWide) 1.5f else 1f)
                            .height(46.dp)
                            .background(bg, RoundedCornerShape(8.dp))
                            .clickable {
                                when (key) {
                                    "ENTER" -> onEnter()
                                    "⌫"    -> onDelete()
                                    else    -> onKey(key[0])
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = key,
                            color      = fg,
                            fontSize   = if (isWide) 11.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign  = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

// ── Word bank (ported from HTML) ───────────────────────────────────────────────
internal val WORDLE_WORDS = listOf(
    "nerve","brain","spine","axons","cells","gland","fiber","motor","sense","sleep",
    "focus","acute","dizzy","spasm","rigid","tonic","flare","input","sight","optic",
    "pulse","tract","lucid","edema","toxin","graft","serum","lymph","genes","viral",
    "gamma","alpha","delta","theta","vagus","ulnar","scalp","skull","lobes","sinus",
    "glial","palsy","decay","apnea","tumor","cysts","nodes","cramp","pains","shock",
    "onset","tests","scans","image","rehab","brace","seize","falls","blurs","stiff",
    "drain","fever","blood","organ","patch","trial","doses","shots","pills","treat",
    "sweat","flush","heart","lungs","liver","colon","renal","femur","ankle","elbow",
    "wrist","thigh","heals","clean","teeth","mouth","nails","tummy","belly","bowel",
    "sugar","child","women","aging","vital","rates","check","nurse","donor","salve",
    "herbs","juice","diets","plant","steam","grain","dairy","fruit","meats","snack",
    "meals","bread","pasta","wheat","beans","seeds","olive","lemon","honey","cocoa",
    "mango","berry","apple","peach","grape","melon","basil","thyme","clove","onion",
    "salad","broth","roast","grill","blend","toast","walks","cycle","swims","dance",
    "lifts","plank","squat","lunge","press","curls","pulls","gains","toned","agile",
    "power","speed","steps","miles","track","climb","sauna","baths","relax","pause",
    "water","fresh","sunny","shade","float","waves","shore","ocean","coral","sandy",
    "winds","clear","green","earth","aroma","peace","whole","alert","awake","aware",
    "alive","mends","bonds","goals","hardy","happy","jolly","bliss","smile","laugh",
    "trust","faith","grace","vigor","brave","proud","noble","gleam","shine","light",
    "spark","surge","boost","soars","peaks","bloom","grown","tends","roots","stems",
    "petal","tides","pools","dunes","coves","haven","oasis","lodge","cabin","porch",
    "trail","creek","brook","marsh","grove","yield","craft","built","forge",
)
