package com.example.snailsjourney

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

/**
 * A minimal desktop launcher for testing the game. This class configures an
 * LWJGL3 application and starts `MyGdxGame`. While the primary target of this
 * project is Android, having a desktop launcher greatly simplifies local
 * iteration and debugging without requiring an emulator or device. You can run
 * this class from the command line using `gradle run`.
 */
object DesktopLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            setTitle("Snail's Journey")
            setWindowedMode(800, 480)
            useVsync(true)
        }
        Lwjgl3Application(MyGdxGame(), config)
    }
}