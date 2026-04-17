package com.accessquest

import com.badlogic.gdx.math.Vector2

/**
 * Stores the last checkpoint position and handles respawn logic.  When the
 * player reaches a checkpoint, the current checkpoint position is updated and
 * some fatigue and heat are recovered.  When fatigue or heat overflow, the
 * player is reset to this checkpoint.
 */
class CheckpointManager(
    private val player: Player,
    private val fatigueSystem: FatigueSystem,
    private val heatSystem: HeatSystem
) {
    val currentCheckpoint: Vector2 = Vector2()

    var fatigueRecoverAmount: Float = 50f
    var heatRecoverAmount: Float = 50f

    fun activateCheckpoint(position: Vector2) {
        currentCheckpoint.set(position)
        fatigueSystem.recoverFatigue(fatigueRecoverAmount)
        heatSystem.recoverHeat(heatRecoverAmount)
    }

    fun returnToCheckpoint() {
        player.respawn(currentCheckpoint)
        fatigueSystem.recoverFatigue(fatigueRecoverAmount)
        heatSystem.recoverHeat(heatRecoverAmount)
    }
}