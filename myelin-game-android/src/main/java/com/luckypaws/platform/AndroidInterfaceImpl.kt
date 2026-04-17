package com.luckypaws.platform

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import kotlin.math.sin

class AndroidInterfaceImpl(
    private val activity: Activity,
) : NativePlatformInterface, TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(activity, this)
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
    private val vibrator: Vibrator = activity.getSystemService(Vibrator::class.java)
    private val toneGenerator: ToneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    private var ttsReady: Boolean = false

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onResults(results: Bundle?) = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onError(error: Int) {
                Log.e("AndroidInterfaceImpl", "SpeechRecognizer error: $error")
            }
        })
    }

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

    override fun speak(text: String, flush: Boolean) {
        if (!ttsReady) {
            Log.w("AndroidInterfaceImpl", "TTS not ready: ignoring speak request")
            return
        }

        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts.speak(text, queueMode, null, "LuckyPawsUtterance")
    }

    override fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
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

    override fun stopListening() {
        activity.runOnUiThread {
            try {
                speechRecognizer.stopListening()
            } catch (e: Exception) {
                Log.e("AndroidInterfaceImpl", "stopListening error", e)
            }
        }
    }

    override fun vibrateHaptic(type: String) {
        val effect = when (type.lowercase()) {
            "success" -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100, 50, 100), -1)
            "failure" -> VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            "tick" -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            "wall_collision" -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 200, 50, 100), -1)
            else -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    override fun playSystemTone(type: String) {
        val toneType = when (type.lowercase()) {
            "beep" -> ToneGenerator.TONE_PROP_BEEP
            "nack" -> ToneGenerator.TONE_PROP_NACK
            "ack" -> ToneGenerator.TONE_PROP_ACK
            else -> ToneGenerator.TONE_PROP_BEEP
        }
        toneGenerator.startTone(toneType, 200)
    }

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
            AudioManager.AUDIO_SESSION_ID_GENERATE,
        )
        track.write(buffer, 0, buffer.size)
        track.play()
        try {
            Thread.sleep(durationMs.toLong())
        } catch (_: InterruptedException) {
        }
        track.stop()
        track.release()
    }

    fun setRecognitionListener(listener: RecognitionListener) {
        speechRecognizer.setRecognitionListener(listener)
    }

    fun dispose() {
        tts.stop()
        tts.shutdown()
        speechRecognizer.destroy()
        toneGenerator.release()
    }
}
