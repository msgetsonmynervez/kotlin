package game

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import game.assets.GameAssets
import game.screens.TitleScreen
import com.luckypaws.platform.NativePlatformInterface

/**
 * Root entry point for the Wheelie Spoon Rush game.  This class sets up
 * shared resources such as the SpriteBatch and AssetManager and decides
 * which screen to display when the application is launched.  It extends
 * libGDX's [Game] class so that screen management is handled for us.
 */
class GameApp(
    val platform: NativePlatformInterface,
) : Game() {
    /** A shared SpriteBatch used to draw all 2D graphics. */
    lateinit var batch: SpriteBatch

    /** Centralised asset manager for loading and disposing of textures and fonts. */
    lateinit var assets: AssetManager

    override fun create() {
        // Instantiate the batch and asset manager.  Assets are loaded on the fly
        // by GameAssets.load() in individual screens as needed.
        batch = SpriteBatch()
        assets = AssetManager()
        // Eagerly load basic assets such as the white pixel texture and default
        // font.  Additional assets can be requested later on demand.
        GameAssets.load(assets)
        platform.speak("Game Started", true)

        // Kick things off with the title screen.  The TitleScreen class
        // transitions to the GameScreen or SettingsScreen based on user input.
        this.setScreen(TitleScreen(this))
    }

    override fun dispose() {
        // Dispose of the current screen first, then shared resources.
        screen?.dispose()
        GameAssets.dispose()
        batch.dispose()
        assets.dispose()
    }
}
