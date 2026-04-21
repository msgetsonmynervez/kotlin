package com.example.frogger.model

/**
 * Represents a live instance of a moving object within the game. The
 * x coordinate is normalized between 0.0 and 1.0 representing the
 * fraction of the available horizontal space. The width is also
 * normalized. Mutable properties allow the view model to update
 * positions each frame.
 */
data class GameObject(
    var x: Float,
    val width: Float
)

/**
 * Mutable state for a single row during gameplay. Each row is
 * configured from a [RowDefinition] and contains a mutable list of
 * game objects with positions that change over time. Rows have a
 * speed and direction used to move objects. SAFE, HOME and START
 * rows do not move objects and use a speed of 0f.
 */
data class RowState(
    val type: RowType,
    val speed: Float,
    val goingLeft: Boolean,
    val objects: MutableList<GameObject>
)

/**
 * The current game state exposed to the UI. The ViewModel updates
 * this state on a regular interval. All fractional positions are
 * normalized to the width of the screen. Row indices run from 0 at
 * the top (home) to [rows.lastIndex] at the bottom (start). Lives
 * decrease when the frog collides or falls into the river. The
 * homes list tracks whether each home slot has been occupied. When
 * all homes are true the game transitions to the LEVEL_COMPLETE or
 * GAME_WIN state.
 */
data class GameState(
    val currentLevelIndex: Int,
    val rows: List<RowState>,
    var frogX: Float,
    var frogRow: Int,
    var lives: Int,
    val homes: MutableList<Boolean>,
    var status: GameStatus
)
