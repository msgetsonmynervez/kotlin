package com.accessracer.game.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle

/**
 * Utility for building a simple high‑contrast [Skin] programmatically.
 * This avoids shipping external JSON skin files while still allowing
 * consistent styling for buttons and sliders.  Colours are chosen to
 * ensure good contrast and large interactive areas.
 */
object SkinFactory {
    fun createSkin(): Skin {
        val skin = Skin()

        // Default font
        val font = BitmapFont()
        skin.add("default-font", font, BitmapFont::class.java)

        // Create coloured drawables for buttons and sliders.  LibGDX
        // reuses texture regions for each style instance.
        fun drawable(color: Color, width: Int = 1, height: Int = 1): Texture {
            val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
            pixmap.setColor(color)
            pixmap.fill()
            val texture = Texture(pixmap)
            pixmap.dispose()
            return texture
        }

        // Button backgrounds: dark for up, bright for down, accent colour for hover
        skin.add("button-up", drawable(Color(0.1f, 0.2f, 0.4f, 1f)))
        skin.add("button-down", drawable(Color(0.2f, 0.3f, 0.6f, 1f)))
        skin.add("button-hover", drawable(Color(0.15f, 0.25f, 0.5f, 1f)))

        // Slider bar and knob
        skin.add("slider-bg", drawable(Color(0.2f, 0.2f, 0.2f, 1f)))
        skin.add("slider-knob", drawable(Color(0.8f, 0.8f, 0.8f, 1f)))
        skin.add("slider-knob-over", drawable(Color(1f, 1f, 1f, 1f)))

        // Label style
        val labelStyle = LabelStyle().apply {
            this.font = skin.getFont("default-font")
            fontColor = Color.WHITE
        }
        skin.add("default", labelStyle)

        // TextButton style
        val buttonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("button-up")
            down = skin.newDrawable("button-down")
            over = skin.newDrawable("button-hover")
            this.font = skin.getFont("default-font")
            fontColor = Color.WHITE
        }
        skin.add("default", buttonStyle)

        // Slider style
        val sliderStyle = SliderStyle().apply {
            background = skin.newDrawable("slider-bg")
            knob = skin.newDrawable("slider-knob")
            knobOver = skin.newDrawable("slider-knob-over")
        }
        skin.add("default-horizontal", sliderStyle, SliderStyle::class.java)

        return skin
    }
}
