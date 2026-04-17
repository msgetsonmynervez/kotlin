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
 * Represents an energy snack item (e.g. an apple) that refills the snail's
 * energy when tapped. Once tapped, the item animates toward the snail and
 * disappears. The amount of energy restored is configurable.
 */
class SnackItemActor(private val snail: SnailActor, private val restoreAmount: Float = 30f) : Actor() {
    private val texture: Texture
    private val sprite: Sprite

    init {
        val size = 24
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.RED)
        pixmap.fillCircle(size / 2, size / 2, size / 2)
        // Draw a small green leaf on top
        pixmap.setColor(Color.GREEN)
        pixmap.fillRectangle(size / 2 - 2, size - 6, 4, 6)
        texture = Texture(pixmap)
        pixmap.dispose()
        sprite = Sprite(texture)
        setSize(size.toFloat(), size.toFloat())

        // When tapped, heal the snail and remove the item
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Refill the snail's energy. Using a method call avoids
                // reflection and encapsulates the logic in SnailActor.
                snail.addEnergy(restoreAmount)
                // Remove this snack from the stage
                this@SnackItemActor.remove()
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