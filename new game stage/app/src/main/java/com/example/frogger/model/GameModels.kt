package com.example.frogger.model

/**
 * Simple direction enumeration used by the game when the frog
 * moves between rows or along the X axis. The values map to
 * intuitive player actions. Only the four cardinal directions are
 * supported since Frogger does not permit diagonal movement.
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

/**
 * Types of rows that can occur within a level. Roads contain
 * vehicles that the player must avoid; rivers contain logs the
 * player must ride on to cross; safe rows have no hazards and
 * provide a brief respite; the home row marks the end of the
 * level and contains a number of home slots; the start row
 * represents where the frog begins each life.
 */
enum class RowType {
    ROAD,
    RIVER,
    SAFE,
    HOME,
    START
}

/**
 * Overall status of the game. The player remains in the Playing
 * state during normal game play. When all homes in the current
 * level are occupied the state moves to LevelComplete. If the
 * player loses all lives the game ends with GameOver. When the
 * final level is completed the status transitions to GameWin.
 */
enum class GameStatus {
    PLAYING,
    LEVEL_COMPLETE,
    GAME_OVER,
    GAME_WIN
}

/**
 * Defines a single moving object such as a car or log. The
 * width and initialX values are specified relative to the
 * available horizontal space in the row (from 0.0f to 1.0f).
 */
data class ObjectDefinition(
    val width: Float,
    val initialX: Float
)

/**
 * Describes a row within a level. For ROAD and RIVER rows the
 * speed and direction determine how the objects move across
 * frames. RIVER rows contain logs that the frog can stand on; if
 * the frog is not on a log when in a river row it will drown.
 * Objects are defined as a list of definitions; at runtime these
 * will be converted to mutable game objects that track their
 * current x‑positions.
 */
data class RowDefinition(
    val type: RowType,
    val speed: Float = 0f,
    val goingLeft: Boolean = false,
    val objects: List<ObjectDefinition> = emptyList()
)

/**
 * Encapsulates all configuration for a level. Each level
 * supplies a list of row definitions in order from top (row 0)
 * to bottom (last row). The homeCount determines how many home
 * slots appear in the home row. Lives specify the number of
 * attempts the player receives before the game ends.
 */
data class GameLevel(
    val rows: List<RowDefinition>,
    val homeCount: Int,
    val lives: Int = 3
)
