package game.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Simple asset registry for the game.  In a production game you would
 * typically use libGDX's AssetManager to asynchronously load textures,
 * atlases, sounds and fonts from disk.  Because this first slice uses only
 * procedurally generated textures and the default font, [GameAssets] hides the
 * details of creating those resources.  Should you decide to add more
 * sophisticated artwork or audio later the methods here can be extended to
 * enqueue and retrieve assets via the [AssetManager].
 */
object GameAssets {
    private var initialised: Boolean = false

    /** A single white pixel texture.  Everything in the game is rendered by
     * stretching and tinting this base texture. */
    lateinit var whiteTex: Texture

    /** The default font used for HUD elements and UI. */
    lateinit var font: BitmapFont

    /** Create the pixel texture and font.  This method should be called once
     * at application startup.  It may be safely called again; subsequent calls
     * will be ignored. */
    fun load(assetManager: AssetManager) {
        if (initialised) return
        // Create a 1x1 white pixel.  We'll tint this to draw rectangles and
        // simplified sprites throughout the game.
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        whiteTex = Texture(pixmap)
        pixmap.dispose()

        // Create a default bitmap font.  On Android this uses a built‑in
        // monospace font.  For improved legibility you can later replace
        // this with a custom font loaded via assetManager.
        font = BitmapFont()
        initialised = true
    }

    /** Dispose all assets created by [load].  Should be called when the
     * application exits. */
    fun dispose() {
        if (!initialised) return
        whiteTex.dispose()
        font.dispose()
        initialised = false
    }
}