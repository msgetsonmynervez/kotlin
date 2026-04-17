package game.entities

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import game.config.Constants

/**
 * Represents the player character – a cartoon spoon in a power wheelchair.
 * The [Player] handles physics integration, jump control, coyote time,
 * jump buffering and collision resolution.  It does not interact with
 * collectibles or enemies directly – that logic lives in [game.level.GameWorld].
 */
class Player(
    startX: Float,
    startY: Float
) {
    /** Current position of the player's bottom‑left corner in world units. */
    val position = Vector2(startX, startY)
    /** Current linear velocity in world units per second. */
    val velocity = Vector2(Constants.PLAYER_SPEED, 0f)
    /** Axis‑aligned bounding box used for collision tests. */
    val bounds = Rectangle(startX, startY, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT)

    /** True if the player is currently grounded. */
    private var onGround: Boolean = false
    /** Remaining coyote time (seconds).  Allows jumping shortly after leaving a platform. */
    private var coyoteTimer: Float = 0f
    /** Remaining jump buffer time (seconds).  Allows a jump input slightly before landing to execute. */
    private var jumpBufferTimer: Float = 0f
    /** True if the player has already performed their midair double jump. */
    private var hasDoubleJumped: Boolean = false

    /** Public counters for collected items and secrets. */
    var collectedCount: Int = 0
    var secretCount: Int = 0

    /** Updates the player's physics and resolves collisions against provided platforms.
     * @param delta time step in seconds
     * @param jumpPressed whether the jump button was pressed this frame
     * @param platforms a list of platforms to test against for ground detection
     */
    fun update(delta: Float, jumpPressed: Boolean, platforms: Iterable<Platform>) {
        // Apply horizontal motion (constant speed).  We preserve X velocity.
        velocity.x = Constants.PLAYER_SPEED
        // Gravity
        velocity.y += Constants.GRAVITY * delta

        // Handle jump input: either initiate a jump, double jump or buffer it
        if (jumpPressed) {
            if (onGround || coyoteTimer > 0f) {
                performJump(primary = true)
            } else if (!hasDoubleJumped) {
                performJump(primary = false)
            } else {
                // Buffer the jump if midair and unable to double jump
                jumpBufferTimer = Constants.JUMP_BUFFER_TIME
            }
        }

        // Integrate position
        position.x += velocity.x * delta
        position.y += velocity.y * delta

        // Collision resolution against platforms (ground detection)
        resolveGround(platforms, delta)

        // Update bounding box for rendering and collision detection
        bounds.set(position.x, position.y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT)

        // Decrement timers
        if (!onGround) {
            coyoteTimer -= delta
            if (coyoteTimer < 0f) coyoteTimer = 0f
        }
        if (jumpBufferTimer > 0f) {
            jumpBufferTimer -= delta
        }
    }

    /** Initiates a jump.  If [primary] is true this is a grounded jump; otherwise
     * it is a double jump. */
    private fun performJump(primary: Boolean) {
        if (primary) {
            velocity.y = Constants.JUMP_VELOCITY
            onGround = false
            hasDoubleJumped = false
            coyoteTimer = 0f
            jumpBufferTimer = 0f
        } else {
            velocity.y = Constants.DOUBLE_JUMP_VELOCITY
            hasDoubleJumped = true
            jumpBufferTimer = 0f
        }
    }

    /** Resolves collisions with all platforms to keep the player on the ground.
     * The algorithm finds the highest platform whose top surface is beneath
     * (or slightly above) the player's feet and snaps the player to it if
     * descending. */
    private fun resolveGround(platforms: Iterable<Platform>, delta: Float) {
        var nearestGround: Float? = null
        var newY: Float? = null
        for (platform in platforms) {
            val ground = platform.groundHeightAt(position.x + Constants.PLAYER_WIDTH / 2f)
            if (ground != null) {
                val top = ground + platform.height
                // We want the platform immediately below the player's feet.
                // Accept it if the player is descending (velocity.y ≤ 0) and the
                // player's bottom is below the top surface plus a small margin.
                if (velocity.y <= 0f && position.y <= top + 0.1f) {
                    if (nearestGround == null || top > nearestGround) {
                        nearestGround = top
                        newY = top
                    }
                }
            }
        }
        if (newY != null) {
            // Snap to ground
            position.y = newY
            velocity.y = 0f
            onGround = true
            coyoteTimer = Constants.COYOTE_TIME
            hasDoubleJumped = false
            // If a jump was buffered, execute it now
            if (jumpBufferTimer > 0f) {
                performJump(primary = true)
                jumpBufferTimer = 0f
            }
        } else {
            // No ground underfoot
            onGround = false
        }
    }
}