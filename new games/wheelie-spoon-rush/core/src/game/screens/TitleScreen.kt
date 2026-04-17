package game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import game.GameApp
import game.ui.BasicSkin
import game.config.Constants
import game.audio.AudioManager
import game.save.SaveManager

/**
 * The initial screen displayed when the game launches.  Presents the game
 * title and two buttons: Start and Settings.  If the cosmetic reward has
 * been unlocked the title changes colour to celebrate the achievement.
 */
class TitleScreen(private val game: GameApp) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera)
    private val stage: Stage = Stage(viewport, game.batch)
    private val skin = BasicSkin.create()

    override fun show() {
        Gdx.input.inputProcessor = stage
        // Build UI
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val title = Label("Wheelie Spoon Rush", skin)
        title.setFontScale(1.5f)
        // Celebrate reward unlock by tinting the title
        if (SaveManager.isRewardUnlocked()) {
            title.color.a = 0.8f // subtle transparency
            title.color.r = 0.9f
            title.color.g = 0.7f
            title.color.b = 1f
        }

        val startButton = TextButton("Start", skin)
        val settingsButton = TextButton("Settings", skin)

        startButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                AudioManager.playClick()
                game.screen = GameScreen(game)
                dispose()
            }
        })
        settingsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                AudioManager.playClick()
                game.screen = SettingsScreen(game)
                dispose()
            }
        })

        table.add(title).padBottom(20f).row()
        table.add(startButton).width(160f).height(50f).padBottom(10f).row()
        table.add(settingsButton).width(160f).height(50f)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(Constants.COLOR_BACKGROUND.r, Constants.COLOR_BACKGROUND.g, Constants.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}