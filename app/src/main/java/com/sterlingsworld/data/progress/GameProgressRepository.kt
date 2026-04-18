package com.sterlingsworld.data.progress

import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class GameProgressRepository(private val dao: GameProgressDao) {

    fun observeProgress(gameId: String): Flow<GameProgressEntity?> =
        dao.observeProgress(gameId)

    fun observeAll(): Flow<List<GameProgressEntity>> = dao.observeAll()

    suspend fun getOrDefault(gameId: String): GameProgressEntity =
        dao.getProgress(gameId) ?: GameProgressEntity(gameId = gameId)

    suspend fun recordSessionStart(gameId: String) {
        val current = getOrDefault(gameId)
        val updated = current.copy(
            playCount = current.playCount + 1,
            lastPlayedAt = Instant.now().toString(),
        )
        dao.insert(updated)
    }

    suspend fun recordRestart(gameId: String) {
        val current = getOrDefault(gameId)
        dao.insert(current.copy(restartCount = current.restartCount + 1))
    }

    suspend fun recordCompletion(gameId: String, result: GameResult) {
        val current = getOrDefault(gameId)
        val now = Instant.now().toString()
        val updated = current.copy(
            completionCount = current.completionCount + 1,
            bestScore = maxOf(current.bestScore, result.score),
            bestStars = maxOf(current.bestStars, result.stars),
            lastPlayedAt = now,
            lastCompletedAt = now,
        )
        dao.insert(updated)
    }

    suspend fun deleteAll() = dao.deleteAll()
}
