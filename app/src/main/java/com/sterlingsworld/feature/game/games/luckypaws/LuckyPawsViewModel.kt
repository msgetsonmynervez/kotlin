package com.sterlingsworld.feature.game.games.luckypaws

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val LUCKY_PAWS_REWARDS = listOf(
    "A quiet afternoon to yourself",
    "Permission to rest",
    "A warm cup of something good",
    "Five minutes of stillness",
    "A gentle walk at your own pace",
    "A soft blanket and nothing urgent",
    "A favourite song, uninterrupted",
    "Ten deep breaths",
)

enum class LuckyPawsPhase { WAITING, REVEALED }

data class LuckyPawsUiState(
    val phase: LuckyPawsPhase = LuckyPawsPhase.WAITING,
    val reward: String,
)

class LuckyPawsViewModel : ViewModel() {

    var selectedReward: String = randomReward()
        private set

    private val _uiState = MutableStateFlow(
        LuckyPawsUiState(reward = selectedReward),
    )
    val uiState: StateFlow<LuckyPawsUiState> = _uiState.asStateFlow()

    fun onReveal() {
        _uiState.update { it.copy(phase = LuckyPawsPhase.REVEALED) }
    }

    fun onReplay() {
        selectedReward = randomReward()
        _uiState.value = LuckyPawsUiState(
            phase = LuckyPawsPhase.WAITING,
            reward = selectedReward,
        )
    }

    fun buildResult(): GameResult {
        val revealed = uiState.value.phase == LuckyPawsPhase.REVEALED
        return GameResult(
            completed = revealed,
            score = if (revealed) 1 else 0,
            stars = if (revealed) 1 else 0,
            durationMs = 0L,
            perfect = false,
        )
    }

    private fun randomReward(): String = LUCKY_PAWS_REWARDS.random()
}
