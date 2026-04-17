package com.myelin.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.myelin.game.model.Barrier
import com.myelin.game.model.Enemy
import com.myelin.game.model.EnemyShot
import com.myelin.game.model.Player
import com.myelin.game.model.Projectile
import com.myelin.game.system.StabilitySystem

class GameScreen : ScreenAdapter() {
    private enum class GamePhase {
        READY,
        WAVE_INTRO,
        RUNNING,
        WON,
        LOST,
    }

    private val worldWidth = 1280f
    private val worldHeight = 720f
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(worldWidth, worldHeight, camera)
    private val renderer = ShapeRenderer()
    private val spriteBatch = SpriteBatch()
    private val font = BitmapFont().apply {
        data.setScale(1.2f)
    }
    private val touchPoint = Vector3()
    private val maxWaves = 5
    private val maxRepairCharge = 100f
    private val maxPointers = 5

    private val player = Player(
        x = worldWidth / 2f - 52f,
        y = 54f,
        width = 104f,
        height = 34f,
    )
    private val stabilitySystem = StabilitySystem()
    private val enemies = mutableListOf<Enemy>()
    private val playerShots = mutableListOf<Projectile>()
    private val enemyShots = mutableListOf<EnemyShot>()
    private val barriers = mutableListOf<Barrier>()

    private var phase = GamePhase.READY
    private var currentWave = 0
    private var eliminations = 0
    private var score = 0
    private var repairCharge = maxRepairCharge
    private var phaseTimer = 0f
    private var fireTimer = 0f
    private var enemyFireTimer = 0f
    private var formationDirection = 1f
    private var targetPlayerX = player.x
    private var fireActive = false
    private var repairActive = false
    private var touchArmed = true
    private var repairingBarrierIndex = -1

    private val movementZone = Rectangle(32f, 0f, 900f, 200f)
    private val fireButton = Rectangle(worldWidth - 220f, 94f, 160f, 72f)
    private val repairButton = Rectangle(worldWidth - 220f, 10f, 160f, 72f)

    init {
        camera.setToOrtho(false, worldWidth, worldHeight)
        createBarriers()
    }

    override fun show() {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
    }

    override fun render(delta: Float) {
        update(delta.coerceAtMost(1f / 30f))

        ScreenUtils.clear(0.04f, 0.05f, 0.09f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        renderer.projectionMatrix = camera.combined
        spriteBatch.projectionMatrix = camera.combined

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        drawArena()
        drawBarriers()
        drawPlayer()
        drawPlayerShots()
        drawEnemyShots()
        drawEnemies()
        drawRepairBeam()
        drawHudBars()
        drawWaveMarkers()
        drawControlButtons()
        drawOverlayPanels()
        renderer.end()

        spriteBatch.begin()
        drawHudText()
        drawOverlayText()
        spriteBatch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        renderer.dispose()
        spriteBatch.dispose()
        font.dispose()
    }

    private fun update(delta: Float) {
        readTouchInput()

        when (phase) {
            GamePhase.READY -> updateTapTransitions()
            GamePhase.WAVE_INTRO -> updateWaveIntro(delta)
            GamePhase.RUNNING -> updateRunning(delta)
            GamePhase.WON,
            GamePhase.LOST -> updateTapTransitions()
        }
    }

    private fun updateWaveIntro(delta: Float) {
        phaseTimer -= delta
        if (phaseTimer <= 0f) {
            phase = GamePhase.RUNNING
        }
    }

    private fun updateRunning(delta: Float) {
        updatePlayerMovement(delta)
        updatePlayerFire(delta)
        updateRepair(delta)
        updateFormation(delta)
        updateEnemyFire(delta)
        updatePlayerShots(delta)
        updateEnemyShots(delta)
        resolveCollisions()
        updateRoundState()

        if (!repairActive) {
            repairCharge = (repairCharge + delta * 10f).coerceAtMost(maxRepairCharge)
            repairingBarrierIndex = -1
        }
    }

    private fun updatePlayerMovement(delta: Float) {
        val approach = 640f * delta
        player.x = MathUtils.lerp(player.x, targetPlayerX, (approach / player.width).coerceAtMost(1f))
        player.x = player.x.coerceIn(32f, worldWidth - 300f - player.width)
    }

    private fun updatePlayerFire(delta: Float) {
        if (!fireActive || repairActive) {
            fireTimer = (fireTimer + delta * 0.35f).coerceAtMost(playerFireInterval())
            return
        }

        fireTimer += delta
        if (fireTimer < playerFireInterval()) {
            return
        }

        fireTimer = 0f
        playerShots += Projectile(
            x = player.x + player.width / 2f - 5f,
            y = player.y + player.height,
            width = 10f,
            height = 26f,
            speed = 650f,
        )
    }

    private fun updateRepair(delta: Float) {
        if (!repairActive || repairCharge <= 0f) {
            repairingBarrierIndex = -1
            return
        }

        val barrierIndex = barriers.indexOfFirst { it.isDamaged && !it.isDestroyed }
        if (barrierIndex == -1) {
            repairingBarrierIndex = -1
            return
        }

        repairingBarrierIndex = barrierIndex
        repairCharge = (repairCharge - delta * 24f).coerceAtLeast(0f)
        barriers[barrierIndex].repair(delta * 28f)
    }

    private fun updateFormation(delta: Float) {
        if (enemies.isEmpty()) {
            return
        }

        val horizontalSpeed = 56f + currentWave * 18f
        enemies.forEach { enemy ->
            enemy.x += formationDirection * horizontalSpeed * delta
        }

        val hitLeftEdge = enemies.any { it.x <= 48f }
        val hitRightEdge = enemies.any { it.x + it.width >= worldWidth - 320f }
        if (hitLeftEdge || hitRightEdge) {
            formationDirection *= -1f
            enemies.forEach { enemy ->
                enemy.x = enemy.x.coerceIn(48f, worldWidth - 320f - enemy.width)
                enemy.y -= 22f + currentWave * 4f
            }
        }

        val iterator = enemies.iterator()
        while (iterator.hasNext()) {
            val enemy = iterator.next()
            if (enemy.y <= player.y + player.height + 8f) {
                iterator.remove()
                stabilitySystem.applyLeak(18f)
            }
        }
    }

    private fun updateEnemyFire(delta: Float) {
        if (enemies.isEmpty()) {
            return
        }

        enemyFireTimer += delta
        val fireInterval = (1.45f - currentWave * 0.16f).coerceAtLeast(0.5f)
        if (enemyFireTimer < fireInterval) {
            return
        }

        enemyFireTimer = 0f
        val shooters = enemies.shuffled().take((1 + currentWave / 2).coerceAtMost(4))
        shooters.forEach { enemy ->
            enemyShots += EnemyShot(
                x = enemy.x + enemy.width / 2f - 4f,
                y = enemy.y - 16f,
                width = 8f,
                height = 20f,
                speed = 240f + currentWave * 20f,
            )
        }
    }

    private fun updatePlayerShots(delta: Float) {
        val iterator = playerShots.iterator()
        while (iterator.hasNext()) {
            val shot = iterator.next()
            shot.y += shot.speed * delta
            if (shot.y > worldHeight + shot.height) {
                iterator.remove()
            }
        }
    }

    private fun updateEnemyShots(delta: Float) {
        val iterator = enemyShots.iterator()
        while (iterator.hasNext()) {
            val shot = iterator.next()
            shot.y -= shot.speed * delta
            if (shot.y + shot.height < 0f) {
                iterator.remove()
                stabilitySystem.applyLeak(4f)
            }
        }
    }

    private fun resolveCollisions() {
        resolvePlayerShotsAgainstEnemies()
        resolveEnemyShotsAgainstDefenses()
        resolveEnemiesAgainstDefenses()
    }

    private fun resolvePlayerShotsAgainstEnemies() {
        val shotIterator = playerShots.iterator()
        while (shotIterator.hasNext()) {
            val shot = shotIterator.next()
            val enemyIterator = enemies.iterator()
            var hit = false

            while (enemyIterator.hasNext()) {
                val enemy = enemyIterator.next()
                if (shot.bounds().overlaps(enemy.bounds())) {
                    enemyIterator.remove()
                    hit = true
                    eliminations += 1
                    score += 100 * currentWave
                    break
                }
            }

            if (hit) {
                shotIterator.remove()
            }
        }
    }

    private fun resolveEnemyShotsAgainstDefenses() {
        val shotIterator = enemyShots.iterator()
        while (shotIterator.hasNext()) {
            val shot = shotIterator.next()
            var consumed = false

            for (barrier in barriers) {
                if (!barrier.isDestroyed && shot.bounds().overlaps(barrier.bounds())) {
                    barrier.damage(18f)
                    consumed = true
                    break
                }
            }

            if (!consumed && shot.bounds().overlaps(player.bounds())) {
                stabilitySystem.applyLeak(9f)
                consumed = true
            }

            if (consumed) {
                shotIterator.remove()
            }
        }
    }

    private fun resolveEnemiesAgainstDefenses() {
        val enemyIterator = enemies.iterator()
        while (enemyIterator.hasNext()) {
            val enemy = enemyIterator.next()
            var removed = false

            for (barrier in barriers) {
                if (!barrier.isDestroyed && enemy.bounds().overlaps(barrier.bounds())) {
                    barrier.damage(36f)
                    stabilitySystem.applyLeak(6f)
                    removed = true
                    break
                }
            }

            if (!removed && enemy.bounds().overlaps(player.bounds())) {
                stabilitySystem.applyLeak(18f)
                removed = true
            }

            if (removed) {
                enemyIterator.remove()
            }
        }
    }

    private fun updateRoundState() {
        if (stabilitySystem.isDepleted) {
            phase = GamePhase.LOST
            clearBoard()
            repairingBarrierIndex = -1
            return
        }

        if (enemies.isEmpty() && enemyShots.isEmpty()) {
            if (currentWave >= maxWaves) {
                phase = GamePhase.WON
                clearBoard()
                repairingBarrierIndex = -1
            } else {
                startNextWave()
            }
        }
    }

    private fun drawArena() {
        renderer.color = Color(0.09f, 0.12f, 0.18f, 1f)
        renderer.rect(32f, 32f, worldWidth - 64f, worldHeight - 64f)

        renderer.color = Color(0.11f, 0.16f, 0.25f, 1f)
        renderer.rect(32f, worldHeight - 88f, worldWidth - 64f, 56f)

        renderer.color = Color(0.1f, 0.14f, 0.22f, 1f)
        renderer.rect(worldWidth - 272f, 32f, 208f, 188f)
    }

    private fun drawPlayer() {
        renderer.color = Color(0.2f, 0.82f, 0.98f, 1f)
        renderer.rect(player.x, player.y, player.width, player.height)

        renderer.color = Color(0.8f, 0.94f, 1f, 1f)
        renderer.rect(player.x + 28f, player.y + player.height, player.width - 56f, 14f)
    }

    private fun drawBarriers() {
        barriers.forEachIndexed { index, barrier ->
            val baseColor = when {
                barrier.isDestroyed -> Color(0.22f, 0.2f, 0.22f, 1f)
                repairingBarrierIndex == index -> Color(0.35f, 0.94f, 0.72f, 1f)
                else -> Color(0.42f, 0.88f, 0.47f, 1f)
            }
            renderer.color = baseColor
            renderer.rect(barrier.x, barrier.y, barrier.width, barrier.height)

            renderer.color = Color(0.12f, 0.15f, 0.18f, 1f)
            renderer.rect(barrier.x, barrier.y - 14f, barrier.width, 8f)

            renderer.color = if (barrier.ratio() > 0.35f) {
                Color(0.42f, 0.88f, 0.47f, 1f)
            } else {
                Color(0.94f, 0.4f, 0.28f, 1f)
            }
            renderer.rect(barrier.x, barrier.y - 14f, barrier.width * barrier.ratio(), 8f)
        }
    }

    private fun drawPlayerShots() {
        renderer.color = Color(1f, 0.83f, 0.24f, 1f)
        playerShots.forEach { shot ->
            renderer.rect(shot.x, shot.y, shot.width, shot.height)
        }
    }

    private fun drawEnemyShots() {
        renderer.color = Color(1f, 0.5f, 0.3f, 1f)
        enemyShots.forEach { shot ->
            renderer.rect(shot.x, shot.y, shot.width, shot.height)
        }
    }

    private fun drawEnemies() {
        enemies.forEachIndexed { index, enemy ->
            renderer.color = if ((index + currentWave) % 2 == 0) {
                Color(0.96f, 0.38f, 0.34f, 1f)
            } else {
                Color(0.98f, 0.54f, 0.28f, 1f)
            }
            renderer.rect(enemy.x, enemy.y, enemy.width, enemy.height)
        }
    }

    private fun drawRepairBeam() {
        val barrier = barriers.getOrNull(repairingBarrierIndex) ?: return
        if (barrier.isDestroyed || !repairActive) {
            return
        }

        renderer.color = Color(0.35f, 0.94f, 0.72f, 0.9f)
        renderer.rect(player.x + player.width / 2f - 5f, player.y + player.height, 10f, barrier.y - player.y)
    }

    private fun drawHudBars() {
        drawBar(
            x = 48f,
            y = worldHeight - 62f,
            width = 270f,
            ratio = stabilitySystem.ratio(),
            fill = if (stabilitySystem.ratio() > 0.35f) Color(0.42f, 0.88f, 0.47f, 1f) else Color(0.94f, 0.4f, 0.28f, 1f),
        )
        drawBar(
            x = 344f,
            y = worldHeight - 62f,
            width = 220f,
            ratio = repairCharge / maxRepairCharge,
            fill = Color(0.35f, 0.94f, 0.72f, 1f),
        )
        drawBar(
            x = worldWidth - 356f,
            y = worldHeight - 62f,
            width = 260f,
            ratio = waveProgressRatio(),
            fill = Color(0.2f, 0.82f, 0.98f, 1f),
        )
    }

    private fun drawWaveMarkers() {
        val baseX = 590f
        for (wave in 1..maxWaves) {
            renderer.color = when {
                wave < currentWave -> Color(0.42f, 0.88f, 0.47f, 1f)
                wave == currentWave -> Color(0.2f, 0.82f, 0.98f, 1f)
                else -> Color(0.24f, 0.28f, 0.34f, 1f)
            }
            renderer.rect(baseX + (wave - 1) * 44f, worldHeight - 58f, 28f, 20f)
        }
    }

    private fun drawControlButtons() {
        drawControlButton(fireButton, fireActive && phase == GamePhase.RUNNING, Color(0.96f, 0.55f, 0.2f, 1f))
        drawControlButton(repairButton, repairActive && phase == GamePhase.RUNNING, Color(0.35f, 0.94f, 0.72f, 1f))
    }

    private fun drawControlButton(bounds: Rectangle, active: Boolean, activeColor: Color) {
        renderer.color = if (active) activeColor else Color(0.17f, 0.21f, 0.28f, 1f)
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height)
    }

    private fun drawOverlayPanels() {
        if (phase == GamePhase.RUNNING) {
            return
        }

        renderer.color = when (phase) {
            GamePhase.READY -> Color(0.07f, 0.11f, 0.18f, 0.9f)
            GamePhase.WAVE_INTRO -> Color(0.08f, 0.14f, 0.21f, 0.78f)
            GamePhase.WON -> Color(0.08f, 0.18f, 0.12f, 0.86f)
            GamePhase.LOST -> Color(0.22f, 0.08f, 0.08f, 0.88f)
            GamePhase.RUNNING -> Color.CLEAR
        }
        renderer.rect(200f, 170f, worldWidth - 400f, worldHeight - 340f)
    }

    private fun drawHudText() {
        font.color = Color.WHITE
        font.draw(spriteBatch, "Stability", 48f, worldHeight - 22f)
        font.draw(spriteBatch, "Repair", 344f, worldHeight - 22f)
        font.draw(spriteBatch, "Score $score", 590f, worldHeight - 22f)
        font.draw(spriteBatch, "Wave $currentWave/$maxWaves", worldWidth - 356f, worldHeight - 22f)
        font.draw(spriteBatch, "Move in left zone", 54f, 78f)
        font.draw(spriteBatch, "FIRE", fireButton.x + 48f, fireButton.y + 46f)
        font.draw(spriteBatch, "REPAIR", repairButton.x + 34f, repairButton.y + 46f)
    }

    private fun drawOverlayText() {
        font.color = Color.WHITE
        when (phase) {
            GamePhase.READY -> {
                font.draw(spriteBatch, "MYELIN PROTOCOL", 420f, 430f)
                font.draw(spriteBatch, "Hold left zone to move. Hold FIRE to shoot. Hold REPAIR to rebuild barriers.", 280f, 370f)
                font.draw(spriteBatch, "Survive five waves and keep the stability network online.", 360f, 332f)
                font.draw(spriteBatch, "Tap anywhere to deploy.", 500f, 270f)
            }

            GamePhase.WAVE_INTRO -> {
                font.draw(spriteBatch, "Wave $currentWave incoming", 490f, 380f)
            }

            GamePhase.WON -> {
                font.draw(spriteBatch, "Network stabilized", 470f, 392f)
                font.draw(spriteBatch, "Final score: $score", 520f, 340f)
                font.draw(spriteBatch, "Tap to run the protocol again.", 450f, 286f)
            }

            GamePhase.LOST -> {
                font.draw(spriteBatch, "Protocol breach", 512f, 392f)
                font.draw(spriteBatch, "Score: $score", 560f, 340f)
                font.draw(spriteBatch, "Tap to restart from wave one.", 452f, 286f)
            }

            GamePhase.RUNNING -> Unit
        }
    }

    private fun drawBar(
        x: Float,
        y: Float,
        width: Float,
        ratio: Float,
        fill: Color,
    ) {
        renderer.color = Color(0.15f, 0.16f, 0.2f, 1f)
        renderer.rect(x, y, width, 20f)
        renderer.color = fill
        renderer.rect(x, y, width * ratio.coerceIn(0f, 1f), 20f)
    }

    private fun waveProgressRatio(): Float {
        if (currentWave <= 0) {
            return 0f
        }

        val totalPerWave = waveEnemyCount(currentWave).toFloat()
        if (totalPerWave <= 0f) {
            return 0f
        }
        return ((totalPerWave - enemies.size.toFloat()) / totalPerWave).coerceIn(0f, 1f)
    }

    private fun playerFireInterval(): Float = (0.25f - currentWave * 0.02f).coerceAtLeast(0.12f)

    private fun readTouchInput() {
        fireActive = false
        repairActive = false
        var movementSeen = false

        for (pointer in 0 until maxPointers) {
            if (!Gdx.input.isTouched(pointer)) {
                continue
            }

            touchPoint.set(Gdx.input.getX(pointer).toFloat(), Gdx.input.getY(pointer).toFloat(), 0f)
            viewport.unproject(touchPoint)

            if (fireButton.contains(touchPoint.x, touchPoint.y)) {
                fireActive = true
                continue
            }
            if (repairButton.contains(touchPoint.x, touchPoint.y)) {
                repairActive = true
                continue
            }
            if (movementZone.contains(touchPoint.x, touchPoint.y)) {
                targetPlayerX = (touchPoint.x - player.width / 2f).coerceIn(32f, worldWidth - 300f - player.width)
                movementSeen = true
            }
        }

        if (!movementSeen && !Gdx.input.isTouched) {
            targetPlayerX = player.x
        }
    }

    private fun updateTapTransitions() {
        if (Gdx.input.isTouched) {
            if (touchArmed) {
                touchArmed = false
                when (phase) {
                    GamePhase.READY -> resetCampaign()
                    GamePhase.WON,
                    GamePhase.LOST -> resetCampaign()
                    GamePhase.WAVE_INTRO,
                    GamePhase.RUNNING -> Unit
                }
            }
            return
        }

        touchArmed = true
    }

    private fun resetCampaign() {
        clearBoard()
        stabilitySystem.restoreFull()
        repairCharge = maxRepairCharge
        eliminations = 0
        score = 0
        currentWave = 0
        player.x = worldWidth / 2f - player.width / 2f
        targetPlayerX = player.x
        barriers.clear()
        createBarriers()
        startNextWave()
    }

    private fun startNextWave() {
        currentWave += 1
        if (currentWave > maxWaves) {
            phase = GamePhase.WON
            return
        }

        clearBoard()
        spawnWave(currentWave)
        fireTimer = 0f
        enemyFireTimer = 0f
        formationDirection = if (currentWave % 2 == 0) -1f else 1f
        phaseTimer = 1.2f
        phase = GamePhase.WAVE_INTRO
    }

    private fun spawnWave(wave: Int) {
        val columns = 4 + wave
        val rows = 2 + (wave - 1) / 2
        val enemyWidth = 54f
        val enemyHeight = 36f
        val spacingX = 24f
        val spacingY = 20f
        val totalWidth = columns * enemyWidth + (columns - 1) * spacingX
        val startX = 100f + ((worldWidth - 420f) - totalWidth) / 2f
        val startY = worldHeight - 170f

        repeat(rows) { row ->
            repeat(columns) { column ->
                enemies += Enemy(
                    x = startX + column * (enemyWidth + spacingX),
                    y = startY - row * (enemyHeight + spacingY),
                    width = enemyWidth,
                    height = enemyHeight,
                    speed = 0f,
                )
            }
        }
    }

    private fun waveEnemyCount(wave: Int): Int {
        val columns = 4 + wave
        val rows = 2 + (wave - 1) / 2
        return columns * rows
    }

    private fun createBarriers() {
        val startX = 180f
        val spacing = 250f
        repeat(3) { index ->
            barriers += Barrier(
                x = startX + spacing * index,
                y = 184f,
                width = 132f,
                height = 44f,
                maxIntegrity = 100f,
            )
        }
    }

    private fun clearBoard() {
        enemies.clear()
        playerShots.clear()
        enemyShots.clear()
    }
}
