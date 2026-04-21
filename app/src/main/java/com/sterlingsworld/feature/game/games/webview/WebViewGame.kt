package com.sterlingsworld.feature.game.games.webview

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.feature.error.TechnicalDifficultiesScreen
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Loads an HTML game from local assets and embeds it in the game shell.
 *
 * Security contract: only trusted local asset games under file:///android_asset/games/
 * are permitted to load. Remote navigation and cross-origin file access are blocked.
 * The Android JS bridge is intentionally minimal — only onGameComplete is exposed.
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
    var hasLoadError by remember(assetFolder) { mutableStateOf(false) }
    var bridge: GameBridge? = null
    if (hasLoadError) {
        TechnicalDifficultiesScreen()
        return
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                val assetPath = "games/$assetFolder/index.html"
                val assetUrl = "file:///android_asset/$assetPath"
                val assetExists = runCatching {
                    context.assets.open(assetPath).close()
                }.isSuccess
                if (!assetExists) {
                    Log.e("WebViewGame", "Missing local game asset: $assetPath")
                    hasLoadError = true
                    return@apply
                }

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true          // required by some asset games for local state
                settings.allowFileAccess = true            // needed to load the asset HTML page itself
                settings.allowContentAccess = false        // no content:// URIs needed
                settings.allowFileAccessFromFileURLs = false
                settings.allowUniversalAccessFromFileURLs = false
                settings.mediaPlaybackRequiresUserGesture = true

                // Block all navigation that is not a local asset within our games directory.
                webViewClient = object : WebViewClient() {
                    private val gamesAssetPrefix = "/android_asset/games/"

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        val uri = request.url
                        val isPermitted = uri.scheme == "file" &&
                            uri.path?.startsWith(gamesAssetPrefix) == true
                        return !isPermitted
                    }

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: WebResourceError,
                    ) {
                        super.onReceivedError(view, request, error)
                        if (request.isForMainFrame) {
                            Log.e(
                                "WebViewGame",
                                "Main frame load error for $assetUrl: ${error.description}",
                            )
                            hasLoadError = true
                        }
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        Log.d(
                            "WebViewGame",
                            "[${consoleMessage.messageLevel()}] ${consoleMessage.sourceId()}:${consoleMessage.lineNumber()} ${consoleMessage.message()}",
                        )
                        return super.onConsoleMessage(consoleMessage)
                    }
                }

                // Only trusted local game assets may call methods on this bridge.
                bridge = GameBridge(context, onDone)
                addJavascriptInterface(bridge!!, "Android")
                loadUrl(assetUrl)
            }
        },
        onRelease = { webView ->
            webView.removeJavascriptInterface("Android")
            bridge?.release()
            bridge = null
            webView.destroy()
        },
    )
}

private class GameBridge(
    context: Context,
    private val onDone: (GameResult) -> Unit,
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val completed = AtomicBoolean(false)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 55)

    @JavascriptInterface
    fun onGameComplete(score: Int, stars: Int, durationMs: Long, perfect: Boolean) {
        // Guard against duplicate calls from the same game session.
        if (!completed.compareAndSet(false, true)) return
        mainHandler.post {
            onDone(GameResult(completed = true, score = score, stars = stars, durationMs = durationMs, perfect = perfect))
        }
    }

    @JavascriptInterface
    fun vibrate(durationMs: Long) {
        val duration = durationMs.coerceIn(1L, 250L)
        val targetVibrator = vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            targetVibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            targetVibrator.vibrate(duration)
        }
    }

    @JavascriptInterface
    fun playArcadeSound(name: String) {
        when (name.lowercase()) {
            "clear" -> toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 110)
        }
    }

    @JavascriptInterface
    fun playSound(name: String) {
        when (name.lowercase()) {
            "blip" -> toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 55)
            "crash" -> toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 95)
            "levelup" -> toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 180)
            else -> playArcadeSound(name)
        }
    }

    fun release() {
        toneGenerator.release()
    }
}
