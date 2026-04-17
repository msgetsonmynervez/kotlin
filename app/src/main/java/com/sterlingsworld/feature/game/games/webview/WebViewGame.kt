package com.sterlingsworld.feature.game.games.webview

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
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
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.allowFileAccessFromFileURLs = false
                settings.allowUniversalAccessFromFileURLs = false
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean = request.url.scheme != "file"
                }
                addJavascriptInterface(GameBridge(onDone), "Android")
                loadUrl("file:///android_asset/games/$assetFolder/index.html")
            }
        },
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
