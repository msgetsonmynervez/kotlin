package com.accessquest

/**
 * Singleton object to manage global state such as currency and unlocked items.  It
 * mirrors the GameManager from the Unity prototype.  You may choose to
 * implement persistent storage via libGDX Preferences or your own save system.
 */
object GameManager {
    var currency: Int = 0
        private set

    // Map of unlocked item types
    private val unlockedItems: MutableMap<ItemType, Boolean> = mutableMapOf()

    init {
        // Initialise all items as locked
        ItemType.values().forEach { unlockedItems[it] = false }
    }

    fun addCurrency(amount: Int) {
        if (amount > 0) {
            currency = (currency + amount).coerceAtMost(Int.MAX_VALUE)
        }
    }

    fun unlockItem(itemType: ItemType, cost: Int = 0): Boolean {
        if (unlockedItems[itemType] == true) return false
        if (currency < cost) return false
        currency -= cost
        unlockedItems[itemType] = true
        return true
    }

    fun isItemUnlocked(itemType: ItemType): Boolean {
        return unlockedItems[itemType] == true
    }
}