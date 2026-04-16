package com.sterlingsworld.feature.game.games.cognitivecreamery

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class CreameryPhase { STUDY, INPUT, ROUND_RESULT, RUN_COMPLETE }

data class CognitiveCreameryUiState(
    val currentRound: Int = 0,
    val totalRounds: Int = ROUND_LENGTHS.size,
    val targetSequence: List<String> = emptyList(),
    val playerSequence: List<String> = emptyList(),
    val availableTokens: List<String> = emptyList(),
    val phase: CreameryPhase = CreameryPhase.STUDY,
    val lastRoundCorrect: Boolean = false,
    val correctRounds: Int = 0,
)

internal val ALL_FLAVORS = listOf("Berry", "Vanilla", "Mint", "Cocoa", "Honey")
internal val ROUND_LENGTHS = listOf(3, 4, 5)

class CognitiveCreameryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(buildRound(0, correctRounds = 0))
    val uiState: StateFlow<CognitiveCreameryUiState> = _uiState.asStateFlow()

    /** Player confirms they have memorized the sequence and is ready to input. */
    fun onReady() {
        _uiState.update { state ->
            if (state.phase != CreameryPhase.STUDY) return@update state
            state.copy(
                phase = CreameryPhase.INPUT,
                playerSequence = emptyList(),
                availableTokens = ALL_FLAVORS.shuffled(),
            )
        }
    }

    /** Player taps a token to append it to their sequence. */
    fun onTokenTapped(token: String) {
        val state = _uiState.value
        if (state.phase != CreameryPhase.INPUT) return
        if (token !in state.availableTokens) return
        if (state.playerSequence.size >= state.targetSequence.size) return

        _uiState.update {
            it.copy(
                playerSequence = it.playerSequence + token,
                availableTokens = it.availableTokens - token,
            )
        }
    }

    /** Remove the last tapped token and return it to available. */
    fun onUndo() {
        val state = _uiState.value
        if (state.phase != CreameryPhase.INPUT) return
        val last = state.playerSequence.lastOrNull() ?: return
        _uiState.update {
            it.copy(
                playerSequence = it.playerSequence.dropLast(1),
                availableTokens = it.availableTokens + last,
            )
        }
    }

    /** Check the player's sequence against the target. Only callable when sequence is full. */
    fun onCheck() {
        val state = _uiState.value
        if (state.phase != CreameryPhase.INPUT) return
        if (state.playerSequence.size != state.targetSequence.size) return

        val correct = state.playerSequence == state.targetSequence
        val newCorrect = state.correctRounds + if (correct) 1 else 0
        val isLastRound = state.currentRound == ROUND_LENGTHS.lastIndex

        _uiState.update {
            it.copy(
                phase = if (isLastRound) CreameryPhase.RUN_COMPLETE else CreameryPhase.ROUND_RESULT,
                lastRoundCorrect = correct,
                correctRounds = newCorrect,
            )
        }
    }

    /** Advance to the next round after viewing the round result. */
    fun onNextRound() {
        val state = _uiState.value
        if (state.phase != CreameryPhase.ROUND_RESULT) return
        _uiState.value = buildRound(state.currentRound + 1, state.correctRounds)
    }

    fun buildResult(): GameResult {
        val state = _uiState.value
        val stars = when (state.correctRounds) {
            ROUND_LENGTHS.size -> 3
            ROUND_LENGTHS.size - 1 -> 2
            else -> 1
        }
        return GameResult(
            completed = true,
            score = state.correctRounds,
            stars = stars,
            durationMs = 0L,
            perfect = state.correctRounds == ROUND_LENGTHS.size,
        )
    }

    companion object {
        fun buildRound(roundIndex: Int, correctRounds: Int): CognitiveCreameryUiState {
            val length = ROUND_LENGTHS[roundIndex]
            val sequence = ALL_FLAVORS.shuffled().take(length)
            return CognitiveCreameryUiState(
                currentRound = roundIndex,
                totalRounds = ROUND_LENGTHS.size,
                targetSequence = sequence,
                playerSequence = emptyList(),
                availableTokens = emptyList(),
                phase = CreameryPhase.STUDY,
                lastRoundCorrect = false,
                correctRounds = correctRounds,
            )
        }
    }
}
