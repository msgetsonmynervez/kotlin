package com.sterlingsworld.feature.game.games.cognitivecreamery

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.domain.model.GameResult

internal val CreameryCard = Color(0xB8211715)
internal val CreameryCardAlt = Color(0xBD2C1F1B)
internal val CreameryGold = Color(0xFFD6AE63)
internal val CreameryText = Color(0xFFF6EADD)
internal val CreameryMuted = Color(0xFFC9B6A1)
internal val CreameryDanger = Color(0xFFD97A7A)
internal val CreameryFrost = Color(0xFFB9E7FF)
private val SymmetryTilesUi = listOf("", "🍭", "🍩", "🧁")

private enum class CreamerySfx { TAP, SUCCESS, FAIL }

private class CreamerySoundPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 55)

    fun play(effect: CreamerySfx) {
        when (effect) {
            CreamerySfx.TAP -> toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 50)
            CreamerySfx.SUCCESS -> toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 110)
            CreamerySfx.FAIL -> toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 140)
        }
    }

    fun release() {
        toneGenerator.release()
    }
}

@Composable
fun CognitiveCreameryGame(
    vm: CognitiveCreameryViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val soundPlayer = remember { CreamerySoundPlayer() }
    var previousOverlay by remember { mutableStateOf<CreameryResultOverlay?>(null) }

    DisposableEffect(Unit) {
        onDispose { soundPlayer.release() }
    }

    LaunchedEffect(uiState.resultOverlay) {
        val current = uiState.resultOverlay
        if (current != null && current != previousOverlay) {
            soundPlayer.play(if (current.success) CreamerySfx.SUCCESS else CreamerySfx.FAIL)
        }
        previousOverlay = current
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_cognitive_creamery),
            contentDescription = "Cognitive Creamery background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f)),
        )
        FatigueVignette(fatigue = uiState.activeFatigue)

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
                    onSelectActivity = {
                        soundPlayer.play(CreamerySfx.TAP)
                        vm.navigateTo(it)
                    },
                    onReset = vm::resetSession,
                    onFinish = { onDone(vm.buildResult()) },
                    modifier = Modifier.weight(1f),
                )
                else -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    when (uiState.currentActivity) {
                        CreameryActivity.CLARITY -> ClarityMode(uiState.clarity, onTap = {
                            soundPlayer.play(CreamerySfx.TAP)
                            vm.onClarityWordTapped(it)
                        })
                        CreameryActivity.SCAN -> ScanMode(uiState.scan, onTap = {
                            soundPlayer.play(CreamerySfx.TAP)
                            vm.onScanTapped(it)
                        })
                        CreameryActivity.SEQUENCE -> SequenceMode(
                            state = uiState.sequence,
                            onReady = {
                                soundPlayer.play(CreamerySfx.TAP)
                                vm.onReadySequence()
                            },
                            onToken = {
                                soundPlayer.play(CreamerySfx.TAP)
                                vm.onSequenceTokenTapped(it)
                            },
                            onUndo = vm::onSequenceUndo,
                            onCheck = vm::onSequenceCheck,
                        )
                        CreameryActivity.CATEGORY -> CategoryMode(uiState.category, onTap = {
                            soundPlayer.play(CreamerySfx.TAP)
                            vm.onCategoryTapped(it)
                        })
                        CreameryActivity.SYMMETRY -> SymmetryMode(
                            state = uiState.symmetry,
                            onCycle = {
                                soundPlayer.play(CreamerySfx.TAP)
                                vm.onSymmetryCycle(it)
                            },
                            onCheck = {
                                soundPlayer.play(CreamerySfx.TAP)
                                vm.onSymmetryCheck()
                            },
                        )
                        CreameryActivity.FLIP -> FlipMode(uiState.flip, onGuess = {
                            soundPlayer.play(CreamerySfx.TAP)
                            vm.onFlipGuess(it)
                        })
                        CreameryActivity.PATTERN -> PatternMode(uiState.pattern, onGuess = {
                            soundPlayer.play(CreamerySfx.TAP)
                            vm.onPatternGuess(it)
                        })
                        CreameryActivity.PARLOR -> Unit
                    }
                }
            }
        }

        uiState.resultOverlay?.let { overlay ->
            ResultOverlay(
                overlay = overlay,
                onMenu = vm::dismissResultOverlay,
                onFinish = { onDone(vm.buildResult()) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreameryTopBar(
    state: CognitiveCreameryUiState,
    onBackToParlor: () -> Unit,
    onFinish: () -> Unit,
) {
    val pulse by rememberInfiniteTransition(label = "creamery_topbar").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                1f at 0
                1.05f at 450
                1f at 900
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "creamery_topbar_pulse",
    )
    val accent = Color(state.currentActivity.accent)

    Card(
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.shadow(12.dp, RoundedCornerShape(24.dp)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "STERLING FOCUS SUITE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CreameryGold,
                        letterSpacing = 3.sp,
                    )
                    Text(
                        text = state.currentActivity.label,
                        style = MaterialTheme.typography.headlineSmall,
                        color = CreameryText,
                        fontWeight = FontWeight.Black,
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.currentActivity != CreameryActivity.PARLOR) {
                        PressScaleButton(label = "Menu", onClick = onBackToParlor, filled = true)
                    }
                    PressScaleButton(label = "Cash Out", onClick = onFinish, filled = false)
                }
            }

            if (state.currentActivity != CreameryActivity.PARLOR) {
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
                        .graphicsLayer {
                            if (state.activeFatigue >= 80) {
                                scaleX = pulse
                                scaleY = pulse
                            }
                        }
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(999.dp)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(state.activeFatigue.toFloat() / CREAMERY_MAX_FATIGUE.toFloat())
                            .height(12.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(accent, CreameryGold, CreameryDanger)),
                                shape = RoundedCornerShape(999.dp),
                            ),
                    )
                }
                Text(
                    text = "Level ${state.activeLevel}/$CREAMERY_MAX_LEVEL • Modes cleared ${state.clearedModes.size}/${PLAYABLE_ACTIVITIES_UI.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CreameryMuted,
                )
            } else {
                Text(
                    text = "Flavor of the day: ${state.flavorOfTheDay.label} • Modes cleared ${state.clearedModes.size}/${PLAYABLE_ACTIVITIES_UI.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CreameryMuted,
                )
            }
        }
    }
}

private val PLAYABLE_ACTIVITIES_UI = listOf(
    CreameryActivity.CLARITY,
    CreameryActivity.SCAN,
    CreameryActivity.SEQUENCE,
    CreameryActivity.CATEGORY,
    CreameryActivity.SYMMETRY,
    CreameryActivity.FLIP,
    CreameryActivity.PATTERN,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MidnightParlor(
    state: CognitiveCreameryUiState,
    onSelectActivity: (CreameryActivity) -> Unit,
    onReset: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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
                    text = "Full Creamery Suite",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreameryText,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "The original build had seven focus games. This port restores all seven as native mini-games with their own 50-level ladders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CreameryMuted,
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PLAYABLE_ACTIVITIES_UI.forEach { activity ->
                CreameryActivityCard(
                    activity = activity,
                    featured = state.flavorOfTheDay == activity,
                    completed = activity in state.clearedModes,
                    onClick = { onSelectActivity(activity) },
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PressScaleButton(
                label = "Reset Session",
                onClick = onReset,
                modifier = Modifier.weight(1f),
                filled = true,
            )
            PressScaleButton(
                label = "Finish Session",
                onClick = onFinish,
                modifier = Modifier.weight(1f),
                filled = false,
            )
        }
    }
}

@Composable
private fun CreameryActivityCard(
    activity: CreameryActivity,
    featured: Boolean,
    completed: Boolean,
    onClick: () -> Unit,
) {
    val accent = Color(activity.accent)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 1.04f else 1f, label = "activity_scale_${activity.name}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (featured) {
                        Tag("FOTD", accent, Color(0xFF140D0B))
                    }
                    if (completed) {
                        Tag("50/50", CreameryGold, Color(0xFF140D0B))
                    }
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

@Composable
private fun Tag(text: String, background: Color, content: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = content,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClarityMode(state: ClarityRound, onTap: (String) -> Unit) {
    ModeCard(title = "Find 3 related concepts", subtitle = state.theme, accent = Color(CreameryActivity.CLARITY.accent)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            state.options.forEach { word ->
                val isFound = word in state.foundWords
                val isWrong = word in state.wrongWords
                val bg by animateColorAsState(
                    targetValue = when {
                        isFound -> Color(CreameryActivity.CLARITY.accent).copy(alpha = 0.88f)
                        isWrong -> CreameryDanger.copy(alpha = 0.28f)
                        else -> Color.White.copy(alpha = 0.08f)
                    },
                    label = "clarity_$word",
                )
                TileButton(
                    text = word,
                    modifier = Modifier
                        .fillMaxWidth(0.48f)
                        .height(64.dp),
                    enabled = !isFound && !isWrong,
                    background = bg,
                    onClick = { onTap(word) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScanMode(state: ScanRound, onTap: (Int) -> Unit) {
    ModeCard(
        title = "Locate All",
        subtitle = "Find ${state.needed} of ${state.target}",
        accent = Color(CreameryActivity.SCAN.accent),
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), maxItemsInEachRow = 4) {
            state.grid.forEachIndexed { index, symbol ->
                val found = index in state.foundIndices
                val wrong = index in state.wrongIndices
                TileButton(
                    text = symbol,
                    modifier = Modifier.size(72.dp),
                    enabled = !found && !wrong,
                    background = when {
                        found -> Color(CreameryActivity.SCAN.accent).copy(alpha = 0.85f)
                        wrong -> CreameryDanger.copy(alpha = 0.3f)
                        else -> Color.White.copy(alpha = 0.08f)
                    },
                    textStyle = MaterialTheme.typography.headlineSmall,
                    onClick = { onTap(index) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SequenceMode(
    state: SequenceRound,
    onReady: () -> Unit,
    onToken: (String) -> Unit,
    onUndo: () -> Unit,
    onCheck: () -> Unit,
) {
    // AAA Mechanic: Automatic transition from STUDY to INPUT
    if (state.phase == SequencePhase.STUDY) {
        val studyDuration = 1000L + (state.targetSequence.size * 600L)
        LaunchedEffect(state.targetSequence) {
            kotlinx.coroutines.delay(studyDuration)
            onReady()
        }
    }

    ModeCard(
        title = "Sweet Sequence",
        subtitle = when (state.phase) {
            SequencePhase.STUDY -> "Watch the pattern"
            SequencePhase.INPUT -> "Repeat it"
            SequencePhase.ROUND_RESULT -> if (state.lastRoundCorrect == true) "Correct" else "Not quite"
        },
        accent = Color(CreameryActivity.SEQUENCE.accent),
    ) {
        when (state.phase) {
            SequencePhase.STUDY -> {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.targetSequence.forEach {
                        GradientChip(it, listOf(Color(CreameryActivity.SEQUENCE.accent), CreameryGold))
                    }
                }
                Spacer(Modifier.height(12.dp))
                // The "Ready" button is removed in favor of automatic transition
            }
            SequencePhase.INPUT -> {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(state.targetSequence.size) { index ->
                        val token = state.playerSequence.getOrNull(index) ?: "?"
                        GradientChip(token, listOf(CreameryGold.copy(alpha = 0.85f), Color(CreameryActivity.SEQUENCE.accent).copy(alpha = 0.8f)))
                    }
                }
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), maxItemsInEachRow = 3) {
                    state.choices.forEach {
                        TileButton(
                            text = it,
                            modifier = Modifier.size(76.dp),
                            background = Color.White.copy(alpha = 0.08f),
                            textStyle = MaterialTheme.typography.headlineSmall,
                            onClick = { onToken(it) },
                        )
                    }
                }
                // Undo and Check buttons removed per plan
            }
            SequencePhase.ROUND_RESULT -> {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.targetSequence.forEach {
                        GradientChip(it, listOf(CreameryDanger.copy(alpha = 0.86f), Color(0xFF6E3434).copy(alpha = 0.86f)))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryMode(state: CategoryRound, onTap: (Int) -> Unit) {
    ModeCard(
        title = "Target Category",
        subtitle = state.targetCategory.uppercase(),
        accent = Color(CreameryActivity.CATEGORY.accent),
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), maxItemsInEachRow = 3) {
            state.items.forEachIndexed { index, item ->
                val matched = index in state.matchedIndices
                val wrong = index in state.wrongIndices
                TileButton(
                    text = item.symbol,
                    modifier = Modifier.size(76.dp),
                    enabled = !matched && !wrong,
                    background = when {
                        matched -> Color(CreameryActivity.CATEGORY.accent).copy(alpha = 0.85f)
                        wrong -> CreameryDanger.copy(alpha = 0.3f)
                        else -> Color.White.copy(alpha = 0.08f)
                    },
                    textStyle = MaterialTheme.typography.headlineSmall,
                    onClick = { onTap(index) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SymmetryMode(state: SymmetryRound, onCycle: (Int) -> Unit, onCheck: () -> Unit) {
    val columns = if (state.cellCount == 6) 3 else 2
    ModeCard(
        title = "Sugar Symmetry",
        subtitle = "Match the blueprint",
        accent = Color(CreameryActivity.SYMMETRY.accent),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            maxItemsInEachRow = 2,
        ) {
            SymmetryGrid(title = "Blueprint", cells = state.blueprint, columns = columns, interactive = false, onTap = {})
            SymmetryGrid(title = "Your Grid", cells = state.player, columns = columns, interactive = true, onTap = onCycle)
        }
        PressScaleButton(
            label = "Check Symmetry",
            onClick = onCheck,
            filled = true,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        )
    }
}

@Composable
private fun SymmetryGrid(
    title: String,
    cells: List<Int>,
    columns: Int,
    interactive: Boolean,
    onTap: (Int) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = CreameryMuted)
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = columns) {
            cells.forEachIndexed { index, tile ->
                TileButton(
                    text = SymmetryTilesUi[tile].ifEmpty { "·" },
                    modifier = Modifier.size(56.dp),
                    enabled = interactive,
                    background = Color.White.copy(alpha = 0.08f),
                    textStyle = MaterialTheme.typography.titleLarge,
                    onClick = { onTap(index) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlipMode(state: FlipRound, onGuess: (String) -> Unit) {
    ModeCard(
        title = "Flavor Flip",
        subtitle = "Select the ${state.rule}",
        accent = Color(CreameryActivity.FLIP.accent),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("SELECT THE ${state.rule}", style = MaterialTheme.typography.labelSmall, color = CreameryMuted)
                Text(
                    text = state.displayWord,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color(state.displayColor),
                    fontWeight = FontWeight.Black,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 2
        ) {
            state.answerLabels.forEach { (label, color) ->
                TileButton(
                    text = "", // Swatch only
                    modifier = Modifier
                        .fillMaxWidth(0.48f)
                        .height(80.dp),
                    background = Color(color),
                    onClick = { onGuess(label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PatternMode(state: PatternRound, onGuess: (String) -> Unit) {
    ModeCard(
        title = "Pattern Pieces",
        subtitle = "Complete the missing treat",
        accent = Color(CreameryActivity.PATTERN.accent),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.sequence.forEach {
                Text(it, style = MaterialTheme.typography.displaySmall)
            }
            Text("?", style = MaterialTheme.typography.displaySmall, color = Color(CreameryActivity.PATTERN.accent), fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), maxItemsInEachRow = 4) {
            state.options.forEach {
                TileButton(
                    text = it,
                    modifier = Modifier.size(72.dp),
                    background = Color.White.copy(alpha = 0.08f),
                    textStyle = MaterialTheme.typography.headlineSmall,
                    onClick = { onGuess(it) },
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = {
                Text(title, style = MaterialTheme.typography.titleLarge, color = CreameryText, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = accent, fontWeight = FontWeight.SemiBold)
                content()
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResultOverlay(
    overlay: CreameryResultOverlay,
    onMenu: () -> Unit,
    onFinish: () -> Unit,
) {
    val accent = Color(overlay.activity.accent)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = overlay.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (overlay.success) accent else CreameryDanger,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = overlay.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CreameryMuted,
                    textAlign = TextAlign.Center,
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PressScaleButton("Back to Menu", onMenu, modifier = Modifier.fillMaxWidth(), filled = true)
                    PressScaleButton("Finish", onFinish, modifier = Modifier.fillMaxWidth(), filled = false)
                }
            }
        }
    }
}

@Composable
private fun GradientChip(text: String, colors: List<Color>, textStyle: TextStyle = MaterialTheme.typography.labelLarge) {
    Box(
        modifier = Modifier
            .background(brush = Brush.horizontalGradient(colors), shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(text = text, color = Color(0xFF120D0B), style = textStyle, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TileButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean = true,
    background: Color,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 1.06f else 1f, label = "tile_$text")
    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(18.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = text, style = textStyle, color = CreameryText, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun PressScaleButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    filled: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 1.08f else 1f, label = "press_scale_$label")

    if (filled) {
        FilledTonalButton(
            onClick = onClick,
            modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
            interactionSource = interactionSource,
            enabled = enabled,
        ) {
            Text(label)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
            interactionSource = interactionSource,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = CreameryGold, contentColor = Color(0xFF120D0B)),
        ) {
            Text(label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FatigueVignette(fatigue: Int) {
    if (fatigue <= 0) return
    val intensity = fatigue.toFloat() / CREAMERY_MAX_FATIGUE.toFloat()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        CreameryFrost.copy(alpha = 0.10f * intensity),
                        CreameryFrost.copy(alpha = 0.26f * intensity),
                    ),
                    radius = 1200f,
                ),
            ),
    )
}

@Composable
private fun SparkleBurst(visible: Boolean) {
    val sparkles = remember {
        listOf("✦", "✧", "✦", "✧", "✦")
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        sparkles.forEachIndexed { index, symbol ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn(initialScale = 0.6f),
                exit = fadeOut() + scaleOut(targetScale = 1.2f),
            ) {
                Text(
                    text = symbol,
                    color = if (index % 2 == 0) CreameryGold else CreameryFrost,
                    fontSize = (18 + index * 2).sp,
                    modifier = Modifier.padding((index * 6).dp),
                )
            }
        }
    }
}
