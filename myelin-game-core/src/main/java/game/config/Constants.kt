package game.config

import com.badlogic.gdx.graphics.Color

/**
 * A central location for tuning game constants.  Keeping all of these values
 * together makes it easier to adjust the feel of the game without hunting
 * through the codebase.  World units in Wheelie Spoon Rush are arbitrary but
 * consistent – the camera shows [WORLD_WIDTH] units horizontally, and the
 * height is derived from the aspect ratio.  All physical distances such as
 * jump velocities and gravity are expressed in these units per second.
 */
object Constants {
    /** Width of the visible world in arbitrary units.  The camera uses this
     * value to calculate its viewport.  The height is derived from the
     * device's aspect ratio via a FitViewport. */
    const val WORLD_WIDTH: Float = 20f

    /** Target aspect ratio (16:9).  The actual height will be computed
     * automatically based on the screen resolution. */
    const val WORLD_HEIGHT: Float = 11.25f

    /** Horizontal speed of the player in world units per second.  The world
     * scrolls at this rate so that the player constantly moves forward. */
    const val PLAYER_SPEED: Float = 5.5f

    /** Downward acceleration applied to the player when in the air. */
    const val GRAVITY: Float = -25f

    /** Initial vertical impulse when the player jumps from the ground. */
    const val JUMP_VELOCITY: Float = 12f

    /** Initial vertical impulse when the player performs a double jump. */
    const val DOUBLE_JUMP_VELOCITY: Float = 11f

    /** Dimensions of the player character in world units.  The width is
     * intentionally narrow so that the player can squeeze through gaps. */
    const val PLAYER_WIDTH: Float = 1.0f
    const val PLAYER_HEIGHT: Float = 1.6f

    /** Height of standard platforms in world units. */
    const val PLATFORM_HEIGHT: Float = 1.0f

    /** The number of secret spoon collectibles placed in the first level.  When
     * all are collected in a single run the reward scaffold unlocks a
     * cosmetic item. */
    const val SECRET_SPOON_TOTAL: Int = 3

    /** Duration of coyote time (in seconds) – the grace period that allows the
     * player to still jump a short moment after leaving a platform. */
    const val COYOTE_TIME: Float = 0.15f

    /** Duration of jump buffering (in seconds) – how long a jump command
     * issued slightly before landing will be remembered and executed on
     * landing. */
    const val JUMP_BUFFER_TIME: Float = 0.15f

    /** Colour palette used for various game elements.  Colours are defined in
     * RGBA.  Feel free to adjust these to achieve the desired whimsical
     * medical tone. */
    val COLOR_PLAYER: Color = Color(0.9f, 0.9f, 1.0f, 1f) // pale spoon colour
    val COLOR_WHEELCHAIR: Color = Color(0.3f, 0.5f, 0.9f, 1f) // wheelchair accent
    val COLOR_PLATFORM: Color = Color(0.6f, 0.4f, 0.2f, 1f) // brown platform
    val COLOR_COLLECTIBLE: Color = Color(0.9f, 0.8f, 0.2f, 1f) // yellow coin
    val COLOR_SECRET: Color = Color(1f, 0.6f, 0.0f, 1f) // orange secret spoon
    val COLOR_ENEMY: Color = Color(1f, 0.3f, 0.3f, 1f) // red enemy
    val COLOR_BACKGROUND: Color = Color(0.8f, 0.9f, 1f, 1f) // light blue background
}