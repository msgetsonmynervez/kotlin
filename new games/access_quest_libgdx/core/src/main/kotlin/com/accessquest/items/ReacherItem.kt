package com.accessquest.items

import com.accessquest.FatigueSystem
import com.accessquest.HeatSystem
import com.accessquest.Item
import com.accessquest.Player

/**
 * Reacher tool enables the player to activate switches or collect pickups at a
 * distance.  In this prototype it does not modify fatigue or heat; future
 * expansions can use this item in specific interactions within levels.
 */
class ReacherItem : Item("Reacher", "Allows reaching distant switches and pickups.") {
    override fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        // No passive effects
    }

    override fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {
        // No passive effects
    }
}