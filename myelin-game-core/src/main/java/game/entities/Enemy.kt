package game.entities

import com.badlogic.gdx.math.Rectangle

/**
 * Represents a harmless enemy in the game world.  Enemies do not kill the
 * player but will cause the player to lose some collected items on contact.
 * They can optionally oscillate horizontally or vertically in a simple
 * sinusoidal pattern.  When the player's bounding box intersects the
 * enemy's [bounds], the world handles the collision response.  Enemies are
 * visually represented by tinted rectangles.
 */
class Enemy(
    private val x: Float,
    private val y: Float,
    val width: Float = 1f,
    val height: Float = 1f,
    val amplitude: Float = 0f,
    val speed: Float = 0f,
    val axisHorizontal: Boolean = true
) {
    private var time: Float = 0f
    val bounds: Rectangle = Rectangle(x, y, width, height)

    fun update(delta: Float) {
        if (amplitude != 0f && speed != 0f) {
            time += delta
            val offset = kotlin.math.sin(time * speed) * amplitude
            if (axisHorizontal) {
                bounds.x = x + offset
                bounds.y = y
            } else {
                bounds.y = y + offset
                bounds.x = x
            }
        } else {
            bounds.x = x
            bounds.y = y
        }
        bounds.width = width
        bounds.height = height
    }
}