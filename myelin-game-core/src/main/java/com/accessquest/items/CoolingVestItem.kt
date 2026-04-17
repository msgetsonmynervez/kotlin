package com.accessquest.items

import com.accessquest.FatigueSystem
import com.accessquest.HeatSystem
import com.accessquest.Item
import com.accessquest.Player

/**
 * Cooling vest slows heat buildup by reducing heat per unit distance and heat
 * per second while in a heat zone.
 */
class CoolingVestItem(
    private val heatPerUnitMultiplier: Float = 0.5f,
    private val heatPerSecondMultiplier: Float = 0.5f
) : Item("Cooling Vest", "Slows heat buildup, enabling traversal of hot zones.") {
    private var originalHeatPerUnit: Float = 0f
    private var originalHeatPerSecond: Float = 0f

    override fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        originalHeatPerUnit = heatSystem.heatPerUnit
        originalHeatPerSecond = heatSystem.heatPerSecond
        heatSystem.heatPerUnit *= heatPerUnitMultiplier
        heatSystem.heatPerSecond *= heatPerSecondMultiplier
    }

    override fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        heatSystem.heatPerUnit = originalHeatPerUnit
        heatSystem.heatPerSecond = originalHeatPerSecond
    }
}