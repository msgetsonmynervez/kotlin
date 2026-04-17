package com.accessquest

/**
 * Manages the player's loadout.  The player may equip up to three items: a
 * mobility tool, a condition tool and a utility tool.  Each item modifies
 * player speed, fatigue or heat systems when equipped.  Replacing an item in a
 * slot will unequip the existing item before applying the new one.
 */
class ItemSystem(
    private val player: Player,
    private val fatigueSystem: FatigueSystem,
    private val heatSystem: HeatSystem
) {
    private val equippedItems: Array<Item?> = arrayOfNulls(3)

    /**
     * Equip [item] into [slotIndex] (0 = mobility, 1 = condition, 2 = utility).  If
     * there is already an item in that slot it will be unequipped first.  Passing
     * `null` will simply clear the slot.
     */
    fun equip(item: Item?, slotIndex: Int) {
        if (slotIndex < 0 || slotIndex >= equippedItems.size) return
        val current = equippedItems[slotIndex]
        current?.onUnequip(player, fatigueSystem, heatSystem)
        equippedItems[slotIndex] = item
        item?.onEquip(player, fatigueSystem, heatSystem)
    }
}