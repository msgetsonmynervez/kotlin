package com.sterlingsworld.catalog

import com.sterlingsworld.data.catalog.CinemaCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CinemaCatalogTest {

    @Test
    fun `catalog has at least one video`() {
        assertTrue(CinemaCatalog.videos.isNotEmpty())
    }

    @Test
    fun `all video ids are unique`() {
        val ids = CinemaCatalog.videos.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `all videos have non-empty title and asset path`() {
        CinemaCatalog.videos.forEach { video ->
            assertTrue("${video.id} title is empty", video.title.isNotEmpty())
            assertTrue("${video.id} assetPath is empty", video.assetPath.isNotEmpty())
        }
    }

    @Test
    fun `all asset paths end with dot mp4`() {
        CinemaCatalog.videos.forEach { video ->
            assertTrue("${video.id} path does not end in .mp4", video.assetPath.endsWith(".mp4"))
        }
    }

    @Test
    fun `byId returns correct video`() {
        val first = CinemaCatalog.videos.first()
        val result = CinemaCatalog.byId(first.id)
        assertNotNull(result)
        assertEquals(first.title, result!!.title)
    }

    @Test
    fun `byId returns null for unknown id`() {
        assertNull(CinemaCatalog.byId("does-not-exist"))
    }

    @Test
    fun `all video asset paths contain no spaces`() {
        CinemaCatalog.videos.forEach { video ->
            assertTrue("${video.id} assetPath contains space: '${video.assetPath}'", !video.assetPath.contains(" "))
        }
    }

}
