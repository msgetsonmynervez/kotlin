package com.sterlingsworld.feature.game.games.spoonsandstairs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import java.util.UUID
import kotlin.math.floor

enum class Lane(val index: Int) {
    LEFT(0),
    CENTER(1),
    RIGHT(2),
}

enum class GameObjectType {
    TOY,
    LAUNDRY,
    STAIRS,
    WATER,
    LIGHTNING,
}

enum class SpoonsAndStairsStatus {
    START_MENU,
    COUNTDOWN,
    PLAYING,
    GAME_OVER,
}

data class SpoonsAndStairsObject(
    val id: UUID = UUID.randomUUID(),
    val type: GameObjectType,
    val lane: Lane,
    val yPosition: Float,
    val widthFraction: Float,
    val heightPx: Float,
    val countsAsHazard: Boolean,
)

private data class SpawnInstruction(
    val type: GameObjectType,
    val lane: Lane,
)

private data class SpawnPattern(
    val instructions: List<SpawnInstruction>,
    val spacingFactor: Float,
)

class SpoonsAndStairsViewModel : ViewModel() {
    var currentLane by mutableStateOf(Lane.CENTER)
        private set

    var spoons by mutableIntStateOf(5)
        private set

    var score by mutableIntStateOf(0)
        private set

    var speedMultiplier by mutableFloatStateOf(1f)
        private set

    var activeObjects by mutableStateOf(listOf<SpoonsAndStairsObject>())
        internal set

    var gameStatus by mutableStateOf(SpoonsAndStairsStatus.START_MENU)
        private set

    var countdownValue by mutableIntStateOf(3)
        private set

    var pickupsCollected by mutableIntStateOf(0)
        private set

    var hazardsDodged by mutableIntStateOf(0)
        private set

    var comboStreak by mutableIntStateOf(0)
        private set

    var bestCombo by mutableIntStateOf(0)
        private set

    var statusMessage by mutableStateOf("Read the lane, protect your spoons, and scoop up recovery pickups.")
        private set

    var lastImpactAtMs by mutableLongStateOf(0L)
        private set

    var lastPickupAtMs by mutableLongStateOf(0L)
        private set

    private var runStarted by mutableStateOf(false)
    private var startedAtMs by mutableLongStateOf(0L)
    private var finishedAtMs by mutableLongStateOf(0L)
    private var countdownTimerSec = 0f
    private var spawnCooldownSec = 0f
    private var survivalScoreRemainder = 0f
    private var patternCursor = 0

    fun moveLeft() {
        moveToLane((currentLane.index - 1).coerceAtLeast(0))
    }

    fun moveRight() {
        moveToLane((currentLane.index + 1).coerceAtMost(Lane.entries.lastIndex))
    }

    fun resetGame() {
        currentLane = Lane.CENTER
        spoons = 5
        score = 0
        speedMultiplier = 1f
        activeObjects = emptyList()
        gameStatus = SpoonsAndStairsStatus.START_MENU
        countdownValue = 3
        pickupsCollected = 0
        hazardsDodged = 0
        comboStreak = 0
        bestCombo = 0
        statusMessage = "Read the lane, protect your spoons, and scoop up recovery pickups."
        runStarted = false
        startedAtMs = 0L
        finishedAtMs = 0L
        countdownTimerSec = 0f
        spawnCooldownSec = 0f
        survivalScoreRemainder = 0f
        patternCursor = 0
        lastImpactAtMs = 0L
        lastPickupAtMs = 0L
    }

    fun startGame() {
        resetGame()
        gameStatus = SpoonsAndStairsStatus.COUNTDOWN
        countdownValue = 3
        countdownTimerSec = 1f
        statusMessage = "Three short beats. Then the route starts moving."
        runStarted = true
        startedAtMs = System.currentTimeMillis()
    }

    fun endRun() {
        if (!runStarted) return
        if (gameStatus == SpoonsAndStairsStatus.PLAYING || gameStatus == SpoonsAndStairsStatus.COUNTDOWN) {
            finishRun()
        }
    }

    fun buildResult(): GameResult {
        val finished = gameStatus == SpoonsAndStairsStatus.GAME_OVER
        val durationMs = when {
            !runStarted -> 0L
            finishedAtMs > 0L -> finishedAtMs - startedAtMs
            else -> System.currentTimeMillis() - startedAtMs
        }.coerceAtLeast(0L)
        val stars = when {
            score >= 3400 && spoons >= 3 -> 3
            score >= 2100 -> 2
            score > 0 -> 1
            else -> 0
        }
        return GameResult(
            completed = finished,
            score = score,
            stars = stars,
            durationMs = durationMs,
            perfect = finished && spoons == 5 && hazardsDodged >= 12,
        )
    }

    internal fun tick(deltaSeconds: Float, playAreaHeightPx: Float) {
        if (playAreaHeightPx <= 0f) return
        when (gameStatus) {
            SpoonsAndStairsStatus.COUNTDOWN -> advanceCountdown(deltaSeconds)
            SpoonsAndStairsStatus.PLAYING -> advanceRun(deltaSeconds, playAreaHeightPx)
            else -> Unit
        }
    }

    private fun advanceCountdown(deltaSeconds: Float) {
        countdownTimerSec -= deltaSeconds
        if (countdownTimerSec > 0f) return

        countdownValue -= 1
        if (countdownValue <= 0) {
            countdownValue = 0
            gameStatus = SpoonsAndStairsStatus.PLAYING
            statusMessage = "Route live. Chase pickups, break hazards cleanly."
            spawnCooldownSec = 0.15f
        } else {
            countdownTimerSec = 1f
            statusMessage = "Starting in $countdownValue..."
        }
    }

    private fun advanceRun(deltaSeconds: Float, playAreaHeightPx: Float) {
        val nextSpeedMultiplier = 1f +
            minOf(1.35f, score / 2500f + hazardsDodged / 18f + pickupsCollected / 25f)
        speedMultiplier = nextSpeedMultiplier

        val movedObjects = activeObjects.map { obj ->
            obj.copy(yPosition = obj.yPosition + baseSpeedPxPerSecond(playAreaHeightPx) * nextSpeedMultiplier * deltaSeconds)
        }

        val playerTop = playAreaHeightPx * 0.78f
        val playerBottom = playAreaHeightPx * 0.94f
        var nextObjects = mutableListOf<SpoonsAndStairsObject>()

        movedObjects.forEach { obj ->
            if (laneMatchesPlayer(obj.lane) && overlapsPlayer(obj, playerTop, playerBottom)) {
                resolveCollision(obj)
            } else if (obj.yPosition - obj.heightPx / 2f > playAreaHeightPx + 48f) {
                if (obj.countsAsHazard) {
                    hazardsDodged += 1
                    comboStreak += 1
                    bestCombo = maxOf(bestCombo, comboStreak)
                    score += 30 + comboStreak * 4
                    statusMessage = "Clean dodge. Keep the lane rhythm."
                }
            } else {
                nextObjects += obj
            }
        }

        activeObjects = nextObjects

        spawnCooldownSec -= deltaSeconds
        if (spawnCooldownSec <= 0f) {
            spawnPattern(playAreaHeightPx)
        }

        survivalScoreRemainder += deltaSeconds * 30f
        val survivalPoints = floor(survivalScoreRemainder).toInt()
        if (survivalPoints > 0) {
            score += survivalPoints
            survivalScoreRemainder -= survivalPoints
        }
    }

    private fun resolveCollision(obj: SpoonsAndStairsObject) {
        activeObjects = activeObjects.filterNot { it.id == obj.id }
        if (obj.countsAsHazard) {
            spoons = (spoons - 1).coerceAtLeast(0)
            comboStreak = 0
            lastImpactAtMs = System.currentTimeMillis()
            statusMessage = "Impact. Reset the lane and rebuild your streak."
            if (spoons == 0) {
                finishRun()
            }
        } else {
            spoons = (spoons + 1).coerceAtMost(5)
            pickupsCollected += 1
            comboStreak += 1
            bestCombo = maxOf(bestCombo, comboStreak)
            lastPickupAtMs = System.currentTimeMillis()
            val pickupBonus = 90 + comboStreak * 12
            score += pickupBonus
            statusMessage = "Pickup secured. Push the combo while the lanes are open."
        }
    }

    private fun finishRun() {
        gameStatus = SpoonsAndStairsStatus.GAME_OVER
        statusMessage = "Run closed. Score $score, dodges $hazardsDodged, pickups $pickupsCollected."
        if (finishedAtMs == 0L) {
            finishedAtMs = System.currentTimeMillis()
        }
    }

    private fun moveToLane(index: Int) {
        val target = Lane.entries[index]
        if (target == currentLane || gameStatus != SpoonsAndStairsStatus.PLAYING) return
        currentLane = target
        statusMessage = "Lane ${target.index + 1}. Stay ahead of the next wave."
    }

    private fun laneMatchesPlayer(lane: Lane): Boolean = lane == currentLane

    private fun overlapsPlayer(obj: SpoonsAndStairsObject, playerTop: Float, playerBottom: Float): Boolean {
        val top = obj.yPosition - obj.heightPx / 2f
        val bottom = obj.yPosition + obj.heightPx / 2f
        return bottom >= playerTop && top <= playerBottom
    }

    private fun spawnPattern(playAreaHeightPx: Float) {
        val difficultyTier = when {
            score >= 2400 -> 2
            score >= 1100 -> 1
            else -> 0
        }
        val pattern = patternBank[difficultyTier][patternCursor % patternBank[difficultyTier].size]
        patternCursor += 1

        val spawned = pattern.instructions.mapIndexed { index, instruction ->
            val spec = objectSpec(instruction.type, playAreaHeightPx)
            SpoonsAndStairsObject(
                type = instruction.type,
                lane = instruction.lane,
                yPosition = -spec.second / 2f - index * spec.second * pattern.spacingFactor,
                widthFraction = spec.first,
                heightPx = spec.second,
                countsAsHazard = instruction.type in hazardTypes,
            )
        }
        activeObjects = activeObjects + spawned
        spawnCooldownSec = (1.05f - difficultyTier * 0.12f).coerceAtLeast(0.55f) * pattern.spacingFactor
    }

    private fun objectSpec(type: GameObjectType, playAreaHeightPx: Float): Pair<Float, Float> = when (type) {
        GameObjectType.TOY -> 0.52f to playAreaHeightPx * 0.10f
        GameObjectType.LAUNDRY -> 0.54f to playAreaHeightPx * 0.11f
        GameObjectType.STAIRS -> 0.62f to playAreaHeightPx * 0.13f
        GameObjectType.WATER -> 0.40f to playAreaHeightPx * 0.09f
        GameObjectType.LIGHTNING -> 0.36f to playAreaHeightPx * 0.08f
    }

    private fun baseSpeedPxPerSecond(playAreaHeightPx: Float): Float = playAreaHeightPx * 0.42f

    companion object {
        private val hazardTypes = setOf(GameObjectType.TOY, GameObjectType.LAUNDRY, GameObjectType.STAIRS)

        private val patternBank = listOf(
            listOf(
                SpawnPattern(listOf(SpawnInstruction(GameObjectType.TOY, Lane.LEFT)), spacingFactor = 1f),
                SpawnPattern(listOf(SpawnInstruction(GameObjectType.WATER, Lane.CENTER)), spacingFactor = 1f),
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.TOY, Lane.LEFT),
                        SpawnInstruction(GameObjectType.LIGHTNING, Lane.RIGHT),
                    ),
                    spacingFactor = 1.2f,
                ),
                SpawnPattern(listOf(SpawnInstruction(GameObjectType.LAUNDRY, Lane.RIGHT)), spacingFactor = 1f),
            ),
            listOf(
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.STAIRS, Lane.CENTER),
                        SpawnInstruction(GameObjectType.WATER, Lane.LEFT),
                    ),
                    spacingFactor = 1.1f,
                ),
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.TOY, Lane.LEFT),
                        SpawnInstruction(GameObjectType.LAUNDRY, Lane.RIGHT),
                    ),
                    spacingFactor = 1f,
                ),
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.LIGHTNING, Lane.CENTER),
                        SpawnInstruction(GameObjectType.STAIRS, Lane.RIGHT),
                    ),
                    spacingFactor = 0.95f,
                ),
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.TOY, Lane.RIGHT),
                        SpawnInstruction(GameObjectType.WATER, Lane.RIGHT),
                    ),
                    spacingFactor = 1.35f,
                ),
            ),
            listOf(
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.TOY, Lane.LEFT),
                        SpawnInstruction(GameObjectType.STAIRS, Lane.CENTER),
                        SpawnInstruction(GameObjectType.LIGHTNING, Lane.RIGHT),
                    ),
                    spacingFactor = 0.9f,
                ),
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.LAUNDRY, Lane.RIGHT),
                        SpawnInstruction(GameObjectType.WATER, Lane.CENTER),
                        SpawnInstruction(GameObjectType.TOY, Lane.LEFT),
                    ),
                    spacingFactor = 0.95f,
                ),
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.STAIRS, Lane.LEFT),
                        SpawnInstruction(GameObjectType.TOY, Lane.RIGHT),
                    ),
                    spacingFactor = 0.8f,
                ),
                SpawnPattern(
                    listOf(
                        SpawnInstruction(GameObjectType.LIGHTNING, Lane.LEFT),
                        SpawnInstruction(GameObjectType.LAUNDRY, Lane.CENTER),
                        SpawnInstruction(GameObjectType.WATER, Lane.RIGHT),
                    ),
                    spacingFactor = 0.88f,
                ),
            ),
        )
    }
}
