package com.accessracer.game.screens

import com.accessracer.game.AccessRacerGame
import com.accessracer.game.GameSettings
import com.accessracer.game.VehicleType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.graphics.OrthographicCamera

/**
 * Core gameplay screen.  Presents an oval track and a player‐controlled
 * vehicle.  The player steers left or right using large on‑screen
 * buttons and accelerates automatically or via an on‑screen throttle
 * based on the setting in [GameSettings].  Laps are counted when the
 * player crosses the finish line.
 */
class RaceScreen(private val game: AccessRacerGame, private val vehicle: VehicleType) : ScreenAdapter() {
    // World viewport and camera
    private val camera = OrthographicCamera()
    private val worldViewport = FitViewport(800f, 480f, camera)

    // UI stage for on‑screen controls and HUD
    private val uiStage = Stage(ExtendViewport(800f, 480f), game.batch)

    // Player state
    private var position = Vector2()
    private var angleDeg: Float = 0f
    private var laps = 1
    private var previousPolarAngle = 0f
    private var elapsedTime = 0f

    // Input state flags
    private var steeringLeft = false
    private var steeringRight = false
    private var accelerating = false

    // Track parameters (radii measured in world units)
    private val outerRadius = 200f
    private val innerRadius = 80f

    // HUD font
    private val font: BitmapFont = game.skin.getFont("default-font")

    init {
        // Centre player at starting line (east side of track)
        position.set(outerRadius - 10f, 0f)
        angleDeg = 90f // facing upwards along y-axis
        previousPolarAngle = computePolarAngle(position)

        // Build UI controls: steering buttons and optional throttle
        buildUI()

        // Combine world and UI stages in an InputMultiplexer to receive input
        val inputMultiplexer = InputMultiplexer()
        inputMultiplexer.addProcessor(uiStage)
        Gdx.input.inputProcessor = inputMultiplexer
    }

    private fun buildUI() {
        val root = Table().apply {
            setFillParent(true)
        }
        // Steering buttons container at bottom left
        val steeringTable = Table()
        // Left button
        val leftBtn = TextButton("\u2B05", game.skin).apply {
            label.setFontScale(2.5f)
        }
        leftBtn.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                steeringLeft = true
                game.platform.vibrateHaptic("tick")
                return true
            }
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                steeringLeft = false
            }
        })
        // Right button
        val rightBtn = TextButton("\u27A1", game.skin).apply {
            label.setFontScale(2.5f)
        }
        rightBtn.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                steeringRight = true
                game.platform.vibrateHaptic("tick")
                return true
            }
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                steeringRight = false
            }
        })
        steeringTable.add(leftBtn).size(100f, 100f).padRight(10f)
        steeringTable.add(rightBtn).size(100f, 100f)

        root.add(steeringTable).left().bottom().pad(20f)

        // Throttle button if auto‑accelerate disabled
        if (!GameSettings.autoAccelerate) {
            val throttleBtn = TextButton("GO", game.skin).apply {
                label.setFontScale(2f)
            }
            throttleBtn.addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    accelerating = true
                    game.platform.vibrateHaptic("tick")
                    return true
                }
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    accelerating = false
                }
            })
            // Place throttle button at bottom right
            root.add(throttleBtn).expandX().right().bottom().pad(20f).size(150f, 150f)
        }

        // HUD: lap and time at top left
        // Heads‑up display for lap and time.  Positioned manually in resize().
        val hudTable = Table()
        hudTable.align(Align.topLeft)
        val lapLabel = Label("Lap: $laps", game.skin)
        val timeLabel = Label("Time: 0.0s", game.skin)
        lapLabel.setFontScale(1.5f)
        timeLabel.setFontScale(1.5f)
        hudTable.add(lapLabel).left().row()
        hudTable.add(timeLabel).left()
        // Add HUD after root so it appears on top; we set its position in resize()
        uiStage.addActor(root)
        uiStage.addActor(hudTable)

        // Store references for updating each frame
        this.lapLabel = lapLabel
        this.timeLabel = timeLabel

        // Save HUD table reference to position it during resize
        this.hudTable = hudTable
    }

    // References to HUD labels for updating
    private lateinit var lapLabel: Label
    private lateinit var timeLabel: Label

    // Reference to HUD table for positioning on resize
    private lateinit var hudTable: Table

    override fun render(delta: Float) {
        // Update game logic
        update(delta)

        // Clear screen
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera
        worldViewport.apply()
        camera.update()

        // Draw track and player
        drawWorld()

        // Draw UI
        uiStage.viewport.apply()
        uiStage.act(delta)
        uiStage.draw()

        // Update HUD text
        lapLabel.setText("Lap: $laps")
        timeLabel.setText(String.format("Time: %.1fs", elapsedTime))
    }

    private fun update(delta: Float) {
        // Update elapsed time
        elapsedTime += delta

        // Determine speed
        val baseSpeed = 80f
        val speed = baseSpeed * GameSettings.gameSpeed *
            if (GameSettings.autoAccelerate) 1f else if (accelerating) 1f else 0f

        // Determine steering
        var turnDirection = 0f
        if (steeringLeft) turnDirection += 1f
        if (steeringRight) turnDirection -= 1f

        // Update angle based on steering sensitivity
        angleDeg += turnDirection * 120f * GameSettings.steeringSensitivity * delta

        // Move position
        val rad = angleDeg * MathUtils.degreesToRadians
        position.x += MathUtils.cos(rad) * speed * delta
        position.y += MathUtils.sin(rad) * speed * delta

        // Keep the player within the track ring by clamping radial distance
        val r = position.len()
        // If outside outer radius or inside inner radius, project onto edge
        if (r > outerRadius - 2f) {
            position.scl((outerRadius - 2f) / r)
            game.platform.vibrateHaptic("tick")
        } else if (r < innerRadius + 2f) {
            position.scl((innerRadius + 2f) / r)
            game.platform.vibrateHaptic("tick")
        }

        // Detect finish line crossing (crossing the positive X axis moving upward)
        val currentPolar = computePolarAngle(position)
        // If we crossed from >300° to <60° and we moved forward (to avoid negative detection)
        if (previousPolarAngle > 300f && currentPolar < 60f && speed > 0f) {
            laps += 1
        }
        previousPolarAngle = currentPolar
    }

    private fun computePolarAngle(vec: Vector2): Float {
        // atan2 returns range (-PI, PI); convert to [0, 360)
        var angle = MathUtils.atan2(vec.y, vec.x) * MathUtils.radiansToDegrees
        if (angle < 0f) angle += 360f
        return angle
    }

    private fun drawWorld() {
        val shapeRenderer = game.shapeRenderer
        // Render track boundaries and finish line
        shapeRenderer.projectionMatrix = camera.combined
        // Track outline
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.LIGHT_GRAY
        // Outer boundary
        shapeRenderer.circle(0f, 0f, outerRadius, 100)
        // Inner boundary
        shapeRenderer.circle(0f, 0f, innerRadius, 100)
        // Finish line at 0 degrees (vertical line at x = innerRadius to outerRadius)
        shapeRenderer.line(innerRadius, 0f, outerRadius, 0f)
        shapeRenderer.end()

        // Draw player as a filled triangle pointing in the direction of motion
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = vehicle.color
        val size = 8f
        // Compute triangle vertices
        val rad = angleDeg * MathUtils.degreesToRadians
        val p1 = Vector2(position.x + MathUtils.cos(rad) * size * 2, position.y + MathUtils.sin(rad) * size * 2)
        val p2 = Vector2(position.x + MathUtils.cos(rad + 2.5f) * size, position.y + MathUtils.sin(rad + 2.5f) * size)
        val p3 = Vector2(position.x + MathUtils.cos(rad - 2.5f) * size, position.y + MathUtils.sin(rad - 2.5f) * size)
        shapeRenderer.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
        shapeRenderer.end()
    }

    override fun resize(width: Int, height: Int) {
        worldViewport.update(width, height)
        uiStage.viewport.update(width, height, true)
        // Position HUD at top‑left corner inside the new viewport
        if (::hudTable.isInitialized) {
            hudTable.setPosition(10f, uiStage.viewport.worldHeight - 40f)
        }
    }

    override fun dispose() {
        uiStage.dispose()
    }
}
