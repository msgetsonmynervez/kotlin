package com.myelin.game.model

import com.badlogic.gdx.math.Rectangle

data class EnemyShot(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val speed: Float,
) {
    fun bounds(): Rectangle = Rectangle(x, y, width, height)
}
