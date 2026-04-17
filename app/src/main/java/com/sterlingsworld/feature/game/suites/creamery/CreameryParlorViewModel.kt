package com.sterlingsworld.feature.game.suites.creamery

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

const val MAX_FATIGUE = 5

enum class CreameryActivity(
    val label: String,
    val emoji: String,
    val description: String,
) {
    PARLOR("The Parlor", "🍦", ""),
    SEQUENCE("Flavor Sequence", "🧠", "Memorize and rebuild the ice cream flavor order"),
    CLARITY("Cognitive Clarity", "🔍", "Find the three words that share a hidden theme"),
}

data class ClarityGroup(val theme: String, val members: List<String>)

val CLARITY_GROUPS = listOf(
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

data class ClarityUiState(
    val currentGroup: ClarityGroup? = null,
    val gridWords: List<String> = emptyList(),
    val foundWords: Set<String> = emptySet(),
    val wrongWords: Set<String> = emptySet(),
    val roundComplete: Boolean = false,
    val roundsCompleted: Int = 0,
)

data class CreameryParlorUiState(
    val currentActivity: CreameryActivity = CreameryActivity.PARLOR,
    val fatigueLevel: Int = 0,
    val isBrainFreeze: Boolean = false,
    val flavorOfTheDay: CreameryActivity = CreameryActivity.SEQUENCE,
    val sessionScore: Int = 0,
    val clarity: ClarityUiState = ClarityUiState(),
)

class CreameryParlorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<CreameryParlorUiState> = _uiState.asStateFlow()

    private fun buildInitialState(): CreameryParlorUiState {
        val playable = listOf(CreameryActivity.SEQUENCE, CreameryActivity.CLARITY)
        return CreameryParlorUiState(
            flavorOfTheDay = playable.random(),
            clarity = buildClarityRound(),
        )
    }

    fun navigateTo(activity: CreameryActivity) {
        if (_uiState.value.isBrainFreeze) return
        _uiState.update { it.copy(currentActivity = activity) }
    }

    fun backToParlor() {
        _uiState.update { it.copy(currentActivity = CreameryActivity.PARLOR) }
    }

    // ── Clarity game ──────────────────────────────────────────────────────

    fun onClarityWordTapped(word: String) {
        val state = _uiState.value
        val clarity = state.clarity
        val group = clarity.currentGroup ?: return
        if (clarity.roundComplete) return
        if (word in clarity.foundWords || word in clarity.wrongWords) return

        if (word in group.members) {
            val newFound = clarity.foundWords + word
            val roundComplete = newFound.size == group.members.size
            _uiState.update {
                it.copy(
                    clarity = clarity.copy(
                        foundWords = newFound,
                        roundComplete = roundComplete,
                        roundsCompleted = if (roundComplete) clarity.roundsCompleted + 1 else clarity.roundsCompleted,
                    ),
                    sessionScore = if (roundComplete) state.sessionScore + 1 else state.sessionScore,
                )
            }
        } else {
            val newFatigue = (state.fatigueLevel + 1).coerceAtMost(MAX_FATIGUE)
            _uiState.update {
                it.copy(
                    clarity = clarity.copy(wrongWords = clarity.wrongWords + word),
                    fatigueLevel = newFatigue,
                    isBrainFreeze = newFatigue >= MAX_FATIGUE,
                )
            }
        }
    }

    fun onClarityNextRound() {
        _uiState.update { it.copy(clarity = buildClarityRound(it.clarity.roundsCompleted)) }
    }

    // ── Brain Freeze ──────────────────────────────────────────────────────

    fun resetBrainFreeze() {
        _uiState.update {
            it.copy(
                fatigueLevel = 0,
                isBrainFreeze = false,
                currentActivity = CreameryActivity.PARLOR,
            )
        }
    }

    companion object {
        fun buildClarityRound(completedCount: Int = 0): ClarityUiState {
            val group = CLARITY_GROUPS.shuffled().first()
            val distractors = ALL_DISTRACTOR_WORDS
                .filter { it !in group.members }
                .shuffled()
                .take(3)
            val grid = (group.members + distractors).shuffled()
            return ClarityUiState(
                currentGroup = group,
                gridWords = grid,
                foundWords = emptySet(),
                wrongWords = emptySet(),
                roundComplete = false,
                roundsCompleted = completedCount,
            )
        }
    }
}
