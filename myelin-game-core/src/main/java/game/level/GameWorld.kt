package game.level

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.utils.Array
import game.entities.Platform
import game.entities.Player
import game.entities.Collectible
import game.entities.Enemy
import game.config.Constants

/**
 * Represents the game world for a single level.  It encapsulates all game
 * objects (player, platforms, enemies and collectibles) and updates them
 * together each frame.  The world also tracks completion state and ensures
 * that enemy collisions only inflict penalties occasionally.
 */
class GameWorld {
    /** All platforms and terrain pieces in the level. */
    val platforms: MutableList<Platform> = ArrayList()
    /** Standard and secret collectibles. */
    val collectibles: MutableList<Collectible> = ArrayList()
    /** Enemies that remove collectibles from the player on contact. */
    val enemies: MutableList<Enemy> = ArrayList()

    /** The player character. */
    val player: Player = Player(1f, 3f)

    /** Length of the level (in world units).  When the player's x coordinate
     * exceeds this value the level is complete. */
    var levelLength: Float = 0f

    /** True when the player reaches the end of the level. */
    var finished: Boolean = false

    /** Cooldown timer preventing multiple penalties from a single enemy
     * collision.  While this timer is greater than zero the player is
     * invulnerable to further enemy hits. */
    private var hurtCooldown: Float = 0f

    init {
        buildLevel()
    }

    /** Builds the handcrafted first level.  Platforms, ramps, moving platforms,
     * collectibles, secret spoons and enemies are all defined here.  Feel free
     * to tweak the positions to adjust difficulty and pacing. */
    private fun buildLevel() {
        // Flat starting ground
        platforms += Platform(0f, 0f, 8f)
        // Gap, then a ramp up to a higher platform
        platforms += Platform(8.5f, 0f, 5f, endY = 2f)
        platforms += Platform(13.5f, 2f, 6f) // high flat platform
        // Descending ramp
        platforms += Platform(19.5f, 2f, 4f, endY = 0.5f)
        platforms += Platform(23.5f, 0.5f, 5f)
        // Moving platform (vertical) – acts as a safe stepping stone above a gap
        platforms += Platform(29f, 3f, 3f, height = 0.6f, endY = 3f, amplitude = 1.0f, speed = 2.5f, axisHorizontal = false)
        // Another ground section
        platforms += Platform(33f, 0f, 6f)
        // Final ramp to finish
        platforms += Platform(39f, 0f, 6f, endY = 1.5f)
        platforms += Platform(45f, 1.5f, 5f)
        // Level end marker – treat as ground to avoid falling off
        platforms += Platform(50f, 0f, 10f)

        // Standard collectibles placed along the path
        collectibles += Collectible(4f, 2f)
        collectibles += Collectible(10f, 3f)
        collectibles += Collectible(15f, 3.5f)
        collectibles += Collectible(24f, 1.5f)
        collectibles += Collectible(26f, 1.5f)
        collectibles += Collectible(34f, 1.0f)
        collectibles += Collectible(42f, 2.0f)

        // Secret spoons hidden off the main path.  These require exploring
        // alternate routes or timing jumps carefully.
        collectibles += Collectible(14f, 5f, isSecret = true)
        collectibles += Collectible(30f, 6f, isSecret = true)
        collectibles += Collectible(48f, 4.5f, isSecret = true)

        // Enemies that patrol horizontally or vertically.  They cause the player
        // to lose some collected items on contact.
        enemies += Enemy(17f, 3f, width = 1f, height = 1f, amplitude = 1.5f, speed = 2f, axisHorizontal = true)
        enemies += Enemy(37f, 2f, width = 1f, height = 1f, amplitude = 1.5f, speed = 1.5f, axisHorizontal = false)

        // Define the length of the level based on the last platform.
        levelLength = 60f
    }

    /** Updates all entities in the world.  The [jumpPressed] flag should be
     * computed from input each frame and tells the player to attempt a jump.
     */
    fun update(delta: Float, jumpPressed: Boolean) {
        if (finished) return
        // Update moving platforms
        for (platform in platforms) {
            platform.update(delta)
        }
        // Update enemies
        for (enemy in enemies) {
            enemy.update(delta)
        }
        // Update player physics and ground collisions
        player.update(delta, jumpPressed, platforms)
        // Check collectible collisions
        for (item in collectibles) {
            if (!item.collected && item.bounds.overlaps(player.bounds)) {
                item.collected = true
                if (item.isSecret) {
                    player.secretCount++
                } else {
                    player.collectedCount++
                }
            }
        }
        // Handle enemy collisions with cooldown
        if (hurtCooldown > 0f) {
            hurtCooldown -= delta
        }
        if (hurtCooldown <= 0f) {
            for (enemy in enemies) {
                if (enemy.bounds.overlaps(player.bounds)) {
                    // Knock the player slightly upwards to convey impact
                    player.velocity.y = Constants.JUMP_VELOCITY * 0.5f
                    // Remove half of the player's collected items (rounded down)
                    val lost = player.collectedCount / 2
                    player.collectedCount -= lost
                    hurtCooldown = 1.0f // one second of invulnerability
                    break
                }
            }
        }
        // Finish check
        if (player.position.x >= levelLength) {
            finished = true
        }
    }

    /** Progress through the level expressed between 0 and 1. */
    fun getProgress(): Float {
        return (player.position.x / levelLength).coerceIn(0f, 1f)
    }
}