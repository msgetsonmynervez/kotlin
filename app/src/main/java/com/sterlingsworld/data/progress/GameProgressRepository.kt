package com.sterlingsworld.data.progress

import com.sterlingsworld.domain.model.GameProgress
import com.sterlingsworld.domain.model.GameResult
import com.sterlingsworld.domain.repository.GameProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class GameProgressRepositoryImpl(private val dao: GameProgressDao) : GameProgressRepository {

    override fun observeProgress(gameId: String): Flow<GameProgress?> =
        dao.observeProgress(gameId).map { it?.toDomain() }

    override fun observeAll(): Flow<List<GameProgress>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun recordSessionStart(gameId: String) {
        dao.atomicSessionStart(gameId, Instant.now().toString())
    }

    override suspend fun recordRestart(gameId: String) {
        dao.atomicRestart(gameId)
    }

    override suspend fun recordCompletion(gameId: String, result: GameResult) {
        dao.atomicCompletion(gameId, result.score, result.stars, Instant.now().toString())
    }

    override suspend fun deleteAll() = dao.deleteAll()
}

private fun GameProgressEntity.toDomain(): GameProgress = GameProgress(
    gameId = gameId,
    completionCount = completionCount,
    bestScore = bestScore,
    bestStars = bestStars,
    restartCount = restartCount,
    playCount = playCount,
    lastPlayedAt = lastPlayedAt,
    lastCompletedAt = lastCompletedAt
)
