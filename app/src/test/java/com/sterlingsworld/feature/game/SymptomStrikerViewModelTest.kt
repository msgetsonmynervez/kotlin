package com.sterlingsworld.feature.game

import com.sterlingsworld.feature.game.games.symptomstriker.BattleConfig
import com.sterlingsworld.feature.game.games.symptomstriker.BattlePhase
import com.sterlingsworld.feature.game.games.symptomstriker.BattleStatus
import com.sterlingsworld.feature.game.games.symptomstriker.DEFAULT_BATTLE_CONFIG
import com.sterlingsworld.feature.game.games.symptomstriker.EnemyDefinition
import com.sterlingsworld.feature.game.games.symptomstriker.EncounterDefinition
import com.sterlingsworld.feature.game.games.symptomstriker.MOVE_LIBRARY
import com.sterlingsworld.feature.game.games.symptomstriker.MoveType
import com.sterlingsworld.feature.game.games.symptomstriker.StatusKey
import com.sterlingsworld.feature.game.games.symptomstriker.SymptomStrikerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SymptomStrikerViewModelTest {

    // ── Test fixtures ─────────────────────────────────────────────────────────

    /**
     * A safe, predictable encounter.
     * Enemy never uses special attacks (specialChance = 0), low damage, high player HP.
     * This keeps tests deterministic without controlling Random.
     */
    private fun safeEncounter(
        playerHp: Int = 200,
        playerSpoons: Int = 8,
        enemyHp: Int = 400,
        enemyNormalPower: Int = 5,
        appliesStatus: StatusKey = StatusKey.LOCKED,
    ) = EncounterDefinition(
        index = 0,
        title = "Test Gym",
        accentColor = 0xFFFF9500,
        enemy = EnemyDefinition(
            id = "test_enemy",
            name = "Test Enemy",
            sprite = "(?_?)\nTEST",
            maxHp = enemyHp,
            normalPower = enemyNormalPower,
            specialPower = enemyNormalPower,
            specialChance = 0.0f,          // never triggers special
            specialChanceEnraged = 0.0f,
            appliesStatus = appliesStatus,
            specialDescription = "Test special",
        ),
        playerStartHp = playerHp,
        playerStartSpoons = playerSpoons,
        moves = listOf("rest", "box_breathing", "physical_therapy", "muscle_relaxant", "push_through"),
        intro = "Test intro",
        symptomsDesc = "Test desc",
    )

    /** Encounter where the enemy ALWAYS triggers its special (for status effect tests). */
    private fun alwaysSpecialEncounter(
        appliesStatus: StatusKey,
        playerHp: Int = 200,
        moves: List<String> = listOf("rest", "box_breathing", "physical_therapy", "muscle_relaxant", "push_through"),
    ) = EncounterDefinition(
        index = 0,
        title = "Test Gym",
        accentColor = 0xFF88CCFF,
        enemy = EnemyDefinition(
            id = "test_enemy",
            name = "Test Enemy",
            sprite = "(?_?)",
            maxHp = 400,
            normalPower = 5,
            specialPower = 5,
            specialChance = 1.0f,          // always triggers special
            specialChanceEnraged = 1.0f,
            appliesStatus = appliesStatus,
            specialDescription = "Always special",
        ),
        playerStartHp = playerHp,
        playerStartSpoons = 8,
        moves = moves,
        intro = "Test intro",
        symptomsDesc = "Test desc",
    )

    private fun vmWith(vararg encounters: EncounterDefinition): SymptomStrikerViewModel =
        SymptomStrikerViewModel.test(
            encounters = encounters.toList(),
            config = DEFAULT_BATTLE_CONFIG,
            random = Random(0),
        )

    private fun SymptomStrikerViewModel.beginBattle() {
        onDismissIntro()
    }

    // ── Phase transitions ─────────────────────────────────────────────────────

    @Test
    fun `initial phase is INTRO`() {
        val vm = vmWith(safeEncounter())
        assertEquals(BattlePhase.INTRO, vm.uiState.value.phase)
    }

    @Test
    fun `dismissing intro transitions to PLAYER_TURN`() {
        val vm = vmWith(safeEncounter())
        vm.beginBattle()
        assertEquals(BattlePhase.PLAYER_TURN, vm.uiState.value.phase)
    }

    @Test
    fun `selecting move before dismissing intro is ignored`() {
        val vm = vmWith(safeEncounter())
        val hpBefore = vm.uiState.value.enemyHp
        vm.onMoveSelected("physical_therapy")
        assertEquals(hpBefore, vm.uiState.value.enemyHp)
        assertEquals(BattlePhase.INTRO, vm.uiState.value.phase)
    }

    // ── Recovery moves ────────────────────────────────────────────────────────

    @Test
    fun `rest heals player HP`() {
        val vm = SymptomStrikerViewModel.test(
            encounters = listOf(safeEncounter(playerHp = 100, enemyHp = 400, enemyNormalPower = 0)),
            config = DEFAULT_BATTLE_CONFIG.copy(minMaxSpoons = 0),
            random = Random(0),
        )
        vm.beginBattle()
        vm.onMoveSelected("push_through") // self-damage only; enemy does 0
        val hpAfterPushThrough = vm.uiState.value.playerHp
        assertTrue("Push Through should lower HP first", hpAfterPushThrough < 100)

        vm.onMoveSelected("rest")
        val hpAfterRest = vm.uiState.value.playerHp
        assertTrue("Rest should heal HP", hpAfterRest > hpAfterPushThrough)
    }

    @Test
    fun `rest restores spoons`() {
        val vm = SymptomStrikerViewModel.test(
            encounters = listOf(safeEncounter(playerSpoons = 2, enemyNormalPower = 0)),
            config = DEFAULT_BATTLE_CONFIG.copy(minMaxSpoons = 0),
            random = Random(0),
        )
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy") // costs 2 spoons -> 0 left
        assertEquals(0, vm.uiState.value.playerSpoons)

        vm.onMoveSelected("rest") // should restore 2 spoons (minus the enemy hit which uses no spoons)
        assertTrue("Rest should restore spoons", vm.uiState.value.playerSpoons >= 2)
    }

    @Test
    fun `box_breathing restores 1 spoon`() {
        val vm = vmWith(safeEncounter(playerSpoons = 2))
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy") // 2 spoons -> 0
        vm.onMoveSelected("box_breathing")    // should restore 1 spoon
        // After enemy hit, spoons may be 1 (box_breathing gives +1, enemy doesn't cost spoons)
        assertTrue("box_breathing should restore at least 1 spoon", vm.uiState.value.playerSpoons >= 1)
    }

    // ── Attack moves ──────────────────────────────────────────────────────────

    @Test
    fun `physical_therapy deals 28 damage to enemy`() {
        val vm = vmWith(safeEncounter(enemyHp = 400))
        vm.beginBattle()
        val enemyHpBefore = vm.uiState.value.enemyHp
        vm.onMoveSelected("physical_therapy")
        assertEquals(enemyHpBefore - 28, vm.uiState.value.enemyHp)
    }

    @Test
    fun `attack requires sufficient spoons`() {
        val vm = SymptomStrikerViewModel.test(
            encounters = listOf(safeEncounter(playerSpoons = 1)),
            config = DEFAULT_BATTLE_CONFIG.copy(minMaxSpoons = 0),
            random = Random(0),
        ) // physical_therapy costs 2
        vm.beginBattle()
        val enemyHpBefore = vm.uiState.value.enemyHp
        vm.onMoveSelected("physical_therapy") // should be blocked: not enough spoons
        assertEquals("Enemy HP should be unchanged with insufficient spoons", enemyHpBefore, vm.uiState.value.enemyHp)
        assertEquals(BattlePhase.PLAYER_TURN, vm.uiState.value.phase) // still player's turn
    }

    @Test
    fun `defeating enemy transitions to ENCOUNTER_WIN when there are more encounters`() {
        // Enemy with 28 HP -> one physical_therapy shot defeats it
        val vm = vmWith(
            safeEncounter(enemyHp = 28, playerSpoons = 8),
            safeEncounter(enemyHp = 400), // second encounter
        )
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy")
        assertEquals(BattlePhase.ENCOUNTER_WIN, vm.uiState.value.phase)
        assertEquals(1, vm.uiState.value.encountersCleared)
    }

    @Test
    fun `defeating last enemy transitions to RUN_WIN`() {
        val vm = vmWith(safeEncounter(enemyHp = 28, playerSpoons = 8))
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy")
        assertEquals(BattlePhase.RUN_WIN, vm.uiState.value.phase)
    }

    @Test
    fun `player HP reaching zero causes ENCOUNTER_LOSS`() {
        // Enemy deals 200 damage per turn (use a high-power safe encounter with always-special off)
        val vm = SymptomStrikerViewModel.test(
            encounters = listOf(
                safeEncounter(playerHp = 5, enemyHp = 400, enemyNormalPower = 200),
            ),
            config = DEFAULT_BATTLE_CONFIG,
            random = Random(0),
        )
        vm.beginBattle()
        vm.onMoveSelected("rest") // enemy attacks for 200 -> player HP drops to 0 or below
        assertEquals(BattlePhase.ENCOUNTER_LOSS, vm.uiState.value.phase)
        assertEquals(0, vm.uiState.value.playerHp)
    }

    // ── Status effects ────────────────────────────────────────────────────────

    @Test
    fun `locked status increases attack spoon cost by 1`() {
        val enc = alwaysSpecialEncounter(StatusKey.LOCKED)
        val vm = SymptomStrikerViewModel.test(listOf(enc), DEFAULT_BATTLE_CONFIG, Random(0))
        vm.beginBattle()
        // After first move, enemy always triggers LOCKED special
        vm.onMoveSelected("rest") // rest -> enemy triggers LOCKED
        assertTrue("LOCKED should be active after enemy special", vm.uiState.value.status.locked > 0)

        // physical_therapy costs 2 normally. With LOCKED it costs 3.
        val spoonsBefore = vm.uiState.value.playerSpoons
        val enemyHpBefore = vm.uiState.value.enemyHp
        vm.onMoveSelected("physical_therapy")
        val spoonsAfter = vm.uiState.value.playerSpoons
        // If LOCKED was active, 3 spoons spent (and enemy damaged); if not, 2 spoons spent
        if (vm.uiState.value.enemyHp < enemyHpBefore) {
            // Attack connected — check spoon cost
            assertEquals("LOCKED should cost 1 extra Spoon", 3, spoonsBefore - spoonsAfter)
        }
    }

    @Test
    fun `fogged status blocks a specific move`() {
        val fogEnc = alwaysSpecialEncounter(
            appliesStatus = StatusKey.FOGGED,
            moves = listOf("rest", "box_breathing", "physical_therapy", "cognitive_therapy", "push_through"),
        )
        val vm = SymptomStrikerViewModel.test(listOf(fogEnc), DEFAULT_BATTLE_CONFIG, Random(0))
        vm.beginBattle()
        vm.onMoveSelected("rest") // enemy triggers FOGGED
        val foggedId = vm.uiState.value.status.foggedMoveId
        assertTrue("FOGGED should be active after enemy special", vm.uiState.value.status.fogged > 0)
        assertTrue("A move should be fogged", foggedId != null)
        // foggedId is not a recovery move (per pickFoggedTarget logic)
        if (foggedId != null) {
            val foggedMove = MOVE_LIBRARY[foggedId]
            assertTrue(
                "Fogged target should be an attack or cure move",
                foggedMove?.type != MoveType.RECOVERY,
            )
        }
    }

    @Test
    fun `trying to use a fogged move is denied`() {
        // Force fog on physical_therapy by using a single-candidate move list
        val fogEnc = alwaysSpecialEncounter(
            appliesStatus = StatusKey.FOGGED,
            moves = listOf("rest", "box_breathing", "physical_therapy", "push_through"),
            // Only attack candidate for fog is physical_therapy
        )
        val vm = SymptomStrikerViewModel.test(listOf(fogEnc), DEFAULT_BATTLE_CONFIG, Random(0))
        vm.beginBattle()
        vm.onMoveSelected("rest") // enemy fogs physical_therapy (only attack candidate)

        val foggedId = vm.uiState.value.status.foggedMoveId
        if (foggedId != null) {
            val enemyHpBefore = vm.uiState.value.enemyHp
            vm.onMoveSelected(foggedId) // blocked
            assertEquals("Blocked move should not damage enemy", enemyHpBefore, vm.uiState.value.enemyHp)
            assertTrue("Battle log should mention block", vm.uiState.value.battleLog.contains("blocked"))
        }
    }

    @Test
    fun `cure move clears its target status`() {
        val lockEnc = alwaysSpecialEncounter(StatusKey.LOCKED)
        val vm = SymptomStrikerViewModel.test(listOf(lockEnc), DEFAULT_BATTLE_CONFIG, Random(0))
        vm.beginBattle()
        vm.onMoveSelected("rest") // enemy applies LOCKED
        assertTrue("LOCKED should be active", vm.uiState.value.status.locked > 0)

        vm.onMoveSelected("muscle_relaxant") // clears LOCKED
        assertTrue(
            "muscle_relaxant should clear LOCKED before enemy turn resolves",
            vm.uiState.value.battleLog.contains("Locked cleared!"),
        )
    }

    @Test
    fun `overheated status ticks down each turn`() {
        val heatEnc = alwaysSpecialEncounter(StatusKey.OVERHEATED)
        val vm = SymptomStrikerViewModel.test(listOf(heatEnc), DEFAULT_BATTLE_CONFIG, Random(0))
        vm.beginBattle()
        vm.onMoveSelected("rest") // enemy applies OVERHEATED (duration = 3)
        val duration = vm.uiState.value.status.overheated
        assertTrue("OVERHEATED should have positive duration", duration > 0)

        vm.onMoveSelected("rest") // ticks to duration-1
        assertEquals(duration - 1, vm.uiState.value.status.overheated)
    }

    // ── Push Through ──────────────────────────────────────────────────────────

    @Test
    fun `push_through costs player HP`() {
        val vm = vmWith(safeEncounter(playerHp = 200, playerSpoons = 0))
        vm.beginBattle()
        val hpBefore = vm.uiState.value.playerHp
        vm.onMoveSelected("push_through")
        val hpCost = maxOf(1, (200 * DEFAULT_BATTLE_CONFIG.pushThroughHpCostPercent).toInt())
        // Player took push_through HP cost + enemy attack
        assertTrue("Push Through should reduce player HP", vm.uiState.value.playerHp < hpBefore)
    }

    @Test
    fun `push_through damage is higher at lower HP`() {
        val safeEnc = safeEncounter(playerHp = 200, enemyHp = 1000, enemyNormalPower = 0)

        // VM at healthy HP — record the first hit
        val vmFull = SymptomStrikerViewModel.test(listOf(safeEnc), DEFAULT_BATTLE_CONFIG, Random(0))
        vmFull.beginBattle()
        val enemyHpBefore1 = vmFull.uiState.value.enemyHp
        vmFull.onMoveSelected("push_through")
        val dmgFull = enemyHpBefore1 - vmFull.uiState.value.enemyHp

        // VM at lower HP in the same max-HP band — pre-damage with several uses first.
        val vmLow = SymptomStrikerViewModel.test(listOf(safeEnc), DEFAULT_BATTLE_CONFIG, Random(0))
        vmLow.beginBattle()
        repeat(4) { vmLow.onMoveSelected("push_through") }
        val enemyHpBefore2 = vmLow.uiState.value.enemyHp
        vmLow.onMoveSelected("push_through")
        val dmgLow = enemyHpBefore2 - vmLow.uiState.value.enemyHp

        assertTrue("Push Through should deal more damage at low HP", dmgLow > dmgFull)
    }

    @Test
    fun `push_through within safe threshold does not trigger penalty`() {
        val vm = vmWith(safeEncounter(playerHp = 500, enemyHp = 1000))
        vm.beginBattle()
        repeat(DEFAULT_BATTLE_CONFIG.pushThroughSafeUses) {
            vm.onMoveSelected("push_through")
        }
        assertEquals(
            "No spoon penalty within safe uses",
            0,
            vm.uiState.value.sessionSpoonPenalty,
        )
    }

    @Test
    fun `push_through overuse permanently reduces max spoons`() {
        val vm = vmWith(safeEncounter(playerHp = 1000, enemyHp = 2000))
        vm.beginBattle()
        val initialMaxSpoons = vm.uiState.value.playerMaxSpoons

        // Use push_through one more time than allowed
        repeat(DEFAULT_BATTLE_CONFIG.pushThroughSafeUses + 1) {
            vm.onMoveSelected("push_through")
        }

        assertTrue("Max spoons should be reduced after overuse", vm.uiState.value.playerMaxSpoons < initialMaxSpoons)
        assertEquals(1, vm.uiState.value.sessionSpoonPenalty)
    }

    @Test
    fun `push_through max spoons cannot go below minimum floor`() {
        val extremeConfig = DEFAULT_BATTLE_CONFIG.copy(
            pushThroughSafeUses = 0, // every use triggers penalty
            pushThroughPenaltySpoons = 8, // large penalty
            minMaxSpoons = 3,
        )
        val vm = SymptomStrikerViewModel.test(
            encounters = listOf(safeEncounter(playerHp = 2000, enemyHp = 5000)),
            config = extremeConfig,
            random = Random(0),
        )
        vm.beginBattle()
        repeat(5) { vm.onMoveSelected("push_through") }
        assertTrue(
            "Max spoons should not go below minMaxSpoons",
            vm.uiState.value.playerMaxSpoons >= extremeConfig.minMaxSpoons,
        )
    }

    // ── Rage behavior ─────────────────────────────────────────────────────────

    @Test
    fun `enemy enters rage when HP drops below 30 percent`() {
        val hp = 100
        val rageThreshold = (hp * DEFAULT_BATTLE_CONFIG.rageTriggerPercent).toInt() // 30
        // Need to deal 71+ damage to trigger rage. physical_therapy deals 28 per hit.
        // With specialChance=0 and 8 spoons, we can deal 28*2=56 then need rest for spoons.
        val vm = vmWith(safeEncounter(playerHp = 500, playerSpoons = 8, enemyHp = hp))
        vm.beginBattle()
        assertFalse("Enemy should not start enraged", vm.uiState.value.enemyEnraged)

        // 28+28=56 damage, still above 30 -> not enraged
        vm.onMoveSelected("physical_therapy")
        vm.onMoveSelected("physical_therapy")
        // Enemy HP is now 44, above 30 — not yet enraged
        assertFalse("Enemy not enraged at HP > 30", vm.uiState.value.enemyEnraged)

        // Third attack: 44-28=16, below 30 -> rage
        vm.onMoveSelected("rest") // recover spoons
        vm.onMoveSelected("physical_therapy")
        assertTrue("Enemy should be enraged below 30% HP", vm.uiState.value.enemyEnraged)
    }

    // ── Multi-encounter and result generation ─────────────────────────────────

    @Test
    fun `advancing encounter resets enemy HP and status`() {
        val vm = vmWith(
            safeEncounter(enemyHp = 28, playerSpoons = 8),
            safeEncounter(enemyHp = 400),
        )
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy") // defeats first enemy
        assertEquals(BattlePhase.ENCOUNTER_WIN, vm.uiState.value.phase)

        vm.onNextEncounter()
        assertEquals(BattlePhase.INTRO, vm.uiState.value.phase) // second encounter starts in INTRO
        assertEquals(400, vm.uiState.value.enemyMaxHp)
        assertEquals(0, vm.uiState.value.status.locked)
    }

    @Test
    fun `result stars are 3 when all encounters cleared with high HP`() {
        // Win single encounter with most HP remaining (200/200 -> ~50%+ after enemy hits)
        val vm = vmWith(safeEncounter(playerHp = 200, enemyHp = 28))
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy") // defeats enemy, player HP barely touched
        val result = vm.buildResult()
        assertEquals(3, result.stars)
        assertTrue(result.completed)
    }

    @Test
    fun `result stars are 2 when all encounters cleared but HP is low`() {
        // Win after taking heavy damage, ending the run below 50% HP.
        val vm = vmWith(safeEncounter(playerHp = 10, enemyHp = 56, enemyNormalPower = 9))
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy") // enemy survives, player drops to 1/10
        vm.onMoveSelected("physical_therapy") // finish run with 1/10 HP remaining
        val result = vm.buildResult()
        assertEquals(2, result.stars)
    }

    @Test
    fun `result stars are 1 when some but not all encounters cleared`() {
        val vm = vmWith(
            safeEncounter(enemyHp = 28),      // clears first
            safeEncounter(playerHp = 5, enemyHp = 400, enemyNormalPower = 200), // loses second
        )
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy") // defeats gym 1
        vm.onNextEncounter()
        vm.onDismissIntro()
        vm.onMoveSelected("rest") // enemy kills player (200 damage, player has 5 HP)
        val result = vm.buildResult()
        assertEquals(1, result.stars)
    }

    @Test
    fun `result stars are 0 when first encounter is lost`() {
        val vm = vmWith(safeEncounter(playerHp = 5, enemyHp = 400, enemyNormalPower = 200))
        vm.beginBattle()
        vm.onMoveSelected("rest") // enemy hits for 200 -> player dead
        val result = vm.buildResult()
        assertEquals(0, result.stars)
    }

    @Test
    fun `result is perfect only when won without triggering push through penalty`() {
        val vm = vmWith(safeEncounter(playerHp = 1000, enemyHp = 28))
        vm.beginBattle()
        vm.onMoveSelected("physical_therapy") // win without using push through at all
        val result = vm.buildResult()
        assertTrue("Should be perfect with no push through penalty", result.perfect)
    }

    @Test
    fun `result is not perfect when push through penalty was triggered`() {
        val vm = vmWith(safeEncounter(playerHp = 2000, enemyHp = 700, enemyNormalPower = 0))
        vm.beginBattle()
        // Use push_through enough to trigger penalty, then continue to a win.
        repeat(DEFAULT_BATTLE_CONFIG.pushThroughSafeUses + 6) {
            if (vm.uiState.value.phase == BattlePhase.PLAYER_TURN) {
                vm.onMoveSelected("push_through")
            }
        }
        assertEquals(BattlePhase.RUN_WIN, vm.uiState.value.phase)
        val result = vm.buildResult()
        assertFalse("Should not be perfect with overuse penalty", result.perfect)
    }

    @Test
    fun `completed is always true in result`() {
        val vm = vmWith(safeEncounter(playerHp = 5, enemyHp = 400, enemyNormalPower = 200))
        vm.beginBattle()
        vm.onMoveSelected("rest") // player dies
        assertTrue(vm.buildResult().completed)
    }

    // ── BattleStatus data class ───────────────────────────────────────────────

    @Test
    fun `BattleStatus tickDown decrements all active counters`() {
        val status = BattleStatus(fogged = 3, overheated = 2, locked = 1, vertigo = 2)
        val ticked = status.tickDown()
        assertEquals(2, ticked.fogged)
        assertEquals(1, ticked.overheated)
        assertEquals(0, ticked.locked)
        assertEquals(1, ticked.vertigo)
    }

    @Test
    fun `BattleStatus tickDown does not go below zero`() {
        val status = BattleStatus(locked = 1)
        val ticked = status.tickDown().tickDown()
        assertEquals(0, ticked.locked)
    }

    @Test
    fun `BattleStatus tickDown clears foggedMoveId when fogged reaches zero`() {
        val status = BattleStatus(fogged = 1, foggedMoveId = "physical_therapy")
        val ticked = status.tickDown()
        assertEquals(0, ticked.fogged)
        assertEquals(null, ticked.foggedMoveId)
    }

    @Test
    fun `BattleStatus cleared removes the specified status only`() {
        val status = BattleStatus(fogged = 3, locked = 2, vertigo = 1, foggedMoveId = "physical_therapy")
        val cleared = status.cleared(StatusKey.FOGGED)
        assertEquals(0, cleared.fogged)
        assertEquals(null, cleared.foggedMoveId)
        assertEquals(2, cleared.locked)  // unchanged
        assertEquals(1, cleared.vertigo) // unchanged
    }

    @Test
    fun `BattleStatus active returns only non-zero effects`() {
        val status = BattleStatus(locked = 2, vertigo = 0, fogged = 0, overheated = 1)
        val active = status.active.map { it.first }
        assertTrue(active.contains(StatusKey.LOCKED))
        assertTrue(active.contains(StatusKey.OVERHEATED))
        assertFalse(active.contains(StatusKey.FOGGED))
        assertFalse(active.contains(StatusKey.VERTIGO))
    }
}
