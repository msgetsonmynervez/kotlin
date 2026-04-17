package com.accessquest.items

import com.accessquest.FatigueSystem
import com.accessquest.HeatSystem
import com.accessquest.Item
import com.accessquest.Player

/**
 * Shoes that improve traction and reduce fatigue accumulation.  Applies a
 * multiplier to the fatigue per unit when equipped.
 */
class SupportiveShoesItem(
    private val fatigueMultiplier: Float = 0.6f
) : Item("Supportive Shoes", "Lowers fatigue on long routes and improves traction.") {
    private var originalFatiguePerUnit: Float = 0f

    override fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        originalFatiguePerUnit = fatigueSystem.fatiguePerUnit
        fatigueSystem.fatiguePerUnit *= fatigueMultiplier
    }

    override fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        fatigueSystem.fatiguePerUnit = originalFatiguePerUnit
    }
}