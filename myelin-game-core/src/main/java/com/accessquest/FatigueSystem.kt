package com.accessquest

/**
 * Tracks and manages the player's fatigue.  Fatigue increases with movement
 * (see [Player.update]) and certain hazards.  When fatigue exceeds
 * [maxFatigue] the player is reset to the last checkpoint via
 * [CheckpointManager.returnToCheckpoint].  Upon reset, some fatigue is retained
 * to discourage reckless play but avoid punishing the player with a full fail
 * state.
 */
class FatigueSystem(
    var maxFatigue: Float = 100f,
    var fatiguePerUnit: Float = 1f,
    var checkpointManager: CheckpointManager? = null
) {
    var currentFatigue: Float = 0f
        private set

    fun addFatigue(distance: Float) {
        currentFatigue += distance * fatiguePerUnit
        if (currentFatigue >= maxFatigue) {
            overflow()
        }
    }

    fun recoverFatigue(amount: Float) {
        currentFatigue = (currentFatigue - amount).coerceAtLeast(0f)
    }

    private fun overflow() {
        // Reset player to checkpoint and retain some fatigue
        checkpointManager?.returnToCheckpoint()
        currentFatigue = maxFatigue * 0.5f
    }
}