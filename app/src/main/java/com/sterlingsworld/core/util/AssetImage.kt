package com.sterlingsworld.core.util

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

/**
 * Loads a PNG/JPG from the Android assets directory as a Compose [ImageBitmap].
 * Returns null if the asset is not found — callers should render a fallback in that case.
 *
 * [assetPath] is relative to `assets/`, e.g. "images/mascot/sterling_wave.png"
 */
@Composable
fun rememberAssetBitmap(assetPath: String): ImageBitmap? {
    val context = LocalContext.current
    return remember(assetPath) { loadAssetBitmap(context, assetPath) }
}

fun loadAssetBitmap(context: Context, assetPath: String): ImageBitmap? {
    return try {
        context.assets.open(assetPath).use { stream ->
            BitmapFactory.decodeStream(stream)?.asImageBitmap()
        }
    } catch (_: Exception) {
        null
    }
}
