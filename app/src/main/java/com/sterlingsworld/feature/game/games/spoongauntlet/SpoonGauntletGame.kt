package com.sterlingsworld.feature.game.games.spoongauntlet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.delay

private val SpoonBg = Color(0xFF1E1B18)
private val SpoonPanel = Color(0xF02A241D)
private val SpoonText = Color(0xFFF5EDE1)
private val SpoonMuted = Color(0xFFCFBEA8)
private val SpoonGold = Color(0xFFD8B25A)
private val SpoonWarm = Color(0xFFD99058)
private val SpoonDanger = Color(0xFFB65448)
private val SpoonAccent = Color(0xFF89C28A)

@Composable
fun SpoonGauntletGame(
    vm: SpoonGauntletViewModel = viewModel(),
    onDone: (GameResult) -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val scene = SPOON_GAUNTLET_SCENES.getOrNull(uiState.currentSceneIndex)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_spoon_gauntlet),
            contentDescription = "Spoon Gauntlet background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.55f), SpoonBg.copy(alpha = 0.94f)))),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (uiState.screen in setOf(SpoonGauntletScreen.SCENE, SpoonGauntletScreen.FLARE_UP, SpoonGauntletScreen.RESULT)) {
                GauntletHud(uiState = uiState)
            }

            when (uiState.screen) {
                SpoonGauntletScreen.TITLE -> TitleScreen(vm::startSession)
                SpoonGauntletScreen.INTRO -> IntroScreen(vm::acknowledgeIntro)
                SpoonGauntletScreen.HERO_SELECT -> HeroScreen(vm::pickHero)
                SpoonGauntletScreen.BOT_INTRO -> BotIntroScreen(vm::continueToBotCatalog)
                SpoonGauntletScreen.BOT_SELECT -> BotScreen(vm::pickBot)
                SpoonGauntletScreen.AGENDA -> AgendaScreen(
                    uiState = uiState,
                    onPickAgenda = vm::pickAgenda,
                )
                SpoonGauntletScreen.SCENE -> if (scene != null) {
                    SceneScreen(
                        uiState = uiState,
                        scene = scene,
                        choices = vm.availableChoices(),
                        onConfirm = vm::confirmChoice,
                    )
                }
                SpoonGauntletScreen.FLARE_UP -> FlareUpScreen(
                    uiState = uiState,
                    onRecover = vm::recoverFromFlareUp,
                )
                SpoonGauntletScreen.RESULT -> ResultScreen(
                    uiState = uiState,
                    onRestart = vm::resetSession,
                    onFinish = { onDone(vm.buildResult()) },
                )
            }
        }
    }
}

@Composable
private fun TitleScreen(onStart: () -> Unit) {
    StoryPanel(
        kicker = "Sterling Arcade",
        title = "Sterling's Spoon Sprint",
        subtitle = "Boundary choices under pressure",
        imageRes = R.drawable.spoon_title_cast,
    ) {
        HoldActionButton(
            label = "Start Session",
            tone = GauntletChoiceTone.NEUTRAL,
            onConfirmed = onStart,
        )
    }
}

@Composable
private fun IntroScreen(onContinue: () -> Unit) {
    StoryPanel(
        title = "Session Overview",
        subtitle = "Your energy budget is the whole game.",
        imageRes = R.drawable.spoon_title_cast,
    ) {
        LoreBlock("Welcome to your central nervous system. It's a hostile work environment.")
        LoreBlock("In this gauntlet, your energy is measured in Spoons. Every action - fighting bureaucracy, managing ableism, or just assembling a plastic race track - costs Spoons.")
        LoreBlock("Run out of Spoons, and your body initiates an autonomic shutdown. Total collapse.")
        LoreBlock("Your choices will also affect your Karma. Will you play the agreeable Martyr and drain yourself for society? Or will you embrace the Savage and set ruthless boundaries to survive?")
        HoldActionButton(
            label = "I Understand the Stakes",
            tone = GauntletChoiceTone.NEUTRAL,
            onConfirmed = onContinue,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeroScreen(onPickHero: (GauntletHero) -> Unit) {
    StoryPanel(title = "Choose Your Lead", subtitle = "Pick the perk you want for the day.") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GauntletHero.entries.forEach { hero ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Image(
                            painter = painterResource(hero.artRes),
                            contentDescription = hero.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Text(hero.label.uppercase(), color = SpoonText, fontWeight = FontWeight.Black)
                        Text(hero.title, color = SpoonGold, style = MaterialTheme.typography.titleMedium)
                        Text(hero.perk, color = SpoonAccent, style = MaterialTheme.typography.bodyMedium)
                        HoldActionButton(
                            label = "Select ${hero.label.substringBefore(' ')}",
                            tone = GauntletChoiceTone.NEUTRAL,
                            onConfirmed = { onPickHero(hero) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BotIntroScreen(onContinue: () -> Unit) {
    StoryPanel(
        title = "Pick a Support Guide",
        subtitle = "Your processor is compromised. Bring backup.",
        imageRes = R.drawable.spoon_title_cast,
    ) {
        LoreBlock("WARNING: Cognitive fatigue detected.")
        LoreBlock("Navigating a world built for healthy people requires ruthless algorithmic precision. You cannot do this alone.")
        LoreBlock("Select a digital AI Assistant. They provide tactical advantages, unlock unique dialogue paths, and handle the social arithmetic your brain is too exhausted to compute.")
        HoldActionButton(
            label = "Browse AI Catalog",
            tone = GauntletChoiceTone.NEUTRAL,
            onConfirmed = onContinue,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BotScreen(onPickBot: (GauntletBot) -> Unit) {
    StoryPanel(title = "Sync Support Guide", subtitle = "Pick the distortion you trust most.") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GauntletBot.entries.forEach { bot ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Image(
                            painter = painterResource(bot.artRes),
                            contentDescription = bot.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Text(bot.label, color = SpoonText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                        Text(bot.perk, color = SpoonGold, style = MaterialTheme.typography.bodyMedium)
                        Text(bot.welcome, color = SpoonMuted, style = MaterialTheme.typography.bodySmall)
                        HoldActionButton(
                            label = "Sync ${bot.label}",
                            tone = GauntletChoiceTone.BOT,
                            onConfirmed = { onPickBot(bot) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AgendaScreen(
    uiState: SpoonGauntletUiState,
    onPickAgenda: (GauntletAgenda) -> Unit,
) {
    StoryPanel(
        title = "07:00 AM - Calibration",
        subtitle = "Set the pace before the first hit lands.",
        imageRes = R.drawable.spoon_agenda_alarm,
    ) {
        uiState.bot?.let {
            Text(it.welcome, color = SpoonGold, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            "System lottery complete. You are starting the day with ${uiState.spoons} Spoons.",
            color = SpoonText,
            style = MaterialTheme.typography.titleMedium,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GauntletAgenda.entries.forEach { agenda ->
                HoldActionButton(
                    label = "${agenda.label} (${if (agenda.modifier > 0) "+" else ""}${agenda.modifier} Spoons)",
                    tone = when (agenda) {
                        GauntletAgenda.HUSTLE -> GauntletChoiceTone.SAVAGE
                        GauntletAgenda.EQUILIBRIUM -> GauntletChoiceTone.NEUTRAL
                        GauntletAgenda.SURVIVAL -> GauntletChoiceTone.MARTYR
                    },
                    onConfirmed = { onPickAgenda(agenda) },
                )
            }
        }
    }
}

@Composable
private fun SceneScreen(
    uiState: SpoonGauntletUiState,
    scene: GauntletScene,
    choices: List<GauntletChoice>,
    onConfirm: (GauntletChoice) -> Unit,
) {
    StoryPanel(
        kicker = "Scene ${uiState.currentSceneIndex + 1} / ${SPOON_GAUNTLET_SCENES.size}",
        title = scene.title,
        subtitle = scene.subtitle,
        imageRes = scene.artRes,
    ) {
        LoreBlock(scene.description, centered = true)
        uiState.eventMessage?.let {
            Text(it, color = SpoonGold, style = MaterialTheme.typography.bodyMedium)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            choices.forEach { choice ->
                HoldActionButton(
                    label = choice.text,
                    tone = choice.tone,
                    onConfirmed = { onConfirm(choice) },
                )
            }
        }
    }
}

@Composable
private fun FlareUpScreen(
    uiState: SpoonGauntletUiState,
    onRecover: () -> Unit,
) {
    StoryPanel(
        title = "Energy Dip",
        subtitle = "Neurological flare-up detected.",
        border = SpoonDanger,
        imageRes = R.drawable.spoon_flareup,
    ) {
        uiState.eventMessage?.let {
            Text(it, color = SpoonGold, style = MaterialTheme.typography.bodyMedium)
        }
        LoreBlock("The friction of the day has triggered an involuntary physical response. Your central nervous system is glitching. 1 Spoon has been drained.", centered = true)
        HoldActionButton(
            label = "Push Through the Fog",
            tone = GauntletChoiceTone.SAVAGE,
            onConfirmed = onRecover,
        )
    }
}

@Composable
private fun ResultScreen(
    uiState: SpoonGauntletUiState,
    onRestart: () -> Unit,
    onFinish: () -> Unit,
) {
    val result = uiState.result ?: return
    StoryPanel(
        title = result.title,
        subtitle = if (result.won) "Steady pacing beat the spiral today." else "The tank is empty.",
        border = if (result.won) SpoonAccent else SpoonDanger,
        imageRes = if (result.won) R.drawable.spoon_result_win else R.drawable.spoon_result_lose,
    ) {
        LoreBlock(result.message, centered = true)
        HoldActionButton(
            label = "Restart Session",
            tone = GauntletChoiceTone.NEUTRAL,
            onConfirmed = onRestart,
        )
        Spacer(Modifier.height(4.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
            Text("Finish Run")
        }
    }
}

@Composable
private fun GauntletHud(uiState: SpoonGauntletUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SpoonPanel),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ASSISTANT: ${uiState.bot?.label ?: "OFFLINE"}",
                    color = SpoonGold,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "Karma ${uiState.karma}",
                    color = SpoonMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(uiState.maxSpoons) { index ->
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                if (index < uiState.spoons) SpoonGold else SpoonGold.copy(alpha = 0.18f),
                                RoundedCornerShape(999.dp),
                            ),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(uiState.karma / 100f)
                        .height(10.dp)
                        .background(
                            Brush.horizontalGradient(listOf(SpoonDanger, SpoonAccent)),
                            RoundedCornerShape(999.dp),
                        ),
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SAVAGE", color = SpoonMuted, style = MaterialTheme.typography.labelSmall)
                Text("MARTYR", color = SpoonMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun StoryPanel(
    title: String,
    subtitle: String,
    kicker: String? = null,
    border: Color = SpoonWarm,
    imageRes: Int? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SpoonPanel),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, border.copy(alpha = 0.5f), RoundedCornerShape(26.dp)),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = {
                imageRes?.let {
                    Image(
                        painter = painterResource(it),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
                kicker?.let {
                    Text(it.uppercase(), color = SpoonGold, style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
                }
                Text(title, color = SpoonText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(subtitle, color = SpoonMuted, style = MaterialTheme.typography.bodyLarge)
                content()
            },
        )
    }
}

@Composable
private fun LoreBlock(text: String, centered: Boolean = false) {
    Text(
        text = text,
        color = SpoonMuted,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = if (centered) TextAlign.Center else TextAlign.Start,
    )
}

@Composable
private fun HoldActionButton(
    label: String,
    tone: GauntletChoiceTone,
    onConfirmed: () -> Unit,
) {
    var progress by remember(label) { mutableFloatStateOf(0f) }
    var holding by remember(label) { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "hold_progress_$label")
    val border = when (tone) {
        GauntletChoiceTone.MARTYR -> SpoonAccent
        GauntletChoiceTone.SAVAGE -> SpoonDanger
        GauntletChoiceTone.BOT -> SpoonGold
        GauntletChoiceTone.NEUTRAL -> SpoonGold
    }
    val textColor = when (tone) {
        GauntletChoiceTone.MARTYR -> SpoonAccent
        GauntletChoiceTone.SAVAGE -> Color(0xFFEFB0A7)
        GauntletChoiceTone.BOT -> SpoonGold
        GauntletChoiceTone.NEUTRAL -> SpoonText
    }

    LaunchedEffect(label, holding) {
        if (!holding) {
            progress = 0f
            return@LaunchedEffect
        }
        val steps = 16
        repeat(steps) { index ->
            delay(50)
            if (!holding) return@LaunchedEffect
            progress = (index + 1) / steps.toFloat()
        }
        if (holding) {
            onConfirmed()
            holding = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(label) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    holding = true
                    waitForUpOrCancellation()
                    holding = false
                }
            }
            .border(2.dp, border, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.72f)),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(72.dp)
                    .background(border.copy(alpha = 0.22f)),
            )
            Text(
                text = label,
                color = textColor,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
