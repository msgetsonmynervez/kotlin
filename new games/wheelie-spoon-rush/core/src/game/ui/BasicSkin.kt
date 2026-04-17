package game.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.graphics.g2d.BitmapFont

/**
 * Utility to create a minimalistic UI skin on the fly.  Using scene2d.ui
 * normally requires a JSON skin and accompanying texture atlas, but this
 * helper generates a simple white pixmap and a default font so that buttons
 * and labels can be created without external assets.  Colours can be
 * customised via parameters.
 */
object BasicSkin {
    fun create(primaryColor: Color = Color(0.2f, 0.6f, 0.8f, 1f),
               secondaryColor: Color = Color(0.15f, 0.45f, 0.6f, 1f)) : Skin {
        val skin = Skin()
        // Generate a 1x1 white texture
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        val whiteTexture = Texture(pixmap)
        pixmap.dispose()
        skin.add("white", whiteTexture)
        // Default font
        val font = BitmapFont()
        skin.add("default", font)
        // Button style
        val buttonStyle = TextButton.TextButtonStyle()
        val upDrawable = TextureRegionDrawable(whiteTexture)
        upDrawable.tint(primaryColor)
        val downDrawable = TextureRegionDrawable(whiteTexture)
        downDrawable.tint(secondaryColor)
        buttonStyle.up = upDrawable
        buttonStyle.down = downDrawable
        buttonStyle.font = font
        skin.add("default", buttonStyle)
        // Label style
        val labelStyle = Label.LabelStyle()
        labelStyle.font = font
        labelStyle.fontColor = Color.BLACK
        skin.add("default", labelStyle)
        return skin
    }
}