package com.sterlingsworld.feature.game.suites.creamery

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

private val ClarityCyan  = Color(0xFF00BCD4)
private val MistakeRed   = Color(0xFFE57373)

@Composable
fun CognitiveClarityGame(
    state: ClarityUiState,
    onWordTapped: (String) -> Unit,
    onNextRound: () -> Unit,
) {
    val group = state.currentGroup ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "FIND THE THEME",
            style = MaterialTheme.typography.labelSmall,
            color = ClarityCyan.copy(alpha = 0.7f),
            letterSpacing = 3.sp,
        )

        Text(
            text = group.theme,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Tap the 3 words that belong together",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 2 × 3 grid (6 words in 3 rows of 2)
        val rows = state.gridWords.chunked(2)
        rows.forEach { rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowWords.forEach { word ->
                    val isFound = word in state.foundWords
                    val isWrong = word in state.wrongWords

                    val targetBg = when {
                        isFound -> ClarityCyan
                        isWrong -> MistakeRed.copy(alpha = 0.35f)
                        else    -> Color.White.copy(alpha = 0.08f)
                    }
                    val bgColor by animateColorAsState(
                        targetValue = targetBg,
                        animationSpec = tween(durationMillis = 300),
                        label = "clarity_bg_$word",
                    )
                    val textColor = when {
                        isFound -> Color.Black
                        isWrong -> MistakeRed
                        else    -> Color.White
                    }

                    Button(
                        onClick = { if (!state.roundComplete) onWordTapped(word) },
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bgColor,
                            contentColor = textColor,
                            disabledContainerColor = bgColor,
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
                // Pad a row that has only 1 word (shouldn't happen with 6 words, but safe)
                if (rowWords.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Text(
            text = "${state.foundWords.size} / ${group.members.size} found",
            style = MaterialTheme.typography.labelMedium,
            color = ClarityCyan.copy(alpha = 0.8f),
        )

        if (state.roundComplete) {
            Button(
                onClick = onNextRound,
                colors = ButtonDefaults.buttonColors(containerColor = ClarityCyan),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Next Round", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            text = "Rounds cleared: ${state.roundsCompleted}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.35f),
        )
    }
}
