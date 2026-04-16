package com.sterlingsworld.feature.game.suites.relaxation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val BeachSand = Color(0xFFFFF8E1)
private val TropicalTeal = Color(0xFF00897B)
private val TropicalTealLight = Color(0xFFE0F2F1)
private val HibiscusRed = Color(0xFFEF5350)
private val CorrectGreen = Color(0xFF43A047)
private val NeutralRevealed = Color(0xFFBDBDBD)

@Composable
fun NervousSystemTriviaGame(
    uiState: TriviaUiState,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
) {
    if (uiState.isComplete) {
        TriviaCompleteCard(
            score = uiState.score,
            total = uiState.totalQuestions,
            onRestart = onRestart,
        )
        return
    }

    val question = uiState.currentQuestion ?: return

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Progress + question card
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = BeachSand),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Question ${uiState.currentIndex + 1} of ${uiState.totalQuestions}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TropicalTeal,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A2A1F),
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // Answer buttons
        question.options.forEachIndexed { index, option ->
            AnswerButton(
                text = option,
                index = index,
                selectedIndex = uiState.selectedAnswerIndex,
                correctIndex = question.correctIndex,
                onSelect = { onSelectAnswer(index) },
            )
        }

        // Next button — appears after answering
        if (uiState.selectedAnswerIndex != null) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TropicalTeal),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = if (uiState.currentIndex + 1 >= uiState.totalQuestions) "See Results" else "Next Question",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun AnswerButton(
    text: String,
    index: Int,
    selectedIndex: Int?,
    correctIndex: Int,
    onSelect: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Spring bounce on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "answer_scale_$index",
    )

    val targetColor = when {
        selectedIndex == null -> TropicalTealLight          // unanswered default
        index == correctIndex -> CorrectGreen               // the right answer
        index == selectedIndex -> HibiscusRed               // wrong choice made
        else -> NeutralRevealed                             // other options after reveal
    }

    val containerColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "answer_color_$index",
    )

    val contentColor = when {
        selectedIndex == null -> TropicalTeal
        index == correctIndex || index == selectedIndex -> Color.White
        else -> Color(0xFF616161)
    }

    val enabled = selectedIndex == null

    Button(
        onClick = onSelect,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 6.dp),
        )
    }
}

@Composable
private fun TriviaCompleteCard(
    score: Int,
    total: Int,
    onRestart: () -> Unit,
) {
    val emoji = when {
        score >= total - 1 -> "🏆"
        score >= total / 2 -> "👍"
        else -> "💪"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = BeachSand),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = emoji, style = MaterialTheme.typography.displaySmall)
                Text(
                    text = "Quiz Complete!",
                    style = MaterialTheme.typography.titleLarge,
                    color = TropicalTeal,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Score: $score / $total",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF1A2A1F),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = when {
                        score >= total -> "Perfect! Your nervous system knowledge is sharp."
                        score >= total - 1 -> "Great job — almost perfect!"
                        score >= total / 2 -> "Good effort. Keep learning!"
                        else -> "Keep practicing. You've got this."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A6B5A),
                )
            }
        }

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = TropicalTeal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Play Again", style = MaterialTheme.typography.labelLarge)
        }
    }
}
