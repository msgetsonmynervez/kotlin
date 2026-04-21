package com.sterlingsworld.feature.game

import com.sterlingsworld.feature.game.games.spoongauntlet.GauntletAgenda
import com.sterlingsworld.feature.game.games.spoongauntlet.GauntletBot
import com.sterlingsworld.feature.game.games.spoongauntlet.GauntletHero
import com.sterlingsworld.feature.game.games.spoongauntlet.SPOON_GAUNTLET_SCENES
import com.sterlingsworld.feature.game.games.spoongauntlet.SpoonGauntletScreen
import com.sterlingsworld.feature.game.games.spoongauntlet.SpoonGauntletViewModel
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpoonGauntletViewModelTest {

    @Test
    fun `g00gl starts with two extra spoons before agenda`() {
        val vm = SpoonGauntletViewModel(random = FixedRandom(ints = listOf(8), doubles = listOf(0.99)))

        vm.startSession()
        vm.acknowledgeIntro()
        vm.pickHero(GauntletHero.JANE)
        vm.continueToBotCatalog()
        vm.pickBot(GauntletBot.G00GL)

        assertEquals(10, vm.uiState.value.spoons)
    }

    @Test
    fun `jane reduces bureaucracy costs by one`() {
        val vm = buildReadyVm(hero = GauntletHero.JANE, bot = GauntletBot.IBOT)
        val startingSpoons = vm.uiState.value.spoons
        val martyrChoice = SPOON_GAUNTLET_SCENES.first().choices.first()

        vm.confirmChoice(martyrChoice)

        assertEquals(startingSpoons - 1, vm.uiState.value.spoons)
    }

    @Test
    fun `i-bot reduces physical costs by one`() {
        val vm = buildReadyVm(hero = GauntletHero.JANE, bot = GauntletBot.IBOT)
        repeat(4) { vm.confirmChoice(SPOON_GAUNTLET_SCENES[vm.uiState.value.currentSceneIndex].choices.last()) }
        val startingSpoons = vm.uiState.value.spoons
        val physicalChoice = SPOON_GAUNTLET_SCENES[vm.uiState.value.currentSceneIndex].choices.first()

        vm.confirmChoice(physicalChoice)

        assertEquals(startingSpoons - 2, vm.uiState.value.spoons)
    }

    @Test
    fun `msoft bypasses one expensive cost`() {
        val vm = buildReadyVm(hero = GauntletHero.JOHN, bot = GauntletBot.MSOFT)
        val startingSpoons = vm.uiState.value.spoons
        val expensiveChoice = SPOON_GAUNTLET_SCENES.first().choices.first()

        vm.confirmChoice(expensiveChoice)

        assertEquals(startingSpoons, vm.uiState.value.spoons)
        assertTrue(vm.uiState.value.mSoftUsed)
        assertTrue(vm.uiState.value.eventMessage?.contains("Mandatory Update") == true)
    }

    @Test
    fun `flare up moves to flare screen then back into run`() {
        val vm = SpoonGauntletViewModel(random = FixedRandom(ints = listOf(9), doubles = listOf(0.0)))
        vm.startSession()
        vm.acknowledgeIntro()
        vm.pickHero(GauntletHero.JANE)
        vm.continueToBotCatalog()
        vm.pickBot(GauntletBot.IBOT)
        vm.pickAgenda(GauntletAgenda.EQUILIBRIUM)

        vm.confirmChoice(SPOON_GAUNTLET_SCENES.first().choices.last())

        assertEquals(SpoonGauntletScreen.FLARE_UP, vm.uiState.value.screen)
        vm.recoverFromFlareUp()
        assertEquals(SpoonGauntletScreen.SCENE, vm.uiState.value.screen)
        assertEquals(1, vm.uiState.value.currentSceneIndex)
    }

    @Test
    fun `clearing all scenes reaches win result`() {
        val vm = buildReadyVm(hero = GauntletHero.JOHN, bot = GauntletBot.G00GL)

        repeat(SPOON_GAUNTLET_SCENES.size) {
            val state = vm.uiState.value
            if (state.screen == SpoonGauntletScreen.SCENE) {
                vm.confirmChoice(SPOON_GAUNTLET_SCENES[state.currentSceneIndex].choices.last())
            }
        }

        assertEquals(SpoonGauntletScreen.RESULT, vm.uiState.value.screen)
        assertTrue(vm.uiState.value.result?.won == true)
        assertTrue(vm.buildResult().completed)
    }

    @Test
    fun `running out of spoons ends in crash result`() {
        val vm = SpoonGauntletViewModel(random = FixedRandom(ints = listOf(8), doubles = List(8) { 0.99 }))
        vm.startSession()
        vm.acknowledgeIntro()
        vm.pickHero(GauntletHero.JOHN)
        vm.continueToBotCatalog()
        vm.pickBot(GauntletBot.G00GL)
        vm.pickAgenda(GauntletAgenda.HUSTLE)

        repeat(4) {
            val state = vm.uiState.value
            if (state.screen == SpoonGauntletScreen.SCENE) {
                vm.confirmChoice(SPOON_GAUNTLET_SCENES[state.currentSceneIndex].choices.first())
            }
        }

        assertEquals(SpoonGauntletScreen.RESULT, vm.uiState.value.screen)
        assertFalse(vm.uiState.value.result?.won == true)
    }

    private fun buildReadyVm(hero: GauntletHero, bot: GauntletBot): SpoonGauntletViewModel {
        val vm = SpoonGauntletViewModel(random = FixedRandom(ints = listOf(10), doubles = List(16) { 0.99 }))
        vm.startSession()
        vm.acknowledgeIntro()
        vm.pickHero(hero)
        vm.continueToBotCatalog()
        vm.pickBot(bot)
        vm.pickAgenda(GauntletAgenda.EQUILIBRIUM)
        return vm
    }
}

private class FixedRandom(
    private val ints: List<Int>,
    private val doubles: List<Double>,
) : Random() {
    private var intIndex = 0
    private var doubleIndex = 0

    override fun nextBits(bitCount: Int): Int = 0

    override fun nextInt(from: Int, until: Int): Int {
        val next = ints[intIndex.coerceAtMost(ints.lastIndex)]
        intIndex += 1
        return next.coerceIn(from, until - 1)
    }

    override fun nextDouble(): Double {
        val next = doubles[doubleIndex.coerceAtMost(doubles.lastIndex)]
        doubleIndex += 1
        return next
    }
}
