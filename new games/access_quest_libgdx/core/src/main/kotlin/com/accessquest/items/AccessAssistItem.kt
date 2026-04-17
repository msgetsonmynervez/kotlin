package com.accessquest.items

import com.accessquest.FatigueSystem
import com.accessquest.HeatSystem
import com.accessquest.Item
import com.accessquest.Player

/**
 * Access Assist tool opens lower‑strain indoor routes or bypasses heavy entries.
 * In this skeleton it has no passive effect; logic should be implemented in
 * level scripts and hazard triggers.
 */
class AccessAssistItem : Item("Access Assist", "Opens alternate indoor routes.") {
    override fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {}
    override fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {}
}