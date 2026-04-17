package game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import game.GameApp
import game.assets.GameAssets
import game.level.GameWorld
import game.config.Constants
import game.audio.AudioManager
import game.save.SaveManager
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import game.ui.BasicSkin

/**
 * Main gameplay screen.  Manages the game world, HUD, pause flow and
 * transitions to the results screen once the level is complete.  Auto‑scroll
 * and input handling live here.
 */
class GameScreen(private val game: GameApp) : ScreenAdapter() {
    // Game world objects
    private val world = GameWorld()

    // Camera and viewport for rendering the world
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera)

    // HUD stage for UI elements (scores, progress, pause button)
    private val hudStage: Stage = Stage(FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), game.batch)
    private val hudSkin = BasicSkin.create()

    // UI actors
    private lateinit var scoreLabel: Label
    private lateinit var secretLabel: Label
    private lateinit var progressBg: Image
    private lateinit var progressBar: Image
    private lateinit var pauseButton: TextButton

    // Pause state
    private var isPaused = false
    private var pauseTable: Table? = null

    override fun show() {
        // Prepare input: multiplex between game and HUD.  The HUD stage will
        // intercept UI touches before they are considered for gameplay.
        Gdx.input.inputProcessor = hudStage
        setupHud()
    }

    /** Construct the HUD elements and position them on screen. */
    private fun setupHud() {
        val table = Table()
        table.top().left()
        table.setFillParent(true)
        hudStage.addActor(table)

        scoreLabel = Label("Score: 0", hudSkin)
        secretLabel = Label("Secrets: 0/${Constants.SECRET_SPOON_TOTAL}", hudSkin)
        // Progress bar background
        progressBg = Image(GameAssets.whiteTex)
        progressBg.color = Color.DARK_GRAY
        progressBg.setSize(150f, 8f)
        // Progress bar fill
        progressBar = Image(GameAssets.whiteTex)
        progressBar.color = Color.GREEN
        progressBar.setSize(0f, 8f)
        val progressTable = Table()
        progressTable.add(progressBg).width(150f).height(8f)
        progressTable.add(progressBar).width(0f).height(8f).expandX().left()
        // Pause button
        pauseButton = TextButton("II", hudSkin)
        pauseButton.setSize(40f, 40f)
        pauseButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                togglePause()
                AudioManager.playClick()
                game.platform.vibrateHaptic("tick")
            }
        })

        table.add(scoreLabel).pad(5f)
        table.add(secretLabel).pad(5f)
        table.add(progressBar).width(150f).height(8f).pad(5f).growX()
        table.add(pauseButton).width(40f).height(40f).pad(5f)
    }

    /** Toggle between paused and unpaused states.  When paused the world stops
     * updating and a modal overlay with resume/retry/quit options appears. */
    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            // Build pause menu on demand
            if (pauseTable == null) {
                pauseTable = Table()
                pauseTable!!.setFillParent(true)
                pauseTable!!.center()
                hudStage.addActor(pauseTable)
                val resume = TextButton("Resume", hudSkin)
                val retry = TextButton("Retry", hudSkin)
                val quit = TextButton("Quit", hudSkin)
                resume.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        isPaused = false
                        pauseTable?.remove()
                        pauseTable = null
                        AudioManager.playClick()
                        game.platform.vibrateHaptic("tick")
                    }
                })
                retry.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        AudioManager.playClick()
                        game.platform.vibrateHaptic("tick")
                        game.screen = GameScreen(game)
                        dispose()
                    }
                })
                quit.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        AudioManager.playClick()
                        game.platform.vibrateHaptic("tick")
                        game.screen = TitleScreen(game)
                        dispose()
                    }
                })
                pauseTable!!.add(Label("Paused", hudSkin)).padBottom(20f).row()
                pauseTable!!.add(resume).width(200f).height(50f).padBottom(10f).row()
                pauseTable!!.add(retry).width(200f).height(50f).padBottom(10f).row()
                pauseTable!!.add(quit).width(200f).height(50f)
            }
        } else {
            // Hide pause UI
            pauseTable?.remove()
            pauseTable = null
        }
    }

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(Constants.COLOR_BACKGROUND.r, Constants.COLOR_BACKGROUND.g, Constants.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update world if not paused and not finished
        if (!isPaused && !world.finished) {
            val jumpPressed = Gdx.input.justTouched()
            world.update(delta, jumpPressed)
        }
        // Camera follows the player with a forward offset
        val playerX = world.player.position.x
        camera.position.x = playerX + 8f
        if (camera.position.x < Constants.WORLD_WIDTH / 2f) {
            camera.position.x = Constants.WORLD_WIDTH / 2f
        }
        camera.position.y = Constants.WORLD_HEIGHT / 2f
        camera.update()
        game.batch.projectionMatrix = camera.combined

        // Draw world: platforms, collectibles, enemies and player
        game.batch.begin()
        // Draw platforms
        for (platform in world.platforms) {
            // Tint the white texture with platform colour and draw it at the
            // platform's current position and size.
            game.batch.color = Constants.COLOR_PLATFORM
            game.batch.draw(GameAssets.whiteTex, platform.bounds.x, platform.bounds.y, platform.bounds.width, platform.bounds.height)
        }
        // Draw collectibles
        for (item in world.collectibles) {
            if (!item.collected) {
                val color = if (item.isSecret) Constants.COLOR_SECRET else Constants.COLOR_COLLECTIBLE
                game.batch.color = color
                game.batch.draw(GameAssets.whiteTex, item.bounds.x, item.bounds.y, item.bounds.width, item.bounds.height)
            }
        }
        // Draw enemies
        for (enemy in world.enemies) {
            game.batch.color = Constants.COLOR_ENEMY
            game.batch.draw(GameAssets.whiteTex, enemy.bounds.x, enemy.bounds.y, enemy.bounds.width, enemy.bounds.height)
        }
        // Draw player: spoon body and wheelchair accent as two rectangles
        game.batch.color = Constants.COLOR_PLAYER
        game.batch.draw(GameAssets.whiteTex, world.player.position.x, world.player.position.y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT)
        // Wheelchair accent at the bottom half of the player rectangle
        game.batch.color = Constants.COLOR_WHEELCHAIR
        game.batch.draw(GameAssets.whiteTex, world.player.position.x, world.player.position.y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT / 2f)
        // Reset batch colour for subsequent draws
        game.batch.color = Color.WHITE
        game.batch.end()

        // Update HUD values
        scoreLabel.setText("Score: ${world.player.collectedCount}")
        secretLabel.setText("Secrets: ${world.player.secretCount}/${Constants.SECRET_SPOON_TOTAL}")
        // Update progress bar width based on world progress
        val progress = world.getProgress()
        progressBar.setSize(150f * progress, 8f)

        // Draw HUD on top
        hudStage.act(delta)
        hudStage.draw()

        // If the level has finished and we are not yet paused or in results, jump to results screen
        if (world.finished) {
            // Persist best run stats
            val scoreTotal = world.player.collectedCount + world.player.secretCount * 5
            SaveManager.updateBestRun(scoreTotal, world.player.secretCount)
            // Unlock reward if all secret spoons found
            if (world.player.secretCount >= Constants.SECRET_SPOON_TOTAL) {
                SaveManager.unlockReward()
            }
            // Play finish sound once
            if (!isPaused) {
                AudioManager.playFinish()
            }
            // Transition to results screen
            game.screen = ResultsScreen(game, world.player.collectedCount, world.player.secretCount, scoreTotal)
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        hudStage.viewport.update(width, height)
    }

    override fun dispose() {
        hudStage.dispose()
    }
}
