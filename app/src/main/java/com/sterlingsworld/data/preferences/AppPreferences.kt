package com.sterlingsworld.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sterlingsworld.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferencesRepositoryImpl(private val context: Context) : AppPreferencesRepository {

    private object Keys {
        val WELCOME_LAST_SEEN_DATE   = stringPreferencesKey("welcome_last_seen_date")
        val WELCOME_LAST_MSG_INDEX   = intPreferencesKey("welcome_last_message_index")
        val SOUND_ENABLED            = booleanPreferencesKey("sound_enabled")
        val VOICEOVER_ENABLED        = booleanPreferencesKey("voiceover_enabled")
        val HAPTIC_ENABLED           = booleanPreferencesKey("haptic_enabled")
    }

    // ── Welcome gating ────────────────────────────────────────────────────────

    override val welcomeLastSeenDate: Flow<String?> = context.dataStore.data
        .map { it[Keys.WELCOME_LAST_SEEN_DATE] }

    override val welcomeLastMessageIndex: Flow<Int> = context.dataStore.data
        .map { it[Keys.WELCOME_LAST_MSG_INDEX] ?: -1 }

    override suspend fun setWelcomeLastSeen(dateStamp: String, messageIndex: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WELCOME_LAST_SEEN_DATE] = dateStamp
            prefs[Keys.WELCOME_LAST_MSG_INDEX] = messageIndex
        }
    }

    // ── Sound ────────────────────────────────────────────────────────────────

    override val soundEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.SOUND_ENABLED] ?: true }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    override val voiceoverEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.VOICEOVER_ENABLED] ?: false }

    override suspend fun setVoiceoverEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VOICEOVER_ENABLED] = enabled }
    }

    override val hapticEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.HAPTIC_ENABLED] ?: true }

    override suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_ENABLED] = enabled }
    }

    // ── Reset ────────────────────────────────────────────────────────────────

    /** Clears all app preferences, returning the app to its initial state. */
    override suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
