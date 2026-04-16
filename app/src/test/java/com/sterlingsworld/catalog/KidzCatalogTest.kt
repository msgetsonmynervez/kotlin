package com.sterlingsworld.catalog

import com.sterlingsworld.data.catalog.KidzCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KidzCatalogTest {

    @Test
    fun `catalog has activities`() {
        assertTrue(KidzCatalog.activities.isNotEmpty())
    }

    @Test
    fun `all activity ids are unique`() {
        val ids = KidzCatalog.activities.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `games helper returns only entries present in activities`() {
        val activityIds = KidzCatalog.activities.map { it.id }.toSet()
        KidzCatalog.games().forEach { game ->
            assertTrue("${game.id} not found in activities", game.id in activityIds)
        }
    }

    @Test
    fun `videos helper returns only entries present in activities`() {
        val activityIds = KidzCatalog.activities.map { it.id }.toSet()
        KidzCatalog.videos().forEach { video ->
            assertTrue("${video.id} not found in activities", video.id in activityIds)
        }
    }

    @Test
    fun `games and videos together account for all activities`() {
        assertEquals(
            KidzCatalog.activities.size,
            KidzCatalog.games().size + KidzCatalog.videos().size,
        )
    }

    @Test
    fun `all KidzGame entries have non-empty gameId`() {
        KidzCatalog.games().forEach { game ->
            assertTrue("${game.id} gameId is empty", game.gameId.isNotEmpty())
        }
    }

    @Test
    fun `all KidzVideo asset paths end with dot mp4`() {
        KidzCatalog.videos().forEach { video ->
            assertTrue(
                "${video.id} assetPath does not end in .mp4",
                video.video.assetPath.endsWith(".mp4"),
            )
        }
    }

    @Test
    fun `videos helper returns at least one entry`() {
        assertTrue("Kidz catalog has no videos — video is expected in v1", KidzCatalog.videos().isNotEmpty())
    }

    @Test
    fun `all KidzVideo entries have non-empty title`() {
        KidzCatalog.videos().forEach { video ->
            assertTrue("${video.id} has empty title", video.title.isNotEmpty())
        }
    }
}
