package com.sterlingsworld.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sterlingsworld.domain.repository.AppPreferencesRepository
import com.sterlingsworld.domain.repository.GameProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefs: AppPreferencesRepository,
    private val progressRepository: GameProgressRepository,
) : ViewModel() {

    val soundEnabled: StateFlow<Boolean> = prefs.soundEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true,
    )

    val voiceoverEnabled: StateFlow<Boolean> = prefs.voiceoverEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    val hapticEnabled: StateFlow<Boolean> = prefs.hapticEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true,
    )

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setSoundEnabled(enabled) }
    }

    fun setVoiceoverEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setVoiceoverEnabled(enabled) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setHapticEnabled(enabled) }
    }

    /**
     * Clears all preferences and game progress, returning the app to its day-one state.
     * The caller is responsible for navigating back to the Welcome screen.
     */
    fun resetApp() {
        viewModelScope.launch {
            prefs.clearAll()
            progressRepository.deleteAll()
        }
    }

    class Factory(
        private val prefs: AppPreferencesRepository,
        private val progress: GameProgressRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(prefs, progress) as T
    }
}
