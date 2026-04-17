package com.wheeliespoon.rush

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import game.GameApp

/**
 * Android launcher activity that bootstraps the libGDX game.  It extends
 * [AndroidApplication], sets up any Android‑specific configuration and
 * instantiates the [GameApp].  No additional Android code is required for
 * this prototype; more complex integrations such as sensors or ads can be
 * added later.
 */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            // Use immersive mode to hide the navigation bar and status bar.
            useImmersiveMode = true
            // We disable the accelerometer and compass since this game does
            // not use them; this saves battery.
            useAccelerometer = false
            useCompass = false
        }
        initialize(GameApp(), config)
    }
}