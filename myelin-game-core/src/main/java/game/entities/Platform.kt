package game.entities

import com.badlogic.gdx.math.Rectangle
import game.config.Constants

/**
 * Represents a platform, ramp or moving platform in the world.  Platforms are
 * axis‑aligned rectangles that the player can run on.  Ramps interpolate
 * between a starting height and an ending height across their width.  Moving
 * platforms oscillate along a given axis with configurable amplitude and
 * frequency.  Each platform contributes to collision and ground height
 * calculations in the [game.level.GameWorld].
 */
open class Platform(
    val x: Float,
    var y: Float,
    val width: Float,
    val height: Float = Constants.PLATFORM_HEIGHT,
    // Ramp configuration: if endY differs from y, the platform slopes from
    // its starting height to the end height over its width.  Otherwise the
    // platform is flat.
    val endY: Float = y,
    // Movement configuration: amplitude and speed specify the extent and rate
    // of oscillation.  axis = true for horizontal, false for vertical.
    val amplitude: Float = 0f,
    val speed: Float = 0f,
    val axisHorizontal: Boolean = false
) {
    /** Time accumulator used for computing oscillation. */
    private var time: Float = 0f

    /** Rectangle used for collision detection.  The x/y coordinates are updated
     * in [update] to account for moving platforms. */
    val bounds: Rectangle = Rectangle(x, y, width, height)

    /**
     * Update the platform's position if it is configured as moving.  This
     * performs a simple sinusoidal oscillation around its original centre.
     */
    open fun update(delta: Float) {
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
            // Static platform
            bounds.x = x
            bounds.y = y
        }
        // Bounds dimensions remain constant
        bounds.width = width
        bounds.height = height
    }

    /**
     * Calculate the ground height at a given x coordinate relative to this
     * platform.  Returns null if the x coordinate lies outside the platform's
     * horizontal extent.  For ramps the height is linearly interpolated.
     */
    fun groundHeightAt(worldX: Float): Float? {
        val localX = worldX - bounds.x
        if (localX < 0f || localX > width) return null
        return if (endY != y) {
            // Ramp – interpolate between start and end height
            y + (endY - y) * (localX / width)
        } else {
            y
        }
    }
}