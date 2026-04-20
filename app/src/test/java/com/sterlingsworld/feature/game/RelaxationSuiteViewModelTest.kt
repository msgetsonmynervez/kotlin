package com.sterlingsworld.feature.game

import com.sterlingsworld.feature.game.suites.relaxation.RelaxationSuiteViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class RelaxationSuiteViewModelTest {

    @Test
    fun `buildResult reflects a completed trivia session`() {
        val vm = RelaxationSuiteViewModel()
        val totalQuestions = vm.uiState.value.trivia.totalQuestions

        repeat(totalQuestions) { questionIndex ->
            val correctIndex = vm.uiState.value.trivia.currentQuestion?.correctIndex
                ?: error("missing trivia question")
            vm.selectAnswer(correctIndex)
            vm.nextQuestion()
            if (questionIndex == totalQuestions - 1) {
                assertTrue(vm.uiState.value.trivia.isComplete)
            }
        }

        val result = vm.buildResult()
        assertTrue(result.completed)
        assertEquals(totalQuestions, result.score)
        assertEquals(3, result.stars)
        assertTrue(result.perfect)
    }

    @Test
    fun `buildResult stays incomplete before trivia is finished`() {
        val vm = RelaxationSuiteViewModel()

        val result = vm.buildResult()

        assertFalse(result.completed)
        assertEquals(0, result.score)
        assertEquals(1, result.stars)
        assertFalse(result.perfect)
    }
}
