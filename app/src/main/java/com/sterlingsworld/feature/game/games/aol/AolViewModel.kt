package com.sterlingsworld.feature.game.games.aol

import com.sterlingsworld.feature.game.games.symptomstriker.*

import androidx.lifecycle.ViewModel
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class AolUiState(
    val phase: BattlePhase = BattlePhase.INTRO,

    // Encounter context
    val encounterIndex: Int = 0,
    val totalEncounters: Int = 8,
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
    val enemyHp: Int = 100,
    val enemyMaxHp: Int = 100,
    val enemyEnraged: Boolean = false,

    // Status & Options
    val status: BattleStatus = BattleStatus(),
    val moves: List<MoveDefinition> = emptyList(),

    // Action Log
    val battleLog: List<String> = emptyList(),

    // Progress
    val encountersCleared: Int = 0,
)

class AolViewModel private constructor(
    private val encounters: List<EncounterDefinition>,
    private val config: BattleConfig,
    private val random: Random,
) : ViewModel() {

    constructor() : this(AOL_ENCOUNTERS, DEFAULT_BATTLE_CONFIG, Random.Default)

    private val _uiState = MutableStateFlow(AolUiState())
    val uiState: StateFlow<AolUiState> = _uiState.asStateFlow()

    private var currentEncounterIndex = 0
    private var enemyHp = 0
    private var enemyEnraged = false
    private var status = BattleStatus()
    private var battleLog = mutableListOf<String>()

    private var playerMaxHp = 80
    private var playerHp = 80
    private var playerMaxSpoons = 8
    private var playerSpoons = 8
    private var pushThroughUses = 0
    private var sessionSpoonPenalty = 0
    private var encountersCleared = 0

    init {
        loadEncounter(0)
    }

    private fun loadEncounter(index: Int) {
        if (index >= encounters.size) {
            _uiState.value = _uiState.value.copy(phase = BattlePhase.RUN_WIN)
            return
        }

        val enc = encounters[index]
        currentEncounterIndex = index

        playerMaxHp = enc.playerStartHp
        playerHp = enc.playerStartHp
        val baseMaxSpoons = enc.playerStartSpoons
        playerMaxSpoons = maxOf(config.minMaxSpoons, baseMaxSpoons - sessionSpoonPenalty)
        playerSpoons = playerMaxSpoons
        pushThroughUses = 0

        enemyHp = enc.enemy.maxHp
        enemyEnraged = false
        status = BattleStatus()

        battleLog = mutableListOf(enc.intro)
        updateState(BattlePhase.INTRO)
    }

    fun submitMove(move: MoveDefinition) {
        if (_uiState.value.phase != BattlePhase.PLAYER_TURN) return
        resolveTurn(move)
    }

    fun continueToNextEncounter() {
        if (_uiState.value.phase != BattlePhase.ENCOUNTER_WIN) return
        loadEncounter(currentEncounterIndex + 1)
    }

    fun acknowledgeIntro() {
        if (_uiState.value.phase != BattlePhase.INTRO) return
        updateState(BattlePhase.PLAYER_TURN)
    }

    private fun resolveTurn(move: MoveDefinition) {
        val enc = encounters[currentEncounterIndex]
        val enemy = enc.enemy
        battleLog.clear()

        // 1. Process player move
        battleLog.add("You used ${move.label}.")

        if (move.id == "heros_roar") {
            pushThroughUses++
            val cost = (playerMaxHp * config.pushThroughHpCostPercent).toInt()
            playerHp = maxOf(1, playerHp - cost)
            battleLog.add("Lost $cost HP pushing through.")

            if (pushThroughUses > config.pushThroughSafeUses) {
                sessionSpoonPenalty += config.pushThroughPenaltySpoons
                playerMaxSpoons = maxOf(config.minMaxSpoons, playerMaxSpoons - config.pushThroughPenaltySpoons)
                playerSpoons = minOf(playerSpoons, playerMaxSpoons)
                battleLog.add("Warning: Max Grace permanently reduced.")
            }
        } else {
            var actualCost = move.spoonCost
            if (move.type == MoveType.ATTACK && status.locked > 0) {
                actualCost += config.lockedExtraSpoonCost
            }
            playerSpoons = maxOf(0, playerSpoons - actualCost)
        }

        if (move.type == MoveType.CURE && move.clearsStatus != null) {
            status = status.cleared(move.clearsStatus)
            battleLog.add("${move.clearsStatus.label} cleared.")
        }

        if (move.type == MoveType.RECOVERY || move.type == MoveType.CURE) {
            if (move.healPercent > 0) {
                val heal = (playerMaxHp * move.healPercent).toInt()
                playerHp = minOf(playerMaxHp, playerHp + heal)
                battleLog.add("Recovered $heal HP.")
            }
            if (move.spoonHeal > 0) {
                playerSpoons = minOf(playerMaxSpoons, playerSpoons + move.spoonHeal)
                battleLog.add("Regained ${move.spoonHeal} Grace.")
            }
        }

        if (move.power > 0) {
            var pwr = move.power
            if (move.id == "heros_roar") {
                val hpRatio = playerHp.toFloat() / playerMaxHp
                pwr = config.pushThroughBasePower +
                    ((1f - hpRatio) * (config.pushThroughMaxPower - config.pushThroughBasePower)).toInt()
            }
            var missed = false
            if (status.vertigo > 0 && random.nextFloat() < config.vertigoMissChance) {
                missed = true
                battleLog.add("The attack missed due to Despair.")
            }
            if (!missed) {
                enemyHp = maxOf(0, enemyHp - pwr)
                battleLog.add("${enemy.name} took $pwr damage.")
            }
        }

        // 2. Check enemy death
        if (enemyHp <= 0) {
            battleLog.add("${enemy.name} was defeated!")
            encountersCleared++
            updateState(BattlePhase.ENCOUNTER_WIN)
            return
        }

        // 3. Process enemy turn
        if (!enemyEnraged && enemyHp.toFloat() / enemy.maxHp <= config.rageTriggerPercent) {
            enemyEnraged = true
            battleLog.add("${enemy.name} is enraged!")
        }

        val chance = if (enemyEnraged) enemy.specialChanceEnraged else enemy.specialChance
        if (random.nextFloat() < chance) {
            battleLog.add("${enemy.name} used ${enemy.specialDescription}!")
            var inflict = true
            if (enemy.appliesStatus == StatusKey.FOGGED) {
                if (status.fogged > 0) inflict = false
            }
            if (inflict) {
                val duration = config.statusDuration
                status = when (enemy.appliesStatus) {
                    StatusKey.FOGGED -> {
                        val validMoves = enc.moves.filter { it != "rest" && it != "prayer_shield" && it != "heros_roar" }
                        val blocked = validMoves.randomOrNull(random)
                        status.copy(fogged = duration, foggedMoveId = blocked)
                    }
                    StatusKey.OVERHEATED -> status.copy(overheated = duration)
                    StatusKey.LOCKED -> status.copy(locked = duration)
                    StatusKey.VERTIGO -> status.copy(vertigo = duration)
                }
                battleLog.add("You are now ${enemy.appliesStatus.label} for $duration turns.")
            } else {
                battleLog.add("You are already suffering from that.")
            }
        } else {
            var dmg = enemy.normalPower
            if (enemyEnraged) {
                dmg = (dmg * config.rageAttackMultiplier).toInt()
            }
            playerHp = maxOf(0, playerHp - dmg)
            battleLog.add("${enemy.name} attacked for $dmg damage.")
        }

        // 4. Tick status timers
        if (status.overheated > 0) {
            val burn = (playerMaxHp * config.overheatedBurnPercent).toInt()
            playerHp = maxOf(0, playerHp - burn)
            battleLog.add("Took $burn poison damage.")
        }

        status = status.tickDown()

        // 5. Check player death
        if (playerHp <= 0) {
            battleLog.add("You have fallen.")
            updateState(BattlePhase.ENCOUNTER_LOSS)
            return
        }

        updateState(BattlePhase.PLAYER_TURN)
    }

    private fun updateState(phase: BattlePhase) {
        val enc = encounters[currentEncounterIndex]
        _uiState.value = AolUiState(
            phase = phase,
            encounterIndex = currentEncounterIndex,
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
            moves = enc.moves.mapNotNull { AOL_MOVE_LIBRARY[it] },
            battleLog = battleLog,
            encountersCleared = encountersCleared,
        )
    }

    companion object {
        internal fun test(
            encounters: List<EncounterDefinition> = AOL_ENCOUNTERS,
            config: BattleConfig = DEFAULT_BATTLE_CONFIG,
            random: Random = Random.Default,
        ): AolViewModel = AolViewModel(encounters, config, random)
    }
}
