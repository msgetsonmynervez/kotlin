package com.sterlingsworld.feature.game.games.ghost

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GhostChoice(
    val code: String,
    val correct: Boolean,
    val feedback: String,
)

data class GhostLevel(
    val narrator: String,
    val characterLine: String,
    val prompt: String,
    val choices: List<GhostChoice>,
)

data class GhostFeedback(
    val correct: Boolean,
    val message: String,
)

data class GhostUiState(
    val currentLevel: Int = 0,
    val totalLevels: Int = GHOST_LEVELS.size,
    val narrator: String = GHOST_LEVELS.first().narrator,
    val characterLine: String = GHOST_LEVELS.first().characterLine,
    val prompt: String = GHOST_LEVELS.first().prompt,
    val choices: List<GhostChoice> = GHOST_LEVELS.first().choices,
    val feedback: GhostFeedback? = null,
    val correctAnswers: Int = 0,
    val isRunComplete: Boolean = false,
)

private val GHOST_LEVELS = listOf(
    GhostLevel(
        narrator = "Patient 849 is stuck in the waiting room. The medical firewall is blocking the door.",
        characterLine = "I've been waiting for hours. I just need to see the doctor. Can you turn this firewall off?",
        prompt = "How do we turn off the firewall?",
        choices = listOf(
            GhostChoice("firewall.active = true;", false, "Error: that keeps the firewall active and the door locked."),
            GhostChoice("firewall.active = false;", true, "Success. The firewall is down and the waiting room is open."),
            GhostChoice("firewall.hide();", false, "Error: hiding the firewall display does not disable the firewall."),
        ),
    ),
    GhostLevel(
        narrator = "Error. Patient file corrupted. Identity unknown.",
        characterLine = "The system forgot who I am. Can you fix my chart before they send me back outside?",
        prompt = "How do we restore the patient name?",
        choices = listOf(
            GhostChoice("patientName = 500;", false, "Error: patient names are text, not numbers."),
            GhostChoice("""patientName = "Sterling";""", true, "Success. Patient Sterling recognized. Record restored."),
            GhostChoice("patientName = false;", false, "Error: the chart cannot identify a boolean as a patient."),
        ),
    ),
    GhostLevel(
        narrator = "Neurologist found, but the exam routine is still idle.",
        characterLine = "The doctor is here. We just need the exam to start before the system freezes again.",
        prompt = "How do we tell the neurologist to start the exam?",
        choices = listOf(
            GhostChoice("neurologist.stopExam();", false, "Error: the exam has not started yet."),
            GhostChoice("neurologist.hide();", false, "Error: hiding the neurologist does not start the exam."),
            GhostChoice("neurologist.startExam();", true, "Success. The doctor is ready and the path is clear."),
        ),
    ),
)

class GhostViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GhostUiState())
    val uiState: StateFlow<GhostUiState> = _uiState.asStateFlow()

    fun submitChoice(index: Int) {
        val state = _uiState.value
        if (state.feedback != null) return

        val choice = state.choices[index]
        _uiState.update {
            it.copy(
                feedback = GhostFeedback(correct = choice.correct, message = choice.feedback),
                correctAnswers = it.correctAnswers + if (choice.correct) 1 else 0,
                isRunComplete = choice.correct && it.currentLevel == GHOST_LEVELS.lastIndex,
            )
        }
    }

    fun advanceAfterSuccess() {
        val state = _uiState.value
        val feedback = state.feedback ?: return
        if (!feedback.correct || state.currentLevel >= GHOST_LEVELS.lastIndex) return

        val nextLevelIndex = state.currentLevel + 1
        val nextLevel = GHOST_LEVELS[nextLevelIndex]
        _uiState.value = state.copy(
            currentLevel = nextLevelIndex,
            narrator = nextLevel.narrator,
            characterLine = nextLevel.characterLine,
            prompt = nextLevel.prompt,
            choices = nextLevel.choices,
            feedback = null,
            isRunComplete = false,
        )
    }

    fun buildResult(): GameResult = GameResult(
        completed = true,
        score = uiState.value.correctAnswers,
        stars = if (uiState.value.correctAnswers == GHOST_LEVELS.size) 3 else 2,
        durationMs = 0L,
        perfect = uiState.value.correctAnswers == GHOST_LEVELS.size,
    )
}
