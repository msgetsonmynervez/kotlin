package com.accessquest

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Logger

/**
 * Main game screen that runs the core gameplay loop.  It holds references to
 * the player, environmental hazards, fatigue/heat systems and checkpoints.  It
 * does not yet draw any sprites; instead it focuses on updating the logic
 * systems each frame.  Extend this class to add rendering via SpriteBatch or
 * Scene2D.
 */
class MainGameScreen(private val game: AccessQuestGame) : ScreenAdapter() {
    private val log = Logger(MainGameScreen::class.java.name, Logger.INFO)

    // Camera for logical coordinate space (not yet used for rendering)
    private val camera = OrthographicCamera(800f, 480f)

    // Game systems
    private val player = Player()
    private val fatigueSystem = FatigueSystem(checkpointManager = null)
    private val heatSystem = HeatSystem(checkpointManager = null)
    private val checkpointManager = CheckpointManager(player, fatigueSystem, heatSystem)
    private val itemSystem = ItemSystem(player, fatigueSystem, heatSystem)
    private val hazards: MutableList<Hazard> = mutableListOf()

    // Goal rectangle: reaching this completes the level
    private val goalRect = Rectangle(750f, 400f, 32f, 32f)

    override fun show() {
        super.show()
        // Now that checkpointManager is created, wire it into fatigue/heat systems
        fatigueSystem.checkpointManager = checkpointManager
        heatSystem.checkpointManager = checkpointManager

        // Set initial checkpoint at player's start position
        checkpointManager.currentCheckpoint.set(player.position)

        // Assign global references for hazard systems so hazards can modify them
        Hazard.playerFatigueSystem = fatigueSystem
        Hazard.playerHeatSystem = heatSystem

        // Populate sample hazards for testing: a heat zone and rough terrain
        hazards.add(Hazard(Rectangle(300f, 200f, 200f, 200f), fatigueMultiplier = 1f, heatMultiplier = 1.5f, isHeatZone = true))
        hazards.add(Hazard(Rectangle(100f, 100f, 150f, 150f), fatigueMultiplier = 1.5f, heatMultiplier = 1f, isHeatZone = false))
    }

    override fun render(delta: Float) {
        // Clear the screen (no rendering yet)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update player movement
        player.update(delta, fatigueSystem, heatSystem)

        // Update passive heat accumulation
        heatSystem.update(delta)

        // Apply hazard effects based on player's position
        for (hazard in hazards) {
            hazard.update(player)
        }

        // Check for level completion
        if (goalRect.contains(player.position)) {
            log.info("Player reached the goal! Level complete.")
            // Reset or proceed to next level here.  For now just reset to start.
            checkpointManager.returnToCheckpoint()
        }

        // Additional game systems (e.g. item cooldowns) could be updated here
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    override fun dispose() {
        // Dispose of any resources (not used yet)
    }
}