package com.sterlingsworld.catalog

import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.domain.model.GameSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameCatalogTest {

    @Test
    fun `catalog contains 10 games total`() {
        assertEquals(10, GameCatalog.all.size)
    }

    @Test
    fun `arcade section has 6 games`() {
        assertEquals(6, GameCatalog.bySection(GameSection.GAMES).size)
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
    fun `byId returns null for unknown id`() {
        val game = GameCatalog.byId("ghost")
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
}
