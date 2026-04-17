package com.myelin.game

import com.badlogic.gdx.Game
import com.myelin.game.screen.BootScreen

class MyelinProtocolGame : Game() {

    override fun create() {
        setScreen(BootScreen(this))
    }
}
