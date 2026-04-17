package com.accessquest

import com.badlogic.gdx.math.Rectangle

/**
 * Represents a hazard in the level.  Hazards occupy a rectangular area and
 * modify fatigue and heat multipliers while the player is within their bounds.
 * They can also mark the area as a heat zone, causing passive heat buildup.
 */
class Hazard(
    private val rect: Rectangle,
    private val fatigueMultiplier: Float = 1f,
    private val heatMultiplier: Float = 1f,
    private val isHeatZone: Boolean = false
) {
    private var active = false
    private var originalFatiguePerUnit: Float = 0f
    private var originalHeatPerUnit: Float = 0f

    /**
     * Checks the player's position each frame and applies or removes hazard
     * effects as appropriate.
     */
    fun update(player: Player) {
        val inside = rect.contains(player.position)
        if (inside && !active) {
            enter(player)
        } else if (!inside && active) {
            exit(player)
        }
    }

    private fun enter(player: Player) {
        active = true
        // Apply fatigue multiplier
        originalFatiguePerUnit = playerFatigueSystem?.fatiguePerUnit ?: 0f
        originalHeatPerUnit = playerHeatSystem?.heatPerUnit ?: 0f
        playerFatigueSystem?.fatiguePerUnit = originalFatiguePerUnit * fatigueMultiplier
        playerHeatSystem?.heatPerUnit = originalHeatPerUnit * heatMultiplier
        // Mark heat zone
        if (isHeatZone) {
            playerHeatSystem?.setInHeatZone(true)
        }
    }

    private fun exit(player: Player) {
        active = false
        // Revert multipliers
        playerFatigueSystem?.fatiguePerUnit = originalFatiguePerUnit
        playerHeatSystem?.heatPerUnit = originalHeatPerUnit
        // Unmark heat zone
        if (isHeatZone) {
            playerHeatSystem?.setInHeatZone(false)
        }
    }

    // Hazard has to access the systems via the player or globally; you can assign these externally
    companion object {
        var playerFatigueSystem: FatigueSystem? = null
        var playerHeatSystem: HeatSystem? = null
    }
}