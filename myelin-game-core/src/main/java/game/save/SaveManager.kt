package game.save

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

/**
 * Centralised utility for persisting small pieces of data such as high scores,
 * completion flags and settings.  libGDX wraps platform-specific storage
 * mechanisms with the [Preferences] API so games can call [flush] once to
 * persist changes.
 */
object SaveManager {
    private const val PREFS_NAME = "wheelie_spoon_rush"
    private const val KEY_BEST_SCORE = "best_score"
    private const val KEY_SECRET_FOUND = "secret_found"
    private const val KEY_UNLOCK_REWARD = "unlock_reward"
    private const val KEY_SFX_VOLUME = "sfx_volume"
    private const val KEY_MUSIC_ENABLED = "music_enabled"

    private val prefs: Preferences by lazy { Gdx.app.getPreferences(PREFS_NAME) }

    /** Retrieve the highest score recorded so far. */
    fun getBestScore(): Int = prefs.getInteger(KEY_BEST_SCORE, 0)

    /** Retrieve the highest number of secret spoons found in a single run. */
    fun getBestSecrets(): Int = prefs.getInteger(KEY_SECRET_FOUND, 0)

    /** Update the best run statistics if the supplied values exceed the stored ones. */
    fun updateBestRun(score: Int, secrets: Int) {
        var updated = false
        if (score > getBestScore()) {
            prefs.putInteger(KEY_BEST_SCORE, score)
            updated = true
        }
        if (secrets > getBestSecrets()) {
            prefs.putInteger(KEY_SECRET_FOUND, secrets)
            updated = true
        }
        if (updated) prefs.flush()
    }

    /** Whether the cosmetic reward has been unlocked by finding all secret spoons. */
    fun isRewardUnlocked(): Boolean = prefs.getBoolean(KEY_UNLOCK_REWARD, false)

    /** Marks the reward as unlocked.  Call this when the player finds all secrets. */
    fun unlockReward() {
        prefs.putBoolean(KEY_UNLOCK_REWARD, true)
        prefs.flush()
    }

    /** Persist the sound effects volume.  Range 0..1. */
    fun setSfxVolume(volume: Float) {
        prefs.putFloat(KEY_SFX_VOLUME, volume)
        prefs.flush()
    }
    fun getSfxVolume(): Float = prefs.getFloat(KEY_SFX_VOLUME, 1f)

    /** Placeholder for music enabled flag.  Music is not yet implemented but
     * stored here for future expansion. */
    fun setMusicEnabled(enabled: Boolean) {
        prefs.putBoolean(KEY_MUSIC_ENABLED, enabled)
        prefs.flush()
    }
    fun isMusicEnabled(): Boolean = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
}