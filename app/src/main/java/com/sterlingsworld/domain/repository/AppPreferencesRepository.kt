package com.sterlingsworld.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    val welcomeLastSeenDate: Flow<String?>
    val welcomeLastMessageIndex: Flow<Int>
    suspend fun setWelcomeLastSeen(dateStamp: String, messageIndex: Int)
    
    val soundEnabled: Flow<Boolean>
    suspend fun setSoundEnabled(enabled: Boolean)
    
    val voiceoverEnabled: Flow<Boolean>
    suspend fun setVoiceoverEnabled(enabled: Boolean)
    
    val hapticEnabled: Flow<Boolean>
    suspend fun setHapticEnabled(enabled: Boolean)
    
    suspend fun clearAll()
}
