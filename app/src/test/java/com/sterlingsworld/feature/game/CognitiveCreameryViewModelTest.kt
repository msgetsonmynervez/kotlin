package com.sterlingsworld.feature.game

import com.sterlingsworld.feature.game.games.cognitivecreamery.CREAMERY_MAX_LEVEL
import com.sterlingsworld.feature.game.games.cognitivecreamery.CreameryActivity
import com.sterlingsworld.feature.game.games.cognitivecreamery.CognitiveCreameryViewModel
import com.sterlingsworld.feature.game.games.cognitivecreamery.SequencePhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CognitiveCreameryViewModelTest {

    @Test
    fun `initial state opens in parlor with playable flavor of the day`() {
        val vm = CognitiveCreameryViewModel()
        val state = vm.uiState.value

        assertEquals(CreameryActivity.PARLOR, state.currentActivity)
        assertTrue(state.flavorOfTheDay != CreameryActivity.PARLOR)
    }

    @Test
    fun `navigate to each activity seeds a playable round`() {
        val vm = CognitiveCreameryViewModel()

        vm.navigateTo(CreameryActivity.CLARITY)
        assertTrue(vm.uiState.value.clarity.options.isNotEmpty())

        vm.navigateTo(CreameryActivity.SCAN)
        assertEquals(16, vm.uiState.value.scan.grid.size)

        vm.navigateTo(CreameryActivity.SEQUENCE)
        assertEquals(SequencePhase.STUDY, vm.uiState.value.sequence.phase)

        vm.navigateTo(CreameryActivity.CATEGORY)
        assertEquals(9, vm.uiState.value.category.items.size)

        vm.navigateTo(CreameryActivity.SYMMETRY)
        assertTrue(vm.uiState.value.symmetry.blueprint.isNotEmpty())

        vm.navigateTo(CreameryActivity.FLIP)
        assertNotNull(vm.uiState.value.flip.targetAnswer)

        vm.navigateTo(CreameryActivity.PATTERN)
        assertEquals(4, vm.uiState.value.pattern.options.size)
    }

    @Test
    fun `clarity correct picks clear the level and auto advance`() {
        val vm = CognitiveCreameryViewModel()
        vm.navigateTo(CreameryActivity.CLARITY)
        val answers = vm.uiState.value.clarity.answers

        answers.forEach(vm::onClarityWordTapped)

        val state = vm.uiState.value
        assertEquals(2, state.activeLevel)
        assertEquals(CreameryActivity.CLARITY, state.currentActivity)
        assertEquals(null, state.resultOverlay)
    }

    @Test
    fun `scan wrong picks build fatigue`() {
        val vm = CognitiveCreameryViewModel()
        vm.navigateTo(CreameryActivity.SCAN)
        val target = vm.uiState.value.scan.target
        val wrongIndex = vm.uiState.value.scan.grid.indexOfFirst { it != target }

        vm.onScanTapped(wrongIndex)

        assertTrue(vm.uiState.value.activeFatigue > 0)
    }

    @Test
    fun `scan round target count stays within original html range`() {
        repeat(20) {
            val round = CognitiveCreameryViewModel.buildScanRound(1)
            assertTrue(round.needed in 2..4)
        }
    }

    @Test
    fun `sequence ready and correct inputs clear the level`() {
        val vm = CognitiveCreameryViewModel()
        vm.navigateTo(CreameryActivity.SEQUENCE)
        vm.onReadySequence()

        val target = vm.uiState.value.sequence.targetSequence
        target.forEach(vm::onSequenceTokenTapped)

        val state = vm.uiState.value
        assertEquals(2, state.activeLevel)
        assertEquals(CreameryActivity.SEQUENCE, state.currentActivity)
        assertEquals(null, state.resultOverlay)
    }

    @Test
    fun `reset session returns creamery to parlor`() {
        val vm = CognitiveCreameryViewModel()
        vm.navigateTo(CreameryActivity.FLIP)
        vm.onFlipGuess("definitely-wrong")
        assertTrue(vm.uiState.value.activeFatigue > 0)

        vm.resetSession()

        val state = vm.uiState.value
        assertEquals(CreameryActivity.PARLOR, state.currentActivity)
        assertEquals(1, state.activeLevel)
        assertEquals(0, state.activeFatigue)
        assertFalse(state.clearedModes.isNotEmpty())
    }

    @Test
    fun `result duration is populated`() {
        val vm = CognitiveCreameryViewModel()
        val result = vm.buildResult()

        assertTrue(result.durationMs >= 0L)
    }

    @Test
    fun `symmetry requires explicit check before clearing`() {
        val vm = CognitiveCreameryViewModel()
        vm.navigateTo(CreameryActivity.SYMMETRY)
        val blueprint = vm.uiState.value.symmetry.blueprint
        blueprint.forEachIndexed { index, target ->
            repeat(target) { vm.onSymmetryCycle(index) }
        }

        assertEquals(1, vm.uiState.value.activeLevel)
        vm.onSymmetryCheck()
        assertEquals(2, vm.uiState.value.activeLevel)
    }

    @Test
    fun `mode can finish at level fifty`() {
        val vm = CognitiveCreameryViewModel()
        vm.navigateTo(CreameryActivity.CLARITY)

        repeat(CREAMERY_MAX_LEVEL) {
            val answers = vm.uiState.value.clarity.answers
            answers.forEach(vm::onClarityWordTapped)
            if (it < CREAMERY_MAX_LEVEL - 1) {
                vm.dismissResultOverlay()
                vm.navigateTo(CreameryActivity.CLARITY)
            }
        }

        assertTrue(vm.uiState.value.clearedModes.contains(CreameryActivity.CLARITY))
    }
}
