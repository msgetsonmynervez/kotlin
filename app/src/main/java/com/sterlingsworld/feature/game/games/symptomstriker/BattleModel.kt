package com.sterlingsworld.feature.game.games.symptomstriker

// ── Shared battle types ──────────────────────────────────────────────────────
// These types are designed for reuse by AOL and Lumi's Star Quest.
// Keep the surface area small — only add here what the battle family actually shares.

enum class MoveType { ATTACK, RECOVERY, CURE }

enum class BattlePhase {
    INTRO,          // encounter intro shown before first turn
    PLAYER_TURN,    // waiting for player to select a move
    ENCOUNTER_WIN,  // single encounter cleared; advance or finish
    ENCOUNTER_LOSS, // player HP reached 0
    RUN_WIN,        // all encounters cleared
}

/** One type of active status effect. Carries the player-facing description. */
enum class StatusKey(
    val label: String,
    val badge: String,
    /** What the effect does — shown in the UI so the player can understand it. */
    val tooltip: String,
    /** Which move counters or clears this effect. */
    val counteredBy: String,
) {
    FOGGED(
        label = "Fogged",
        badge = "\uD83C\uDF2B",   // 🌫
        tooltip = "One of your moves is blocked each turn.",
        counteredBy = "Cog Therapy",
    ),
    OVERHEATED(
        label = "Overheated",
        badge = "\uD83D\uDD25",   // 🔥
        tooltip = "You take burn damage at the end of each turn.",
        counteredBy = "Cooling Vest",
    ),
    LOCKED(
        label = "Locked",
        badge = "\uD83E\uDDBE",   // 🦾
        tooltip = "Attacks cost +1 extra Spoon.",
        counteredBy = "Muscle Relax",
    ),
    VERTIGO(
        label = "Vertigo",
        badge = "\uD83C\uDF00",   // 🌀
        tooltip = "Your attacks have a 35% miss chance.",
        counteredBy = "Mobility Aid",
    ),
}

data class BattleStatus(
    val fogged: Int = 0,
    val foggedMoveId: String? = null,
    val overheated: Int = 0,
    val locked: Int = 0,
    val vertigo: Int = 0,
) {
    /** Returns a list of (StatusKey, turnsRemaining) pairs for active effects. */
    val active: List<Pair<StatusKey, Int>>
        get() = buildList {
            if (fogged > 0) add(StatusKey.FOGGED to fogged)
            if (overheated > 0) add(StatusKey.OVERHEATED to overheated)
            if (locked > 0) add(StatusKey.LOCKED to locked)
            if (vertigo > 0) add(StatusKey.VERTIGO to vertigo)
        }

    /** Advance all timers by one turn. */
    fun tickDown(): BattleStatus = copy(
        fogged = maxOf(0, fogged - 1),
        foggedMoveId = if (fogged > 1) foggedMoveId else null,
        overheated = maxOf(0, overheated - 1),
        locked = maxOf(0, locked - 1),
        vertigo = maxOf(0, vertigo - 1),
    )

    /** Return a copy with the given status fully cleared. */
    fun cleared(key: StatusKey): BattleStatus = when (key) {
        StatusKey.FOGGED -> copy(fogged = 0, foggedMoveId = null)
        StatusKey.OVERHEATED -> copy(overheated = 0)
        StatusKey.LOCKED -> copy(locked = 0)
        StatusKey.VERTIGO -> copy(vertigo = 0)
    }
}

data class MoveDefinition(
    val id: String,
    val label: String,
    val subLabel: String = "",
    val type: MoveType,
    /** Base Spoon cost. Push Through uses HP instead — spoonCost = 0. */
    val spoonCost: Int,
    val power: Int = 0,
    val healPercent: Float = 0f,   // fraction of playerMaxHp
    val spoonHeal: Int = 0,
    /** If non-null, this move clears that status when used. */
    val clearsStatus: StatusKey? = null,
    val description: String,
)

/** Tunable constants for the battle loop. Keep defaults matching the source design. */
data class BattleConfig(
    val pushThroughHpCostPercent: Float = 0.10f,
    val pushThroughBasePower: Int = 50,
    val pushThroughMaxPower: Int = 120,
    val pushThroughSafeUses: Int = 3,
    val pushThroughPenaltySpoons: Int = 1,
    val minMaxSpoons: Int = 3,
    val overheatedBurnPercent: Float = 0.05f,
    val vertigoMissChance: Float = 0.35f,
    val lockedExtraSpoonCost: Int = 1,
    val rageTriggerPercent: Float = 0.30f,
    val rageAttackMultiplier: Float = 1.50f,
    val statusDuration: Int = 3,
)

val DEFAULT_BATTLE_CONFIG = BattleConfig()
