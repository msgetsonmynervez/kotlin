package game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import game.GameApp
import game.config.Constants
import game.save.SaveManager
import game.audio.AudioManager
import game.ui.BasicSkin

/**
 * Presents adjustable settings such as sound effect volume and a stub for
 * future music support.  Values are persisted via [SaveManager] and will be
 * remembered between game sessions.  Includes a back button to return to
 * the title screen.
 */
class SettingsScreen(private val game: GameApp) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera)
    private val stage: Stage = Stage(viewport, game.batch)
    private val skin: Skin = BasicSkin.create()

    override fun show() {
        Gdx.input.inputProcessor = stage
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        // Slider style built at runtime.  Knob and background are tinted rectangles.
        val sliderSkin = Skin()
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(0.6f, 0.6f, 0.8f, 1f)
        pixmap.fill()
        val background = Texture(pixmap)
        pixmap.setColor(0.3f, 0.5f, 0.9f, 1f)
        pixmap.fill()
        val knob = Texture(pixmap)
        pixmap.dispose()
        val sliderStyle = SliderStyle(
            TextureRegionDrawable(background),
            TextureRegionDrawable(knob)
        )
        sliderStyle.knobBefore = TextureRegionDrawable(background)

        val volumeLabel = Label("SFX Volume", skin)
        val volumeSlider = Slider(0f, 1f, 0.01f, false, sliderSkin)
        volumeSlider.value = SaveManager.getSfxVolume()
        volumeSlider.setSize(200f, 20f)
        volumeSlider.addListener { event ->
            SaveManager.setSfxVolume(volumeSlider.value)
            false
        }

        val musicLabel = Label("Music", skin)
        val musicButton = TextButton(if (SaveManager.isMusicEnabled()) "On" else "Off", skin)
        musicButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val newState = !SaveManager.isMusicEnabled()
                SaveManager.setMusicEnabled(newState)
                musicButton.setText(if (newState) "On" else "Off")
                AudioManager.playClick()
            }
        })

        val backButton = TextButton("Back", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                AudioManager.playClick()
                game.screen = TitleScreen(game)
                dispose()
            }
        })

        table.add(Label("Settings", skin)).padBottom(20f).row()
        table.add(volumeLabel).left().padBottom(5f)
        table.add(volumeSlider).width(180f).padBottom(5f).row()
        table.add(musicLabel).left().padBottom(20f)
        table.add(musicButton).width(100f).padBottom(20f).row()
        table.add(backButton).colspan(2).width(150f).height(50f)
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