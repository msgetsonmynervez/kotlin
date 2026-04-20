package com.sterlingsworld.core.media

import android.content.Context
import android.net.Uri
import android.util.Log
import com.sterlingsworld.BuildConfig
import com.sterlingsworld.data.catalog.StudioCatalog

/**
 * Resolves Studio audio asset [Uri]s for the current build environment.
 *
 * - **Debug / sideload** (`USE_ASSET_PACKS = false`): probes `AssetManager` for each
 *   track. Returns a valid `file:///android_asset/` URI if the asset exists, or null
 *   if the MP3 corpus is not present in the local source tree. This produces an honest
 *   [StudioAvailability.UNAVAILABLE] state rather than broken URIs.
 *
 * - **QA / release** (`USE_ASSET_PACKS = true`): v1 targets the `:studio-audio`
 *   install-time PAD module. The current implementation assumes the delivered pack
 *   remains reachable through the same `AssetManager` probe and `file:///android_asset/`
 *   URI path. This must still be proven on a Play-installed build.
 *   If the pack is not reachable, the probe returns null and the service degrades honestly.
 *
 * All branching logic is delegated to [StudioAudioResolver] for unit-test coverage.
 */
class StudioAudioLocator(private val context: Context) {

    /**
     * Returns the [Uri] for [assetPath], or null if the asset is not accessible.
     */
    fun resolve(assetPath: String): Uri? {
        val accessible = isAssetAccessible(assetPath)
        val uriString = StudioAudioResolver.buildUri(
            assetPath = assetPath,
            assetAccessible = accessible,
            useAssetPacks = BuildConfig.USE_ASSET_PACKS,
        ) ?: return null
        return Uri.parse(uriString)
    }

    /**
     * Resolves all catalog tracks and returns a pair of (resolved URIs, availability).
     *
     * Tracks that cannot be resolved are silently skipped so the player queue only
     * contains reachable items. The returned [StudioAvailability] reflects whether
     * all, some, or none of the tracks resolved.
     */
    fun resolveAll(): Pair<List<Pair<String, Uri>>, StudioAvailability> {
        val all = StudioCatalog.playableTracks
        val resolved = all.mapNotNull { track ->
            val uri = resolve(track.assetPath) ?: return@mapNotNull null
            track.id to uri
        }
        val availability = StudioAudioResolver.computeAvailability(
            resolvedCount = resolved.size,
            totalCount = all.size,
        )

        // In PAD builds, any unavailable track is a release validation failure.
        // If you see this log from a Play-delivered install, the PAD asset-path assumption is broken.
        if (BuildConfig.USE_ASSET_PACKS && availability != StudioAvailability.READY) {
            Log.w(
                "StudioAudioLocator",
                "PAD build: ${all.size - resolved.size}/${all.size} tracks not reachable " +
                    "via AssetManager. Validate that the :studio-audio pack is install-time " +
                    "and that file:///android_asset/ URIs resolve from the delivered pack. " +
                    "Availability = $availability",
            )
        }

        return resolved to availability
    }

    private fun isAssetAccessible(assetPath: String): Boolean {
        return try {
            context.assets.open(assetPath).close()
            true
        } catch (_: Exception) {
            false
        }
    }
}
