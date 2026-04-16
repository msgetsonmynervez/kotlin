package com.sterlingsworld.feature.game.games.symptomstriker

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.core.ui.theme.Accent
import com.sterlingsworld.core.ui.theme.ErrorColor
import com.sterlingsworld.core.ui.theme.Overlay
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Secondary
import com.sterlingsworld.core.ui.theme.SuccessColor
import com.sterlingsworld.core.ui.theme.Surface as AppSurface
import com.sterlingsworld.core.ui.theme.SurfaceStrong
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.domain.model.GameResult

@Composable
fun SymptomStrikerGame(
    vm: SymptomStrikerViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // Battle field — always rendered beneath overlays
        BattleField(state = state, onMoveSelected = vm::onMoveSelected)

        // Overlays on top
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

// ── Battle field ─────────────────────────────────────────────────────────────

@Composable
private fun BattleField(
    state: SymptomStrikerUiState,
    onMoveSelected: (String) -> Unit,
) {
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
        StatusHud(state)
        BattleLog(state)
        MoveGrid(state, onMoveSelected)
        Spacer(Modifier.height(8.dp))
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
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${state.encounterIndex + 1} / ${state.totalEncounters}",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
        )
    }
}

@Composable
private fun EnemyCard(state: SymptomStrikerUiState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceStrong),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.enemyName,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (state.enemyEnraged) ErrorColor else TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                if (state.enemyEnraged) {
                    Text(
                        text = "\u26a0\ufe0f RAGE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ErrorColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            // Enemy sprite — monospace text art
            Text(
                text = state.enemySprite,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                color = if (state.enemyEnraged) ErrorColor else TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            // Enemy HP bar
            HpBar(
                current = state.enemyHp,
                max = state.enemyMaxHp,
                color = if (state.enemyEnraged) ErrorColor else Secondary,
                label = "HP: ${state.enemyHp} / ${state.enemyMaxHp}",
            )
        }
    }
}

@Composable
private fun PlayerStatusCard(state: SymptomStrikerUiState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Player HP
            HpBar(
                current = state.playerHp,
                max = state.playerMaxHp,
                color = SuccessColor,
                label = "Your HP: ${state.playerHp} / ${state.playerMaxHp}",
            )
            // Spoon dots
            SpoonRow(spoons = state.playerSpoons, maxSpoons = state.playerMaxSpoons)
            // Push through warning if penalty triggered
            if (state.sessionSpoonPenalty > 0) {
                Text(
                    text = "Overuse penalty: \u2212${state.sessionSpoonPenalty} max Spoons this session",
                    style = MaterialTheme.typography.labelSmall,
                    color = ErrorColor,
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
        colors = CardDefaults.cardColors(containerColor = SurfaceStrong),
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
            // Tooltips for each active status so the player knows what to do
            state.status.active.forEach { (key, _) ->
                Text(
                    text = "${key.badge} ${key.label}: ${key.tooltip} Counter: ${key.counteredBy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(key: StatusKey, turns: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Accent.copy(alpha = 0.25f),
        modifier = Modifier.padding(0.dp),
    ) {
        Text(
            text = "${key.badge} ${key.label} (${turns}t)",
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun BattleLog(state: SymptomStrikerUiState) {
    if (state.battleLog.isBlank()) return
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = state.battleLog,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(10.dp),
            lineHeight = 18.sp,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MoveGrid(
    state: SymptomStrikerUiState,
    onMoveSelected: (String) -> Unit,
) {
    val regularMoves = state.moves.filter { it.id != "push_through" }
    val pushThrough = state.moves.find { it.id == "push_through" }

    // Regular moves in 2-column grid
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2,
    ) {
        regularMoves.forEach { move ->
            val isBlocked = state.status.foggedMoveId == move.id
            val effectiveCost = if (move.type == MoveType.ATTACK && state.status.locked > 0) {
                move.spoonCost + DEFAULT_BATTLE_CONFIG.lockedExtraSpoonCost
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

    // Push Through — full width, visually distinct
    if (pushThrough != null) {
        val pushThroughOverused = state.pushThroughUses >= state.pushThroughSafeUses
        OutlinedButton(
            onClick = { onMoveSelected("push_through") },
            enabled = state.phase == BattlePhase.PLAYER_TURN,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (pushThroughOverused) ErrorColor else Secondary,
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
                    color = if (pushThroughOverused) ErrorColor else TextMuted,
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
                blocked -> ErrorColor.copy(alpha = 0.15f)
                move.type == MoveType.RECOVERY -> SuccessColor.copy(alpha = 0.15f)
                move.type == MoveType.CURE -> Primary.copy(alpha = 0.15f)
                else -> SurfaceStrong
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
                color = if (blocked) ErrorColor else TextPrimary,
                textAlign = TextAlign.Center,
            )
            if (subLabel.isNotEmpty()) {
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (blocked) ErrorColor else TextMuted,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ── Shared small widgets ──────────────────────────────────────────────────────

@Composable
private fun HpBar(current: Int, max: Int, color: Color, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        LinearProgressIndicator(
            progress = { if (max > 0) current.toFloat() / max else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
        )
    }
}

@Composable
private fun SpoonRow(spoons: Int, maxSpoons: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "Spoons: $spoons / $maxSpoons",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(maxSpoons) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (index < spoons) Accent else Accent.copy(alpha = 0.2f)),
                )
            }
        }
    }
}

// ── Overlays ──────────────────────────────────────────────────────────────────

@Composable
private fun IntroOverlay(state: SymptomStrikerUiState, onBegin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Overlay),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceStrong),
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
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.introText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.symptomsDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
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
            .background(Overlay),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceStrong),
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
                    color = SuccessColor,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = state.battleLog,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "HP remaining: ${state.playerHp} / ${state.playerMaxHp}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
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
            .background(Overlay),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceStrong),
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
                    color = if (won) SuccessColor else ErrorColor,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (won) {
                        "You cleared all ${state.totalEncounters} gyms. Final HP: ${state.playerHp} / ${state.playerMaxHp}."
                    } else {
                        "You ran out of HP at ${state.encounterTitle}. Gyms cleared: ${state.encountersCleared} / ${state.totalEncounters}."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                )
                if (state.sessionSpoonPenalty > 0) {
                    Text(
                        text = "Push Through penalty: \u2212${state.sessionSpoonPenalty} max Spoon(s) used",
                        style = MaterialTheme.typography.labelSmall,
                        color = ErrorColor,
                    )
                }
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (won) SuccessColor else Primary,
                    ),
                ) {
                    Text(if (won) "Finish Run" else "End Run")
                }
            }
        }
    }
}
