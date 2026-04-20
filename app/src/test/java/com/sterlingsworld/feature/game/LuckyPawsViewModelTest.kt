package com.sterlingsworld.feature.game

import com.sterlingsworld.feature.game.games.luckypaws.LuckyPawsPhase
import com.sterlingsworld.feature.game.games.luckypaws.LuckyPawsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LuckyPawsViewModelTest {

    @Test
    fun `initial phase is waiting`() {
        val vm = LuckyPawsViewModel()

        assertEquals(LuckyPawsPhase.WAITING, vm.uiState.value.phase)
    }

    @Test
    fun `onReveal transitions to revealed`() {
        val vm = LuckyPawsViewModel()

        vm.onReveal()

        assertEquals(LuckyPawsPhase.REVEALED, vm.uiState.value.phase)
    }

    @Test
    fun `onReplay transitions back to waiting`() {
        val vm = LuckyPawsViewModel()
        vm.onReveal()

        vm.onReplay()

        assertEquals(LuckyPawsPhase.WAITING, vm.uiState.value.phase)
    }

    @Test
    fun `buildResult reflects revealed reward state instead of perfect score`() {
        val vm = LuckyPawsViewModel()
        vm.onReveal()
        val result = vm.buildResult()

        assertTrue(result.completed)
        assertEquals(1, result.score)
        assertEquals(1, result.stars)
        assertFalse(result.perfect)
    }

    @Test
    fun `reward string is not empty after init`() {
        val vm = LuckyPawsViewModel()

        assertFalse(vm.uiState.value.reward.isEmpty())
    }
}
