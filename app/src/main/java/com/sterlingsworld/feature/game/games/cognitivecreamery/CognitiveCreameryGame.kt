package com.sterlingsworld.feature.game.games.cognitivecreamery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.core.ui.theme.Accent
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Secondary
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.SurfaceStrong
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.domain.model.GameResult

@Composable
fun CognitiveCreameryGame(
    vm: CognitiveCreameryViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceStrong),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Cognitive Creamery",
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary,
                )
                Text(
                    text = "Round ${uiState.currentRound + 1} of ${uiState.totalRounds}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Accent,
                )
                Text(
                    text = "Memorize the flavor sequence, then rebuild it in the right order.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            }
        }

        when (uiState.phase) {
            CreameryPhase.STUDY -> StudyPhase(state = uiState, onReady = vm::onReady)
            CreameryPhase.INPUT -> InputPhase(
                state = uiState,
                onTokenTapped = vm::onTokenTapped,
                onUndo = vm::onUndo,
                onCheck = vm::onCheck,
            )
            CreameryPhase.ROUND_RESULT -> RoundResultPhase(state = uiState, onNext = vm::onNextRound)
            CreameryPhase.RUN_COMPLETE -> RunCompletePhase(
                state = uiState,
                onDone = { onDone(vm.buildResult()) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudyPhase(state: CognitiveCreameryUiState, onReady: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Memorize this sequence",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.targetSequence.forEachIndexed { index, flavor ->
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "${index + 1}. $flavor",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Accent.copy(alpha = 0.18f),
                            labelColor = Primary,
                        ),
                    )
                }
            }
            Button(
                onClick = onReady,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("I've got it — Ready")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InputPhase(
    state: CognitiveCreameryUiState,
    onTokenTapped: (String) -> Unit,
    onUndo: () -> Unit,
    onCheck: () -> Unit,
) {
    val sequenceFull = state.playerSequence.size == state.targetSequence.size

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Rebuild the sequence",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )

            // Slots — show what the player has built so far
            Text(
                text = "Your sequence (${state.playerSequence.size} / ${state.targetSequence.size}):",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
            if (state.playerSequence.isEmpty()) {
                Text(
                    text = "Tap a flavor below to start",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.playerSequence.forEachIndexed { index, flavor ->
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = "${index + 1}. $flavor",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Primary.copy(alpha = 0.15f),
                                labelColor = Primary,
                            ),
                        )
                    }
                }
            }

            // Available token buttons
            if (!sequenceFull) {
                Text(
                    text = "Available flavors:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.availableTokens.forEach { token ->
                        OutlinedButton(
                            onClick = { onTokenTapped(token) },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(text = token, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.playerSequence.isNotEmpty()) {
                    FilledTonalButton(onClick = onUndo) {
                        Text("Undo")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Button(
                    onClick = onCheck,
                    enabled = sequenceFull,
                ) {
                    Text("Check")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoundResultPhase(state: CognitiveCreameryUiState, onNext: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (state.lastRoundCorrect) "Correct!" else "Not quite",
                style = MaterialTheme.typography.titleMedium,
                color = if (state.lastRoundCorrect) Accent else Secondary,
            )
            Text(
                text = if (state.lastRoundCorrect) {
                    "You got the sequence right."
                } else {
                    "The correct sequence was:"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
            if (!state.lastRoundCorrect) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.targetSequence.forEachIndexed { index, flavor ->
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = "${index + 1}. $flavor",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Secondary.copy(alpha = 0.12f),
                                labelColor = Secondary,
                            ),
                        )
                    }
                }
            }
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Next Round")
            }
        }
    }
}

@Composable
private fun RunCompletePhase(state: CognitiveCreameryUiState, onDone: () -> Unit) {
    val stars = when (state.correctRounds) {
        ROUND_LENGTHS.size -> 3
        ROUND_LENGTHS.size - 1 -> 2
        else -> 1
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Run complete",
                style = MaterialTheme.typography.titleLarge,
                color = Primary,
            )
            Text(
                text = "${"★".repeat(stars)}${"☆".repeat(3 - stars)}",
                style = MaterialTheme.typography.headlineMedium,
                color = Accent,
            )
            Text(
                text = "${state.correctRounds} of ${state.totalRounds} sequences correct",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
            Text(
                text = when (state.correctRounds) {
                    ROUND_LENGTHS.size -> "Perfect run. Your memory is sharp."
                    ROUND_LENGTHS.size - 1 -> "Good session. One round slipped — try again for perfect."
                    else -> "Keep practicing. The sequences get easier each run."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Finish")
            }
        }
    }
}
