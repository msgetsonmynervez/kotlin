package game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.viewport.FitViewport
import game.GameApp
import game.config.Constants
import game.save.SaveManager
import game.audio.AudioManager
import game.ui.BasicSkin

/**
 * Screen shown after the player finishes a level.  Presents a summary of
 * collectibles gathered, secret spoons found, the computed score, and
 * whether new records have been achieved.  Allows the player to replay the
 * level or return to the title screen.
 */
class ResultsScreen(
    private val game: GameApp,
    private val collected: Int,
    private val secrets: Int,
    private val score: Int
) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera)
    private val stage = Stage(viewport, game.batch)
    private val skin = BasicSkin.create()

    override fun show() {
        Gdx.input.inputProcessor = stage
        val table = Table()
        table.setFillParent(true)
        table.center()
        stage.addActor(table)

        val title = Label("Level Complete!", skin)
        title.setFontScale(1.3f)
        val collectedLabel = Label("Collected: $collected", skin)
        val secretLabel = Label("Secrets: $secrets/${Constants.SECRET_SPOON_TOTAL}", skin)
        val scoreLabel = Label("Score: $score", skin)
        // Determine if this run set a new record
        val bestScore = SaveManager.getBestScore()
        val bestSecrets = SaveManager.getBestSecrets()
        val newRecord = score > bestScore || secrets > bestSecrets
        val recordLabel = Label(if (newRecord) "New Record!" else "Best: $bestScore", skin)
        // Reward unlocked message
        val rewardLabel = if (SaveManager.isRewardUnlocked() && secrets >= Constants.SECRET_SPOON_TOTAL) {
            Label("Reward Unlocked!", skin)
        } else null

        val retryButton = TextButton("Replay", skin)
        val menuButton = TextButton("Menu", skin)
        retryButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                AudioManager.playClick()
                game.platform.vibrateHaptic("tick")
                game.screen = GameScreen(game)
                dispose()
            }
        })
        menuButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                AudioManager.playClick()
                game.platform.vibrateHaptic("tick")
                game.screen = TitleScreen(game)
                dispose()
            }
        })

        table.add(title).padBottom(20f).row()
        table.add(collectedLabel).padBottom(5f).row()
        table.add(secretLabel).padBottom(5f).row()
        table.add(scoreLabel).padBottom(5f).row()
        table.add(recordLabel).padBottom(15f).row()
        if (rewardLabel != null) {
            table.add(rewardLabel).padBottom(15f).row()
        }
        table.add(retryButton).width(200f).height(50f).padBottom(10f).row()
        table.add(menuButton).width(200f).height(50f)
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
