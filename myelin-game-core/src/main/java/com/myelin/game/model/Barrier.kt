package com.myelin.game.model

import com.badlogic.gdx.math.Rectangle

data class Barrier(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val maxIntegrity: Float,
    var integrity: Float = maxIntegrity,
) {
    fun bounds(): Rectangle = Rectangle(x, y, width, height)

    fun damage(amount: Float) {
        integrity = (integrity - amount).coerceAtLeast(0f)
    }

    fun repair(amount: Float) {
        integrity = (integrity + amount).coerceAtMost(maxIntegrity)
    }

    fun ratio(): Float = if (maxIntegrity == 0f) 0f else integrity / maxIntegrity

    val isDestroyed: Boolean
        get() = integrity <= 0f

    val isDamaged: Boolean
        get() = integrity < maxIntegrity
}
