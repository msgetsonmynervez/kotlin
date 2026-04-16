package com.sterlingsworld.feature.game.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sterlingsworld.data.progress.GameProgressRepository
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class GamePhase { PLAYING, PAUSED }

data class GameShellUiState(
    val gamePhase: GamePhase = GamePhase.PLAYING,
    val gameId: String,
)

sealed class GameShellEvent {
    data object Exit : GameShellEvent()
    data object Restart : GameShellEvent()
    data class Complete(val result: GameResult) : GameShellEvent()
}

class GameShellViewModel(
    gameId: String,
    private val progressRepository: GameProgressRepository,
    private val eventScope: CoroutineScope? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameShellUiState(gameId = gameId))
    val uiState: StateFlow<GameShellUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameShellEvent>()
    val events: SharedFlow<GameShellEvent> = _events.asSharedFlow()

    init {
        launchInScope {
            progressRepository.recordSessionStart(gameId)
        }
    }

    private fun launchInScope(block: suspend () -> Unit) {
        val scope = eventScope
        if (scope != null) {
            scope.launch { block() }
        } else {
            viewModelScope.launch { block() }
        }
    }

    fun onPause() {
        _uiState.update { it.copy(gamePhase = GamePhase.PAUSED) }
    }

    fun onResume() {
        _uiState.update { it.copy(gamePhase = GamePhase.PLAYING) }
    }

    fun onExit() {
        launchInScope {
            _events.emit(GameShellEvent.Exit)
        }
    }

    fun onRestart() {
        launchInScope {
            progressRepository.recordRestart(uiState.value.gameId)
            _events.emit(GameShellEvent.Restart)
        }
    }

    fun onComplete(result: GameResult) {
        launchInScope {
            progressRepository.recordCompletion(uiState.value.gameId, result)
            _events.emit(GameShellEvent.Complete(result))
        }
    }

    class Factory(
        private val gameId: String,
        private val progressRepository: GameProgressRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GameShellViewModel(
                gameId = gameId,
                progressRepository = progressRepository,
            ) as T
    }
}
