package com.sterlingsworld.feature.game.suites.relaxation

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val SolBg         = Color(0xFFFFF8E1)
private val SolTeal       = Color(0xFF00897B)
private val SolTealLight  = Color(0xFFE0F2F1)
private val SolCardBg     = Color.White
private val SolRedSuit    = Color(0xFFD32F2F)
private val SolBlackSuit  = Color(0xFF1A2A1F)
private val SolSelectedBg = Color(0xFFFFF9C4)
private val SolEmptyBg    = Color(0x22004D40)

// ── Data ───────────────────────────────────────────────────────────────────────
private val SUITS = listOf("♠", "♥", "♦", "♣")
private val RANKS = listOf("A","2","3","4","5","6","7","8","9","10","J","Q","K")

data class SolitaireCard(
    val suit: String, val rank: String, val value: Int,
    val isRed: Boolean, val faceUp: Boolean = false,
)

sealed class SolSource {
    object Waste : SolSource()
    data class Tableau(val col: Int, val cardIdx: Int) : SolSource()
    data class Foundation(val pile: Int) : SolSource()
}

data class SolitaireUiState(
    val stock:      List<SolitaireCard>       = emptyList(),
    val waste:      List<SolitaireCard>       = emptyList(),
    val foundation: List<List<SolitaireCard>> = List(4) { emptyList() },
    val tableau:    List<List<SolitaireCard>> = List(7) { emptyList() },
    val selected:   SolSource?               = null,
    val isWon:      Boolean                  = false,
    val message:    String                   = "",
)

// ── ViewModel ──────────────────────────────────────────────────────────────────
class IslandSolitaireViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SolitaireUiState())
    val uiState: StateFlow<SolitaireUiState> = _uiState.asStateFlow()

    init { newGame() }

    fun newGame() {
        val deck = buildDeck().shuffled().toMutableList()
        val tableau = Array(7) { mutableListOf<SolitaireCard>() }
        var idx = 0
        for (col in 0..6) {
            for (row in 0..col) {
                tableau[col].add(deck[idx++].copy(faceUp = row == col))
            }
        }
        val stock = deck.drop(idx).map { it.copy(faceUp = false) }
        _uiState.value = SolitaireUiState(
            stock     = stock,
            tableau   = tableau.map { it.toList() },
        )
    }

    fun drawCard() {
        val s = _uiState.value
        if (s.stock.isEmpty()) {
            // Recycle waste back to stock (face down)
            _uiState.update { it.copy(stock = it.waste.reversed().map { c -> c.copy(faceUp = false) }, waste = emptyList(), selected = null) }
        } else {
            val drawn = s.stock.last().copy(faceUp = true)
            _uiState.update { it.copy(stock = it.stock.dropLast(1), waste = it.waste + drawn, selected = null) }
        }
    }

    fun tapCard(source: SolSource) {
        val s = _uiState.value
        // Deselect if tapping the already-selected source
        if (s.selected == source) { _uiState.update { it.copy(selected = null) }; return }

        if (s.selected != null) {
            // Try to place selected cards onto this source location
            if (tryPlaceOn(source)) return
        }

        // Try auto-move to foundation; if fails, select the card
        val cards = getCards(source)
        if (cards.isEmpty() || !cards.first().faceUp) return
        if (cards.size == 1 && tryAutoMoveToFoundation(source)) return

        _uiState.update { it.copy(selected = source, message = "") }
    }

    fun tapFoundation(pile: Int) {
        val s = _uiState.value
        if (s.selected == null) return
        tryPlaceOn(SolSource.Foundation(pile))
    }

    fun tapEmptyTableau(col: Int) {
        val s = _uiState.value
        if (s.selected == null) return
        tryPlaceOn(SolSource.Tableau(col, 0))
    }

    private fun tryAutoMoveToFoundation(source: SolSource): Boolean {
        val cards = getCards(source)
        val card  = cards.firstOrNull() ?: return false
        for (pile in 0..3) {
            if (canPlaceOnFoundation(card, pile)) {
                doPlace(source, SolSource.Foundation(pile))
                return true
            }
        }
        return false
    }

    private fun tryPlaceOn(target: SolSource): Boolean {
        val s = _uiState.value
        val source = s.selected ?: return false
        val cards  = getCards(source)
        if (cards.isEmpty()) return false

        val valid = when (target) {
            is SolSource.Foundation -> cards.size == 1 && canPlaceOnFoundation(cards[0], target.pile)
            is SolSource.Tableau    -> {
                val col = target.col
                canPlaceOnTableau(cards[0], col)
            }
            SolSource.Waste -> false
        }
        if (valid) { doPlace(source, target); return true }
        _uiState.update { it.copy(selected = null) }
        return false
    }

    private fun doPlace(source: SolSource, target: SolSource) {
        var s = _uiState.value
        val cards = getCards(source)

        // Remove from source
        s = when (source) {
            SolSource.Waste           -> s.copy(waste = s.waste.dropLast(1))
            is SolSource.Tableau      -> {
                val newTab = s.tableau.mapIndexed { i, pile ->
                    if (i == source.col) pile.subList(0, source.cardIdx) else pile
                }
                // Flip new top card of source column if face-down
                val flipped = newTab.mapIndexed { i, pile ->
                    if (i == source.col && pile.isNotEmpty() && !pile.last().faceUp)
                        pile.dropLast(1) + pile.last().copy(faceUp = true)
                    else pile
                }
                s.copy(tableau = flipped)
            }
            is SolSource.Foundation   -> s.copy(foundation = s.foundation.mapIndexed { i, p ->
                if (i == source.pile) p.dropLast(1) else p
            })
        }

        // Add to target
        s = when (target) {
            is SolSource.Foundation -> s.copy(foundation = s.foundation.mapIndexed { i, p ->
                if (i == target.pile) p + cards else p
            })
            is SolSource.Tableau -> s.copy(tableau = s.tableau.mapIndexed { i, p ->
                if (i == target.col) p + cards else p
            })
            SolSource.Waste -> s
        }

        // Check win
        val won = s.foundation.sumOf { it.size } == 52
        s = s.copy(selected = null, isWon = won, message = if (won) "You won! Aloha!" else "")
        _uiState.value = s
    }

    private fun canPlaceOnFoundation(card: SolitaireCard, pile: Int): Boolean {
        val fp = _uiState.value.foundation[pile]
        if (fp.isEmpty()) return card.rank == "A"
        val top = fp.last()
        return top.suit == card.suit && card.value == top.value + 1
    }

    private fun canPlaceOnTableau(card: SolitaireCard, col: Int): Boolean {
        val tp = _uiState.value.tableau[col]
        if (tp.isEmpty()) return card.rank == "K"
        val top = tp.last()
        if (!top.faceUp) return false
        return card.isRed != top.isRed && card.value == top.value - 1
    }

    private fun getCards(source: SolSource): List<SolitaireCard> {
        val s = _uiState.value
        return when (source) {
            SolSource.Waste           -> s.waste.lastOrNull()?.let { listOf(it) } ?: emptyList()
            is SolSource.Tableau      -> if (source.cardIdx < s.tableau[source.col].size)
                                             s.tableau[source.col].subList(source.cardIdx, s.tableau[source.col].size)
                                         else emptyList()
            is SolSource.Foundation   -> s.foundation[source.pile].lastOrNull()?.let { listOf(it) } ?: emptyList()
        }
    }

    private fun buildDeck(): List<SolitaireCard> = SUITS.flatMap { suit ->
        RANKS.mapIndexed { idx, rank ->
            SolitaireCard(suit = suit, rank = rank, value = idx, isRed = suit == "♥" || suit == "♦")
        }
    }
}

// ── Root Composable ────────────────────────────────────────────────────────────
@Composable
fun IslandSolitaireGame(vm: IslandSolitaireViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SolBg),
    ) {
        AnimatedVisibility(
            visible = uiState.message.isNotEmpty(),
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (uiState.isWon) SolTeal else Color(0xFF78909C))
                    .padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(uiState.message, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
        ) {
            val cardW: Dp = (maxWidth - 24.dp) / 7f  // 7 cols + 6 gaps of 4dp
            val cardH: Dp = cardW * 1.4f
            val overlapDown = cardW * 0.35f
            val overlapUp   = cardW * 0.45f

            Column {
                // Header: Stock | Waste | --- | Foundation ×4
                SolitaireHeader(
                    uiState  = uiState,
                    cardW    = cardW,
                    cardH    = cardH,
                    onDraw   = vm::drawCard,
                    onTapWaste = { vm.tapCard(SolSource.Waste) },
                    onTapFoundation = { pile -> vm.tapCard(SolSource.Foundation(pile)) },
                )

                Spacer(Modifier.height(8.dp))

                // Tableau — scrollable for deep columns
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            if (col > 0) Spacer(Modifier.width(4.dp))
                            TableauColumn(
                                pile        = uiState.tableau[col],
                                colIdx      = col,
                                selected    = uiState.selected,
                                cardW       = cardW,
                                cardH       = cardH,
                                overlapDown = overlapDown,
                                overlapUp   = overlapUp,
                                onTapCard   = { idx -> vm.tapCard(SolSource.Tableau(col, idx)) },
                                onTapEmpty  = { vm.tapEmptyTableau(col) },
                                modifier    = Modifier.weight(1f),
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    // New Game button
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SolTeal)
                                .clickable(onClick = vm::newGame)
                                .padding(horizontal = 28.dp, vertical = 10.dp),
                        ) {
                            Text("New Game", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SolitaireHeader(
    uiState: SolitaireUiState, cardW: Dp, cardH: Dp,
    onDraw: () -> Unit,
    onTapWaste: () -> Unit,
    onTapFoundation: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // Stock
        Box(
            modifier = Modifier
                .width(cardW).height(cardH)
                .clip(RoundedCornerShape(6.dp))
                .background(if (uiState.stock.isNotEmpty()) SolTeal else SolEmptyBg)
                .border(1.dp, SolTeal, RoundedCornerShape(6.dp))
                .clickable(onClick = onDraw),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = if (uiState.stock.isNotEmpty()) "🌴" else "↻",
                fontSize = 18.sp,
                color    = if (uiState.stock.isNotEmpty()) Color.White else SolTeal,
            )
        }

        // Waste
        Box(
            modifier = Modifier
                .width(cardW).height(cardH)
                .clip(RoundedCornerShape(6.dp))
                .background(if (uiState.waste.isNotEmpty()) SolCardBg else SolEmptyBg)
                .border(1.dp, SolTeal, RoundedCornerShape(6.dp))
                .clickable(onClick = onTapWaste),
            contentAlignment = Alignment.Center,
        ) {
            uiState.waste.lastOrNull()?.let { card ->
                CardFace(card = card, isSelected = uiState.selected == SolSource.Waste, cardW = cardW)
            }
        }

        Spacer(Modifier.weight(1f))

        // 4 Foundations
        SUITS.forEachIndexed { idx, suit ->
            val pile  = uiState.foundation[idx]
            val top   = pile.lastOrNull()
            val isRed = suit == "♥" || suit == "♦"
            Box(
                modifier = Modifier
                    .width(cardW).height(cardH)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (top != null) SolCardBg else SolEmptyBg)
                    .border(1.dp, SolTeal, RoundedCornerShape(6.dp))
                    .clickable { onTapFoundation(idx) },
                contentAlignment = Alignment.Center,
            ) {
                if (top != null) {
                    CardFace(card = top, isSelected = false, cardW = cardW)
                } else {
                    Text(suit, fontSize = (cardW.value * 0.4f).sp, color = if (isRed) SolRedSuit else SolBlackSuit)
                }
            }
        }
    }
}

@Composable
private fun TableauColumn(
    pile: List<SolitaireCard>, colIdx: Int, selected: SolSource?,
    cardW: Dp, cardH: Dp, overlapDown: Dp, overlapUp: Dp,
    onTapCard: (Int) -> Unit, onTapEmpty: () -> Unit,
    modifier: Modifier,
) {
    fun offsetFor(idx: Int): Dp {
        var off = 0.dp
        for (i in 0 until idx) off += if (pile[i].faceUp) overlapUp else overlapDown
        return off
    }

    val totalHeight = if (pile.isEmpty()) cardH else offsetFor(pile.lastIndex) + cardH

    Box(modifier = modifier.height(totalHeight.coerceAtLeast(cardH))) {
        if (pile.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(cardW).height(cardH)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, SolTeal.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .background(SolEmptyBg)
                    .clickable(onClick = onTapEmpty),
                contentAlignment = Alignment.Center,
            ) {
                Text("K", fontSize = 14.sp, color = SolTeal.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
            }
        } else {
            pile.forEachIndexed { idx, card ->
                val isSelected = selected is SolSource.Tableau
                    && (selected as SolSource.Tableau).col == colIdx
                    && idx >= (selected as SolSource.Tableau).cardIdx
                Box(
                    modifier = Modifier
                        .width(cardW).height(cardH)
                        .offset(y = offsetFor(idx))
                        .clip(RoundedCornerShape(6.dp))
                        .then(if (card.faceUp) Modifier.clickable { onTapCard(idx) } else Modifier),
                ) {
                    if (card.faceUp) {
                        CardFace(card = card, isSelected = isSelected, cardW = cardW)
                    } else {
                        CardBack(cardW = cardW)
                    }
                }
            }
        }
    }
}

@Composable
private fun CardFace(card: SolitaireCard, isSelected: Boolean, cardW: Dp) {
    val suitColor = if (card.isRed) SolRedSuit else SolBlackSuit
    val rankSz    = (cardW.value * 0.28f).sp
    val suitSz    = (cardW.value * 0.32f).sp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSelected) SolSelectedBg else SolCardBg)
            .border(1.dp, if (isSelected) Color(0xFFFFB300) else SolTeal.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
    ) {
        Text(
            text       = "${card.rank}${card.suit}",
            color      = suitColor,
            fontSize   = rankSz,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.align(Alignment.TopStart).padding(2.dp),
            lineHeight = rankSz,
        )
        Text(
            text       = card.suit,
            color      = suitColor,
            fontSize   = suitSz,
            modifier   = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun CardBack(cardW: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SolTeal, RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFF00695C), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text("🌴", fontSize = (cardW.value * 0.35f).sp)
    }
}
