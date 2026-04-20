package com.sterlingsworld.feature.game.games.cognitivecreamery

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

internal val CreameryCard = Color(0xB8211715)
internal val CreameryCardAlt = Color(0xBD2C1F1B)
internal val CreameryGold = Color(0xFFD6AE63)
internal val CreameryText = Color(0xFFF6EADD)
internal val CreameryMuted = Color(0xFFC9B6A1)
internal val CreamerySequence = Color(0xFFD39D63)
internal val CreameryClarity = Color(0xFF8BCFD6)
internal val CreameryDanger = Color(0xFFD97A7A)
internal val CreameryFrost = Color(0xFFB9E7FF)

private enum class CreamerySfx {
    TAP, SUCCESS
}

private class CreamerySoundPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 55)

    fun play(effect: CreamerySfx) {
        when (effect) {
            CreamerySfx.TAP -> toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 60)
            CreamerySfx.SUCCESS -> toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 120)
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
    var showSparkle by remember { mutableStateOf(false) }
    var previousScore by remember { mutableIntStateOf(uiState.sessionScore) }
    var previousFatigue by remember { mutableIntStateOf(uiState.fatigueLevel) }
    var previousPhase by remember { mutableStateOf(uiState.sequence.phase) }
    var previousClarityComplete by remember { mutableStateOf(uiState.clarity.roundComplete) }
    var initialized by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { soundPlayer.release() }
    }

    LaunchedEffect(
        uiState.sessionScore,
        uiState.fatigueLevel,
        uiState.sequence.phase,
        uiState.clarity.roundComplete,
    ) {
        if (!initialized) {
            previousScore = uiState.sessionScore
            previousFatigue = uiState.fatigueLevel
            previousPhase = uiState.sequence.phase
            previousClarityComplete = uiState.clarity.roundComplete
            initialized = true
            return@LaunchedEffect
        }

        if (uiState.sessionScore > previousScore) {
            soundPlayer.play(CreamerySfx.SUCCESS)
        }
        if (
            (uiState.sequence.phase == CreameryPhase.ROUND_RESULT &&
                previousPhase != CreameryPhase.ROUND_RESULT &&
                uiState.sequence.lastRoundCorrect) ||
            (uiState.clarity.roundComplete && !previousClarityComplete)
        ) {
            showSparkle = true
            delay(850)
            showSparkle = false
        }

        previousScore = uiState.sessionScore
        previousFatigue = uiState.fatigueLevel
        previousPhase = uiState.sequence.phase
        previousClarityComplete = uiState.clarity.roundComplete
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_cognitive_creamery),
            contentDescription = "Cognitive Creamery parlor background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f)),
        )
        FatigueVignette(fatigueLevel = uiState.fatigueLevel)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CreameryTopBar(
                state = uiState,
                previousFatigue = previousFatigue,
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
                    onFinish = { onDone(vm.buildResult()) },
                    onReset = vm::resetSession,
                )
                CreameryActivity.SEQUENCE -> SequenceSuite(
                    state = uiState.sequence,
                    fatigueIncreased = uiState.fatigueLevel > previousFatigue,
                    onReady = {
                        soundPlayer.play(CreamerySfx.TAP)
                        vm.onReady()
                    },
                    onTokenTapped = {
                        soundPlayer.play(CreamerySfx.TAP)
                        vm.onTokenTapped(it)
                    },
                    onUndo = vm::onUndo,
                    onCheck = vm::onCheck,
                    onNextRound = {
                        soundPlayer.play(CreamerySfx.TAP)
                        vm.onNextRound()
                    },
                    onBackToParlor = vm::backToParlor,
                    onDone = { onDone(vm.buildResult()) },
                )
                CreameryActivity.CLARITY -> CognitiveClarityGame(
                    state = uiState.clarity,
                    onWordTapped = {
                        soundPlayer.play(CreamerySfx.TAP)
                        vm.onClarityWordTapped(it)
                    },
                    onNextRound = {
                        soundPlayer.play(CreamerySfx.TAP)
                        vm.onClarityNextRound()
                    },
                    onBackToParlor = vm::backToParlor,
                )
            }
        }

        SparkleBurst(visible = showSparkle)

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
    previousFatigue: Int,
    onBackToParlor: () -> Unit,
    onFinish: () -> Unit,
) {
    val pulse by rememberInfiniteTransition(label = "fatigue_pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 900
                1f at 0
                1.06f at 450
                1f at 900
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "fatigue_pulse_value",
    )
    val wobbleX = remember { Animatable(0f) }
    LaunchedEffect(state.fatigueLevel) {
        if (state.fatigueLevel <= previousFatigue) return@LaunchedEffect
        val offsets = listOf(-8f, 7f, -5f, 4f, -2f, 0f)
        offsets.forEach {
            wobbleX.snapTo(it)
            delay(22)
        }
        wobbleX.snapTo(0f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.shadow(12.dp, RoundedCornerShape(24.dp)),
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
                        PressScaleButton(
                            label = "Parlor",
                            onClick = onBackToParlor,
                            filled = true,
                        )
                    }
                    PressScaleButton(
                        label = "Cash Out",
                        onClick = onFinish,
                        filled = false,
                    )
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
                    .offset { IntOffset(wobbleX.value.toInt(), 0) }
                    .graphicsLayer {
                        val levelRatio = state.fatigueLevel.toFloat() / MAX_FATIGUE.toFloat()
                        val scale = if (levelRatio >= 0.8f) pulse else 1f
                        scaleX = scale
                        scaleY = scale
                    }
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.fatigueLevel.toFloat() / MAX_FATIGUE.toFloat())
                        .height(12.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(CreameryClarity, CreameryGold, CreameryDanger),
                            ),
                            shape = RoundedCornerShape(999.dp),
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
                    text = "Sequence and Clarity now play inside one moody parlor with a shared fatigue meter.",
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
                CreameryActivityCard(
                    activity = activity,
                    featured = state.flavorOfTheDay == activity,
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
    onClick: () -> Unit,
) {
    val accent = when (activity) {
        CreameryActivity.SEQUENCE -> CreamerySequence
        CreameryActivity.CLARITY -> CreameryClarity
        CreameryActivity.PARLOR -> CreameryGold
    }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 1.08f else 1f, label = "activity_scale_${activity.name}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
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
                if (featured) {
                    Text(
                        text = "FOTD",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF140D0B),
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .background(accent, RoundedCornerShape(999.dp))
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

@Composable
private fun SequenceSuite(
    state: SequenceUiState,
    fatigueIncreased: Boolean,
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
            fatigueIncreased = fatigueIncreased,
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
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 }),
                        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it / 6 }),
                    ) {
                        GradientChip(
                            text = "${index + 1}. $flavor",
                            colors = listOf(
                                CreamerySequence.copy(alpha = 0.82f),
                                CreameryGold.copy(alpha = 0.82f),
                            ),
                        )
                    }
                }
            }
            PressScaleButton(
                label = "Ready",
                onClick = onReady,
                modifier = Modifier.fillMaxWidth(),
                filled = false,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SequenceInputPhase(
    state: SequenceUiState,
    fatigueIncreased: Boolean,
    onTokenTapped: (String) -> Unit,
    onUndo: () -> Unit,
    onCheck: () -> Unit,
) {
    val sequenceFull = state.playerSequence.size == state.targetSequence.size
    val wobble = remember { Animatable(0f) }
    LaunchedEffect(fatigueIncreased) {
        if (!fatigueIncreased) return@LaunchedEffect
        listOf(-9f, 8f, -6f, 4f, -2f, 0f).forEach {
            wobble.snapTo(it)
            delay(18)
        }
        wobble.snapTo(0f)
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreameryCard),
        modifier = Modifier.offset { IntOffset(wobble.value.toInt(), 0) },
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
                    GradientChip(
                        text = "${index + 1}. $flavor",
                        colors = listOf(
                            CreameryGold.copy(alpha = 0.88f),
                            CreamerySequence.copy(alpha = 0.80f),
                        ),
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.availableTokens.forEach { token ->
                    PressScaleButton(
                        label = token,
                        onClick = { onTokenTapped(token) },
                        filled = true,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.playerSequence.isNotEmpty()) {
                    PressScaleButton(
                        label = "Undo",
                        onClick = onUndo,
                        filled = true,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                PressScaleButton(
                    label = "Check",
                    onClick = onCheck,
                    enabled = sequenceFull,
                    filled = false,
                )
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
                        GradientChip(
                            text = "${index + 1}. $flavor",
                            colors = listOf(
                                CreameryDanger.copy(alpha = 0.86f),
                                Color(0xFF6E3434).copy(alpha = 0.86f),
                            ),
                        )
                    }
                }
            }
            PressScaleButton(
                label = "Next Round",
                onClick = onNextRound,
                modifier = Modifier.fillMaxWidth(),
                filled = false,
            )
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
                PressScaleButton(
                    label = "Back to Parlor",
                    onClick = onBackToParlor,
                    modifier = Modifier.weight(1f),
                    filled = true,
                )
                PressScaleButton(
                    label = "Finish",
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                    filled = false,
                )
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
                    PressScaleButton(
                        label = "Recover",
                        onClick = onRecover,
                        modifier = Modifier.weight(1f),
                        filled = true,
                    )
                    PressScaleButton(
                        label = "Finish",
                        onClick = onFinish,
                        modifier = Modifier.weight(1f),
                        filled = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientChip(
    text: String,
    colors: List<Color>,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(colors),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            color = Color(0xFF120D0B),
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
        )
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
    val scale by animateFloatAsState(if (pressed) 1.1f else 1f, label = "press_scale_$label")

    if (filled) {
        FilledTonalButton(
            onClick = onClick,
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            interactionSource = interactionSource,
            enabled = enabled,
        ) {
            Text(label)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            interactionSource = interactionSource,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = CreameryGold,
                contentColor = Color(0xFF120D0B),
            ),
        ) {
            Text(label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FatigueVignette(fatigueLevel: Int) {
    if (fatigueLevel <= 0) return
    val intensity = fatigueLevel.toFloat() / MAX_FATIGUE.toFloat()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        CreameryFrost.copy(alpha = 0.12f * intensity),
                        CreameryFrost.copy(alpha = 0.32f * intensity),
                    ),
                    radius = 1200f,
                ),
            ),
    )
}

@Composable
private fun SparkleBurst(visible: Boolean) {
    val sparkles = remember {
        listOf(
            Triple(IntOffset(-42, -36), 22.sp, 0L),
            Triple(IntOffset(38, -28), 18.sp, 80L),
            Triple(IntOffset(-18, 30), 16.sp, 140L),
            Triple(IntOffset(28, 34), 20.sp, 40L),
            Triple(IntOffset(0, -54), 24.sp, 110L),
        )
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        sparkles.forEachIndexed { index, (offset, size, delayMs) ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn(initialScale = 0.6f),
                exit = fadeOut() + scaleOut(targetScale = 1.2f),
            ) {
                Text(
                    text = if (index % 2 == 0) "✦" else "✧",
                    color = if (index % 2 == 0) CreameryGold else CreameryClarity,
                    fontSize = size,
                    modifier = Modifier.offset {
                        IntOffset(offset.x, offset.y)
                    },
                )
            }
        }
    }
}
