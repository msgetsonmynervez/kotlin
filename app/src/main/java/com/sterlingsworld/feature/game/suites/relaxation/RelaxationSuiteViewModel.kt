package com.sterlingsworld.feature.game.suites.relaxation

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class RelaxationActivity(val label: String) {
    TRIVIA("Trivia"),
    WORDLE("Wordle"),
    SUDOKU("Sudoku"),
    CROSSWORD("Crossword"),
    SOLITAIRE("Solitaire"),
    MATCH3("Match-3"),
}

data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
)

data class TriviaUiState(
    val questions: List<TriviaQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isComplete: Boolean = false,
) {
    val currentQuestion: TriviaQuestion? get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int get() = questions.size
}

data class RelaxationSuiteUiState(
    val currentActivity: RelaxationActivity = RelaxationActivity.TRIVIA,
    val trivia: TriviaUiState = TriviaUiState(),
)

class RelaxationSuiteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RelaxationSuiteUiState())
    val uiState: StateFlow<RelaxationSuiteUiState> = _uiState.asStateFlow()

    init {
        startTrivia()
    }

    fun selectActivity(activity: RelaxationActivity) {
        _uiState.update { it.copy(currentActivity = activity) }
    }

    fun startTrivia() {
        val questions = ALL_TRIVIA_QUESTIONS.shuffled().take(5).map { q ->
            val correct = q.options[q.correctIndex]
            val shuffled = q.options.shuffled()
            q.copy(options = shuffled, correctIndex = shuffled.indexOf(correct))
        }
        _uiState.update { it.copy(trivia = TriviaUiState(questions = questions)) }
    }

    fun selectAnswer(index: Int) {
        val trivia = _uiState.value.trivia
        if (trivia.selectedAnswerIndex != null) return
        val correct = trivia.currentQuestion?.correctIndex ?: return
        _uiState.update {
            it.copy(
                trivia = trivia.copy(
                    selectedAnswerIndex = index,
                    score = trivia.score + if (index == correct) 1 else 0,
                ),
            )
        }
    }

    fun nextQuestion() {
        val trivia = _uiState.value.trivia
        val nextIndex = trivia.currentIndex + 1
        _uiState.update {
            it.copy(
                trivia = trivia.copy(
                    currentIndex = nextIndex,
                    selectedAnswerIndex = null,
                    isComplete = nextIndex >= trivia.questions.size,
                ),
            )
        }
    }

    fun buildResult(): GameResult {
        val trivia = _uiState.value.trivia
        val total = trivia.totalQuestions.coerceAtLeast(1)
        val stars = when {
            trivia.score >= total -> 3
            trivia.score >= total - 1 -> 2
            else -> 1
        }
        return GameResult(
            completed = trivia.isComplete,
            score = trivia.score,
            stars = stars,
            durationMs = 0L,
            perfect = trivia.score >= total,
        )
    }
}

internal val ALL_TRIVIA_QUESTIONS = listOf(
    TriviaQuestion(
        question = "What organ is the control center of the nervous system?",
        options = listOf("Brain", "Heart", "Liver", "Stomach"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "The brain and spinal cord make up the...",
        options = listOf("Central nervous system", "Digestive system", "Respiratory system", "Skeletal system"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "What runs down your back inside your spine?",
        options = listOf("Spinal cord", "Bicep", "Esophagus", "Aorta"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "True or false: The brain uses electrical signals to send messages.",
        options = listOf("True", "False"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "Does your brain help process all five senses?",
        options = listOf("Yes, all five", "Only three", "Only hearing", "Only sight"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "What are the cells called that carry messages in the nervous system?",
        options = listOf("Neurons (nerve cells)", "Red blood cells", "Skin cells", "Fat cells"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "What protects your brain from bumps?",
        options = listOf("Your skull", "Your ribs", "Your muscles", "Your skin"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "Pulling your hand away from something hot is called a...",
        options = listOf("Reflex", "Sneeze", "Yawn", "Cramp"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "About how much does the adult brain weigh?",
        options = listOf("About 3 pounds", "About 10 pounds", "About 1 ounce", "About 20 pounds"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "Which body part lets you feel hot and cold?",
        options = listOf("Your skin", "Your bones", "Your hair", "Your nails"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "What happens to your brain during sleep?",
        options = listOf("It rests and repairs", "It shuts off completely", "It stops working", "It shrinks"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "Does the nervous system control breathing and heartbeat?",
        options = listOf("Yes", "No"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "Which helps keep your brain healthy?",
        options = listOf("All of these", "Exercise", "Sleep", "Puzzles"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "What does \"MS\" stand for?",
        options = listOf("Multiple Sclerosis", "Muscle Strain", "Memory Shortage", "Motor System"),
        correctIndex = 0,
    ),
    TriviaQuestion(
        question = "How many senses do humans traditionally have?",
        options = listOf("Five", "Three", "Seven", "Ten"),
        correctIndex = 0,
    ),
)
