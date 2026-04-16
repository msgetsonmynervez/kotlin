package com.sterlingsworld.core.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StudioAudioResolverTest {

    // ── buildUri ─────────────────────────────────────────────────────────────

    @Test
    fun `buildUri returns asset URI when asset is accessible in debug`() {
        val path = "audio/music/music-track-01.mp3"
        val result = StudioAudioResolver.buildUri(
            assetPath = path,
            assetAccessible = true,
            useAssetPacks = false,
        )
        assertEquals("file:///android_asset/$path", result)
    }

    @Test
    fun `buildUri returns null when asset is not accessible in debug`() {
        val result = StudioAudioResolver.buildUri(
            assetPath = "audio/music/music-track-01.mp3",
            assetAccessible = false,
            useAssetPacks = false,
        )
        assertNull(result)
    }

    @Test
    fun `buildUri returns asset URI when pack location accessible in release`() {
        val path = "audio/music/music-track-01.mp3"
        val result = StudioAudioResolver.buildUri(
            assetPath = path,
            assetAccessible = true,
            useAssetPacks = true,
        )
        // Install-time packs are merged into AssetManager namespace; same URI scheme.
        assertEquals("file:///android_asset/$path", result)
    }

    @Test
    fun `buildUri returns null when pack not accessible in release`() {
        val result = StudioAudioResolver.buildUri(
            assetPath = "audio/music/music-track-01.mp3",
            assetAccessible = false,
            useAssetPacks = true,
        )
        assertNull(result)
    }

    @Test
    fun `buildUri URI contains the exact asset path`() {
        val path = "audio/music/groove/music-track-66.mp3"
        val result = StudioAudioResolver.buildUri(
            assetPath = path,
            assetAccessible = true,
            useAssetPacks = false,
        )
        assertTrue(result?.contains(path) == true)
    }

    // ── computeAvailability ───────────────────────────────────────────────────

    @Test
    fun `computeAvailability is UNAVAILABLE when no tracks resolved`() {
        val result = StudioAudioResolver.computeAvailability(resolvedCount = 0, totalCount = 126)
        assertEquals(StudioAvailability.UNAVAILABLE, result)
    }

    @Test
    fun `computeAvailability is READY when all tracks resolved`() {
        val result = StudioAudioResolver.computeAvailability(resolvedCount = 126, totalCount = 126)
        assertEquals(StudioAvailability.READY, result)
    }

    @Test
    fun `computeAvailability is WAITING_FOR_ASSETS when partial tracks resolved`() {
        val result = StudioAudioResolver.computeAvailability(resolvedCount = 60, totalCount = 126)
        assertEquals(StudioAvailability.WAITING_FOR_ASSETS, result)
    }

    @Test
    fun `computeAvailability is UNAVAILABLE when total is zero`() {
        val result = StudioAudioResolver.computeAvailability(resolvedCount = 0, totalCount = 0)
        assertEquals(StudioAvailability.UNAVAILABLE, result)
    }

    @Test
    fun `computeAvailability is READY when single track resolves`() {
        val result = StudioAudioResolver.computeAvailability(resolvedCount = 1, totalCount = 1)
        assertEquals(StudioAvailability.READY, result)
    }

    @Test
    fun `computeAvailability is WAITING_FOR_ASSETS when one of many tracks resolved`() {
        val result = StudioAudioResolver.computeAvailability(resolvedCount = 1, totalCount = 126)
        assertEquals(StudioAvailability.WAITING_FOR_ASSETS, result)
    }

    @Test
    fun `computeAvailability is WAITING_FOR_ASSETS when all but one track resolved`() {
        val result = StudioAudioResolver.computeAvailability(resolvedCount = 125, totalCount = 126)
        assertEquals(StudioAvailability.WAITING_FOR_ASSETS, result)
    }

    @Test
    fun `buildUri always produces file asset URI scheme when accessible`() {
        val result = StudioAudioResolver.buildUri(
            assetPath = "audio/music/music-track-01.mp3",
            assetAccessible = true,
            useAssetPacks = false,
        )
        assertTrue(result?.startsWith("file:///android_asset/") == true)
    }
}
