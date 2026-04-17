package com.example.snailsjourney

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

/**
 * Represents a small obstacle (e.g. rock or puddle) that the player can tap
 * to clear before the snail reaches it. If the snail collides with an
 * uncleared obstacle, it will slow down temporarily.
 */
class ObstacleActor : Actor() {
    private val texture: Texture
    private val sprite: Sprite

    init {
        val size = 32
        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.DARK_GRAY)
        pixmap.fillCircle(size / 2, size / 2, size / 2)
        texture = Texture(pixmap)
        pixmap.dispose()
        sprite = Sprite(texture)
        setSize(size.toFloat(), size.toFloat())
        originX = size / 2f
        originY = size / 2f

        // Add click listener to allow the player to tap and remove this obstacle
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Remove the actor from its parent stage. LibGDX will handle
                // cleanup on the next frame.
                this@ObstacleActor.remove()
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.setPosition(x, y)
        sprite.draw(batch)
    }

    /**
     * Releases the texture associated with this obstacle. Call this when the
     * stage or game is disposing of its resources to free GPU memory.
     */
    fun dispose() {
        texture.dispose()
    }
}