package com.example.snailsjourney

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

/**
 * A rest spot (e.g. a bench) where the snail can recover energy. When the
 * player taps the bench, the snail enters a resting state and quickly
 * regenerates energy. After one use, the bench disappears to avoid repeated
 * activations.
 */
class BenchActor(private val snail: SnailActor) : Actor() {
    private val texture: Texture
    private val sprite: Sprite

    init {
        val width = 40
        val height = 20
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.BROWN)
        pixmap.fillRectangle(0, 0, width, height)
        texture = Texture(pixmap)
        pixmap.dispose()
        sprite = Sprite(texture)
        setSize(width.toFloat(), height.toFloat())

        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Trigger the snail's rest. The snail will begin recovering
                // energy faster. Remove the bench to prevent repeated rests.
                snail.rest()
                this@BenchActor.remove()
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.setPosition(x, y)
        sprite.draw(batch)
    }

    fun dispose() {
        texture.dispose()
    }
}