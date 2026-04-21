package com.sterlingsworld.catalog

import com.sterlingsworld.data.catalog.StudioCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StudioCatalogTest {

    @Test
    fun `catalog has exactly 5 albums`() {
        assertEquals(5, StudioCatalog.albums.size)
    }

    @Test
    fun `total track count is 136`() {
        assertEquals(136, StudioCatalog.allTracks.size)
    }

    @Test
    fun `sterling main library has 48 tracks`() {
        val album = StudioCatalog.albumById("sterling-main")
        assertNotNull(album)
        assertEquals(48, album!!.tracks.size)
    }

    @Test
    fun `dark side of the spoon has 15 tracks`() {
        val album = StudioCatalog.albumById("dark-side-of-the-spoon")
        assertNotNull(album)
        assertEquals(15, album!!.tracks.size)
    }

    @Test
    fun `stand up has 10 tracks`() {
        val album = StudioCatalog.albumById("stand-up")
        assertNotNull(album)
        assertEquals(10, album!!.tracks.size)
    }

    @Test
    fun `groove has 33 tracks`() {
        val album = StudioCatalog.albumById("groove")
        assertNotNull(album)
        assertEquals(33, album!!.tracks.size)
    }

    @Test
    fun `neural garden has 30 tracks`() {
        val album = StudioCatalog.albumById("neural-garden")
        assertNotNull(album)
        assertEquals(30, album!!.tracks.size)
    }

    @Test
    fun `all track ids are unique across all albums`() {
        val ids = StudioCatalog.allTracks.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `all tracks have non-empty asset paths`() {
        StudioCatalog.allTracks.forEach { track ->
            assertTrue("${track.id} has empty assetPath", track.assetPath.isNotEmpty())
        }
    }

    @Test
    fun `all asset paths end with dot mp3`() {
        StudioCatalog.allTracks.forEach { track ->
            assertTrue("${track.id} path does not end in .mp3", track.assetPath.endsWith(".mp3"))
        }
    }

    @Test
    fun `playable albums match current release policy`() {
        assertEquals(3, StudioCatalog.availableAlbums.size)
        assertEquals(55, StudioCatalog.playableTracks.size)
        assertTrue(StudioCatalog.availableAlbums.any { it.id == "dark-side-of-the-spoon" })
        assertTrue(StudioCatalog.availableAlbums.any { it.id == "stand-up" })
        assertTrue(StudioCatalog.availableAlbums.any { it.id == "neural-garden" })
        assertTrue(StudioCatalog.downloadableAlbums.any { it.id == "groove" })
        assertTrue(StudioCatalog.downloadableAlbums.any { it.id == "sterling-main" })
    }
}
