package com.sterlingsworld.feature.game.games.ghost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
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
fun GhostGame(
    vm: GhostViewModel = viewModel(),
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "HoldPlease Healthcare",
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary,
                )
                Text(
                    text = "Level ${uiState.currentLevel + 1} / ${uiState.totalLevels}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Accent,
                )
                Text(
                    text = uiState.narrator,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
                Text(
                    text = uiState.characterLine,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                )
                Text(
                    text = uiState.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
            }
        }

        val feedback = uiState.feedback
        if (feedback != null) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = if (feedback.correct) "Correct" else "Try a different command",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (feedback.correct) Accent else Secondary,
                    )
                    Text(
                        text = feedback.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        if (uiState.isRunComplete) {
                            Button(onClick = { onDone(vm.buildResult()) }) {
                                Text("Finish Run")
                            }
                        } else {
                            Button(onClick = vm::advanceAfterSuccess, enabled = feedback.correct) {
                                Text("Next Level")
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                uiState.choices.forEachIndexed { index, choice ->
                    OutlinedButton(
                        onClick = { vm.submitChoice(index) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = choice.code,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
        }
    }
}
