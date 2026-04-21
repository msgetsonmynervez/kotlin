package com.sterlingsworld.feature.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sterlingsworld.core.util.localDateStamp
import com.sterlingsworld.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val welcomeMessages = listOf(
    "Sterling says: We are pacing ourselves like professionals today.",
    "Sterling says: Rest counts as progress in this park.",
    "Sterling says: If today feels slow, that still counts as moving.",
    "Sterling says: Gentle wins are still wins.",
)

data class WelcomeUiState(
    val message: String = welcomeMessages[0],
    val isLoading: Boolean = true,
)

class WelcomeViewModel(
    private val prefs: AppPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState

    init {
        viewModelScope.launch {
            val lastIndex = prefs.welcomeLastMessageIndex.first()
            val nextIndex = (lastIndex + 1).coerceAtLeast(0) % welcomeMessages.size
            _uiState.value = WelcomeUiState(
                message = welcomeMessages[nextIndex],
                isLoading = false,
            )
        }
    }

    fun onEnterPark() {
        val currentMessage = _uiState.value.message
        val index = welcomeMessages.indexOf(currentMessage).coerceAtLeast(0)
        viewModelScope.launch {
            prefs.setWelcomeLastSeen(
                dateStamp = localDateStamp(),
                messageIndex = index,
            )
        }
    }

    class Factory(private val prefs: AppPreferencesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            WelcomeViewModel(prefs) as T
    }
}
