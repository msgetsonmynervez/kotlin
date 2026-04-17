package game.entities

import com.badlogic.gdx.math.Rectangle
import game.config.Constants

/**
 * Represents a collectible item in the game world.  Collectibles are either
 * standard items (increase the player's score) or secret spoons (unlock
 * cosmetic rewards).  When the player's bounding box intersects the
 * collectible's [bounds], the item is picked up and marked as collected.
 */
class Collectible(
    val x: Float,
    val y: Float,
    val isSecret: Boolean = false
) {
    var collected: Boolean = false
    val bounds: Rectangle = Rectangle(x, y, 0.6f, 0.6f)
}