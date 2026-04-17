package com.accessracer.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

/**
 * Centralised repository for user‑adjustable game settings.  Settings
 * are stored in a libGDX [Preferences] instance so they persist
 * between sessions.  Default values follow the recommendations in
 * accessibility guidelines (normal game speed, normal steering
 * sensitivity and auto‑acceleration enabled).  Callers should avoid
 * reading preferences until the application has been created (when
 * the `Gdx.app` instance exists).
 */
object GameSettings {
    private val prefs: Preferences by lazy { Gdx.app.getPreferences("access_racer_settings") }

    /** Global speed multiplier applied to the vehicle’s velocity. */
    var gameSpeed: Float
        get() = prefs.getFloat("gameSpeed", 1f)
        set(value) {
            prefs.putFloat("gameSpeed", value)
            prefs.flush()
        }

    /** Sensitivity multiplier applied to steering input (higher = more responsive). */
    var steeringSensitivity: Float
        get() = prefs.getFloat("steeringSensitivity", 1f)
        set(value) {
            prefs.putFloat("steeringSensitivity", value)
            prefs.flush()
        }

    /** Whether the vehicle should accelerate automatically. */
    var autoAccelerate: Boolean
        get() = prefs.getBoolean("autoAccelerate", true)
        set(value) {
            prefs.putBoolean("autoAccelerate", value)
            prefs.flush()
        }
}