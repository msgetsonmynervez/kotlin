package com.myelin.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.myelin.game.MyelinProtocolGame

class BootScreen(
    private val game: MyelinProtocolGame,
) : ScreenAdapter() {
    private var elapsed = 0f

    override fun render(delta: Float) {
        elapsed += delta

        Gdx.gl.glClearColor(0.05f, 0.06f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (elapsed >= 0.6f) {
            game.screen = GameScreen()
            dispose()
        }
    }
}
