package com.luckypaws.platform

interface NativePlatformInterface {
    fun speak(text: String, flush: Boolean)

    fun startListening()

    fun stopListening()

    fun vibrateHaptic(type: String)

    fun playSystemTone(type: String)

    fun playProceduralTone(frequency: Double, durationMs: Int)
}
