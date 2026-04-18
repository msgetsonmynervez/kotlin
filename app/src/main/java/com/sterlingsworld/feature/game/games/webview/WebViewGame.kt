package com.sterlingsworld.feature.game.games.webview

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sterlingsworld.domain.model.GameResult

/**
 * Loads an HTML game from assets and embeds it in the game shell.
 *
 * The HTML game may optionally call Android.onGameComplete() to signal completion:
 *   Android.onGameComplete(score, stars, durationMs, perfect)
 *
 * If the game never calls it, the user exits normally via the GameShell pause/exit flow.
 */
@Composable
fun WebViewGame(
    assetFolder: String,
    onDone: (GameResult) -> Unit,
) {
    val currentOnDone by rememberUpdatedState(onDone)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            addJavascriptInterface(GameBridge { currentOnDone(it) }, "Android")
            loadUrl("file:///android_asset/games/$assetFolder/index.html")
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { webView },
        onRelease = { it.destroy() }
    )
}

private class GameBridge(private val onDone: (GameResult) -> Unit) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onGameComplete(score: Int, stars: Int, durationMs: Long, perfect: Boolean) {
        mainHandler.post {
            onDone(GameResult(completed = true, score = score, stars = stars, durationMs = durationMs, perfect = perfect))
        }
    }
}
