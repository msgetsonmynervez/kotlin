package com.sterlingsworld.feature.game.games.cognitivecreamery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.domain.model.GameResult

private val CreameryBackgroundTop = Color(0xFF18110E)
private val CreameryBackgroundBottom = Color(0xFF0F0A08)
private val CreameryCard = Color(0xFF231916)
private val CreameryCardAlt = Color(0xFF2C211C)
private val CreameryGold = Color(0xFFD6AE63)
private val CreameryText = Color(0xFFF6EADD)
private val CreameryMuted = Color(0xFFC9B6A1)
private val CreamerySequence = Color(0xFFD39D63)
private val CreameryClarity = Color(0xFF8BCFD6)
private val CreameryDanger = Color(0xFFD97A7A)

@Composable
fun CognitiveCreameryGame(
    vm: CognitiveCreameryViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CreameryBackgroundTop, CreameryBackgroundBottom),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CreameryTopBar(
                state = uiState,
                onBackToParlor = vm::backToParlor,
                onFinish = { onDone(vm.buildResult()) },
            )

            when (uiState.currentActivity) {
                CreameryActivity.PARLOR -> MidnightParlor(
                    state = uiState,
                    onSelectActivity = vm::navigateTo,
                    onFinish = { onDone(vm.buildResult()) },
                    onReset = vm::resetSession,
                )
                CreameryActivity.SEQUENCE -> SequenceSuite(
                    state = uiState.sequence,
                    onReady = vm::onReady,
                    onTokenTapped = vm::onTokenTapped,
                    onUndo = vm::onUndo,
                    onCheck = vm::onCheck,
                    onNextRound = vm::onNextRound,
                    onBackToParlor = vm::backToParlor,
                    onDone = { onDone(vm.buildResult()) },
                )
                CreameryActivity.CLARITY -> CognitiveClarityGame(
                    state = uiState.clarity,
                    onWordTapped = vm::onClarityWordTapped,
                    onNextRound = vm::onClarityNextRound,
                    onBackToParlor = vm::backToParlor,
                )
            }
        }

        if (uiState.isBrainFreeze) {
            BrainFreezeOverlay(
                onRecover = vm::resetBrainFreeze,
                onFinish = { onDone(vm.buildResult()) },
            )
        }
    }
}

@Composable
private fun CreameryTopBar(
    state: CognitiveCreameryUiState,
    onBackToParlor: () -> Unit,
    onFinish: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "STERLING FOCUS SUITE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CreameryGold,
                        letterSpacing = 3.sp,
                    )
                    Text(
                        text = "Cognitive Creamery",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CreameryText,
                        fontWeight = FontWeight.Black,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.currentActivity != CreameryActivity.PARLOR) {
                        FilledTonalButton(onClick = onBackToParlor) {
                            Text("Parlor")
                        }
                    }
                    OutlinedButton(onClick = onFinish) {
                        Text("Cash Out")
                    }
                }
            }

            Text(
                text = "Fatigue",
                style = MaterialTheme.typography.labelSmall,
                color = CreameryMuted,
                letterSpacing = 2.sp,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.fatigueLevel.toFloat() / MAX_FATIGUE.toFloat())
                        .height(12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(CreameryClarity, CreameryGold, CreameryDanger),
                            ),
                        ),
                )
            }

            Text(
                text = "Session score ${state.sessionScore}  •  Flavor of the day: ${state.flavorOfTheDay.label}",
                style = MaterialTheme.typography.bodySmall,
                color = CreameryMuted,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MidnightParlor(
    state: CognitiveCreameryUiState,
    onSelectActivity: (CreameryActivity) -> Unit,
    onFinish: () -> Unit,
    onReset: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CreameryCardAlt),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "MIDNIGHT PARLOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = CreameryGold,
                    letterSpacing = 4.sp,
                )
                Text(
                    text = "Choose a calm focus activity.",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreameryText,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Sequence stays available, Clarity is now native, and the suite shares one fatigue bar across the whole session.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CreameryMuted,
                )
            }
        }

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            listOf(CreameryActivity.SEQUENCE, CreameryActivity.CLARITY).forEach { activity ->
                val accent = when (activity) {
                    CreameryActivity.SEQUENCE -> CreamerySequence
                    CreameryActivity.CLARITY -> CreameryClarity
                    CreameryActivity.PARLOR -> CreameryGold
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectActivity(activity) },
                    colors = CardDefaults.cardColors(containerColor = CreameryCard),
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = activity.badge,
                                style = MaterialTheme.typography.labelMedium,
                                color = accent,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                            )
                            if (state.flavorOfTheDay == activity) {
                                Text(
                                    text = "FOTD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CreameryBackgroundBottom,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(accent)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                        Text(
                            text = activity.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = CreameryText,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = activity.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CreameryMuted,
                        )
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilledTonalButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
            ) {
                Text("Reset Session")
            }
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f),
            ) {
                Text("Finish Session")
            }
        }
    }
}

@Composable
private fun SequenceSuite(
    state: SequenceUiState,
    onReady: () -> Unit,
    onTokenTapped: (String) -> Unit,
    onUndo: () -> Unit,
    onCheck: () -> Unit,
    onNextRound: () -> Unit,
    onBackToParlor: () -> Unit,
    onDone: () -> Unit,
) {
    when (state.phase) {
        CreameryPhase.STUDY -> SequenceStudyPhase(state = state, onReady = onReady)
        CreameryPhase.INPUT -> SequenceInputPhase(
            state = state,
            onTokenTapped = onTokenTapped,
            onUndo = onUndo,
            onCheck = onCheck,
        )
        CreameryPhase.ROUND_RESULT -> SequenceRoundResultPhase(state = state, onNextRound = onNextRound)
        CreameryPhase.RUN_COMPLETE -> SequenceRunCompletePhase(
            state = state,
            onBackToParlor = onBackToParlor,
            onDone = onDone,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SequenceStudyPhase(state: SequenceUiState, onReady: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Flavor Sequence",
                style = MaterialTheme.typography.titleLarge,
                color = CreameryText,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Round ${state.currentRound + 1} of ${state.totalRounds}",
                style = MaterialTheme.typography.labelMedium,
                color = CreamerySequence,
            )
            Text(
                text = "Memorize the order, then rebuild it from memory in the next step.",
                style = MaterialTheme.typography.bodyMedium,
                color = CreameryMuted,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.targetSequence.forEachIndexed { index, flavor ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${index + 1}. $flavor", fontWeight = FontWeight.SemiBold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = CreamerySequence.copy(alpha = 0.18f),
                            labelColor = CreameryText,
                        ),
                    )
                }
            }
            Button(onClick = onReady, modifier = Modifier.fillMaxWidth()) {
                Text("Ready")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SequenceInputPhase(
    state: SequenceUiState,
    onTokenTapped: (String) -> Unit,
    onUndo: () -> Unit,
    onCheck: () -> Unit,
) {
    val sequenceFull = state.playerSequence.size == state.targetSequence.size

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Rebuild the order",
                style = MaterialTheme.typography.titleLarge,
                color = CreameryText,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${state.playerSequence.size} / ${state.targetSequence.size} selected",
                style = MaterialTheme.typography.labelMedium,
                color = CreameryMuted,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.playerSequence.forEachIndexed { index, flavor ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${index + 1}. $flavor") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = CreameryGold.copy(alpha = 0.18f),
                            labelColor = CreameryText,
                        ),
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.availableTokens.forEach { token ->
                    OutlinedButton(
                        onClick = { onTokenTapped(token) },
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(token)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.playerSequence.isNotEmpty()) {
                    FilledTonalButton(onClick = onUndo) { Text("Undo") }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Button(onClick = onCheck, enabled = sequenceFull) {
                    Text("Check")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SequenceRoundResultPhase(
    state: SequenceUiState,
    onNextRound: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = if (state.lastRoundCorrect) "Correct" else "Not quite",
                style = MaterialTheme.typography.titleLarge,
                color = if (state.lastRoundCorrect) CreameryClarity else CreameryDanger,
                fontWeight = FontWeight.Bold,
            )
            if (!state.lastRoundCorrect) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.targetSequence.forEachIndexed { index, flavor ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text("${index + 1}. $flavor") },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CreameryDanger.copy(alpha = 0.18f),
                                labelColor = CreameryText,
                            ),
                        )
                    }
                }
            }
            Button(onClick = onNextRound, modifier = Modifier.fillMaxWidth()) {
                Text("Next Round")
            }
        }
    }
}

@Composable
private fun SequenceRunCompletePhase(
    state: SequenceUiState,
    onBackToParlor: () -> Unit,
    onDone: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Sequence complete",
                style = MaterialTheme.typography.titleLarge,
                color = CreameryText,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${state.correctRounds} of ${state.totalRounds} rounds cleared",
                style = MaterialTheme.typography.bodyLarge,
                color = CreameryMuted,
                textAlign = TextAlign.Center,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(onClick = onBackToParlor, modifier = Modifier.weight(1f)) {
                    Text("Back to Parlor")
                }
                Button(onClick = onDone, modifier = Modifier.weight(1f)) {
                    Text("Finish")
                }
            }
        }
    }
}

@Composable
private fun BrainFreezeOverlay(
    onRecover: () -> Unit,
    onFinish: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.58f)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = CreameryCardAlt),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Brain Freeze",
                    style = MaterialTheme.typography.headlineSmall,
                    color = CreameryText,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "Too many misses built up fatigue. Recover to continue the suite or cash out now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CreameryMuted,
                    textAlign = TextAlign.Center,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(onClick = onRecover, modifier = Modifier.weight(1f)) {
                        Text("Recover")
                    }
                    Button(onClick = onFinish, modifier = Modifier.weight(1f)) {
                        Text("Finish")
                    }
                }
            }
        }
    }
}
