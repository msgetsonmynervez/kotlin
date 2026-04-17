package game.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import game.save.SaveManager

/**
 * Plays short sound effects for the various in‑game actions.  Each sound is
 * loaded on first use and cached for the duration of the application.  The
 * volume of the sounds can be adjusted via [SaveManager.setSfxVolume].
 */
object AudioManager {
    private var jumpSound: Sound? = null
    private var doubleJumpSound: Sound? = null
    private var collectSound: Sound? = null
    private var enemyHitSound: Sound? = null
    private var finishSound: Sound? = null
    private var clickSound: Sound? = null

    private fun loadSound(path: String): Sound {
        return Gdx.audio.newSound(Gdx.files.internal(path))
    }

    private fun getVolume(): Float = SaveManager.getSfxVolume()

    fun playJump() {
        if (jumpSound == null) jumpSound = loadSound("sfx/jump.wav")
        jumpSound?.play(getVolume())
    }
    fun playDoubleJump() {
        if (doubleJumpSound == null) doubleJumpSound = loadSound("sfx/double_jump.wav")
        doubleJumpSound?.play(getVolume())
    }
    fun playCollect() {
        if (collectSound == null) collectSound = loadSound("sfx/collect.wav")
        collectSound?.play(getVolume())
    }
    fun playEnemyHit() {
        if (enemyHitSound == null) enemyHitSound = loadSound("sfx/enemy_hit.wav")
        enemyHitSound?.play(getVolume())
    }
    fun playFinish() {
        if (finishSound == null) finishSound = loadSound("sfx/finish.wav")
        finishSound?.play(getVolume())
    }
    fun playClick() {
        if (clickSound == null) clickSound = loadSound("sfx/click.wav")
        clickSound?.play(getVolume())
    }
    /** Dispose all loaded sounds. */
    fun dispose() {
        jumpSound?.dispose(); jumpSound = null
        doubleJumpSound?.dispose(); doubleJumpSound = null
        collectSound?.dispose(); collectSound = null
        enemyHitSound?.dispose(); enemyHitSound = null
        finishSound?.dispose(); finishSound = null
        clickSound?.dispose(); clickSound = null
    }
}