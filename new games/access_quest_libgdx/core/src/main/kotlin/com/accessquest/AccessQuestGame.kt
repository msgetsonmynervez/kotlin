package com.accessquest

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Logger

/**
 * Entry point for the Access Quest game.  This class extends libGDX’s
 * [Game] and manages screen transitions.  When the game is created it
 * instantiates a [MainGameScreen] which holds all game systems.  You could add
 * additional screens (e.g. menus, loadout selection, results) later and switch
 * between them here.
 */
class AccessQuestGame : Game() {
    private val log = Logger(AccessQuestGame::class.java.name, Logger.INFO)

    override fun create() {
        log.info("Creating Access Quest game")
        setScreen(MainGameScreen(this))
    }

    override fun dispose() {
        screen?.dispose()
        super.dispose()
    }
}