package com.sterlingsworld.core.platform

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import kotlin.math.sin

/**
 * Android implementation of [NativePlatformInterface] that bridges LibGDX core
 * calls to real hardware and system services. This class must be constructed
 * from an Android [Activity] to access Context-dependent APIs.
 *
 * The TextToSpeech engine is configured with a lower pitch and slower rate
 * to create the "Sterling" character. Speech recognition runs on the UI
 * thread to comply with Android's threading requirements.
 */
class AndroidInterfaceImpl(private val activity: Activity) : NativePlatformInterface, TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(activity, this)
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
    private val vibrator: Vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val toneGenerator: ToneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    private var ttsReady: Boolean = false

    init {
        // Configure the speech recognizer with a no-op listener by default. Game code
        // can replace this listener via [setRecognitionListener] if desired.
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Log.e("AndroidInterfaceImpl", "SpeechRecognizer error: $error")
            }
            override fun onResults(results: android.os.Bundle?) {}
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })
    }

    /** Callback from TextToSpeech indicating readiness. */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setPitch(0.8f)
            tts.setSpeechRate(0.9f)
            ttsReady = true
        } else {
            Log.e("AndroidInterfaceImpl", "TTS initialization failed: $status")
        }
    }

    /**
     * Speaks text using TTS. If the engine isn't ready yet the call will be
     * ignored and a warning will be logged. The utterance ID is static since
     * we do not track per-call callbacks in this bridge implementation.
     */
    override fun speak(text: String, flush: Boolean) {
        if (!ttsReady) {
            Log.w("AndroidInterfaceImpl", "TTS not ready: ignoring speak request")
            return
        }
        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts.speak(text, queueMode, null, "SterlingUtterance")
    }

    /**
     * Starts listening for voice input. The recognizer is configured for
     * free‑form recognition and partial results to support continuous input.
     */
    override fun startListening() {
        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        activity.runOnUiThread {
            try {
                speechRecognizer.startListening(intent)
            } catch (e: Exception) {
                Log.e("AndroidInterfaceImpl", "startListening error", e)
            }
        }
    }

    /** Stops any active speech recognition session. */
    override fun stopListening() {
        activity.runOnUiThread {
            try {
                speechRecognizer.stopListening()
            } catch (e: Exception) {
                Log.e("AndroidInterfaceImpl", "stopListening error", e)
            }
        }
    }

    /**
     * Triggers a vibration pattern based on a string key. Patterns are
     * deterministic so game logic can specify human‑readable names.
     */
    override fun vibrateHaptic(type: String) {
        val effect: VibrationEffect = when (type.lowercase()) {
            "success" -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100, 50, 100), -1)
            "failure" -> VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            "tick" -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            "wall_collision" -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 200, 50, 100), -1)
            else -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(effect)
        }
    }

    /**
     * Plays a short tone via ToneGenerator. The duration is fixed at 200ms
     * but could be adjusted by the game logic with different type keys.
     */
    override fun playSystemTone(type: String) {
        val toneType = when (type.lowercase()) {
            "beep" -> ToneGenerator.TONE_PROP_BEEP
            "nack" -> ToneGenerator.TONE_PROP_NACK
            "ack" -> ToneGenerator.TONE_PROP_ACK
            else -> ToneGenerator.TONE_PROP_BEEP
        }
        toneGenerator.startTone(toneType, 200)
    }

    /**
     * Generates a sine wave tone procedurally and streams it through an
     * AudioTrack in static mode. This method blocks until the audio has
     * finished playing; for long tones it may be advisable to run on a
     * background thread from game logic.
     */
    override fun playProceduralTone(frequency: Double, durationMs: Int) {
        val sampleRate = 44100
        val sampleCount = (durationMs / 1000.0 * sampleRate).toInt()
        val buffer = ShortArray(sampleCount)
        for (i in buffer.indices) {
            val angle = 2.0 * Math.PI * i * frequency / sampleRate
            buffer[i] = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
        }
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val audioFormat = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val track = AudioTrack(
            audioAttributes,
            audioFormat,
            buffer.size * 2,
            AudioTrack.MODE_STATIC,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        track.write(buffer, 0, buffer.size)
        track.play()
        try {
            Thread.sleep(durationMs.toLong())
        } catch (ignored: InterruptedException) {
        }
        track.stop()
        track.release()
    }

    /**
     * Allow callers to provide their own [RecognitionListener] to process
     * results. This keeps the bridge decoupled from game logic; the game
     * can register or replace the listener as needed.
     */
    fun setRecognitionListener(listener: RecognitionListener) {
        speechRecognizer.setRecognitionListener(listener)
    }

    /**
     * Clean up resources. Should be called when the Activity is destroyed to
     * avoid leaking system services.
     */
    fun dispose() {
        tts.stop()
        tts.shutdown()
        speechRecognizer.destroy()
        toneGenerator.release()
    }
}
