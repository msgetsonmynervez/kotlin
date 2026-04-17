package com.myelin.game.system

class StabilitySystem(
    private val maxStability: Float = 100f,
) {
    var stability: Float = maxStability
        private set

    val isDepleted: Boolean
        get() = stability <= 0f

    fun applyLeak(amount: Float) {
        stability = (stability - amount).coerceAtLeast(0f)
    }

    fun restoreFull() {
        stability = maxStability
    }

    fun ratio(): Float = if (maxStability == 0f) 0f else stability / maxStability
}
