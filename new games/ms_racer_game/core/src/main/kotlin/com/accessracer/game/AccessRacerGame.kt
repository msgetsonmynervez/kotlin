package com.accessracer.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.accessracer.game.ui.SkinFactory
import com.accessracer.game.screens.MainMenuScreen
import com.badlogic.gdx.scenes.scene2d.ui.Skin

/**
 * Entry point for the cross‑platform game.  Responsible for
 * initialising shared resources like [SpriteBatch], [ShapeRenderer]
 * and a simple [Skin] used across screens.  When the application
 * starts it displays the main menu.
 */
class AccessRacerGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var skin: Skin

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        skin = SkinFactory.createSkin()

        setScreen(MainMenuScreen(this))
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        shapeRenderer.dispose()
        skin.dispose()
    }
}