package com.sterlingsworld.feature.game.games.cognitivecreamery

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ClarityPanel = Color(0xFF231916)
private val ClarityText = Color(0xFFF6EADD)
private val ClarityMuted = Color(0xFFC9B6A1)
private val ClarityCyan = Color(0xFF8BCFD6)
private val ClarityRed = Color(0xFFD97A7A)

@Composable
fun CognitiveClarityGame(
    state: ClarityUiState,
    onWordTapped: (String) -> Unit,
    onNextRound: () -> Unit,
    onBackToParlor: () -> Unit,
) {
    val group = state.currentGroup ?: return

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = ClarityPanel),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "COGNITIVE CLARITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = ClarityCyan,
                    letterSpacing = 3.sp,
                )
                FilledTonalButton(onClick = onBackToParlor) {
                    Text("Parlor")
                }
            }

            Text(
                text = group.theme,
                style = MaterialTheme.typography.headlineLarge,
                color = ClarityText,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Tap the three words that belong with the theme.",
                style = MaterialTheme.typography.bodyMedium,
                color = ClarityMuted,
                textAlign = TextAlign.Center,
            )

            val rows = state.gridWords.chunked(2)
            rows.forEach { words ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    words.forEach { word ->
                        val isFound = word in state.foundWords
                        val isWrong = word in state.wrongWords
                        val targetColor = when {
                            isFound -> ClarityCyan
                            isWrong -> ClarityRed.copy(alpha = 0.35f)
                            else -> Color.White.copy(alpha = 0.08f)
                        }
                        val background by animateColorAsState(
                            targetValue = targetColor,
                            animationSpec = tween(250),
                            label = "clarity_button_$word",
                        )
                        val textColor = when {
                            isFound -> Color.Black
                            isWrong -> ClarityRed
                            else -> ClarityText
                        }

                        Button(
                            onClick = { if (!state.roundComplete) onWordTapped(word) },
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = background,
                                contentColor = textColor,
                                disabledContainerColor = background,
                                disabledContentColor = textColor,
                            ),
                            enabled = !state.roundComplete || isFound,
                        ) {
                            Text(
                                text = word,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                    if (words.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Text(
                text = "${state.foundWords.size} / ${group.members.size} found",
                style = MaterialTheme.typography.labelMedium,
                color = ClarityCyan,
            )

            if (state.roundComplete) {
                Button(onClick = onNextRound, modifier = Modifier.fillMaxWidth()) {
                    Text("Next Round")
                }
            }

            Text(
                text = "Rounds cleared: ${state.roundsCompleted}",
                style = MaterialTheme.typography.labelSmall,
                color = ClarityMuted,
            )
        }
    }
}
