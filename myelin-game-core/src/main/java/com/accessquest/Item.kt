package com.accessquest

/**
 * Base class for all accessibility tools.  Items can modify player speed,
 * fatigue or heat accumulation or provide special abilities.  Derived items
 * override [onEquip] and [onUnequip] to apply and revert their effects.
 */
abstract class Item(
    val itemName: String,
    val description: String
) {
    open fun onEquip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {}
    open fun onUnequip(player: Player, fatigueSystem: FatigueSystem, heatSystem: HeatSystem) {}
}

/**
 * Enum of item types.  Used to index into the GameManager's unlocked array or
 * select items from the world map.
 */
enum class ItemType {
    CANE,
    SUPPORTIVE_SHOES,
    COOLING_VEST,
    MOBILITY_SCOOTER,
    REACHER,
    ACCESS_ASSIST,
    RECOVERY_SUPPORT,
}