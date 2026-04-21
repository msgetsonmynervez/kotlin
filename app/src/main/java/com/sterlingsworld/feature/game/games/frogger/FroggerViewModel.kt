package com.sterlingsworld.feature.game.games.frogger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.feature.game.games.frogger.model.Direction
import com.sterlingsworld.feature.game.games.frogger.model.GameLevel
import com.sterlingsworld.feature.game.games.frogger.model.GameObject
import com.sterlingsworld.feature.game.games.frogger.model.GameState
import com.sterlingsworld.feature.game.games.frogger.model.GameStatus
import com.sterlingsworld.feature.game.games.frogger.model.ObjectDefinition
import com.sterlingsworld.feature.game.games.frogger.model.RowDefinition
import com.sterlingsworld.feature.game.games.frogger.model.RowState
import com.sterlingsworld.feature.game.games.frogger.model.RowType
import kotlin.math.floor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FroggerViewModel : ViewModel() {
    private val frogWidth = 0.08f
    private val moveCooldownMs = 170L
    private val startingLives = 3

    val frogWidthNormalized: Float
        get() = frogWidth

    private val levels = listOf(
        createLevel1(),
        createLevel2(),
        createLevel3(),
    )

    private val _state = MutableStateFlow(initializeStateForLevel(levelIndex = 0, status = GameStatus.START_MENU))
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var gameLoopJob: Job? = null
    private var startedAtMs = 0L
    private var levelStartedAtMs = 0L
    private var lastMoveAtMs = 0L

    init {
        startGameLoop()
    }

    fun buildResult(): GameResult {
        val snapshot = _state.value
        val completionBonus = if (snapshot.status == GameStatus.GAME_WIN) 800 else 0
        val score = snapshot.score + completionBonus + snapshot.lives * 120
        val stars = when {
            snapshot.status == GameStatus.GAME_WIN && snapshot.lives == startingLives -> 3
            snapshot.status == GameStatus.GAME_WIN -> 2
            score >= 900 -> 1
            else -> 0
        }
        return GameResult(
            completed = snapshot.status == GameStatus.GAME_WIN,
            score = score,
            stars = stars,
            durationMs = (System.currentTimeMillis() - startedAtMs).coerceAtLeast(0L),
            perfect = snapshot.status == GameStatus.GAME_WIN && snapshot.lives == startingLives,
        )
    }

    fun startGame() {
        val snapshot = _state.value
        startedAtMs = System.currentTimeMillis()
        levelStartedAtMs = startedAtMs
        lastMoveAtMs = 0L
        _state.value = snapshot.copy(
            status = GameStatus.PLAYING,
            statusMessage = "Fill each home slot. Ride logs, avoid traffic.",
        )
    }

    fun moveFrog(direction: Direction) {
        val snapshot = _state.value
        if (snapshot.status != GameStatus.PLAYING) return

        val now = System.currentTimeMillis()
        if (now - lastMoveAtMs < moveCooldownMs) return

        var newX = snapshot.frogX
        var newRow = snapshot.frogRow
        val stepX = frogWidth

        when (direction) {
            Direction.LEFT -> newX -= stepX
            Direction.RIGHT -> newX += stepX
            Direction.UP -> newRow -= 1
            Direction.DOWN -> newRow += 1
        }

        newX = newX.coerceIn(0f, 1f - frogWidth)
        newRow = newRow.coerceIn(0, snapshot.rows.lastIndex)
        if (newX == snapshot.frogX && newRow == snapshot.frogRow) return

        lastMoveAtMs = now
        val moveScore = when (direction) {
            Direction.UP -> 24
            Direction.LEFT, Direction.RIGHT -> 8
            Direction.DOWN -> 0
        }
        _state.value = snapshot.copy(
            frogX = newX,
            frogRow = newRow,
            score = snapshot.score + moveScore,
            statusMessage = if (direction == Direction.UP) "Good hop. Keep the pressure on." else snapshot.statusMessage,
        )
    }

    fun resumeAfterLifeLost() {
        val snapshot = _state.value
        if (snapshot.status != GameStatus.LIFE_LOST) return
        levelStartedAtMs = System.currentTimeMillis()
        _state.value = snapshot.copy(
            status = GameStatus.PLAYING,
            statusMessage = "Reset. Read one lane ahead.",
        )
    }

    fun nextLevel() {
        val snapshot = _state.value
        if (snapshot.status != GameStatus.LEVEL_COMPLETE) return
        _state.value = initializeStateForLevel(
            levelIndex = snapshot.currentLevelIndex + 1,
            lives = snapshot.lives,
            score = snapshot.score,
            crossings = snapshot.crossings,
            status = GameStatus.START_MENU,
        )
    }

    fun restartCurrentLevel() {
        val snapshot = _state.value
        levelStartedAtMs = 0L
        lastMoveAtMs = 0L
        _state.value = initializeStateForLevel(
            levelIndex = snapshot.currentLevelIndex,
            lives = startingLives,
            score = 0,
            crossings = 0,
            status = GameStatus.START_MENU,
        )
    }

    fun restartRun() {
        startedAtMs = 0L
        levelStartedAtMs = 0L
        lastMoveAtMs = 0L
        _state.value = initializeStateForLevel(levelIndex = 0, status = GameStatus.START_MENU)
    }

    private fun startGameLoop() {
        if (gameLoopJob?.isActive == true) return
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(16)
                if (_state.value.status == GameStatus.PLAYING) {
                    updateGameState()
                }
            }
        }
    }

    private fun initializeStateForLevel(
        levelIndex: Int,
        lives: Int = startingLives,
        score: Int = 0,
        crossings: Int = 0,
        status: GameStatus,
    ): GameState {
        val level = levels[levelIndex]
        val rowStates = level.rows.map { def ->
            RowState(
                type = def.type,
                speed = def.speed,
                goingLeft = def.goingLeft,
                objects = def.objects.map { obj ->
                    GameObject(x = obj.initialX, width = obj.width)
                },
            )
        }
        return GameState(
            currentLevelIndex = levelIndex,
            rows = rowStates,
            frogX = 0.5f - frogWidth / 2f,
            frogRow = rowStates.lastIndex,
            lives = lives,
            homes = List(level.homeCount) { false },
            status = status,
            score = score,
            crossings = crossings,
            statusMessage = when (status) {
                GameStatus.START_MENU -> level.introCopy
                else -> ""
            },
        )
    }

    private fun updateGameState() {
        val snapshot = _state.value
        var frogX = snapshot.frogX
        var frogRow = snapshot.frogRow
        var lives = snapshot.lives
        var score = snapshot.score
        var crossings = snapshot.crossings
        val homes = snapshot.homes.toMutableList()
        val updatedRows = snapshot.rows.map { row ->
            if (row.speed == 0f) {
                row
            } else {
                row.copy(
                    objects = row.objects.map { obj ->
                        val nextX = if (row.goingLeft) {
                            (obj.x - row.speed).let { if (it + obj.width < 0f) 1f else it }
                        } else {
                            (obj.x + row.speed).let { if (it > 1f) -obj.width else it }
                        }
                        obj.copy(x = nextX)
                    },
                )
            }
        }
        val level = levels[snapshot.currentLevelIndex]

        updatedRows.forEachIndexed { rowIndex, row ->
            if (rowIndex != frogRow) return@forEachIndexed

            when (row.type) {
                RowType.ROAD -> {
                    if (row.objects.any { intersects(frogX, frogWidth, it.x, it.width) }) {
                        loseLife(
                            snapshot = snapshot,
                            updatedRows = updatedRows,
                            remainingLives = lives - 1,
                            reason = "Traffic clipped you. Wait for the gap, then hop.",
                        )
                        return
                    }
                }

                RowType.RIVER -> {
                    val supportingLog = row.objects.firstOrNull { intersects(frogX, frogWidth, it.x, it.width) }
                    if (supportingLog == null) {
                        loseLife(
                            snapshot = snapshot,
                            updatedRows = updatedRows,
                            remainingLives = lives - 1,
                            reason = "You slipped into the water. Land squarely on a log.",
                        )
                        return
                    }
                    frogX += if (row.goingLeft) -row.speed else row.speed
                }

                else -> Unit
            }
        }

        frogX = frogX.coerceIn(0f, 1f - frogWidth)

        if (frogRow == 0 && updatedRows.first().type == RowType.HOME) {
            val slotWidth = 1f / level.homeCount
            val homeIndex = floor((frogX + frogWidth / 2f) / slotWidth).toInt()
            val clampedIndex = homeIndex.coerceIn(0, homes.lastIndex)
            if (homes[clampedIndex]) {
                loseLife(
                    snapshot = snapshot,
                    updatedRows = updatedRows,
                    remainingLives = lives - 1,
                    reason = "That home is already full. Aim for an open slot.",
                )
                return
            }

            homes[clampedIndex] = true
            crossings += 1
            val elapsed = (System.currentTimeMillis() - levelStartedAtMs).coerceAtLeast(0L)
            val speedBonus = (260 - elapsed / 35L).toInt().coerceAtLeast(40)
            score += 420 + speedBonus
            frogX = 0.5f - frogWidth / 2f
            frogRow = updatedRows.lastIndex

            if (homes.all { it }) {
                val isFinalLevel = snapshot.currentLevelIndex == levels.lastIndex
                _state.value = snapshot.copy(
                    rows = updatedRows,
                    frogX = frogX,
                    frogRow = frogRow,
                    lives = lives,
                    homes = homes,
                    status = if (isFinalLevel) GameStatus.GAME_WIN else GameStatus.LEVEL_COMPLETE,
                    score = score + if (isFinalLevel) 500 else 220,
                    crossings = crossings,
                    statusMessage = if (isFinalLevel) {
                        "Every crossing is clear. Clean finish."
                    } else {
                        "Crossing secured. The next route is tighter."
                    },
                )
                return
            }
        }

        _state.value = snapshot.copy(
            rows = updatedRows,
            frogX = frogX,
            frogRow = frogRow,
            lives = lives,
            homes = homes,
            score = score,
            crossings = crossings,
        )
    }

    private fun loseLife(
        snapshot: GameState,
        updatedRows: List<RowState>,
        remainingLives: Int,
        reason: String,
    ) {
        val status = if (remainingLives <= 0) GameStatus.GAME_OVER else GameStatus.LIFE_LOST
        _state.value = snapshot.copy(
            rows = updatedRows,
            frogX = 0.5f - frogWidth / 2f,
            frogRow = updatedRows.lastIndex,
            lives = remainingLives.coerceAtLeast(0),
            status = status,
            statusMessage = reason,
        )
    }

    private fun intersects(x1: Float, w1: Float, x2: Float, w2: Float): Boolean {
        val inset = 0.015f
        return x1 + inset < x2 + w2 - inset && x1 + w1 - inset > x2 + inset
    }

    private fun createLevel1(): GameLevel {
        val rows = listOf(
            RowDefinition(type = RowType.HOME),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0036f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(width = 0.24f, initialX = 0.02f),
                    ObjectDefinition(width = 0.24f, initialX = 0.54f),
                ),
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0044f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(width = 0.22f, initialX = 0.12f),
                    ObjectDefinition(width = 0.22f, initialX = 0.62f),
                ),
            ),
            RowDefinition(type = RowType.SAFE),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0055f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(width = 0.16f, initialX = 0.08f),
                    ObjectDefinition(width = 0.18f, initialX = 0.58f),
                ),
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0064f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(width = 0.15f, initialX = 0.18f),
                    ObjectDefinition(width = 0.15f, initialX = 0.72f),
                ),
            ),
            RowDefinition(type = RowType.START),
        )
        return GameLevel(rows = rows, homeCount = 3, lives = startingLives, introCopy = "Cross the side street, then ride the river logs into three open homes.")
    }

    private fun createLevel2(): GameLevel {
        val rows = listOf(
            RowDefinition(type = RowType.HOME),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0056f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.18f, 0.00f),
                    ObjectDefinition(0.24f, 0.34f),
                    ObjectDefinition(0.18f, 0.76f),
                ),
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0061f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.18f, 0.16f),
                    ObjectDefinition(0.18f, 0.48f),
                    ObjectDefinition(0.24f, 0.84f),
                ),
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0067f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.16f, 0.08f),
                    ObjectDefinition(0.18f, 0.42f),
                    ObjectDefinition(0.18f, 0.78f),
                ),
            ),
            RowDefinition(type = RowType.SAFE),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0084f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.12f, 0.04f),
                    ObjectDefinition(0.16f, 0.42f),
                    ObjectDefinition(0.12f, 0.82f),
                ),
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0088f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.14f, 0.18f),
                    ObjectDefinition(0.14f, 0.54f),
                    ObjectDefinition(0.18f, 0.92f),
                ),
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0092f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.13f, 0.09f),
                    ObjectDefinition(0.13f, 0.44f),
                    ObjectDefinition(0.13f, 0.79f),
                ),
            ),
            RowDefinition(type = RowType.START),
        )
        return GameLevel(rows = rows, homeCount = 4, lives = startingLives, introCopy = "More traffic, less slack. Use the center safe row to reset your read.")
    }

    private fun createLevel3(): GameLevel {
        val rows = listOf(
            RowDefinition(type = RowType.HOME),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0066f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.16f, 0.02f),
                    ObjectDefinition(0.16f, 0.28f),
                    ObjectDefinition(0.20f, 0.56f),
                    ObjectDefinition(0.16f, 0.88f),
                ),
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0072f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.18f, 0.10f),
                    ObjectDefinition(0.16f, 0.38f),
                    ObjectDefinition(0.18f, 0.66f),
                    ObjectDefinition(0.16f, 0.96f),
                ),
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0078f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.14f, 0.06f),
                    ObjectDefinition(0.18f, 0.32f),
                    ObjectDefinition(0.14f, 0.62f),
                    ObjectDefinition(0.18f, 0.90f),
                ),
            ),
            RowDefinition(
                type = RowType.RIVER,
                speed = 0.0082f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.16f, 0.18f),
                    ObjectDefinition(0.16f, 0.46f),
                    ObjectDefinition(0.16f, 0.74f),
                    ObjectDefinition(0.16f, 1.02f),
                ),
            ),
            RowDefinition(type = RowType.SAFE),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0102f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.12f, 0.02f),
                    ObjectDefinition(0.14f, 0.31f),
                    ObjectDefinition(0.12f, 0.63f),
                    ObjectDefinition(0.16f, 0.94f),
                ),
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0108f,
                goingLeft = false,
                objects = listOf(
                    ObjectDefinition(0.12f, 0.12f),
                    ObjectDefinition(0.16f, 0.44f),
                    ObjectDefinition(0.12f, 0.72f),
                    ObjectDefinition(0.14f, 1.02f),
                ),
            ),
            RowDefinition(
                type = RowType.ROAD,
                speed = 0.0112f,
                goingLeft = true,
                objects = listOf(
                    ObjectDefinition(0.13f, 0.04f),
                    ObjectDefinition(0.13f, 0.36f),
                    ObjectDefinition(0.13f, 0.68f),
                    ObjectDefinition(0.13f, 1.00f),
                ),
            ),
            RowDefinition(type = RowType.START),
        )
        return GameLevel(rows = rows, homeCount = 5, lives = startingLives, introCopy = "Final rush. Commit to a lane early and treat each home slot as a separate puzzle.")
    }
}
