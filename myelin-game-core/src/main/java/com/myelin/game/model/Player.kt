package com.myelin.game.model

import com.badlogic.gdx.math.Rectangle

data class Player(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
) {
    fun bounds(): Rectangle = Rectangle(x, y, width, height)
}
