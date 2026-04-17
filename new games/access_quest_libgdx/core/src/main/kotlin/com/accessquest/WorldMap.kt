package com.accessquest

/**
 * Represents the branching world map.  Each node corresponds to a level and
 * contains the level name, scene identifier (for multiple screens), the item
 * rewarded on completion and the indices of the next nodes.  Completing a node
 * unlocks its next nodes and rewards the item via a GameManager (not yet
 * implemented in this skeleton).
 */
class WorldMap {
    data class MapNode(
        val levelName: String,
        val sceneName: String,
        val rewardItem: ItemType,
        val nextNodes: List<Int>,
        var unlocked: Boolean = false
    )

    val nodes: MutableList<MapNode> = mutableListOf()

    fun unlockNode(index: Int) {
        if (index !in nodes.indices) return
        nodes[index].unlocked = true
        // Reward the item here via GameManager if implemented
    }

    fun onLevelCompleted(sceneName: String) {
        val index = nodes.indexOfFirst { it.sceneName == sceneName }
        if (index >= 0) {
            // Unlock connected nodes
            val node = nodes[index]
            node.nextNodes.forEach { next -> if (next in nodes.indices) nodes[next].unlocked = true }
        }
    }
}