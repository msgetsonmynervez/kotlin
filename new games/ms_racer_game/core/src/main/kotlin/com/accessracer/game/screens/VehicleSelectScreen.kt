package com.accessracer.game.screens

import com.accessracer.game.AccessRacerGame
import com.accessracer.game.VehicleType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * Screen allowing the player to choose a mobility device before starting
 * the race.  Two large buttons represent the available options.
 */
class VehicleSelectScreen(private val game: AccessRacerGame) : ScreenAdapter() {
    private val stage: Stage = Stage(ExtendViewport(800f, 480f), game.batch)

    init {
        val table = Table().apply {
            setFillParent(true)
            align(Align.center)
        }

        val title = Label("Choose your ride", game.skin).apply {
            setFontScale(1.8f)
        }
        table.add(title).padBottom(30f).row()

        // Create a button for each vehicle type
        for (vehicle in VehicleType.values()) {
            val button = TextButton(vehicle.displayName, game.skin).apply {
                label.setFontScale(1.3f)
                // Tint the button using the vehicle colour for quick association
                color = Color(vehicle.color)
            }
            button.addListener { _ ->
                game.screen = RaceScreen(game, vehicle)
                true
            }
            table.add(button).fillX().uniform().padBottom(20f).row()
        }

        // Back button
        val backButton = TextButton("Back", game.skin).apply {
            label.setFontScale(1.3f)
        }
        backButton.addListener { _ ->
            game.screen = MainMenuScreen(game)
            true
        }
        table.add(backButton).fillX().uniform()

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