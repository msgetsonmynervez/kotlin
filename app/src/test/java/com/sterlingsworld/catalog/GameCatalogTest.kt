package com.sterlingsworld.catalog

import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.domain.model.GameSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameCatalogTest {

    @Test
    fun `catalog contains 14 games total`() {
        assertEquals(14, GameCatalog.all.size)
    }

    @Test
    fun `arcade section has 9 games`() {
        assertEquals(9, GameCatalog.bySection(GameSection.GAMES).size)
    }

    @Test
    fun `kidz section has 5 games`() {
        assertEquals(5, GameCatalog.bySection(GameSection.KIDZ).size)
    }

    @Test
    fun `all game ids are unique`() {
        val ids = GameCatalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `byId returns correct game`() {
        val game = GameCatalog.byId("symptom-striker")
        assertNotNull(game)
        assertEquals("Symptom Striker", game!!.title)
    }

    @Test
    fun `byId returns correct ghost game`() {
        val game = GameCatalog.byId("ghost")
        assertNotNull(game)
        assertEquals("Ghost", game!!.title)
    }

    @Test
    fun `byId returns null for unknown id`() {
        val game = GameCatalog.byId("does-not-exist")
        assertTrue(game == null)
    }

    @Test
    fun `all games have non-empty title description and objective`() {
        GameCatalog.all.forEach { game ->
            assertTrue("${game.id} title empty", game.title.isNotEmpty())
            assertTrue("${game.id} description empty", game.description.isNotEmpty())
            assertTrue("${game.id} objective empty", game.objective.isNotEmpty())
        }
    }

    @Test
    fun `all games have positive estimated duration`() {
        GameCatalog.all.forEach { game ->
            assertTrue("${game.id} duration <= 0", game.estimatedDurationSec > 0)
        }
    }

    @Test
    fun `current production games are ship ready`() {
        assertTrue(GameCatalog.isShipReady("aol"))
        assertTrue(GameCatalog.isShipReady("lucky-paws"))
        assertTrue(GameCatalog.isShipReady("ghost"))
        assertTrue(GameCatalog.isShipReady("cognitive-creamery"))
        assertTrue(GameCatalog.isShipReady("symptom-striker"))
        assertTrue(GameCatalog.isShipReady("relaxation-retreat"))
        assertTrue(GameCatalog.isShipReady("spoon-gauntlet"))
        assertFalse(GameCatalog.isShipReady("access-quest"))
        assertFalse(GameCatalog.isShipReady("access-racer"))
        assertFalse(GameCatalog.isShipReady("snails-journey"))
        assertFalse(GameCatalog.isShipReady("kidz-doodle-land"))
    }

    @Test
    fun `exactly seven games are ship-ready across the full catalog`() {
        val shipReady = GameCatalog.all.filter { GameCatalog.isShipReady(it.id) }
        assertEquals(7, shipReady.size)
        assertTrue(shipReady.any { it.id == "aol" })
        assertTrue(shipReady.any { it.id == "lucky-paws" })
        assertTrue(shipReady.any { it.id == "ghost" })
        assertTrue(shipReady.any { it.id == "cognitive-creamery" })
        assertTrue(shipReady.any { it.id == "symptom-striker" })
        assertTrue(shipReady.any { it.id == "relaxation-retreat" })
        assertTrue(shipReady.any { it.id == "spoon-gauntlet" })
    }

    @Test
    fun `isShipReady returns false for unknown id`() {
        assertFalse(GameCatalog.isShipReady("does-not-exist"))
    }

    @Test
    fun `all kidz section games are not ship-ready`() {
        GameCatalog.bySection(GameSection.KIDZ).forEach { game ->
            assertFalse("${game.id} should not be ship-ready in v1", GameCatalog.isShipReady(game.id))
        }
    }
}
