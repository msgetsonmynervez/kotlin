package com.sterlingsworld.feature.game

import com.sterlingsworld.feature.game.games.ghost.GhostViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GhostViewModelTest {

    @Test
    fun `initial state starts at level one with no feedback`() {
        val vm = GhostViewModel()

        assertEquals(0, vm.uiState.value.currentLevel)
        assertEquals(3, vm.uiState.value.totalLevels)
        assertNull(vm.uiState.value.feedback)
        assertFalse(vm.uiState.value.isRunComplete)
    }

    @Test
    fun `wrong answer records feedback and does not complete run`() {
        val vm = GhostViewModel()

        vm.submitChoice(0)

        assertNotNull(vm.uiState.value.feedback)
        assertFalse(vm.uiState.value.feedback!!.correct)
        assertEquals(0, vm.uiState.value.correctAnswers)
        assertFalse(vm.uiState.value.isRunComplete)
    }

    @Test
    fun `correct answer records feedback and increments correct answers`() {
        val vm = GhostViewModel()

        vm.submitChoice(1)

        assertNotNull(vm.uiState.value.feedback)
        assertTrue(vm.uiState.value.feedback!!.correct)
        assertEquals(1, vm.uiState.value.correctAnswers)
        assertFalse(vm.uiState.value.isRunComplete)
    }

    @Test
    fun `advanceAfterSuccess moves to next level only after correct answer`() {
        val vm = GhostViewModel()
        val promptBefore = vm.uiState.value.prompt

        vm.submitChoice(1)
        vm.advanceAfterSuccess()

        assertEquals(1, vm.uiState.value.currentLevel)
        assertNull(vm.uiState.value.feedback)
        assertTrue(vm.uiState.value.prompt != promptBefore)
    }

    @Test
    fun `advanceAfterSuccess is ignored after wrong answer`() {
        val vm = GhostViewModel()
        val promptBefore = vm.uiState.value.prompt

        vm.submitChoice(0)
        vm.advanceAfterSuccess()

        assertEquals(0, vm.uiState.value.currentLevel)
        assertEquals(promptBefore, vm.uiState.value.prompt)
        assertNotNull(vm.uiState.value.feedback)
    }

    @Test
    fun `final correct answer marks run complete`() {
        val vm = GhostViewModel()

        vm.submitChoice(1)
        vm.advanceAfterSuccess()
        vm.submitChoice(1)
        vm.advanceAfterSuccess()
        vm.submitChoice(2)

        assertEquals(2, vm.uiState.value.currentLevel)
        assertTrue(vm.uiState.value.isRunComplete)
        assertTrue(vm.uiState.value.feedback!!.correct)
        assertEquals(3, vm.uiState.value.correctAnswers)
    }

    @Test
    fun `submitChoice is ignored once feedback is already present`() {
        val vm = GhostViewModel()

        vm.submitChoice(1)
        val firstFeedback = vm.uiState.value.feedback
        val firstCorrectAnswers = vm.uiState.value.correctAnswers
        vm.submitChoice(0)

        assertEquals(firstFeedback, vm.uiState.value.feedback)
        assertEquals(firstCorrectAnswers, vm.uiState.value.correctAnswers)
    }

    @Test
    fun `perfect run produces three stars and perfect result`() {
        val vm = GhostViewModel()

        vm.submitChoice(1)
        vm.advanceAfterSuccess()
        vm.submitChoice(1)
        vm.advanceAfterSuccess()
        vm.submitChoice(2)

        val result = vm.buildResult()
        assertTrue(result.completed)
        assertEquals(3, result.score)
        assertEquals(3, result.stars)
        assertTrue(result.perfect)
    }

    @Test
    fun `partial run produces two star non perfect result`() {
        val vm = GhostViewModel()

        vm.submitChoice(1)
        vm.advanceAfterSuccess()
        vm.submitChoice(0)

        val result = vm.buildResult()
        assertTrue(result.completed)
        assertEquals(1, result.score)
        assertEquals(2, result.stars)
        assertFalse(result.perfect)
    }
}
