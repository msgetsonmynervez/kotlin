package com.accessquest

/**
 * Manages heat accumulation.  Heat increases while moving or when inside a
 * heat zone.  When [currentHeat] exceeds [maxHeat], the player is reset to
 * the last checkpoint and some heat is retained.  Items like the cooling vest
 * can modify [heatPerUnit] and [heatPerSecond].
 */
class HeatSystem(
    var maxHeat: Float = 100f,
    var heatPerUnit: Float = 0.5f,
    var heatPerSecond: Float = 10f,
    var checkpointManager: CheckpointManager? = null
) {
    var currentHeat: Float = 0f
        private set
    private var inHeatZone: Boolean = false

    fun addHeat(amount: Float) {
        currentHeat += amount
        if (currentHeat >= maxHeat) {
            overflow()
        }
    }

    fun recoverHeat(amount: Float) {
        currentHeat = (currentHeat - amount).coerceAtLeast(0f)
    }

    /**
     * Called each frame to update passive heat accumulation when the player is
     * inside a heat zone.
     */
    fun update(delta: Float) {
        if (inHeatZone) {
            addHeat(heatPerSecond * delta)
        }
    }

    fun setInHeatZone(inZone: Boolean) {
        inHeatZone = inZone
    }

    private fun overflow() {
        checkpointManager?.returnToCheckpoint()
        currentHeat = maxHeat * 0.5f
    }
}