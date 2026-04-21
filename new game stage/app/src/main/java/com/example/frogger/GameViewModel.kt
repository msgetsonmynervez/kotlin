package com.example.frogger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frogger.model.Direction
import com.example.frogger.model.GameLevel
import com.example.frogger.model.GameObject
import com.example.frogger.model.GameState
import com.example.frogger.model.GameStatus
import com.example.frogger.model.ObjectDefinition
import com.example.frogger.model.RowDefinition
import com.example.frogger.model.RowState
import com.example.frogger.model.RowType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.floor

/**
 * ViewModel responsible for driving the Frogger game logic. It holds the
 * current [GameState] and emits updates via a [StateFlow]. A coroutine
 * runs on a fixed interval to update the positions of road vehicles and
 * river logs, check for collisions or drownings, and manage level
 * progression. User input is exposed through the [moveFrog] function.
 */
class GameViewModel : ViewModel() {

    /** Width of the frog in normalized horizontal units. The frog occupies
     * this fraction of the screen width. Adjusting this value will
     * proportionally change the frog's hit box for collisions and log
     * occupancy. */
    private val frogWidth = 0.08f

    /**
     * Public accessor for the frog's width in normalized units. Exposed
     * to the UI layer so that it can correctly size the frog relative
     * to the available horizontal space. Keeping the underlying field
     * private ensures only this class mutates the value.
     */
    val frogWidthNormalized: Float
        get() = frogWidth

    /** A list of all levels available in the game. Each level defines its
     * own set of rows, object counts, speeds and home count. Difficulty
     * increases by adding more hazards and increasing speeds. */
    private val levels: List<GameLevel> = listOf(
        createLevel1(),
        createLevel2(),
        createLevel3()
    )

    // Mutable state backing the public [state] property. Whenever the
    // object reference is replaced the UI will recompose.
    private val _state: MutableStateFlow<GameState> = MutableStateFlow(
        initializeStateForLevel(levelIndex = 0)
    )
    val state: StateFlow<GameState> = _state

    private var gameLoopJob: Job? = null

    init {
        startGameLoop()
    }

    /**
     * Initialize a fresh [GameState] for the supplied level index. Copies
     * level definitions into mutable state so positions can be updated
     * independently of the level definitions. The frog always begins on
     * the bottom start row centred horizontally.
     */
    private fun initializeStateForLevel(levelIndex: Int): GameState {
        val level = levels[levelIndex]
        val rowStates = level.rows.map { def ->
            RowState(
                type = def.type,
                speed = def.speed,
                goingLeft = def.goingLeft,
                objects = def.objects.map { obj ->
                    GameObject(x = obj.initialX, width = obj.width)
                }.toMutableList()
            )
        }
        val startRowIndex = rowStates.lastIndex
        val homes = MutableList(level.homeCount) { false }
        return GameState(
            currentLevelIndex = levelIndex,
            rows = rowStates,
            frogX = 0.5f - frogWidth / 2f,
            frogRow = startRowIndex,
            lives = level.lives,
            homes = homes,
            status = GameStatus.PLAYING
        )
    }

    /** Launch the repeating game loop if it is not already running. The
     * loop updates the game roughly 60 times per second. If the game
     * reaches a non‑playing state (level completed, game over or win) the
     * loop is suspended until [restartCurrentLevel] or [nextLevel] is
     * called. */
    private fun startGameLoop() {
        if (gameLoopJob != null && gameLoopJob?.isActive == true) return
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(16) // Approximately 60 FPS
                val current = _state.value
                when (current.status) {
                    GameStatus.PLAYING -> updateGameState()
                    else -> Unit
                }
            }
        }
    }

    /**
     * Advance the game state by moving all objects, checking for
     * collisions/drownings and handling arrivals in home slots. This
     * function mutates the existing rows and frog positions and then
     * publishes a new [GameState] instance so Compose recomposes.
     */
    private fun updateGameState() {
        val state = _state.value
        var frogX = state.frogX
        var frogRow = state.frogRow
        var lives = state.lives
        val homes = state.homes
        val rows = state.rows
        val level = levels[state.currentLevelIndex]

        // Move all objects and perform collision detection.
        // We'll also keep track of whether the frog is standing on a
        // river log so we can move it with the log.
        var frogOnLog = false
        for ((rowIndex, row) in rows.withIndex()) {
            if (row.speed != 0f) {
                for (obj in row.objects) {
                    if (row.goingLeft) {
                        obj.x -= row.speed
                        if (obj.x + obj.width < 0f) {
                            obj.x = 1f
                        }
                    } else {
                        obj.x += row.speed
                        if (obj.x > 1f) {
                            obj.x = -obj.width
                        }
                    }
                }
            }

            // Handle frog interactions when it is in the current row.
            if (rowIndex == frogRow) {
                when (row.type) {
                    RowType.ROAD -> {
                        // Collision: if frog intersects any vehicle
                        for (obj in row.objects) {
                            if (intersects(frogX, frogWidth, obj.x, obj.width)) {
                                // Lose a life
                                lives -= 1
                                if (lives <= 0) {
                                    // Game over
                                    val newState = state.copy(
                                        lives = 0,
                                        status = GameStatus.GAME_OVER
                                    )
                                    _state.value = newState
                                    return
                                } else {
                                    // Reset frog to start
                                    frogX = 0.5f - frogWidth / 2f
                                    frogRow = rows.lastIndex
                                }
                                break
                            }
                        }
                    }
                    RowType.RIVER -> {
                        // Determine if frog stands on a log
                        var onAnyLog = false
                        for (obj in row.objects) {
                            if (intersects(frogX, frogWidth, obj.x, obj.width)) {
                                onAnyLog = true
                                // Move with the log
                                frogX += if (row.goingLeft) -row.speed else row.speed
                                break
                            }
                        }
                        if (!onAnyLog) {
                            // Frog fell into water
                            lives -= 1
                            if (lives <= 0) {
                                val newState = state.copy(
                                    lives = 0,
                                    status = GameStatus.GAME_OVER
                                )
                                _state.value = newState
                                return
                            } else {
                                frogX = 0.5f - frogWidth / 2f
                                frogRow = rows.lastIndex
                            }
                        }
                    }
                    else -> {
                        // SAFE, HOME and START rows don't move the frog here
                    }
                }
            }
        }

        // Clamp frogX to the horizontal bounds
        if (frogX < 0f) frogX = 0f
        if (frogX + frogWidth > 1f) frogX = 1f - frogWidth

        // Check home row arrival
        if (frogRow == 0 && rows.first().type == RowType.HOME) {
            // Determine which home slot the frog has landed in
            val slotWidth = 1f / level.homeCount
            val homeIndex = floor((frogX + frogWidth / 2f) / slotWidth).toInt()
            if (homeIndex in homes.indices && !homes[homeIndex]) {
                homes[homeIndex] = true
            }
            // Reset frog for next attempt
            frogX = 0.5f - frogWidth / 2f
            frogRow = rows.lastIndex
            // Check if level complete
            if (homes.all { it }) {
                // Completed level
                val status = if (state.currentLevelIndex == levels.lastIndex) {
                    GameStatus.GAME_WIN
                } else {
                    GameStatus.LEVEL_COMPLETE
                }
                val newState = state.copy(
                    frogX = frogX,
                    frogRow = frogRow,
                    lives = lives,
                    status = status
                )
                _state.value = newState
                return
            }
        }

        // Publish updated state. We create a new GameState to ensure
        // Compose sees a different object and recomposes. The rows and
        // homes lists are mutated above but reused here to avoid
        // unnecessary allocations.
        val updated = state.copy(
            frogX = frogX,
            frogRow = frogRow,
            lives = lives
        )
        _state.value = updated
    }

    /** Request the frog to move in the specified [direction]. This function
     * runs on the main thread and directly mutates the frog's row or
     * horizontal position. Movement that would take the frog beyond the
     * playfield is clamped. */
    fun moveFrog(direction: Direction) {
        val state = _state.value
        if (state.status != GameStatus.PLAYING) return
        val rows = state.rows
        var newX = state.frogX
        var newRow = state.frogRow
        val stepX = frogWidth // move one frog width at a time horizontally
        when (direction) {
            Direction.LEFT -> newX -= stepX
            Direction.RIGHT -> newX += stepX
            Direction.UP -> newRow -= 1
            Direction.DOWN -> newRow += 1
        }
        // Clamp horizontal movement
        if (newX < 0f) newX = 0f
        if (newX + frogWidth > 1f) newX = 1f - frogWidth
        // Clamp vertical movement
        if (newRow < 0) newRow = 0
        if (newRow > rows.lastIndex) newRow = rows.lastIndex

        _state.value = state.copy(frogX = newX, frogRow = newRow)
    }

    /** Start the next level if the current one has been completed. Resets
     * game state for the next level and resumes the game loop. */
    fun nextLevel() {
        val state = _state.value
        if (state.status != GameStatus.LEVEL_COMPLETE) return
        val nextIndex = state.currentLevelIndex + 1
        val newState = initializeStateForLevel(nextIndex)
        _state.value = newState
    }

    /** Restart the current level after a game over. Resets all homes and
     * lives for the current level and resumes playing. */
    fun restartCurrentLevel() {
        val state = _state.value
        val newState = initializeStateForLevel(state.currentLevelIndex)
        _state.value = newState
    }

    /** Simple intersection check between two horizontal line segments.
     * Returns true if ranges [x1, x1 + w1] and [x2, x2 + w2] overlap. */
    private fun intersects(x1: Float, w1: Float, x2: Float, w2: Float): Boolean {
        return x1 < x2 + w2 && x1 + w1 > x2
    }

    // region Level definitions

    /** Create the first level. The layout consists of one home row, two
     * river rows, one safe row, two road rows and one start row. Cars and
     * logs are relatively sparse and move slowly. */
    private fun createLevel1(): GameLevel {
        val rows = listOf(
            RowDefinition(type = RowType.HOME),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.004f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(width = 0.2f, initialX = 0.0f),
                    ObjectDefinition(width = 0.2f, initialX = 0.5f)
                )
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.005f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(width = 0.2f, initialX = 0.3f),
                    ObjectDefinition(width = 0.2f, initialX = 0.8f)
                )
            ),
            RowDefinition(type = RowType.SAFE),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.006f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(width = 0.15f, initialX = 0.0f),
                    ObjectDefinition(width = 0.15f, initialX = 0.5f)
                )
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.008f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(width = 0.15f, initialX = 0.2f),
                    ObjectDefinition(width = 0.15f, initialX = 0.7f)
                )
            ),
            RowDefinition(type = RowType.START)
        )
        return GameLevel(rows = rows, homeCount = 3, lives = 3)
    }

    /** Create the second level. More rows and faster speeds increase the
     * challenge. The number of home slots grows to four. */
    private fun createLevel2(): GameLevel {
        val rows = listOf(
            RowDefinition(type = RowType.HOME),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.006f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.18f, 0.0f),
                    ObjectDefinition(0.18f, 0.35f),
                    ObjectDefinition(0.18f, 0.7f)
                )
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0065f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.18f, 0.2f),
                    ObjectDefinition(0.18f, 0.55f),
                    ObjectDefinition(0.18f, 0.9f)
                )
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.007f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.18f, 0.1f),
                    ObjectDefinition(0.18f, 0.45f),
                    ObjectDefinition(0.18f, 0.8f)
                )
            ),
            RowDefinition(type = RowType.SAFE),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.009f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.14f, 0.0f),
                    ObjectDefinition(0.14f, 0.4f),
                    ObjectDefinition(0.14f, 0.8f)
                )
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0095f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.14f, 0.2f),
                    ObjectDefinition(0.14f, 0.6f),
                    ObjectDefinition(0.14f, 0.9f)
                )
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.01f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.14f, 0.1f),
                    ObjectDefinition(0.14f, 0.5f),
                    ObjectDefinition(0.14f, 0.9f)
                )
            ),
            RowDefinition(type = RowType.START)
        )
        return GameLevel(rows = rows, homeCount = 4, lives = 3)
    }

    /** Create the third and final level. It features even more lanes,
     * higher speeds and five home slots. */
    private fun createLevel3(): GameLevel {
        val rows = listOf(
            RowDefinition(type = RowType.HOME),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.007f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.16f, 0.0f),
                    ObjectDefinition(0.16f, 0.3f),
                    ObjectDefinition(0.16f, 0.6f),
                    ObjectDefinition(0.16f, 0.9f)
                )
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0075f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.16f, 0.15f),
                    ObjectDefinition(0.16f, 0.45f),
                    ObjectDefinition(0.16f, 0.75f),
                    ObjectDefinition(0.16f, 1.05f)
                )
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.008f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.16f, 0.05f),
                    ObjectDefinition(0.16f, 0.35f),
                    ObjectDefinition(0.16f, 0.65f),
                    ObjectDefinition(0.16f, 0.95f)
                )
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0085f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.16f, 0.25f),
                    ObjectDefinition(0.16f, 0.55f),
                    ObjectDefinition(0.16f, 0.85f),
                    ObjectDefinition(0.16f, 1.15f)
                )
            ),
            RowDefinition(type = RowType.SAFE),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.011f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.12f, 0.0f),
                    ObjectDefinition(0.12f, 0.35f),
                    ObjectDefinition(0.12f, 0.7f),
                    ObjectDefinition(0.12f, 1.05f)
                )
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0115f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.12f, 0.15f),
                    ObjectDefinition(0.12f, 0.5f),
                    ObjectDefinition(0.12f, 0.85f),
                    ObjectDefinition(0.12f, 1.2f)
                )
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.012f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.12f, 0.05f),
                    ObjectDefinition(0.12f, 0.4f),
                    ObjectDefinition(0.12f, 0.75f),
                    ObjectDefinition(0.12f, 1.1f)
                )
            ),
            RowDefinition(type = RowType.START)
        )
        return GameLevel(rows = rows, homeCount = 5, lives = 3)
    }
    // endregion
}