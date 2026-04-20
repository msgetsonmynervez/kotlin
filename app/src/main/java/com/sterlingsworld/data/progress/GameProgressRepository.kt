package com.sterlingsworld.data.progress

import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class GameProgressRepository(private val dao: GameProgressDao) {

    fun observeProgress(gameId: String): Flow<GameProgressEntity?> =
        dao.observeProgress(gameId)

    fun observeAll(): Flow<List<GameProgressEntity>> = dao.observeAll()

    suspend fun recordSessionStart(gameId: String) {
        dao.atomicSessionStart(gameId, Instant.now().toString())
    }

    suspend fun recordRestart(gameId: String) {
        dao.atomicRestart(gameId)
    }

    suspend fun recordCompletion(gameId: String, result: GameResult) {
        dao.atomicCompletion(gameId, result.score, result.stars, Instant.now().toString())
    }

    suspend fun deleteAll() = dao.deleteAll()
}
