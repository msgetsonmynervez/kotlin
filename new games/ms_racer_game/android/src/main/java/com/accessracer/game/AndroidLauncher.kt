package com.accessracer.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

/**
 * Android entry point for AccessRacer.  This activity launches the core
 * libGDX game using the provided configuration.  Immersive mode is
 * enabled to hide the status and navigation bars during play.
 */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            // Enable immersive full‑screen mode
            useImmersiveMode = true
            // Disable unused sensors to save battery
            useAccelerometer = false
            useCompass = false
        }
        initialize(AccessRacerGame(), config)
    }
}