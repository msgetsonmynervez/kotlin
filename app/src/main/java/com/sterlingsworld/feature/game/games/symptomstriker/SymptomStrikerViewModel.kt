package com.sterlingsworld.feature.game.games.symptomstriker

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class SymptomStrikerUiState(
    val phase: BattlePhase = BattlePhase.INTRO,

    // Encounter context
    val encounterIndex: Int = 0,
    val totalEncounters: Int = 3,
    val encounterTitle: String = "",
    val introText: String = "",
    val symptomsDesc: String = "",
    val accentColor: Long = 0xFFFF9500,

    // Player
    val playerHp: Int = 80,
    val playerMaxHp: Int = 80,
    val playerSpoons: Int = 8,
    val playerMaxSpoons: Int = 8,
    val pushThroughUses: Int = 0,
    val pushThroughSafeUses: Int = DEFAULT_BATTLE_CONFIG.pushThroughSafeUses,
    val sessionSpoonPenalty: Int = 0,

    // Enemy
    val enemyName: String = "",
    val enemySprite: String = "",
    val enemyHp: Int = 0,
    val enemyMaxHp: Int = 0,
    val enemyEnraged: Boolean = false,

    // Active statuses with remaining turn counts
    val status: BattleStatus = BattleStatus(),

    // Moves available this encounter (resolved from MOVE_LIBRARY)
    val moves: List<MoveDefinition> = emptyList(),

    // Narrative feedback for the last resolved turn
    val battleLog: String = "",

    // Progress across encounters
    val encountersCleared: Int = 0,
)

class SymptomStrikerViewModel private constructor(
    private val encounters: List<EncounterDefinition>,
    private val config: BattleConfig,
    private val random: Random,
) : ViewModel() {

    /** Production constructor — no arguments needed from the Compose entry point. */
    constructor() : this(
        encounters = SYMPTOM_STRIKER_ENCOUNTERS,
        config = DEFAULT_BATTLE_CONFIG,
        random = Random.Default,
    )

    // ── Per-battle mutable state ─────────────────────────────────────────────

    private var encounterIndex = 0
    private var playerHp = 0
    private var playerMaxHp = 0
    private var playerSpoons = 0
    private var playerMaxSpoons = 0
    private var pushThroughUses = 0
    private var enemyHp = 0
    private var enemyEnraged = false
    private var status = BattleStatus()
    private var battleLog = ""
    private var phase = BattlePhase.INTRO

    // ── Session state — survives encounter transitions ───────────────────────

    private var sessionSpoonPenalty = 0
    private var encountersCleared = 0
    private val startTimeMs = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(SymptomStrikerUiState())
    val uiState: StateFlow<SymptomStrikerUiState> = _uiState.asStateFlow()

    init {
        initEncounter(0)
    }

    // ── Public events ────────────────────────────────────────────────────────

    /** Dismiss the intro card and begin the first turn. */
    fun onDismissIntro() {
        if (phase != BattlePhase.INTRO) return
        phase = BattlePhase.PLAYER_TURN
        battleLog = encounters[encounterIndex].symptomsDesc
        pushState()
    }

    /** Player selected a move from the action grid. */
    fun onMoveSelected(moveId: String) {
        if (phase != BattlePhase.PLAYER_TURN) return
        val move = MOVE_LIBRARY[moveId] ?: return
        val enc = encounters[encounterIndex]

        // Blocked by fog?
        if (status.foggedMoveId == moveId) {
            battleLog = "That move is blocked by brain fog! Choose something else."
            pushState()
            return
        }

        // Spoon check (skip for push_through — it costs HP instead)
        if (moveId != "push_through") {
            val effectiveCost = effectiveSpoonCost(move)
            if (playerSpoons < effectiveCost) {
                battleLog = "Not enough Spoons. Rest to recover energy."
                pushState()
                return
            }
        }

        val log = StringBuilder()

        // ── Apply player move ────────────────────────────────────────────────
        when (moveId) {
            "push_through" -> applyPushThrough(log)
            else -> applyRegularMove(move, log)
        }

        // ── Tick status down after player acts ──────────────────────────────
        status = status.tickDown()

        // ── Check if enemy defeated ──────────────────────────────────────────
        if (enemyHp <= 0) {
            enemyHp = 0
            encountersCleared++
            phase = if (encounterIndex >= encounters.lastIndex) {
                BattlePhase.RUN_WIN
            } else {
                BattlePhase.ENCOUNTER_WIN
            }
            log.append(" You defeated the ${enc.enemy.name}!")
            battleLog = log.toString()
            pushState()
            return
        }

        // ── Enemy turn ───────────────────────────────────────────────────────
        applyEnemyTurn(enc, log)

        // ── Overheated burn (applied during enemy phase) ─────────────────────
        if (status.overheated > 0) {
            val burn = maxOf(1, (playerMaxHp * config.overheatedBurnPercent).toInt())
            playerHp -= burn
            log.append(" Heat burns for $burn damage.")
        }

        // ── Update rage state ────────────────────────────────────────────────
        val rageThreshold = (enc.enemy.maxHp * config.rageTriggerPercent).toInt()
        if (!enemyEnraged && enemyHp <= rageThreshold && enemyHp > 0) {
            enemyEnraged = true
            log.append(" \u26a0\ufe0f ${enc.enemy.name} enters RAGE!")
        }

        // ── Check if player defeated ─────────────────────────────────────────
        if (playerHp <= 0) {
            playerHp = 0
            phase = BattlePhase.ENCOUNTER_LOSS
        }

        battleLog = log.toString()
        pushState()
    }

    /** Advance from ENCOUNTER_WIN to the next encounter. */
    fun onNextEncounter() {
        if (phase != BattlePhase.ENCOUNTER_WIN) return
        initEncounter(encounterIndex + 1)
    }

    /** Build the final GameResult for the shell to record. */
    fun buildResult(): GameResult {
        val won = phase == BattlePhase.RUN_WIN
        val hpRatio = if (playerMaxHp > 0) playerHp.toFloat() / playerMaxHp else 0f

        val score = encountersCleared * 100 +
            if (won) (hpRatio * 50).toInt() else 0

        val stars = when {
            won && hpRatio >= 0.50f -> 3
            won -> 2
            encountersCleared >= 1 -> 1
            else -> 0
        }

        // Perfect: won the full run without ever triggering the push-through penalty
        val perfect = won && sessionSpoonPenalty == 0

        return GameResult(
            completed = true,
            score = score,
            stars = stars,
            durationMs = System.currentTimeMillis() - startTimeMs,
            perfect = perfect,
        )
    }

    // ── Battle logic ─────────────────────────────────────────────────────────

    private fun initEncounter(index: Int) {
        val enc = encounters[index]
        encounterIndex = index
        playerMaxHp = enc.playerStartHp
        playerHp = enc.playerStartHp
        playerMaxSpoons = maxOf(config.minMaxSpoons, enc.playerStartSpoons - sessionSpoonPenalty)
        playerSpoons = playerMaxSpoons
        pushThroughUses = 0
        enemyHp = enc.enemy.maxHp
        enemyEnraged = false
        status = BattleStatus()
        phase = BattlePhase.INTRO
        battleLog = ""
        pushState()
    }

    private fun applyRegularMove(move: MoveDefinition, log: StringBuilder) {
        val cost = effectiveSpoonCost(move)
        playerSpoons -= cost

        // Heal
        if (move.healPercent > 0f) {
            val heal = (playerMaxHp * move.healPercent).toInt()
            val actual = minOf(heal, playerMaxHp - playerHp)
            playerHp = minOf(playerMaxHp, playerHp + actual)
            if (actual > 0) log.append("Healed $actual HP. ")
        }

        // Spoon restore
        if (move.spoonHeal > 0) {
            val gained = minOf(move.spoonHeal, playerMaxSpoons - playerSpoons)
            playerSpoons = minOf(playerMaxSpoons, playerSpoons + gained)
            if (gained > 0) log.append("+$gained Spoon(s). ")
        }

        // Clear a status
        val cleared = move.clearsStatus
        if (cleared != null && status.active.any { it.first == cleared }) {
            status = status.cleared(cleared)
            log.append("${cleared.label} cleared! ")
        }

        // Deal damage
        if (move.power > 0) {
            val missed = move.type == MoveType.ATTACK && status.vertigo > 0 &&
                random.nextFloat() < config.vertigoMissChance
            if (missed) {
                log.append("${move.label} missed! (Vertigo). ")
            } else {
                val enemy = encounters[encounterIndex].enemy
                enemyHp = maxOf(0, enemyHp - move.power)
                log.append("${move.label} deals ${move.power} damage. ")
                if (enemyHp > 0) log.append("${enemy.name} HP: $enemyHp/${enemy.maxHp}.")
            }
        }
    }

    private fun applyPushThrough(log: StringBuilder) {
        val hpCost = maxOf(1, (playerMaxHp * config.pushThroughHpCostPercent).toInt())
        playerHp -= hpCost
        pushThroughUses++

        val missingRatio = 1f - (playerHp.toFloat() / playerMaxHp.toFloat()).coerceIn(0f, 1f)
        val power = config.pushThroughBasePower +
            ((config.pushThroughMaxPower - config.pushThroughBasePower) * missingRatio).toInt()

        val enc = encounters[encounterIndex]
        enemyHp = maxOf(0, enemyHp - power)
        log.append("Push Through: spent $hpCost HP, dealt $power damage. ")

        if (pushThroughUses > config.pushThroughSafeUses) {
            sessionSpoonPenalty += config.pushThroughPenaltySpoons
            playerMaxSpoons = maxOf(config.minMaxSpoons, playerMaxSpoons - config.pushThroughPenaltySpoons)
            if (playerSpoons > playerMaxSpoons) playerSpoons = playerMaxSpoons
            log.append("Your body pays the price \u2014 permanently lost 1 max Spoon! ")
        } else if (pushThroughUses == config.pushThroughSafeUses) {
            log.append("WARNING: One more overuse will permanently reduce your max Spoons. ")
        }

        if (enemyHp > 0) log.append("${enc.enemy.name} HP: $enemyHp/${enc.enemy.maxHp}.")
    }

    private fun applyEnemyTurn(enc: EncounterDefinition, log: StringBuilder) {
        val specialChance = if (enemyEnraged) {
            enc.enemy.specialChanceEnraged
        } else {
            enc.enemy.specialChance
        }
        val triggerSpecial = random.nextFloat() < specialChance

        val rawPower = if (triggerSpecial) enc.enemy.specialPower else enc.enemy.normalPower
        val actualPower = if (enemyEnraged) {
            (rawPower * config.rageAttackMultiplier).toInt()
        } else {
            rawPower
        }

        playerHp = maxOf(0, playerHp - actualPower)

        if (triggerSpecial) {
            log.append("${enc.enemy.name} used ${enc.enemy.specialDescription} for $actualPower damage! ")
            applyEnemyStatus(enc, log)
        } else {
            log.append("${enc.enemy.name} attacks for $actualPower damage. ")
        }

        log.append("Your HP: $playerHp/$playerMaxHp.")
    }

    private fun applyEnemyStatus(enc: EncounterDefinition, log: StringBuilder) {
        when (enc.enemy.appliesStatus) {
            StatusKey.LOCKED -> {
                if (status.locked == 0) {
                    status = status.copy(locked = config.statusDuration)
                    log.append("You are LOCKED \u2014 attacks cost +1 Spoon for ${config.statusDuration} turns. ")
                }
            }
            StatusKey.FOGGED -> {
                if (status.fogged == 0) {
                    val targetId = pickFoggedTarget(enc.moves)
                    status = status.copy(fogged = config.statusDuration, foggedMoveId = targetId)
                    val targetLabel = targetId?.let { MOVE_LIBRARY[it]?.label } ?: "a move"
                    log.append("You are FOGGED \u2014 $targetLabel is blocked for ${config.statusDuration} turns. ")
                }
            }
            StatusKey.OVERHEATED -> {
                if (status.overheated == 0) {
                    status = status.copy(overheated = config.statusDuration)
                    log.append("You are OVERHEATED \u2014 burn damage each turn for ${config.statusDuration} turns. ")
                }
            }
            StatusKey.VERTIGO -> {
                if (status.vertigo == 0) {
                    status = status.copy(vertigo = config.statusDuration)
                    log.append("You have VERTIGO \u2014 attacks may miss for ${config.statusDuration} turns. ")
                }
            }
        }
    }

    /** Pick a move to fog: prefer attack/cure moves that cost Spoons (not free recovery or push_through). */
    private fun pickFoggedTarget(moveIds: List<String>): String? {
        val candidates = moveIds.filter { id ->
            val move = MOVE_LIBRARY[id] ?: return@filter false
            move.type != MoveType.RECOVERY && id != "push_through" && move.spoonCost > 0
        }
        return if (candidates.isNotEmpty()) candidates[random.nextInt(candidates.size)] else null
    }

    private fun effectiveSpoonCost(move: MoveDefinition): Int {
        var cost = move.spoonCost
        if (move.type == MoveType.ATTACK && status.locked > 0) {
            cost += config.lockedExtraSpoonCost
        }
        return cost
    }

    private fun pushState() {
        val enc = encounters[encounterIndex]
        _uiState.value = SymptomStrikerUiState(
            phase = phase,
            encounterIndex = encounterIndex,
            totalEncounters = encounters.size,
            encounterTitle = enc.title,
            introText = enc.intro,
            symptomsDesc = enc.symptomsDesc,
            accentColor = enc.accentColor,
            playerHp = playerHp,
            playerMaxHp = playerMaxHp,
            playerSpoons = playerSpoons,
            playerMaxSpoons = playerMaxSpoons,
            pushThroughUses = pushThroughUses,
            pushThroughSafeUses = config.pushThroughSafeUses,
            sessionSpoonPenalty = sessionSpoonPenalty,
            enemyName = enc.enemy.name,
            enemySprite = enc.enemy.sprite,
            enemyHp = enemyHp,
            enemyMaxHp = enc.enemy.maxHp,
            enemyEnraged = enemyEnraged,
            status = status,
            moves = enc.moves.mapNotNull { MOVE_LIBRARY[it] },
            battleLog = battleLog,
            encountersCleared = encountersCleared,
        )
    }

    companion object {
        /** For unit tests — injects encounter data, config, and Random. */
        internal fun test(
            encounters: List<EncounterDefinition> = SYMPTOM_STRIKER_ENCOUNTERS,
            config: BattleConfig = DEFAULT_BATTLE_CONFIG,
            random: Random = Random.Default,
        ): SymptomStrikerViewModel = SymptomStrikerViewModel(encounters, config, random)
    }
}
