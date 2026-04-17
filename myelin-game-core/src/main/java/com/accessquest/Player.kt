package com.accessquest

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2

/**
 * Represents the player character in the game world.  The player has a position
 * and a speed and reads directional input to update its velocity.  It calls
 * into [FatigueSystem] and [HeatSystem] each frame to accumulate fatigue and
 * heat based on the distance moved.  Movement is normalised so diagonal
 * movement does not exceed axis‑aligned speed.
 */
class Player {
    val position = Vector2(50f, 50f)
    private val velocity = Vector2()
    var speed: Float = 120f

    /**
     * Updates the player's velocity and position based on input.  Supports
     * keyboard input for now; virtual joystick values can be plugged in by
     * replacing the calls to [Gdx.input.isKeyPressed].
     */
    fun update(delta: Float, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        // Read input axes (arrow keys or WASD).  For mobile, substitute with
        // joystick values between -1 and 1.
        var horizontal = 0f
        var vertical = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontal -= 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontal += 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            vertical -= 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            vertical += 1f
        }

        // Normalise to prevent faster diagonal movement
        val direction = Vector2(horizontal, vertical)
        if (direction.len2() > 0f) {
            direction.nor()
        }
        velocity.set(direction).scl(speed)

        // Move the player
        val deltaX = velocity.x * delta
        val deltaY = velocity.y * delta
        position.add(deltaX, deltaY)

        // Accumulate fatigue and heat based on distance travelled
        val distance = direction.len() * speed * delta
        fatigueSystem.addFatigue(distance)
        heatSystem.addHeat(distance)
    }

    /**
     * Resets the player's position and clears velocity.  Called when returning to
     * a checkpoint.
     */
    fun respawn(at: Vector2) {
        position.set(at)
        velocity.setZero()
    }
}