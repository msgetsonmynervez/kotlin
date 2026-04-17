package com.example.snailsjourney

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.math.Rectangle

/**
 * Implements the first playable level. This screen assembles the snail, obstacles,
 * snack items, rest benches, and manages interactions between them. It uses a
 * fixed viewport so the game scales appropriately across devices.
 */
class Level1Screen(private val game: MyGdxGame) : Screen {
    private val viewport = FitViewport(800f, 480f)
    private val stage = Stage(viewport, game.batch)

    private val snail = SnailActor()
    private val obstacles = mutableListOf<ObstacleActor>()
    private val snacks = mutableListOf<SnackItemActor>()
    private val benches = mutableListOf<BenchActor>()
    private var slowTimer = 0f
    private var finished = false

    override fun show() {
        // Position snail near the left edge
        snail.setPosition(20f, 100f)
        stage.addActor(snail)

        // Predefine positions for obstacles
        val obstaclePositions = floatArrayOf(250f, 450f, 650f)
        for (xPos in obstaclePositions) {
            val obstacle = ObstacleActor()
            obstacle.setPosition(xPos, 100f)
            obstacles.add(obstacle)
            stage.addActor(obstacle)
        }

        // Snack item that restores energy
        val snack1 = SnackItemActor(snail, restoreAmount = 40f)
        snack1.setPosition(350f, 150f)
        snacks.add(snack1)
        stage.addActor(snack1)

        // Bench for resting
        val bench = BenchActor(snail)
        bench.setPosition(550f, 100f)
        benches.add(bench)
        stage.addActor(bench)

        // Set the stage as the input processor so actors receive touch events
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        // Clear the screen to a sky blue colour
        Gdx.gl.glClearColor(0.6f, 0.85f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Handle slow state timer: when the snail is slowed, gradually count
        // down and resume normal speed after one second.
        if (snail.isSlow()) {
            slowTimer -= delta
            if (slowTimer <= 0f) {
                snail.resumeSpeed()
            }
        }

        // Check collisions between snail and obstacles. Use a copy of the list
        // to avoid concurrent modification when removing obstacles.
        val snailBounds = snail.getBounds()
        val iterator = obstacles.iterator()
        while (iterator.hasNext()) {
            val obstacle = iterator.next()
            val obstacleRect = Rectangle(obstacle.x, obstacle.y, obstacle.width, obstacle.height)
            if (snailBounds.overlaps(obstacleRect)) {
                // Slow the snail and set a timer
                snail.slowDown()
                slowTimer = 1f
                // Remove the obstacle from the stage and list
                obstacle.remove()
                iterator.remove()
            }
        }

        // Advance the stage (actors update themselves)
        stage.act(delta)
        stage.draw()

        // Check for level completion. When the snail reaches the far right of
        // the world, transition to a finish state. For now we just stop
        // movement.
        if (!finished && snail.x + snail.width >= viewport.worldWidth - 60f) {
            finished = true
            // Stop snail movement by resting (so energy refills) and set
            // snail position to final picnic spot
            snail.rest()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        // Dispose of actors and stage
        snail.dispose()
        obstacles.forEach { it.dispose() }
        snacks.forEach { it.dispose() }
        benches.forEach { it.dispose() }
        stage.dispose()
    }
}