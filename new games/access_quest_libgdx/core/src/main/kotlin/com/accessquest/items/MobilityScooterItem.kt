package com.accessquest.items

import com.accessquest.FatigueSystem
import com.accessquest.HeatSystem
import com.accessquest.Item
import com.accessquest.Player

/**
 * Mobility scooter provides faster movement and reduces fatigue.  It multiplies
 * the player's speed and decreases the fatigue per unit distance.  In a full
 * implementation you could restrict scooter usage in indoor zones via hazard
 * triggers or level flags.
 */
class MobilityScooterItem(
    private val speedMultiplier: Float = 1.5f,
    private val fatigueMultiplier: Float = 0.5f
) : Item("Mobility Scooter", "Major mobility unlock; increases speed and lowers fatigue.") {
    private var originalSpeed: Float = 0f
    private var originalFatiguePerUnit: Float = 0f

    override fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        originalSpeed = player.speed
        player.speed *= speedMultiplier
        originalFatiguePerUnit = fatigueSystem.fatiguePerUnit
        fatigueSystem.fatiguePerUnit *= fatigueMultiplier
    }

    override fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        player.speed = originalSpeed
        fatigueSystem.fatiguePerUnit = originalFatiguePerUnit
    }
}