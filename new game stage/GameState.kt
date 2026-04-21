package com.example.spoonsandstairs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

/**
 * Defines the three possible lanes the player can occupy.  The integer
 * `index` matches the visual ordering left‑to‑right (0 = left, 1 = centre,
 * 2 = right) and is used to compute X‑coordinates when rendering.
 */
enum class Lane(val index: Int) {
    LEFT(0),
    CENTER(1),
    RIGHT(2)
}

/**
 * The different kinds of objects that can scroll down the screen.  Toys,
 * laundry and stairs are hazards that remove spoons on contact.  Water and
 * lightning are power ups that replenish spoons when collected.
 */
enum class GameObjectType {
    TOY,
    LAUNDRY,
    STAIRS,
    WATER,
    LIGHTNING
}

/**
 * High level status of the game.  `START_MENU` indicates the title screen,
 * `PLAYING` is the active game loop and `GAME_OVER` stops the engine and
 * displays the game over UI.
 */
enum class GameStatus {
    START_MENU,
    PLAYING,
    GAME_OVER
}

/**
 * Represents an object currently on screen.  Each object has a unique id
 * generated via [UUID] to allow stable list diffing, a type (hazard or
 * power up), a fixed lane determining its horizontal position, and a
 * mutable `yPosition` describing the distance travelled downwards in
 * pixels.  The coordinate system has its origin at the top of the play
 * area so larger `yPosition` values correspond to objects closer to the
 * bottom of the screen.
 */
data class GameObject(
    val id: UUID = UUID.randomUUID(),
    val type: GameObjectType,
    val lane: Lane,
    var yPosition: Float
)

/**
 * A lightweight ViewModel managing all mutable game state.  Compose reads
 * these properties directly so changes automatically trigger recomposition.
 */
class GameViewModel : ViewModel() {

    /**
     * The lane the player currently occupies.  Default is centre.  This is
     * updated by user input and used by the engine for collision tests.
     */
    var currentLane by mutableStateOf(Lane.CENTER)
        private set

    /**
     * The spoon count representing health.  Starts at five and cannot
     * exceed five.  When this reaches zero the game status becomes
     * [GameStatus.GAME_OVER].
     */
    var spoons by mutableStateOf(5)
        private set

    /**
     * Running score.  This increments each frame in the engine and can be
     * displayed in the HUD.  It resets when a new game begins.
     */
    var score by mutableStateOf(0)
        private set

    /**
     * Multiplier applied to the base scroll speed.  This can be adjusted
     * dynamically (e.g. by power ups) to increase difficulty over time.
     */
    var speedMultiplier by mutableStateOf(1f)
        private set

    /**
     * The list of objects currently active in the play area.  The engine
     * modifies this list on every frame to move objects downward, spawn
     * new ones and remove those that have collided with the player or
     * scrolled off the screen.
     */
    var activeObjects by mutableStateOf(listOf<GameObject>())
        internal set

    /**
     * Tracks whether we are showing the start menu, actively playing or
     * displaying a game over screen.  The UI uses this to decide which
     * composables to display.
     */
    var gameStatus by mutableStateOf(GameStatus.START_MENU)
        private set

    /**
     * Move the player one lane to the left if possible.  Does nothing
     * when the player is already in the leftmost lane.
     */
    fun moveLeft() {
        if (currentLane.ordinal > 0) {
            currentLane = Lane.values()[currentLane.ordinal - 1]
        }
    }

    /**
     * Move the player one lane to the right if possible.  Does nothing
     * when the player is already in the rightmost lane.
     */
    fun moveRight() {
        if (currentLane.ordinal < Lane.values().size - 1) {
            currentLane = Lane.values()[currentLane.ordinal + 1]
        }
    }

    /**
     * Append a newly spawned object to the list of active objects.
     */
    internal fun addGameObject(obj: GameObject) {
        activeObjects = activeObjects + obj
    }

    /**
     * Remove an object from the active list.  This is called when an
     * object either goes off screen or collides with the player.
     */
    internal fun removeGameObject(obj: GameObject) {
        activeObjects = activeObjects - obj
    }

    /**
     * Reduce the spoon count by one.  Triggers game over when the spoon
     * count drops to zero.  Spoon count is clamped to the range 0–5.
     */
    internal fun decrementSpoons() {
        spoons--
        if (spoons <= 0) {
            spoons = 0
            gameStatus = GameStatus.GAME_OVER
        }
    }

    /**
     * Increase the spoon count by one, capping the value at five.  Called
     * when the player collects a power up.
     */
    internal fun incrementSpoons() {
        if (spoons < 5) {
            spoons++
        }
    }

    /**
     * Increment the score by the specified amount.  Called each frame to
     * reflect how long the player has survived.
     */
    internal fun incrementScore(amount: Int = 1) {
        score += amount
    }

    /**
     * Reset all mutable state to its initial values.  This is invoked
     * whenever the game starts anew.
     */
    fun resetGame() {
        currentLane = Lane.CENTER
        spoons = 5
        score = 0
        speedMultiplier = 1f
        activeObjects = emptyList()
        gameStatus = GameStatus.START_MENU
    }

    /**
     * Transition from the start menu into an active game.  Resets state
     * and switches the [gameStatus] to [GameStatus.PLAYING].
     */
    fun startGame() {
        resetGame()
        gameStatus = GameStatus.PLAYING
    }
}