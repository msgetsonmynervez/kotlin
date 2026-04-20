package com.sterlingsworld.feature.game

import com.sterlingsworld.data.progress.GameProgressDao
import com.sterlingsworld.data.progress.GameProgressEntity
import com.sterlingsworld.data.progress.GameProgressRepository
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.feature.game.shell.GamePhase
import com.sterlingsworld.feature.game.shell.GameShellEvent
import com.sterlingsworld.feature.game.shell.GameShellViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GameShellViewModelTest {

    @Test
    fun `initial state has playing phase`() {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val vm = buildViewModel(scope = scope)

        assertEquals(GamePhase.PLAYING, vm.uiState.value.gamePhase)
        scope.cancel()
    }

    @Test
    fun `onPause transitions to paused`() {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val vm = buildViewModel(scope = scope)

        vm.onPause()

        assertEquals(GamePhase.PAUSED, vm.uiState.value.gamePhase)
        scope.cancel()
    }

    @Test
    fun `onResume transitions to playing`() {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val vm = buildViewModel(scope = scope)
        vm.onPause()

        vm.onResume()

        assertEquals(GamePhase.PLAYING, vm.uiState.value.gamePhase)
        scope.cancel()
    }

    @Test
    fun `creating view model records session start`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val dao = FakeGameProgressDao()

        buildViewModel(dao = dao, scope = scope)

        assertEquals(1, dao.getProgress("lucky-paws")?.playCount)
        scope.cancel()
    }

    @Test
    fun `onExit emits exit event`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val vm = buildViewModel(scope = scope)
        val event = collectNextEvent(vm, scope) {
            vm.onExit()
        }

        assertEquals(GameShellEvent.Exit, event)
        scope.cancel()
    }

    @Test
    fun `onRestart emits restart event`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val dao = FakeGameProgressDao()
        val vm = buildViewModel(dao = dao, scope = scope)
        val event = collectNextEvent(vm, scope) {
            vm.onRestart()
        }

        assertEquals(GameShellEvent.Restart, event)
        assertEquals(1, dao.getProgress("lucky-paws")?.restartCount)
        scope.cancel()
    }

    @Test
    fun `restart event is distinct from exit event`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val vm = buildViewModel(scope = scope)

        val restartEvent = collectNextEvent(vm, scope) { vm.onRestart() }
        val exitEvent = collectNextEvent(vm, scope) { vm.onExit() }

        // Restart must not equal Exit — if this fails the shell would eject the player
        // instead of restarting the game (regression guard for GameShellScreen routing).
        assertTrue(restartEvent != exitEvent)
        assertEquals(GameShellEvent.Restart, restartEvent)
        assertEquals(GameShellEvent.Exit, exitEvent)
        scope.cancel()
    }

    @Test
    fun `onComplete emits complete event`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
        val dao = FakeGameProgressDao()
        val vm = buildViewModel(dao = dao, scope = scope)
        val result = GameResult(completed = true, score = 7, stars = 2, durationMs = 40L, perfect = false)
        val event = collectNextEvent(vm, scope) {
            vm.onComplete(result)
        }

        assertEquals(GameShellEvent.Complete(result), event)
        assertEquals(1, dao.getProgress("lucky-paws")?.completionCount)
        assertEquals(7, dao.getProgress("lucky-paws")?.bestScore)
        scope.cancel()
    }

    private fun buildViewModel(
        dao: FakeGameProgressDao = FakeGameProgressDao(),
        scope: CoroutineScope? = null,
    ): GameShellViewModel {
        val repository = GameProgressRepository(dao)
        return if (scope == null) {
            GameShellViewModel(
                gameId = "lucky-paws",
                progressRepository = repository,
            )
        } else {
            GameShellViewModel(
                gameId = "lucky-paws",
                progressRepository = repository,
                eventScope = scope,
            )
        }
    }

    private suspend fun collectNextEvent(
        vm: GameShellViewModel,
        scope: CoroutineScope,
        trigger: () -> Unit,
    ): GameShellEvent {
        val deferred = CompletableDeferred<GameShellEvent>()
        val job = scope.launch {
            deferred.complete(vm.events.first())
        }

        trigger()

        val event = deferred.await()
        job.cancel()
        return event
    }
}

private class FakeGameProgressDao : GameProgressDao() {
    private val entities = MutableStateFlow<Map<String, GameProgressEntity>>(emptyMap())

    override fun observeProgress(gameId: String): Flow<GameProgressEntity?> =
        entities.map { it[gameId] }

    override fun observeAll(): Flow<List<GameProgressEntity>> =
        entities.map { it.values.toList() }

    suspend fun getProgress(gameId: String): GameProgressEntity? = entities.value[gameId]

    override suspend fun insertOrIgnore(entity: GameProgressEntity) {
        if (entity.gameId !in entities.value) {
            entities.value = entities.value + (entity.gameId to entity)
        }
    }

    override suspend fun incrementPlayCount(gameId: String, now: String) {
        val current = entities.value[gameId] ?: return
        entities.value = entities.value + (
            gameId to current.copy(
                playCount = current.playCount + 1,
                lastPlayedAt = now,
            )
        )
    }

    override suspend fun incrementRestartCount(gameId: String) {
        val current = entities.value[gameId] ?: return
        entities.value = entities.value + (
            gameId to current.copy(
                restartCount = current.restartCount + 1,
            )
        )
    }

    override suspend fun incrementCompletion(gameId: String, score: Int, stars: Int, now: String) {
        val current = entities.value[gameId] ?: return
        entities.value = entities.value + (
            gameId to current.copy(
                completionCount = current.completionCount + 1,
                bestScore = maxOf(current.bestScore, score),
                bestStars = maxOf(current.bestStars, stars),
                lastPlayedAt = now,
                lastCompletedAt = now,
            )
        )
    }

    override suspend fun deleteAll() {
        entities.value = emptyMap()
    }
}
