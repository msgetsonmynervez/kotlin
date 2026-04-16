package com.sterlingsworld.feature.game.games.symptomstriker

// ── Symptom Striker specific encounter and move data ────────────────────────
// Extracted from the source HTML (app/src/main/assets/games/symptom-striker/index.html).
// Structure separates shared mechanics (BattleModel) from game-specific content (here).

data class EnemyDefinition(
    val id: String,
    val name: String,
    val sprite: String,
    val maxHp: Int,
    val normalPower: Int,
    val specialPower: Int,
    /** Chance to use the status-inflicting special when HP is healthy. */
    val specialChance: Float,
    /** Chance to use the special when enraged (HP below rageTriggerPercent). */
    val specialChanceEnraged: Float,
    /** Which status this enemy inflicts with its special attack. */
    val appliesStatus: StatusKey,
    /** Player-facing description of the special attack shown in the battle log. */
    val specialDescription: String,
)

data class EncounterDefinition(
    val index: Int,
    val title: String,
    val accentColor: Long,
    val enemy: EnemyDefinition,
    val playerStartHp: Int,
    val playerStartSpoons: Int,
    /** Ordered list of move IDs available in this encounter. */
    val moves: List<String>,
    val intro: String,
    val symptomsDesc: String,
)

// ── Move library ─────────────────────────────────────────────────────────────

internal val MOVE_LIBRARY: Map<String, MoveDefinition> = mapOf(

    "rest" to MoveDefinition(
        id = "rest", label = "Rest", subLabel = "+2 Spoons",
        type = MoveType.RECOVERY, spoonCost = 0,
        healPercent = 0.20f, spoonHeal = 2,
        description = "Take a rest. Heal 20% max HP and regain 2 Spoons.",
    ),

    "box_breathing" to MoveDefinition(
        id = "box_breathing", label = "Box Breathing", subLabel = "+1 Spoon",
        type = MoveType.RECOVERY, spoonCost = 0,
        healPercent = 0.15f, spoonHeal = 1,
        description = "Slow breath work. Heal 15% max HP and regain 1 Spoon.",
    ),

    "physical_therapy" to MoveDefinition(
        id = "physical_therapy", label = "Physio", subLabel = "Cost: 2",
        type = MoveType.ATTACK, spoonCost = 2, power = 28,
        description = "Targeted physio routine. Deals 28 damage.",
    ),

    "cognitive_therapy" to MoveDefinition(
        id = "cognitive_therapy", label = "Cog Therapy", subLabel = "Clears Fog",
        type = MoveType.CURE, spoonCost = 1, power = 18,
        clearsStatus = StatusKey.FOGGED,
        description = "Cognitive therapy. Deals 18 damage and clears Fogged.",
    ),

    "cooling_vest" to MoveDefinition(
        id = "cooling_vest", label = "Cooling Vest", subLabel = "Clears Heat",
        type = MoveType.CURE, spoonCost = 1, healPercent = 0.10f,
        clearsStatus = StatusKey.OVERHEATED,
        description = "Cooling vest. Heals 10% max HP and clears Overheated.",
    ),

    "mobility_aid" to MoveDefinition(
        id = "mobility_aid", label = "Mobility Aid", subLabel = "Clears Spin",
        type = MoveType.CURE, spoonCost = 1, power = 15,
        clearsStatus = StatusKey.VERTIGO,
        description = "Grounding aid. Deals 15 damage and clears Vertigo.",
    ),

    "muscle_relaxant" to MoveDefinition(
        id = "muscle_relaxant", label = "Muscle Relax", subLabel = "Clears Lock",
        type = MoveType.CURE, spoonCost = 1, healPercent = 0.15f,
        clearsStatus = StatusKey.LOCKED,
        description = "Muscle relaxant. Heals 15% max HP and clears Locked.",
    ),

    "push_through" to MoveDefinition(
        id = "push_through", label = "Push Through", subLabel = "Costs HP",
        type = MoveType.ATTACK, spoonCost = 0, // costs HP, not Spoons
        description = "Fight through the pain. Costs 10% max HP. Hits harder at low health. " +
            "More than ${DEFAULT_BATTLE_CONFIG.pushThroughSafeUses} uses per battle permanently reduce max Spoons.",
    ),
)

// ── Three-encounter vertical slice ───────────────────────────────────────────
// Source: 8 gyms in the HTML. Sprint 6 ships gyms 1, 2, 3 as a polished vertical slice.
// AOL and Lumi's Star Quest reuse the same BattleModel with their own encounter lists.

internal val SYMPTOM_STRIKER_ENCOUNTERS: List<EncounterDefinition> = listOf(

    EncounterDefinition(
        index = 0,
        title = "Gym 1 \u00b7 The Fatigue",
        accentColor = 0xFFFF9500,
        enemy = EnemyDefinition(
            id = "fatigue_entity",
            name = "Fatigue Entity",
            sprite = "(x_x)\nFATIGUE",
            maxHp = 100,
            normalPower = 12,
            specialPower = 10,
            specialChance = 0.30f,
            specialChanceEnraged = 0.55f,
            appliesStatus = StatusKey.LOCKED,
            specialDescription = "Nerve Spasm \u2014 muscles seize, attacks cost +1 Spoon",
        ),
        playerStartHp = 80,
        playerStartSpoons = 8,
        moves = listOf("rest", "box_breathing", "physical_therapy", "muscle_relaxant", "push_through"),
        intro = "The Fatigue Entity lumbers forward. Heavy limbs, endless exhaustion.",
        symptomsDesc = "MS fatigue can be overwhelming. Nerve Spasm attacks lock your muscles \u2014 use Muscle Relax to clear Locked.",
    ),

    EncounterDefinition(
        index = 1,
        title = "Gym 2 \u00b7 The Fog",
        accentColor = 0xFF88CCFF,
        enemy = EnemyDefinition(
            id = "fog_brain",
            name = "Fog Brain",
            sprite = "(~.~)\n~ FOG ~",
            maxHp = 115,
            normalPower = 13,
            specialPower = 11,
            specialChance = 0.35f,
            specialChanceEnraged = 0.60f,
            appliesStatus = StatusKey.FOGGED,
            specialDescription = "Brain Fog \u2014 one of your moves is blocked for 3 turns",
        ),
        playerStartHp = 90,
        playerStartSpoons = 8,
        moves = listOf("rest", "box_breathing", "physical_therapy", "cognitive_therapy", "push_through"),
        intro = "The Fog Brain drifts into view. Confusion warps your thoughts.",
        symptomsDesc = "Cognitive fog dims your options each turn. Use Cog Therapy to clear Fogged.",
    ),

    EncounterDefinition(
        index = 2,
        title = "Gym 3 \u00b7 The Spin",
        accentColor = 0xFF00FFCC,
        enemy = EnemyDefinition(
            id = "vertigo_vortex",
            name = "Vertigo Vortex",
            sprite = "(@_@)\n~ VORTEX ~",
            maxHp = 130,
            normalPower = 16,
            specialPower = 12,
            specialChance = 0.30f,
            specialChanceEnraged = 0.50f,
            appliesStatus = StatusKey.VERTIGO,
            specialDescription = "Vertigo Spin \u2014 your attacks have a 35% miss chance for 3 turns",
        ),
        playerStartHp = 100,
        playerStartSpoons = 8,
        moves = listOf("rest", "box_breathing", "physical_therapy", "mobility_aid", "push_through"),
        intro = "The Vertigo Vortex spins into view. The horizon vanishes. Balance gone.",
        symptomsDesc = "Brainstem lesions cause severe vertigo. Spin attacks make your hits unreliable \u2014 use Mobility Aid to clear Vertigo.",
    ),
)
