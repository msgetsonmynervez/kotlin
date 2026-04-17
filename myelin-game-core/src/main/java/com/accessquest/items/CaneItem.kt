package com.accessquest.items

import com.accessquest.FatigueSystem
import com.accessquest.HeatSystem
import com.accessquest.Item
import com.accessquest.Player

/**
 * Early mobility tool that reduces fatigue penalties on unstable terrain.  This
 * item multiplies the fatigue per unit distance by [fatigueMultiplier] while
 * equipped.
 */
class CaneItem(
    private val fatigueMultiplier: Float = 0.7f
) : Item("Cane", "Reduces fatigue accumulation on rough terrain.") {
    private var originalFatiguePerUnit: Float = 0f

    override fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        originalFatiguePerUnit = fatigueSystem.fatiguePerUnit
        fatigueSystem.fatiguePerUnit *= fatigueMultiplier
    }

    override fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        fatigueSystem.fatiguePerUnit = originalFatiguePerUnit
    }
}