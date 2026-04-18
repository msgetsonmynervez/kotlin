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
    fun `catalog contains 15 games total`() {
        assertEquals(15, GameCatalog.all.size)
    }

    @Test
    fun `arcade section has 11 games`() {
        assertEquals(11, GameCatalog.bySection(GameSection.GAMES).size)
    }

    @Test
    fun `kidz section has 4 games`() {
        assertEquals(4, GameCatalog.bySection(GameSection.KIDZ).size)
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
    fun `lucky paws ghost cognitive creamery and symptom striker are ship ready`() {
        assertTrue(GameCatalog.isShipReady("lucky-paws"))
        assertTrue(GameCatalog.isShipReady("ghost"))
        assertTrue(GameCatalog.isShipReady("cognitive-creamery"))
        assertTrue(GameCatalog.isShipReady("symptom-striker"))
        assertFalse(GameCatalog.isShipReady("kidz-doodle-land"))
    }

    @Test
    fun `exactly five games are ship-ready across the full catalog`() {
        val shipReady = GameCatalog.all.filter { GameCatalog.isShipReady(it.id) }
        assertEquals(5, shipReady.size)
        assertTrue(shipReady.any { it.id == "lucky-paws" })
        assertTrue(shipReady.any { it.id == "ghost" })
        assertTrue(shipReady.any { it.id == "cognitive-creamery" })
        assertTrue(shipReady.any { it.id == "symptom-striker" })
        assertTrue(shipReady.any { it.id == "relaxation-retreat" })
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
