package com.sterlingsworld.core.media

/**
 * Pure, Android-free path-resolution logic for Studio audio assets.
 *
 * All environment branching decisions live here so they can be unit-tested
 * without Android framework types. [StudioAudioLocator] wraps this object
 * and supplies the real [assetAccessible] check via [android.content.res.AssetManager].
 */
object StudioAudioResolver {

    /**
     * Returns the `file:///android_asset/` URI for [assetPath] if [assetAccessible]
     * is true, or null if the asset cannot be reached.
     *
     * For the current v1 plan, install-time PAD is expected to expose content through
     * the same asset path used by bundled assets. This is an explicit assumption that
     * still requires device validation from a Play-delivered install. The [useAssetPacks]
     * flag is reserved for future divergence if runtime pack-location handling becomes
     * necessary.
     */
    fun buildUri(assetPath: String, assetAccessible: Boolean, useAssetPacks: Boolean): String? {
        // useAssetPacks reserved: v1 assumes install-time PAD keeps the same asset URI
        // shape. If runtime pack-location resolution is required later, this branch is
        // where the behavior diverges.
        return if (assetAccessible) "file:///android_asset/$assetPath" else null
    }

    /**
     * Computes [StudioAvailability] from how many tracks resolved out of the total.
     *
     * - 0 resolved -> [StudioAvailability.UNAVAILABLE]
     * - all resolved -> [StudioAvailability.READY]
     * - partial -> [StudioAvailability.WAITING_FOR_ASSETS] (corpus mid-download)
     */
    fun computeAvailability(resolvedCount: Int, totalCount: Int): StudioAvailability = when {
        totalCount == 0 || resolvedCount == 0 -> StudioAvailability.UNAVAILABLE
        resolvedCount < totalCount -> StudioAvailability.WAITING_FOR_ASSETS
        else -> StudioAvailability.READY
    }
}
