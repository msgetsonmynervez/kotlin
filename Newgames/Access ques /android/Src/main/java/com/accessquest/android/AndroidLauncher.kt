package com.accessquest.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.accessquest.AccessQuestGame

/**
 * Android launcher that bootstraps the Access Quest game.  It extends
 * [AndroidApplication] and calls [initialize] with an instance of
 * [AccessQuestGame].  You can configure platform‑specific settings via
 * [AndroidApplicationConfiguration].
 */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
            // Disable audio if not needed for the prototype
            disableAudio = false
        }
        initialize(AccessQuestGame(), config)
    }
}