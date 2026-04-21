package com.sterlingsworld.feature.game.games.frogger.model

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

enum class RowType {
    ROAD,
    RIVER,
    SAFE,
    HOME,
    START,
}

enum class GameStatus {
    START_MENU,
    PLAYING,
    LIFE_LOST,
    LEVEL_COMPLETE,
    GAME_OVER,
    GAME_WIN,
}

data class ObjectDefinition(
    val width: Float,
    val initialX: Float,
)

data class RowDefinition(
    val type: RowType,
    val speed: Float = 0f,
    val goingLeft: Boolean = false,
    val objects: List<ObjectDefinition> = emptyList(),
)

data class GameLevel(
    val rows: List<RowDefinition>,
    val homeCount: Int,
    val lives: Int = 3,
    val introCopy: String,
)
