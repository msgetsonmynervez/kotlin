package com.sterlingsworld.feature.game.games.symptomstriker

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.domain.model.GameResult
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BattleTextPrimary = Color(0xFFF3EFE2)
private val BattlePrimary = Color(0xFF2D6A4F)
private val BattleSecondary = Color(0xFFC97B63)
private val BattleAccent = Color(0xFFF4B942)
private val BattleError = Color(0xFFB04A3A)
private val BattleSuccess = Color(0xFF4D8C57)
private val BattleOverlay = Color(0x8C173224)

private data class CombatFeedback(
    val id: Int,
    val text: String,
    val color: Color,
    val alignment: Alignment,
)

private enum class SymptomStrikerSfx {
    SELECT, IMPACT, HIT, FANFARE
}

private class SymptomStrikerSoundPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 65)

    fun play(effect: SymptomStrikerSfx) {
        when (effect) {
            SymptomStrikerSfx.SELECT -> toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
            SymptomStrikerSfx.IMPACT -> toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 110)
            SymptomStrikerSfx.HIT -> toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 100)
            SymptomStrikerSfx.FANFARE -> toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 220)
        }
    }

    fun release() {
        toneGenerator.release()
    }
}

@Composable
fun SymptomStrikerGame(
    vm: SymptomStrikerViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val overlayVisible = state.phase != BattlePhase.PLAYER_TURN
    val soundPlayer = remember { SymptomStrikerSoundPlayer() }
    val feedbacks = remember { mutableStateListOf<CombatFeedback>() }
    var feedbackId by remember { mutableIntStateOf(0) }
    var previousPlayerHp by remember { mutableIntStateOf(state.playerHp) }
    var previousEnemyHp by remember { mutableIntStateOf(state.enemyHp) }
    var previousPhase by remember { mutableStateOf(state.phase) }
    var initialized by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { soundPlayer.release() }
    }

    LaunchedEffect(state.playerHp, state.enemyHp, state.phase) {
        if (!initialized) {
            previousPlayerHp = state.playerHp
            previousEnemyHp = state.enemyHp
            previousPhase = state.phase
            initialized = true
            return@LaunchedEffect
        }

        when {
            state.playerHp < previousPlayerHp -> {
                val amount = previousPlayerHp - state.playerHp
                feedbacks += CombatFeedback(
                    id = feedbackId++,
                    text = "-$amount",
                    color = BattleError,
                    alignment = Alignment.BottomCenter,
                )
                soundPlayer.play(SymptomStrikerSfx.IMPACT)
            }
            state.playerHp > previousPlayerHp -> {
                val amount = state.playerHp - previousPlayerHp
                feedbacks += CombatFeedback(
                    id = feedbackId++,
                    text = "+$amount",
                    color = BattleSuccess,
                    alignment = Alignment.BottomCenter,
                )
            }
        }

        when {
            state.enemyHp < previousEnemyHp -> {
                val amount = previousEnemyHp - state.enemyHp
                feedbacks += CombatFeedback(
                    id = feedbackId++,
                    text = "-$amount",
                    color = BattleError,
                    alignment = Alignment.Center,
                )
                soundPlayer.play(SymptomStrikerSfx.HIT)
            }
            state.enemyHp > previousEnemyHp -> {
                val amount = state.enemyHp - previousEnemyHp
                feedbacks += CombatFeedback(
                    id = feedbackId++,
                    text = "+$amount",
                    color = BattleSuccess,
                    alignment = Alignment.Center,
                )
            }
        }

        if (state.phase == BattlePhase.ENCOUNTER_WIN && previousPhase != BattlePhase.ENCOUNTER_WIN) {
            soundPlayer.play(SymptomStrikerSfx.FANFARE)
        }

        previousPlayerHp = state.playerHp
        previousEnemyHp = state.enemyHp
        previousPhase = state.phase
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_symptom_striker),
            contentDescription = "Symptom Striker battle background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .then(if (overlayVisible) Modifier.blur(8.dp) else Modifier),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xD9111B17),
                            Color(0x9911171B),
                            Color(0xE6141D1A),
                        ),
                    ),
                ),
        )

        BattleField(
            state = state,
            overlayVisible = overlayVisible,
            onMoveSelected = {
                soundPlayer.play(SymptomStrikerSfx.SELECT)
                vm.onMoveSelected(it)
            },
        )

        feedbacks.forEach { feedback ->
            FloatingCombatText(
                feedback = feedback,
                onFinished = { finishedId -> feedbacks.removeAll { it.id == finishedId } },
            )
        }

        when (state.phase) {
            BattlePhase.INTRO -> IntroOverlay(
                state = state,
                onBegin = vm::onDismissIntro,
            )
            BattlePhase.ENCOUNTER_WIN -> EncounterWinOverlay(
                state = state,
                onNext = vm::onNextEncounter,
            )
            BattlePhase.ENCOUNTER_LOSS -> OutcomeOverlay(
                won = false,
                state = state,
                onFinish = { onDone(vm.buildResult()) },
            )
            BattlePhase.RUN_WIN -> OutcomeOverlay(
                won = true,
                state = state,
                onFinish = { onDone(vm.buildResult()) },
            )
            BattlePhase.PLAYER_TURN -> Unit
        }
    }
}

@Composable
private fun BattleField(
    state: SymptomStrikerUiState,
    overlayVisible: Boolean,
    onMoveSelected: (String) -> Unit,
) {
    val turnSignature = "${state.phase}:${state.playerHp}:${state.enemyHp}:${state.battleLog}"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        EncounterHeader(state)
        EnemyCard(state)
        PlayerStatusCard(state)
        SessionProgressBanner(state)
        StatusHud(state)
        AnimatedContent(
            targetState = turnSignature,
            transitionSpec = {
                (fadeIn(animationSpec = keyframes { durationMillis = 220 }) + slideInVertically { it / 5 })
                    .togetherWith(fadeOut(animationSpec = keyframes { durationMillis = 160 }) + slideOutVertically { -it / 8 })
            },
            label = "battle_turn_transition",
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BattleLog(state)
                MoveGrid(state, onMoveSelected)
            }
        }
        if (overlayVisible) {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EncounterHeader(state: SymptomStrikerUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = state.encounterTitle,
            style = MaterialTheme.typography.titleSmall,
            color = BattleTextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${state.encounterIndex + 1} / ${state.totalEncounters}",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.75f),
        )
    }
}

@Composable
private fun EnemyCard(state: SymptomStrikerUiState) {
    val shake = rememberHpShake(state.enemyHp)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.58f)),
        modifier = Modifier
            .fillMaxWidth()
            .offset { shake },
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.enemyName,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (state.enemyEnraged) Color(0xFFFFD0C7) else BattleTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                if (state.enemyEnraged) {
                    Text(
                        text = "\u26a0\ufe0f RAGE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF6E40),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            GlowingSprite(
                portraitRes = state.enemyPortraitRes,
                sprite = state.enemySprite,
                enraged = state.enemyEnraged,
            )
            HpBar(
                current = state.enemyHp,
                max = state.enemyMaxHp,
                color = if (state.enemyEnraged) Color(0xFFFF7043) else BattleSecondary,
                label = "HP: ${state.enemyHp} / ${state.enemyMaxHp}",
            )
        }
    }
}

@Composable
private fun GlowingSprite(
    portraitRes: Int?,
    sprite: String,
    enraged: Boolean,
) {
    val pulse by rememberInfiniteTransition(label = "sprite_pulse").animateFloat(
        initialValue = 0.75f,
        targetValue = if (enraged) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = if (enraged) 900 else 1800
                1.0f at 0
                1.15f at durationMillis / 2
                if (enraged) 0.85f else 1.0f at durationMillis
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "glow_strength",
    )
    val glowColor = if (enraged) Color(0xFFFF7043) else Color(0xFF59E3FF)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val radius = size.minDimension * 0.45f
                drawCircle(
                    color = glowColor.copy(alpha = 0.18f * pulse),
                    radius = radius,
                    center = center,
                )
                drawCircle(
                    color = glowColor.copy(alpha = 0.10f * pulse),
                    radius = radius * 1.35f,
                    center = center,
                    style = Stroke(width = 4.dp.toPx()),
                )
            }
            .padding(vertical = 8.dp),
    ) {
        if (portraitRes != null) {
            Image(
                painter = painterResource(id = portraitRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .shadow(if (enraged) 18.dp else 10.dp, RoundedCornerShape(16.dp)),
            )
        } else {
            Text(
                text = sprite,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                color = if (enraged) Color(0xFFFFE0D4) else Color(0xFFE6FAFF),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .shadow(if (enraged) 18.dp else 10.dp, RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun PlayerStatusCard(state: SymptomStrikerUiState) {
    val shake = rememberHpShake(state.playerHp)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.52f)),
        modifier = Modifier
            .fillMaxWidth()
            .offset { shake },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HpBar(
                current = state.playerHp,
                max = state.playerMaxHp,
                color = BattleSuccess,
                label = if (state.status.masked > 0) {
                    "Your HP: ???"
                } else {
                    "Your HP: ${state.playerHp} / ${state.playerMaxHp}"
                },
                masked = state.status.masked > 0,
            )
            SpoonRow(
                spoons = state.playerSpoons,
                maxSpoons = state.playerMaxSpoons,
                masked = state.status.masked > 0,
            )
            if (state.sessionSpoonPenalty > 0) {
                Text(
                    text = "Overuse penalty: \u2212${state.sessionSpoonPenalty} max Spoons this session",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFAB91),
                )
            }
        }
    }
}

@Composable
private fun SessionProgressBanner(state: SymptomStrikerUiState) {
    if (state.unlockedMoveLabels.isEmpty() && state.latestUnlockedMoveLabel == null) return

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.48f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            state.latestUnlockedMoveLabel?.let { latest ->
                Text(
                    text = "Session unlock: $latest",
                    style = MaterialTheme.typography.labelMedium,
                    color = BattleAccent,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (state.unlockedMoveLabels.isNotEmpty()) {
                Text(
                    text = "Unlocked moves: ${state.unlockedMoveLabels.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.74f),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatusHud(state: SymptomStrikerUiState) {
    if (state.status.active.isEmpty()) return

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                state.status.active.forEach { (key, turns) ->
                    StatusBadge(key = key, turns = turns)
                }
            }
            state.status.active.forEach { (key, _) ->
                Text(
                    text = "${key.badge} ${key.label}: ${key.tooltip} Counter: ${key.counteredBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.72f),
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(key: StatusKey, turns: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BattleAccent.copy(alpha = 0.28f),
    ) {
        Text(
            text = "${key.badge} ${key.label} (${turns}t)",
            style = MaterialTheme.typography.labelMedium,
            color = BattleTextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun BattleLog(state: SymptomStrikerUiState) {
    if (state.battleLog.isBlank()) return
    val snippets = remember(state.battleLog) { buildLogSnippets(state.battleLog) }
    var expanded by remember(state.battleLog) { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = if (expanded) state.battleLog else snippets.joinToString("\n"),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp,
            )
            if (snippets.size > 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = if (expanded) "Hide full log" else "See full log",
                        style = MaterialTheme.typography.labelSmall,
                        color = BattleAccent,
                        modifier = Modifier
                            .background(Color.Transparent)
                            .padding(top = 2.dp),
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Text(
                            text = if (expanded) "\u25B2" else "\u25BC",
                            color = BattleAccent,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MoveGrid(
    state: SymptomStrikerUiState,
    onMoveSelected: (String) -> Unit,
) {
    val regularMoves = remember(state.moves, state.status.numb, state.battleLog) {
        val source = state.moves.filter { it.id != "push_through" }
        if (state.status.numb > 0) source.shuffled(Random(state.battleLog.hashCode().toLong())) else source
    }
    val pushThrough = state.moves.find { it.id == "push_through" }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2,
    ) {
        regularMoves.forEach { move ->
            val isBlocked = state.status.foggedMoveId == move.id
            val effectiveCost = if (move.type == MoveType.ATTACK && state.status.locked > 0) {
                move.spoonCost + com.sterlingsworld.feature.game.games.symptomstriker.DEFAULT_BATTLE_CONFIG.lockedExtraSpoonCost
            } else {
                move.spoonCost
            }
            val canAfford = state.playerSpoons >= effectiveCost || move.type == MoveType.RECOVERY

            MoveButton(
                move = move,
                blocked = isBlocked,
                disabled = !canAfford || state.phase != BattlePhase.PLAYER_TURN,
                effectiveCost = if (effectiveCost != move.spoonCost) effectiveCost else null,
                modifier = Modifier.weight(1f),
                onClick = { onMoveSelected(move.id) },
            )
        }
    }

    if (pushThrough != null) {
        val pushThroughOverused = state.pushThroughUses >= state.pushThroughSafeUses
        OutlinedButton(
            onClick = { onMoveSelected("push_through") },
            enabled = state.phase == BattlePhase.PLAYER_TURN,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (pushThroughOverused) Color(0xFFFFAB91) else BattleSecondary,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = pushThrough.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (pushThroughOverused) {
                        "Costs HP \u2022 OVERUSE \u2014 next use loses 1 max Spoon"
                    } else {
                        "Costs HP \u2022 ${state.pushThroughUses}/${state.pushThroughSafeUses} safe uses"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (pushThroughOverused) Color(0xFFFFAB91) else Color.White.copy(alpha = 0.72f),
                )
            }
        }
    }
}

@Composable
private fun MoveButton(
    move: MoveDefinition,
    blocked: Boolean,
    disabled: Boolean,
    effectiveCost: Int?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val subLabel = effectiveCost?.let { "Cost: $it" } ?: move.subLabel
    FilledTonalButton(
        onClick = onClick,
        enabled = !disabled,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = when {
                blocked -> BattleError.copy(alpha = 0.28f)
                move.type == MoveType.RECOVERY -> BattleSuccess.copy(alpha = 0.22f)
                move.type == MoveType.CURE -> BattlePrimary.copy(alpha = 0.22f)
                else -> Color.Black.copy(alpha = 0.48f)
            },
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (blocked) "\uD83D\uDEAB ${move.label}" else move.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (blocked) Color(0xFFFFCCBC) else BattleTextPrimary,
                textAlign = TextAlign.Center,
            )
            if (subLabel.isNotEmpty()) {
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (blocked) Color(0xFFFFCCBC) else Color.White.copy(alpha = 0.72f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun HpBar(
    current: Int,
    max: Int,
    color: Color,
    label: String,
    masked: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.74f))
        LinearProgressIndicator(
            progress = { if (masked) 1f else if (max > 0) current.toFloat() / max else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .shadow(8.dp, RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.18f), RoundedCornerShape(6.dp)),
            color = if (masked) BattleAccent else color,
            trackColor = if (masked) BattleAccent.copy(alpha = 0.25f) else color.copy(alpha = 0.18f),
        )
    }
}

@Composable
private fun SpoonRow(spoons: Int, maxSpoons: Int, masked: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = if (masked) "Spoons: ???" else "Spoons: $spoons / $maxSpoons",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.74f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(maxSpoons) { index ->
                Icon(
                    painter = painterResource(id = R.drawable.ic_spoon),
                    contentDescription = null,
                    tint = when {
                        masked -> BattleAccent
                        index < spoons -> BattleAccent
                        else -> BattleAccent.copy(alpha = 0.22f)
                    },
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun FloatingCombatText(
    feedback: CombatFeedback,
    onFinished: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val travel = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(feedback.id) {
        launch {
            travel.animateTo(
                targetValue = -56f,
                animationSpec = keyframes {
                    durationMillis = 1000
                    0f at 0 with LinearOutSlowInEasing
                    -56f at 1000
                },
            )
        }
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 1000
                1f at 0 with FastOutLinearInEasing
                0f at 1000
            },
        )
        onFinished(feedback.id)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = feedback.alignment,
    ) {
        Text(
            text = feedback.text,
            color = feedback.color,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = with(density) { travel.value.dp.roundToPx() },
                    )
                }
                .alpha(alpha.value)
                .shadow(10.dp),
        )
    }
}

@Composable
private fun IntroOverlay(state: SymptomStrikerUiState, onBegin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BattleOverlay),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.68f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.encounterTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = BattleAccent,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.introText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BattleTextPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.symptomsDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f),
                    textAlign = TextAlign.Center,
                )
                Button(
                    onClick = onBegin,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Begin Battle")
                }
            }
        }
    }
}

@Composable
private fun EncounterWinOverlay(state: SymptomStrikerUiState, onNext: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BattleOverlay),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.68f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "\u2705 Gym Cleared!",
                    style = MaterialTheme.typography.titleLarge,
                    color = BattleSuccess,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = state.battleLog,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f),
                    textAlign = TextAlign.Center,
                )
                state.latestUnlockedMoveLabel?.let { unlocked ->
                    Text(
                        text = "Unlocked for the rest of this run: $unlocked",
                        style = MaterialTheme.typography.labelMedium,
                        color = BattleAccent,
                        textAlign = TextAlign.Center,
                    )
                }
                Text(
                    text = "HP remaining: ${state.playerHp} / ${state.playerMaxHp}",
                    style = MaterialTheme.typography.labelMedium,
                    color = BattleTextPrimary,
                )
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Next Gym \u2192")
                }
            }
        }
    }
}

@Composable
private fun OutcomeOverlay(
    won: Boolean,
    state: SymptomStrikerUiState,
    onFinish: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BattleOverlay),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.68f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (won) "\uD83C\uDFC6 Run Complete!" else "\u274C Fell Short",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (won) BattleSuccess else BattleError,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (won) {
                        "You cleared all ${state.totalEncounters} gyms. Final HP: ${state.playerHp} / ${state.playerMaxHp}."
                    } else {
                        "You ran out of HP at ${state.encounterTitle}. Gyms cleared: ${state.encountersCleared} / ${state.totalEncounters}."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = BattleTextPrimary,
                    textAlign = TextAlign.Center,
                )
                if (state.sessionSpoonPenalty > 0) {
                    Text(
                        text = "Push Through penalty: \u2212${state.sessionSpoonPenalty} max Spoon(s) used",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFAB91),
                    )
                }
                if (won && state.unlockedMoveLabels.isNotEmpty()) {
                    Text(
                        text = "Session unlocks carried: ${state.unlockedMoveLabels.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BattleAccent,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (won) BattleSuccess else BattlePrimary,
                    ),
                ) {
                    Text(if (won) "Finish Run" else "End Run")
                }
            }
        }
    }
}

@Composable
private fun rememberHpShake(current: Int): IntOffset {
    val x = remember { Animatable(0f) }
    val y = remember { Animatable(0f) }
    var previous by remember { mutableIntStateOf(current) }
    val density = LocalDensity.current

    LaunchedEffect(current) {
        if (current >= previous) {
            previous = current
            return@LaunchedEffect
        }
        previous = current
        val offsets = List(6) {
            Pair(Random.nextInt(-7, 8).toFloat(), Random.nextInt(-5, 6).toFloat())
        } + listOf(0f to 0f)
        offsets.forEach { (dx, dy) ->
            x.snapTo(dx)
            y.snapTo(dy)
            delay(24)
        }
        x.animateTo(0f)
        y.animateTo(0f)
    }

    return IntOffset(
        x = with(density) { x.value.dp.roundToPx() },
        y = with(density) { y.value.dp.roundToPx() },
    )
}

private fun buildLogSnippets(log: String): List<String> {
    val parts = log
        .split(Regex("(?<=[.!?])\\s+"))
        .map { it.trim() }
        .filter { it.isNotBlank() }
    return if (parts.size <= 3) parts else parts.takeLast(3)
}
