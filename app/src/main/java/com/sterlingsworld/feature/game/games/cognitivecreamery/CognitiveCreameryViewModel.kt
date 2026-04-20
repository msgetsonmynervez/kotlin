package com.sterlingsworld.feature.game.games.cognitivecreamery

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

const val MAX_FATIGUE = 5

enum class CreameryActivity(
    val label: String,
    val badge: String,
    val description: String,
) {
    PARLOR(
        label = "Midnight Parlor",
        badge = "PARLOR",
        description = "Choose a short focus activity and keep the session calm.",
    ),
    SEQUENCE(
        label = "Flavor Sequence",
        badge = "SEQ",
        description = "Memorize the flavor order, then rebuild it from memory.",
    ),
    CLARITY(
        label = "Cognitive Clarity",
        badge = "CLR",
        description = "Find the three words that share the hidden semantic theme.",
    ),
}

enum class CreameryPhase { STUDY, INPUT, ROUND_RESULT, RUN_COMPLETE }

data class SequenceUiState(
    val currentRound: Int = 0,
    val totalRounds: Int = ROUND_LENGTHS.size,
    val targetSequence: List<String> = emptyList(),
    val playerSequence: List<String> = emptyList(),
    val availableTokens: List<String> = emptyList(),
    val phase: CreameryPhase = CreameryPhase.STUDY,
    val lastRoundCorrect: Boolean = false,
    val correctRounds: Int = 0,
)

data class ClarityGroup(val theme: String, val members: List<String>)

data class ClarityUiState(
    val currentGroup: ClarityGroup? = null,
    val gridWords: List<String> = emptyList(),
    val foundWords: Set<String> = emptySet(),
    val wrongWords: Set<String> = emptySet(),
    val roundComplete: Boolean = false,
    val roundsCompleted: Int = 0,
)

data class CognitiveCreameryUiState(
    val currentActivity: CreameryActivity = CreameryActivity.PARLOR,
    val flavorOfTheDay: CreameryActivity = CreameryActivity.SEQUENCE,
    val fatigueLevel: Int = 0,
    val isBrainFreeze: Boolean = false,
    val sessionScore: Int = 0,
    val sequence: SequenceUiState = SequenceUiState(),
    val clarity: ClarityUiState = ClarityUiState(),
)

internal val ALL_FLAVORS = listOf("Berry", "Vanilla", "Mint", "Cocoa", "Honey")
internal val ROUND_LENGTHS = listOf(3, 4, 5)

internal val CLARITY_GROUPS = listOf(
    ClarityGroup("Library", listOf("Book", "Quiet", "Shelf")),
    ClarityGroup("Ocean", listOf("Wave", "Salt", "Coral")),
    ClarityGroup("Kitchen", listOf("Spoon", "Steam", "Recipe")),
    ClarityGroup("Sleep", listOf("Pillow", "Dream", "Dark")),
    ClarityGroup("Forest", listOf("Moss", "Bark", "Canopy")),
    ClarityGroup("Hospital", listOf("Nurse", "Chart", "Beep")),
    ClarityGroup("Music", listOf("Chord", "Beat", "Lyric")),
    ClarityGroup("City", listOf("Traffic", "Siren", "Crowd")),
    ClarityGroup("Garden", listOf("Soil", "Petal", "Hose")),
    ClarityGroup("Bakery", listOf("Flour", "Yeast", "Glaze")),
)

private val ALL_DISTRACTOR_WORDS = listOf(
    "Cloud", "Chair", "Flame", "Sand", "Clock", "Coin",
    "Leaf", "String", "Stone", "Glass", "Paint", "Rope",
    "Smoke", "Tower", "Blade", "Frost", "Cable", "Dust",
)

class CognitiveCreameryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<CognitiveCreameryUiState> = _uiState.asStateFlow()

    fun navigateTo(activity: CreameryActivity) {
        val state = _uiState.value
        if (state.isBrainFreeze) return
        _uiState.update { it.copy(currentActivity = activity) }
    }

    fun backToParlor() {
        _uiState.update { it.copy(currentActivity = CreameryActivity.PARLOR) }
    }

    fun resetBrainFreeze() {
        _uiState.update {
            it.copy(
                currentActivity = CreameryActivity.PARLOR,
                fatigueLevel = 0,
                isBrainFreeze = false,
            )
        }
    }

    fun resetSession() {
        _uiState.value = buildInitialState()
    }

    fun onReady() {
        val state = _uiState.value
        if (state.currentActivity != CreameryActivity.SEQUENCE) return
        if (state.sequence.phase != CreameryPhase.STUDY) return

        _uiState.update {
            it.copy(
                sequence = it.sequence.copy(
                    phase = CreameryPhase.INPUT,
                    playerSequence = emptyList(),
                    availableTokens = ALL_FLAVORS.shuffled(),
                ),
            )
        }
    }

    fun onTokenTapped(token: String) {
        val state = _uiState.value
        val sequence = state.sequence
        if (state.currentActivity != CreameryActivity.SEQUENCE) return
        if (sequence.phase != CreameryPhase.INPUT) return
        if (token !in sequence.availableTokens) return
        if (sequence.playerSequence.size >= sequence.targetSequence.size) return

        _uiState.update {
            val updated = it.sequence
            it.copy(
                sequence = updated.copy(
                    playerSequence = updated.playerSequence + token,
                    availableTokens = updated.availableTokens - token,
                ),
            )
        }
    }

    fun onUndo() {
        val state = _uiState.value
        val sequence = state.sequence
        if (state.currentActivity != CreameryActivity.SEQUENCE) return
        if (sequence.phase != CreameryPhase.INPUT) return
        val last = sequence.playerSequence.lastOrNull() ?: return

        _uiState.update {
            val updated = it.sequence
            it.copy(
                sequence = updated.copy(
                    playerSequence = updated.playerSequence.dropLast(1),
                    availableTokens = updated.availableTokens + last,
                ),
            )
        }
    }

    fun onCheck() {
        val state = _uiState.value
        val sequence = state.sequence
        if (state.currentActivity != CreameryActivity.SEQUENCE) return
        if (sequence.phase != CreameryPhase.INPUT) return
        if (sequence.playerSequence.size != sequence.targetSequence.size) return

        val correct = sequence.playerSequence == sequence.targetSequence
        val isLastRound = sequence.currentRound == ROUND_LENGTHS.lastIndex
        val fatigue = if (correct) state.fatigueLevel else (state.fatigueLevel + 1).coerceAtMost(MAX_FATIGUE)
        val nextCorrectRounds = sequence.correctRounds + if (correct) 1 else 0
        val nextScore = state.sessionScore + if (correct) 1 else 0

        _uiState.update {
            it.copy(
                fatigueLevel = fatigue,
                isBrainFreeze = fatigue >= MAX_FATIGUE,
                sessionScore = nextScore,
                sequence = it.sequence.copy(
                    phase = if (isLastRound) CreameryPhase.RUN_COMPLETE else CreameryPhase.ROUND_RESULT,
                    lastRoundCorrect = correct,
                    correctRounds = nextCorrectRounds,
                ),
            )
        }
    }

    fun onNextRound() {
        val state = _uiState.value
        val sequence = state.sequence
        if (state.currentActivity != CreameryActivity.SEQUENCE) return
        if (sequence.phase != CreameryPhase.ROUND_RESULT) return

        _uiState.update {
            it.copy(sequence = buildSequenceRound(sequence.currentRound + 1, sequence.correctRounds))
        }
    }

    fun onClarityWordTapped(word: String) {
        val state = _uiState.value
        if (state.currentActivity != CreameryActivity.CLARITY) return
        val clarity = state.clarity
        val group = clarity.currentGroup ?: return
        if (clarity.roundComplete) return
        if (word in clarity.foundWords || word in clarity.wrongWords) return

        if (word in group.members) {
            val newFound = clarity.foundWords + word
            val roundComplete = newFound.size == group.members.size
            _uiState.update {
                it.copy(
                    clarity = it.clarity.copy(
                        foundWords = newFound,
                        roundComplete = roundComplete,
                        roundsCompleted = if (roundComplete) it.clarity.roundsCompleted + 1 else it.clarity.roundsCompleted,
                    ),
                    sessionScore = if (roundComplete) it.sessionScore + 1 else it.sessionScore,
                )
            }
        } else {
            val newFatigue = (state.fatigueLevel + 1).coerceAtMost(MAX_FATIGUE)
            _uiState.update {
                it.copy(
                    clarity = it.clarity.copy(wrongWords = it.clarity.wrongWords + word),
                    fatigueLevel = newFatigue,
                    isBrainFreeze = newFatigue >= MAX_FATIGUE,
                )
            }
        }
    }

    fun onClarityNextRound() {
        val state = _uiState.value
        if (state.currentActivity != CreameryActivity.CLARITY) return
        _uiState.update {
            it.copy(clarity = buildClarityRound(it.clarity.roundsCompleted))
        }
    }

    fun buildResult(): GameResult {
        val state = _uiState.value
        val perfectSession = !state.isBrainFreeze && state.sessionScore >= ROUND_LENGTHS.size
        val stars = when {
            state.isBrainFreeze -> 1
            perfectSession -> 3
            state.sessionScore >= 2 -> 2
            else -> 1
        }
        return GameResult(
            completed = true,
            score = state.sessionScore,
            stars = stars,
            durationMs = 0L,
            perfect = perfectSession,
        )
    }

    companion object {
        fun buildSequenceRound(roundIndex: Int = 0, correctRounds: Int = 0): SequenceUiState {
            val length = ROUND_LENGTHS[roundIndex]
            return SequenceUiState(
                currentRound = roundIndex,
                totalRounds = ROUND_LENGTHS.size,
                targetSequence = ALL_FLAVORS.shuffled().take(length),
                playerSequence = emptyList(),
                availableTokens = emptyList(),
                phase = CreameryPhase.STUDY,
                lastRoundCorrect = false,
                correctRounds = correctRounds,
            )
        }

        fun buildClarityRound(completedCount: Int = 0): ClarityUiState {
            val group = CLARITY_GROUPS.shuffled().first()
            val distractors = ALL_DISTRACTOR_WORDS
                .filter { it !in group.members }
                .shuffled()
                .take(3)

            return ClarityUiState(
                currentGroup = group,
                gridWords = (group.members + distractors).shuffled(),
                foundWords = emptySet(),
                wrongWords = emptySet(),
                roundComplete = false,
                roundsCompleted = completedCount,
            )
        }

        private fun buildInitialState(): CognitiveCreameryUiState {
            val playable = listOf(CreameryActivity.SEQUENCE, CreameryActivity.CLARITY)
            return CognitiveCreameryUiState(
                currentActivity = CreameryActivity.PARLOR,
                flavorOfTheDay = playable.random(),
                fatigueLevel = 0,
                isBrainFreeze = false,
                sessionScore = 0,
                sequence = buildSequenceRound(),
                clarity = buildClarityRound(),
            )
        }
    }
}
