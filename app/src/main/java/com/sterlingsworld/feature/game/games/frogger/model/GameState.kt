package com.sterlingsworld.feature.game.games.frogger.model

data class GameObject(
    val x: Float,
    val width: Float,
)

data class RowState(
    val type: RowType,
    val speed: Float,
    val goingLeft: Boolean,
    val objects: List<GameObject>,
)

data class GameState(
    val currentLevelIndex: Int,
    val rows: List<RowState>,
    val frogX: Float,
    val frogRow: Int,
    val lives: Int,
    val homes: List<Boolean>,
    val status: GameStatus,
    val score: Int,
    val crossings: Int,
    val statusMessage: String,
)
