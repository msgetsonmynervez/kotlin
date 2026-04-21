package com.sterlingsworld.feature.game.games.spoongauntlet

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class SpoonGauntletViewModel(
    private val random: Random = Random.Default,
    private val clock: () -> Long = { System.currentTimeMillis() },
) : ViewModel() {

    private val startedAtMs = clock()

    private val _uiState = MutableStateFlow(SpoonGauntletUiState())
    val uiState: StateFlow<SpoonGauntletUiState> = _uiState.asStateFlow()

    fun startSession() {
        _uiState.value = _uiState.value.copy(screen = SpoonGauntletScreen.INTRO)
    }

    fun acknowledgeIntro() {
        _uiState.value = _uiState.value.copy(screen = SpoonGauntletScreen.HERO_SELECT)
    }

    fun pickHero(hero: GauntletHero) {
        _uiState.value = _uiState.value.copy(hero = hero, screen = SpoonGauntletScreen.BOT_INTRO)
    }

    fun continueToBotCatalog() {
        _uiState.value = _uiState.value.copy(screen = SpoonGauntletScreen.BOT_SELECT)
    }

    fun pickBot(bot: GauntletBot) {
        val startingSpoons = random.nextInt(8, 13) + if (bot == GauntletBot.G00GL) 2 else 0
        _uiState.value = _uiState.value.copy(
            bot = bot,
            spoons = startingSpoons,
            maxSpoons = startingSpoons,
            screen = SpoonGauntletScreen.AGENDA,
        )
    }

    fun pickAgenda(agenda: GauntletAgenda) {
        val sessionSpoons = (_uiState.value.spoons + agenda.modifier).coerceAtLeast(1)
        _uiState.value = _uiState.value.copy(
            agenda = agenda,
            spoons = sessionSpoons,
            maxSpoons = sessionSpoons,
            currentSceneIndex = 0,
            screen = SpoonGauntletScreen.SCENE,
            eventMessage = null,
            result = null,
        )
    }

    fun availableChoices(): List<GauntletChoice> {
        val state = _uiState.value
        val scene = SPOON_GAUNTLET_SCENES.getOrNull(state.currentSceneIndex) ?: return emptyList()
        return buildList {
            addAll(scene.choices)
            state.bot?.let { bot ->
                scene.botChoices[bot]?.let(::add)
            }
        }
    }

    fun confirmChoice(choice: GauntletChoice) {
        val state = _uiState.value
        if (state.screen != SpoonGauntletScreen.SCENE) return

        val scene = SPOON_GAUNTLET_SCENES.getOrNull(state.currentSceneIndex) ?: return
        var finalCost = choice.cost
        var eventMessage: String? = null

        if (state.hero == GauntletHero.JANE && scene.type == GauntletSceneType.BUREAUCRACY) {
            finalCost = (finalCost - 1).coerceAtLeast(0)
        }
        if (state.bot == GauntletBot.IBOT && scene.type == GauntletSceneType.PHYSICAL) {
            finalCost = (finalCost - 1).coerceAtLeast(0)
        }
        var mSoftUsed = state.mSoftUsed
        if (state.bot == GauntletBot.MSOFT && !mSoftUsed && finalCost >= 2) {
            finalCost = 0
            mSoftUsed = true
            eventMessage = "M-S0ft: Running Mandatory Update. Resource cost bypassed."
        }

        val flareChance = if (state.hero == GauntletHero.JOHN) 0.05 else 0.15
        val flareUp = random.nextDouble() < flareChance
        val nextSpoons = (state.spoons - finalCost - if (flareUp) 1 else 0).coerceAtLeast(0)
        val nextKarma = (state.karma + choice.karmaDelta).coerceIn(0, 100)

        _uiState.value = state.copy(
            spoons = nextSpoons,
            karma = nextKarma,
            mSoftUsed = mSoftUsed,
            eventMessage = eventMessage,
            screen = if (flareUp) SpoonGauntletScreen.FLARE_UP else SpoonGauntletScreen.SCENE,
        )

        if (!flareUp) {
            proceedToNextBeat()
        }
    }

    fun recoverFromFlareUp() {
        if (_uiState.value.screen != SpoonGauntletScreen.FLARE_UP) return
        proceedToNextBeat()
    }

    fun resetSession() {
        _uiState.value = SpoonGauntletUiState()
    }

    fun buildResult(): GameResult {
        val state = _uiState.value
        val won = state.result?.won == true
        val scenesCleared = if (state.result != null) {
            if (won) SPOON_GAUNTLET_SCENES.size else state.currentSceneIndex
        } else {
            state.currentSceneIndex
        }
        val score = scenesCleared * 100 + state.spoons * 12 + state.karma
        val stars = when {
            won && state.spoons >= 4 -> 3
            won -> 2
            scenesCleared > 0 -> 1
            else -> 0
        }
        return GameResult(
            completed = state.result != null,
            score = score,
            stars = stars,
            durationMs = (clock() - startedAtMs).coerceAtLeast(0L),
            perfect = won && state.spoons == state.maxSpoons,
        )
    }

    private fun proceedToNextBeat() {
        val state = _uiState.value
        if (state.spoons <= 0) {
            endGame(win = false)
            return
        }

        val nextIndex = state.currentSceneIndex + 1
        if (nextIndex >= SPOON_GAUNTLET_SCENES.size) {
            endGame(win = true)
            return
        }

        _uiState.value = state.copy(
            currentSceneIndex = nextIndex,
            screen = SpoonGauntletScreen.SCENE,
        )
    }

    private fun endGame(win: Boolean) {
        val state = _uiState.value
        val persona = when {
            state.karma >= 70 -> "The Self-Sacrificing Martyr"
            state.karma <= 30 -> "The Boundaried Savage"
            else -> "The Balanced Survivor"
        }
        val result = if (win) {
            SpoonGauntletResult(
                won = true,
                title = "TRIAGE COMPLETE",
                message = "You reached the end of the day with ${state.spoons} spoons remaining.\n\nYour survival strategy: $persona.\n\nSteady pacing beat the spiral today.",
            )
        } else {
            SpoonGauntletResult(
                won = false,
                title = "SYSTEM CRASH",
                message = "The tank is empty. Your body has initiated an emergency shutdown sequence. The world fades to static.",
            )
        }
        _uiState.value = state.copy(
            screen = SpoonGauntletScreen.RESULT,
            result = result,
        )
    }
}
