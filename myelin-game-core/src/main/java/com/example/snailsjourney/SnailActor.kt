package com.example.snailsjourney

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.min

/**
 * Represents the friendly snail that the player helps guide through the level.
 * The snail automatically moves from left to right. Its energy slowly
 * depletes as it moves; when energy reaches zero it rests to recover.
 */
class SnailActor : Actor() {
    enum class State { MOVING, RESTING, SLOW }

    private val texture: Texture
    private val sprite: Sprite
    private val renderer: ShapeRenderer = ShapeRenderer()

    // Movement and energy properties
    private var state = State.MOVING
    var speed: Float = 50f // pixels per second
    private var energyMax: Float = 100f
    var energy: Float = energyMax
        private set
    private var energyDrainRate: Float = 10f // energy per second
    private var energyRecoverRate: Float = 30f // energy per second when resting

    init {
        // Generate a simple snail texture using a Pixmap. The snail consists of a
        // circle (shell) and a rectangle (body). This avoids external image
        // dependencies and keeps the project self‑contained.
        val width = 64
        val height = 32
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.BROWN)
        // Draw body
        pixmap.fillRectangle(0, 8, width - 20, 16)
        // Draw shell as a circle
        pixmap.setColor(Color.ORANGE)
        pixmap.fillCircle(width - 24, 16, 16)
        texture = Texture(pixmap)
        pixmap.dispose()
        sprite = Sprite(texture)
        // Initial size and origin
        setSize(width.toFloat(), height.toFloat())
        originX = width / 2f
        originY = height / 2f
    }

    override fun act(delta: Float) {
        super.act(delta)
        when (state) {
            State.MOVING -> {
                // Advance horizontally
                x += speed * delta
                // Drain energy
                energy = (energy - energyDrainRate * delta).coerceAtLeast(0f)
                if (energy == 0f) {
                    // Energy depleted: switch to resting state
                    state = State.RESTING
                }
            }
            State.RESTING -> {
                // Recover energy
                energy = min(energyMax, energy + energyRecoverRate * delta)
                if (energy >= energyMax) {
                    // Fully recovered: resume moving
                    state = State.MOVING
                }
            }
            State.SLOW -> {
                // Temporary slow state when hitting an obstacle
                x += (speed / 2f) * delta
                energy = (energy - energyDrainRate * delta).coerceAtLeast(0f)
                // The slow state is reset externally after a short delay
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        // Draw the snail sprite
        sprite.setPosition(x, y)
        sprite.draw(batch)
        // Draw energy bar above the snail using ShapeRenderer. We need to end
        // SpriteBatch before using ShapeRenderer.
        batch.end()
        renderer.projectionMatrix = batch.projectionMatrix
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        // Background bar (gray)
        val barWidth = width
        val barHeight = 6f
        val barX = x
        val barY = y + height + 4f
        renderer.color = Color.LIGHT_GRAY
        renderer.rect(barX, barY, barWidth, barHeight)
        // Filled part proportional to energy
        val filledWidth = (energy / energyMax) * barWidth
        renderer.color = if (energy / energyMax > 0.3f) Color.GREEN else Color.RED
        renderer.rect(barX, barY, filledWidth, barHeight)
        renderer.end()
        batch.begin()
    }

    /**
     * Triggered when the snail should rest immediately (for example after the
     * player taps on a bench). The snail transitions to the RESTING state and
     * begins refilling its energy.
     */
    fun rest() {
        state = State.RESTING
    }

    /**
     * Immediately increase the snail's energy by the given amount. Energy is
     * clamped to [0, energyMax]. If the snail was resting and reaches full
     * energy, it will automatically resume moving.
     */
    fun addEnergy(amount: Float) {
        energy = (energy + amount).coerceAtMost(energyMax)
        if (energy >= energyMax) {
            state = State.MOVING
        }
    }

    /**
     * Puts the snail into a temporary slow state. The `Level1Screen` should
     * monitor how long the snail remains slowed and call `resumeSpeed` when
     * appropriate.
     */
    fun slowDown() {
        state = State.SLOW
    }

    /**
     * Resume normal movement if the snail isn't resting. Use this after a
     * slow‑down has elapsed. If the snail is currently resting, this call has
     * no effect.
     */
    fun resumeSpeed() {
        if (state == State.SLOW) {
            state = State.MOVING
        }
    }

    /** Returns `true` if the snail is currently slowed. */
    fun isSlow() = state == State.SLOW

    /** Returns `true` if the snail is resting (recovering energy). */
    fun isResting() = state == State.RESTING

    /**
     * Returns the bounding rectangle of the snail for simple collision tests.
     */
    fun getBounds() = com.badlogic.gdx.math.Rectangle(x, y, width, height)

    /**
     * Disposes the texture resources used by this actor. Should be called when
     * the game or screen is disposed to free GPU memory.
     */
    fun dispose() {
        texture.dispose()
        renderer.dispose()
    }
}
