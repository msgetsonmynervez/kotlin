package com.sterlingsworld.core.platform

/**
 * Defines the operations that the LibGDX core game can invoke on the native
 * Android layer. Because LibGDX cannot access Android-specific hardware or
 * services directly from its core module, this interface acts as a bridge.
 *
 * Each method corresponds to a specific system capability required by the
 * "Lucky Paws" application. Implementations must ensure that no external
 * audio assets are used and that all processing remains local to the device.
 */
interface NativePlatformInterface {
    /**
     * Use the system Text‑to‑Speech engine to speak arbitrary text. The
     * `flush` parameter controls whether any currently queued utterances
     * should be cancelled before speaking the new text.
     */
    fun speak(text: String, flush: Boolean)

    /**
     * Begin listening for voice input using the SpeechRecognizer API. This
     * should be invoked on the UI thread to ensure proper interaction with
     * Android components.
     */
    fun startListening()

    /**
     * Stop listening for voice input. This call should cancel any active
     * recognition session and free related resources.
     */
    fun stopListening()

    /**
     * Trigger a haptic feedback pattern. The `type` string identifies
     * preconfigured patterns such as "success", "failure", "tick", or
     * "wall_collision". Implementations should interpret this string
     * deterministically to produce the appropriate vibration effect.
     */
    fun vibrateHaptic(type: String)

    /**
     * Play a predefined system tone using the ToneGenerator API. Valid values
     * for `type` might include "beep", "ack", or "nack". Unknown values
     * should fall back to a neutral beep.
     */
    fun playSystemTone(type: String)

    /**
     * Generate a procedural tone on the fly without relying on external
     * resources. The tone is a sine wave at the provided frequency and
     * lasts for the specified number of milliseconds.
     */
    fun playProceduralTone(frequency: Double, durationMs: Int)
}
