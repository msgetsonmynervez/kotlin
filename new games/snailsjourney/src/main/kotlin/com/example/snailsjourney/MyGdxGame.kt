package com.example.snailsjourney

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * The main LibGDX game class. A single instance of this game is created by
 * the platform launcher (e.g. Android or desktop). It manages a global
 * `SpriteBatch` which can be reused across screens to render textures
 * efficiently. Upon creation, the first screen is set to `Level1Screen`.
 */
class MyGdxGame : Game() {
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()
        // Start the first level. Additional levels can be set here later.
        setScreen(Level1Screen(this))
    }

    override fun render() {
        // Delegate to the current screen for rendering logic.
        super.render()
    }

    override fun dispose() {
        // Dispose of global resources.
        batch.dispose()
        super.dispose()
    }
}