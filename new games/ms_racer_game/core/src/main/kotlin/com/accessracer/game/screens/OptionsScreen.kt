package com.accessracer.game.screens

import com.accessracer.game.AccessRacerGame
import com.accessracer.game.GameSettings
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * Options screen allowing players to modify accessibility settings such
 * as game speed, steering sensitivity and auto‑acceleration.  Changes
 * are persisted via [GameSettings].
 */
class OptionsScreen(private val game: AccessRacerGame) : ScreenAdapter() {
    private val stage: Stage = Stage(ExtendViewport(800f, 480f), game.batch)

    init {
        val table = Table().apply {
            setFillParent(true)
            align(Align.center)
        }

        val title = Label("Options", game.skin).apply { setFontScale(2f) }
        table.add(title).padBottom(30f).row()

        // Game speed slider
        val speedLabel = Label("Game Speed", game.skin).apply { setFontScale(1.2f) }
        table.add(speedLabel).left().padBottom(10f)
        val speedSlider = Slider(0.5f, 1.5f, 0.1f, false, game.skin).apply {
            value = GameSettings.gameSpeed
        }
        speedSlider.addListener { _ ->
            GameSettings.gameSpeed = speedSlider.value
            true
        }
        table.add(speedSlider).width(400f).padBottom(20f).row()

        // Steering sensitivity slider
        val steerLabel = Label("Steering Sensitivity", game.skin).apply { setFontScale(1.2f) }
        table.add(steerLabel).left().padBottom(10f)
        val steerSlider = Slider(0.5f, 1.5f, 0.1f, false, game.skin).apply {
            value = GameSettings.steeringSensitivity
        }
        steerSlider.addListener { _ ->
            GameSettings.steeringSensitivity = steerSlider.value
            true
        }
        table.add(steerSlider).width(400f).padBottom(20f).row()

        // Auto acceleration toggle button
        val autoButton = TextButton(autoLabel(GameSettings.autoAccelerate), game.skin).apply {
            label.setFontScale(1.2f)
        }
        autoButton.addListener { _ ->
            // Toggle the setting and update label
            GameSettings.autoAccelerate = !GameSettings.autoAccelerate
            autoButton.setText(autoLabel(GameSettings.autoAccelerate))
            true
        }
        table.add(autoButton).colspan(2).padBottom(30f).row()

        // Back button
        val backButton = TextButton("Back", game.skin).apply {
            label.setFontScale(1.5f)
        }
        backButton.addListener { _ ->
            game.screen = MainMenuScreen(game)
            true
        }
        table.add(backButton).colspan(2)

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

    private fun autoLabel(enabled: Boolean) = if (enabled) "Auto‑Accelerate: On" else "Auto‑Accelerate: Off"
}