package com.accessracer.game.screens

import com.accessracer.game.AccessRacerGame
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * Main menu screen shown when the game starts.  Presents options to
 * start a race, open the options menu or exit the application.  Uses
 * a high‑contrast skin and large buttons for accessibility.
 */
class MainMenuScreen(private val game: AccessRacerGame) : ScreenAdapter() {
    private val stage: Stage = Stage(ExtendViewport(800f, 480f), game.batch)

    init {
        // Build UI using a table for easy layout
        val table = Table()
        table.setFillParent(true)
        table.align(Align.center)

        val title = Label("Access Racer", game.skin).apply {
            setFontScale(2f)
        }
        table.add(title).padBottom(40f).row()

        // Start Race button
        val startButton = TextButton("Start Race", game.skin).apply {
            label.setFontScale(1.5f)
        }
        startButton.addListener { _ ->
            game.screen = VehicleSelectScreen(game)
            true
        }
        table.add(startButton).fillX().uniform().padBottom(20f).row()

        // Options button
        val optionsButton = TextButton("Options", game.skin).apply {
            label.setFontScale(1.5f)
        }
        optionsButton.addListener { _ ->
            game.screen = OptionsScreen(game)
            true
        }
        table.add(optionsButton).fillX().uniform().padBottom(20f).row()

        // Exit button (only relevant on desktop; Android will ignore)
        val exitButton = TextButton("Exit", game.skin).apply {
            label.setFontScale(1.5f)
        }
        exitButton.addListener { _ ->
            Gdx.app.exit()
            true
        }
        table.add(exitButton).fillX().uniform()

        stage.addActor(table)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }
}