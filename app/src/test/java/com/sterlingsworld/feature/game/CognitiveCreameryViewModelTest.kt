package com.sterlingsworld.feature.game

import com.sterlingsworld.feature.game.games.cognitivecreamery.ALL_FLAVORS
import com.sterlingsworld.feature.game.games.cognitivecreamery.CreameryActivity
import com.sterlingsworld.feature.game.games.cognitivecreamery.CognitiveCreameryViewModel
import com.sterlingsworld.feature.game.games.cognitivecreamery.CreameryPhase
import com.sterlingsworld.feature.game.games.cognitivecreamery.ROUND_LENGTHS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CognitiveCreameryViewModelTest {

    // ── initial state ──────────────────────────────────────────────────────────

    @Test
    fun `initial phase is STUDY`() {
        val vm = sequenceVm()
        assertEquals(CreameryPhase.STUDY, vm.uiState.value.sequence.phase)
    }

    @Test
    fun `initial round is 0`() {
        val vm = CognitiveCreameryViewModel()
        assertEquals(0, vm.uiState.value.sequence.currentRound)
    }

    @Test
    fun `initial target sequence has length matching round 0`() {
        val vm = CognitiveCreameryViewModel()
        assertEquals(ROUND_LENGTHS[0], vm.uiState.value.sequence.targetSequence.size)
    }

    @Test
    fun `initial target sequence contains only valid flavors`() {
        val vm = CognitiveCreameryViewModel()
        vm.uiState.value.sequence.targetSequence.forEach { flavor ->
            assertTrue("$flavor is not a valid flavor", flavor in ALL_FLAVORS)
        }
    }

    @Test
    fun `initial target sequence has no duplicates`() {
        val vm = CognitiveCreameryViewModel()
        val seq = vm.uiState.value.sequence.targetSequence
        assertEquals(seq.size, seq.toSet().size)
    }

    @Test
    fun `initial player sequence is empty`() {
        val vm = CognitiveCreameryViewModel()
        assertTrue(vm.uiState.value.sequence.playerSequence.isEmpty())
    }

    @Test
    fun `total rounds equals ROUND_LENGTHS size`() {
        val vm = CognitiveCreameryViewModel()
        assertEquals(ROUND_LENGTHS.size, vm.uiState.value.sequence.totalRounds)
    }

    // ── STUDY → INPUT transition ───────────────────────────────────────────────

    @Test
    fun `onReady transitions from STUDY to INPUT`() {
        val vm = sequenceVm()
        vm.onReady()
        assertEquals(CreameryPhase.INPUT, vm.uiState.value.sequence.phase)
    }

    @Test
    fun `onReady populates available tokens with all 5 flavors`() {
        val vm = sequenceVm()
        vm.onReady()
        assertEquals(ALL_FLAVORS.size, vm.uiState.value.sequence.availableTokens.size)
        assertEquals(ALL_FLAVORS.toSet(), vm.uiState.value.sequence.availableTokens.toSet())
    }

    @Test
    fun `onReady clears player sequence`() {
        val vm = sequenceVm()
        vm.onReady()
        assertTrue(vm.uiState.value.sequence.playerSequence.isEmpty())
    }

    @Test
    fun `onReady is a no-op when not in STUDY phase`() {
        val vm = sequenceVm()
        vm.onReady()
        assertEquals(CreameryPhase.INPUT, vm.uiState.value.sequence.phase)
        vm.onReady() // second call should do nothing
        assertEquals(CreameryPhase.INPUT, vm.uiState.value.sequence.phase)
    }

    // ── token selection ────────────────────────────────────────────────────────

    @Test
    fun `onTokenTapped appends token to player sequence`() {
        val vm = sequenceVm()
        vm.onReady()
        val token = vm.uiState.value.sequence.availableTokens.first()
        vm.onTokenTapped(token)
        assertEquals(listOf(token), vm.uiState.value.sequence.playerSequence)
    }

    @Test
    fun `onTokenTapped removes token from available tokens`() {
        val vm = sequenceVm()
        vm.onReady()
        val token = vm.uiState.value.sequence.availableTokens.first()
        vm.onTokenTapped(token)
        assertFalse(token in vm.uiState.value.sequence.availableTokens)
    }

    @Test
    fun `onTokenTapped is ignored when not in INPUT phase`() {
        val vm = CognitiveCreameryViewModel()
        // still in STUDY phase
        val token = ALL_FLAVORS.first()
        vm.onTokenTapped(token)
        assertTrue(vm.uiState.value.sequence.playerSequence.isEmpty())
    }

    @Test
    fun `onTokenTapped is ignored when sequence is already full`() {
        val vm = sequenceVm()
        vm.onReady()
        // fill exactly the required number of slots
        val needed = vm.uiState.value.sequence.targetSequence.size
        repeat(needed) {
            val token = vm.uiState.value.sequence.availableTokens.first()
            vm.onTokenTapped(token)
        }
        val sizeBeforeExtra = vm.uiState.value.sequence.playerSequence.size
        // try to add one more
        vm.uiState.value.sequence.availableTokens.firstOrNull()?.let { extra ->
            vm.onTokenTapped(extra)
        }
        assertEquals(sizeBeforeExtra, vm.uiState.value.sequence.playerSequence.size)
    }

    // ── undo ──────────────────────────────────────────────────────────────────

    @Test
    fun `onUndo removes last player token and returns it to available`() {
        val vm = sequenceVm()
        vm.onReady()
        val token = vm.uiState.value.sequence.availableTokens.first()
        vm.onTokenTapped(token)
        vm.onUndo()
        assertTrue(vm.uiState.value.sequence.playerSequence.isEmpty())
        assertTrue(token in vm.uiState.value.sequence.availableTokens)
    }

    @Test
    fun `onUndo is a no-op when player sequence is empty`() {
        val vm = sequenceVm()
        vm.onReady()
        val availableBefore = vm.uiState.value.sequence.availableTokens.toList()
        vm.onUndo()
        assertEquals(availableBefore.toSet(), vm.uiState.value.sequence.availableTokens.toSet())
        assertTrue(vm.uiState.value.sequence.playerSequence.isEmpty())
    }

    @Test
    fun `onUndo is ignored when not in INPUT phase`() {
        val vm = CognitiveCreameryViewModel()
        // STUDY phase — undo should do nothing
        vm.onUndo()
        assertEquals(CreameryPhase.STUDY, vm.uiState.value.sequence.phase)
    }

    // ── check ─────────────────────────────────────────────────────────────────

    @Test
    fun `onCheck is ignored when player sequence is not full`() {
        val vm = sequenceVm()
        vm.onReady()
        vm.onTokenTapped(vm.uiState.value.sequence.availableTokens.first())
        vm.onCheck()
        assertEquals(CreameryPhase.INPUT, vm.uiState.value.sequence.phase)
    }

    @Test
    fun `correct sequence on non-final round transitions to ROUND_RESULT`() {
        val vm = sequenceVm()
        vm.onReady()
        // submit the correct sequence for round 0
        submitCorrectSequence(vm)
        assertEquals(CreameryPhase.ROUND_RESULT, vm.uiState.value.sequence.phase)
        assertTrue(vm.uiState.value.sequence.lastRoundCorrect)
    }

    @Test
    fun `incorrect sequence on non-final round transitions to ROUND_RESULT`() {
        val vm = sequenceVm()
        vm.onReady()
        submitWrongSequence(vm)
        assertEquals(CreameryPhase.ROUND_RESULT, vm.uiState.value.sequence.phase)
        assertFalse(vm.uiState.value.sequence.lastRoundCorrect)
    }

    @Test
    fun `correct sequence increments correctRounds`() {
        val vm = sequenceVm()
        vm.onReady()
        submitCorrectSequence(vm)
        assertEquals(1, vm.uiState.value.sequence.correctRounds)
    }

    @Test
    fun `incorrect sequence does not increment correctRounds`() {
        val vm = sequenceVm()
        vm.onReady()
        submitWrongSequence(vm)
        assertEquals(0, vm.uiState.value.sequence.correctRounds)
    }

    // ── round progression ─────────────────────────────────────────────────────

    @Test
    fun `onNextRound advances to next round in STUDY phase`() {
        val vm = sequenceVm()
        vm.onReady()
        submitCorrectSequence(vm)
        vm.onNextRound()
        assertEquals(1, vm.uiState.value.sequence.currentRound)
        assertEquals(CreameryPhase.STUDY, vm.uiState.value.sequence.phase)
    }

    @Test
    fun `round 1 target sequence has length matching round lengths index 1`() {
        val vm = sequenceVm()
        vm.onReady()
        submitCorrectSequence(vm)
        vm.onNextRound()
        assertEquals(ROUND_LENGTHS[1], vm.uiState.value.sequence.targetSequence.size)
    }

    @Test
    fun `onNextRound is a no-op when not in ROUND_RESULT phase`() {
        val vm = CognitiveCreameryViewModel()
        vm.onNextRound() // still in STUDY
        assertEquals(0, vm.uiState.value.sequence.currentRound)
        assertEquals(CreameryPhase.STUDY, vm.uiState.value.sequence.phase)
    }

    @Test
    fun `correctRounds carries across round boundary`() {
        val vm = sequenceVm()
        vm.onReady()
        submitCorrectSequence(vm)
        val correctAfterR0 = vm.uiState.value.sequence.correctRounds
        vm.onNextRound()
        assertEquals(correctAfterR0, vm.uiState.value.sequence.correctRounds)
    }

    // ── final round → RUN_COMPLETE ────────────────────────────────────────────

    @Test
    fun `completing the final round transitions to RUN_COMPLETE`() {
        val vm = playAllRounds(correctAll = false)
        assertEquals(CreameryPhase.RUN_COMPLETE, vm.uiState.value.sequence.phase)
    }

    @Test
    fun `perfect run produces 3 stars`() {
        val vm = playAllRounds(correctAll = true)
        val result = vm.buildResult()
        assertEquals(3, result.stars)
        assertTrue(result.perfect)
    }

    @Test
    fun `perfect run buildResult completed is true`() {
        val vm = playAllRounds(correctAll = true)
        assertTrue(vm.buildResult().completed)
    }

    @Test
    fun `two correct rounds produces 2 stars`() {
        val vm = sequenceVm()
        // Round 0: correct
        vm.onReady(); submitCorrectSequence(vm); vm.onNextRound()
        // Round 1: correct
        vm.onReady(); submitCorrectSequence(vm); vm.onNextRound()
        // Round 2: wrong — goes to RUN_COMPLETE
        vm.onReady(); submitWrongSequence(vm)
        assertEquals(2, vm.buildResult().stars)
        assertFalse(vm.buildResult().perfect)
    }

    @Test
    fun `zero correct rounds produces 1 star`() {
        val vm = sequenceVm()
        vm.onReady(); submitWrongSequence(vm); vm.onNextRound()
        vm.onReady(); submitWrongSequence(vm); vm.onNextRound()
        vm.onReady(); submitWrongSequence(vm)
        assertEquals(1, vm.buildResult().stars)
    }

    @Test
    fun `buildResult score equals correctRounds`() {
        val vm = playAllRounds(correctAll = true)
        assertEquals(ROUND_LENGTHS.size, vm.buildResult().score)
    }

    @Test
    fun `clarity-only session can earn 3 stars`() {
        val vm = CognitiveCreameryViewModel()
        vm.navigateTo(CreameryActivity.CLARITY)

        repeat(3) {
            val group = vm.uiState.value.clarity.currentGroup ?: error("missing clarity group")
            group.members.forEach(vm::onClarityWordTapped)
            if (it < 2) {
                vm.onClarityNextRound()
            }
        }

        val result = vm.buildResult()
        assertEquals(3, result.score)
        assertEquals(3, result.stars)
        assertTrue(result.perfect)
    }

    // ── buildRound companion ──────────────────────────────────────────────────

    @Test
    fun `buildRound produces correct phase and round index`() {
        val state = CognitiveCreameryViewModel.buildSequenceRound(1, correctRounds = 2)
        assertEquals(1, state.currentRound)
        assertEquals(CreameryPhase.STUDY, state.phase)
        assertEquals(2, state.correctRounds)
    }

    @Test
    fun `buildRound sequence length matches ROUND_LENGTHS entry`() {
        ROUND_LENGTHS.forEachIndexed { index, length ->
            val state = CognitiveCreameryViewModel.buildSequenceRound(index, correctRounds = 0)
            assertEquals(length, state.targetSequence.size)
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /**
     * Submits the correct target sequence for the current INPUT phase.
     * Requires [vm] to already be in INPUT phase.
     */
    private fun submitCorrectSequence(vm: CognitiveCreameryViewModel) {
        val target = vm.uiState.value.sequence.targetSequence
        target.forEach { flavor -> vm.onTokenTapped(flavor) }
        vm.onCheck()
    }

    /**
     * Submits a guaranteed wrong sequence by picking available tokens
     * in their current (shuffled) order, which is almost always wrong.
     * Falls back to reversing the target if the shuffled order happens to match.
     */
    private fun submitWrongSequence(vm: CognitiveCreameryViewModel) {
        val target = vm.uiState.value.sequence.targetSequence
        val available = vm.uiState.value.sequence.availableTokens.toMutableList()
        // Build a sequence that differs from target
        val wrong = if (available.take(target.size) != target) {
            available.take(target.size)
        } else {
            target.reversed()
        }
        wrong.forEach { flavor -> vm.onTokenTapped(flavor) }
        vm.onCheck()
    }

    /**
     * Plays all 3 rounds either all-correct or all-wrong, ending at RUN_COMPLETE.
     */
    private fun playAllRounds(correctAll: Boolean): CognitiveCreameryViewModel {
        val vm = sequenceVm()
        repeat(ROUND_LENGTHS.size) { index ->
            vm.onReady()
            if (correctAll) submitCorrectSequence(vm) else submitWrongSequence(vm)
            if (index < ROUND_LENGTHS.lastIndex) vm.onNextRound()
        }
        return vm
    }

    private fun sequenceVm(): CognitiveCreameryViewModel =
        CognitiveCreameryViewModel().apply {
            navigateTo(CreameryActivity.SEQUENCE)
        }
}
