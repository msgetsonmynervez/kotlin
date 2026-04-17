package com.accessquest.items

import com.accessquest.FatigueSystem
import com.accessquest.HeatSystem
import com.accessquest.Item
import com.accessquest.Player

/**
 * Emergency buffer item that increases the maximum fatigue and heat capacity.
 */
class RecoverySupportItem(
    private val fatigueMaxMultiplier: Float = 1.5f,
    private val heatMaxMultiplier: Float = 1.5f
) : Item("Recovery Support", "Increases fatigue and heat thresholds for longer pushes.") {
    private var originalMaxFatigue: Float = 0f
    private var originalMaxHeat: Float = 0f

    override fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        originalMaxFatigue = fatigueSystem.maxFatigue
        originalMaxHeat = heatSystem.maxHeat
        fatigueSystem.maxFatigue *= fatigueMaxMultiplier
        heatSystem.maxHeat *= heatMaxMultiplier
    }

    override fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        fatigueSystem.maxFatigue = originalMaxFatigue
        heatSystem.maxHeat = originalMaxHeat
    }
}