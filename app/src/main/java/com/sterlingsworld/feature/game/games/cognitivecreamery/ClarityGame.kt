package com.sterlingsworld.feature.game.games.cognitivecreamery

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
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
                    color = CreameryClarity,
                    letterSpacing = 3.sp,
                )
                PressScaleButton(
                    label = "Parlor",
                    onClick = onBackToParlor,
                    filled = true,
                )
            }

            Text(
                text = group.theme,
                style = MaterialTheme.typography.headlineLarge,
                color = CreameryText,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Tap the three words that belong with the theme.",
                style = MaterialTheme.typography.bodyMedium,
                color = CreameryMuted,
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
                        val background by animateColorAsState(
                            targetValue = when {
                                isFound -> CreameryClarity.copy(alpha = 0.88f)
                                isWrong -> CreameryDanger.copy(alpha = 0.32f)
                                else -> Color.White.copy(alpha = 0.08f)
                            },
                            label = "clarity_button_bg_$word",
                        )
                        val borderColor by animateColorAsState(
                            targetValue = when {
                                isFound -> CreameryClarity
                                isWrong -> CreameryDanger
                                else -> Color.Transparent
                            },
                            label = "clarity_button_border_$word",
                        )
                        val fade by animateFloatAsState(
                            targetValue = if (isFound) 0.58f else 1f,
                            label = "clarity_fade_$word",
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isFound || isWrong) 1.04f else 1f,
                            label = "clarity_scale_$word",
                        )

                        BoxedWordButton(
                            text = word,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .alpha(fade),
                            containerBrush = Brush.horizontalGradient(
                                listOf(
                                    background,
                                    background.copy(alpha = 0.85f),
                                ),
                            ),
                            borderColor = borderColor,
                            enabled = !state.roundComplete || isFound,
                            onClick = { if (!state.roundComplete) onWordTapped(word) },
                        )
                    }
                    if (words.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Text(
                text = "${state.foundWords.size} / ${group.members.size} found",
                style = MaterialTheme.typography.labelMedium,
                color = CreameryClarity,
            )

            if (state.roundComplete) {
                PressScaleButton(
                    label = "Next Round",
                    onClick = onNextRound,
                    modifier = Modifier.fillMaxWidth(),
                    filled = false,
                )
            }

            Text(
                text = "Rounds cleared: ${state.roundsCompleted}",
                style = MaterialTheme.typography.labelSmall,
                color = CreameryMuted,
            )
        }
    }
}

@Composable
private fun BoxedWordButton(
    text: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier,
    containerBrush: Brush,
    borderColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 1.1f else 1f, label = "word_scale_$text")

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(containerBrush, RoundedCornerShape(18.dp))
            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
            .background(Color.Transparent, RoundedCornerShape(18.dp))
            .then(
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                ),
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(18.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = textStyle,
                color = CreameryText,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}
