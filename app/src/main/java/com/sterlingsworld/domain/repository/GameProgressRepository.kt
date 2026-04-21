package com.sterlingsworld.domain.repository

import com.sterlingsworld.domain.model.GameProgress
import com.sterlingsworld.domain.model.GameResult
import kotlinx.coroutines.flow.Flow

interface GameProgressRepository {
    fun observeProgress(gameId: String): Flow<GameProgress?>
    fun observeAll(): Flow<List<GameProgress>>
    suspend fun recordSessionStart(gameId: String)
    suspend fun recordRestart(gameId: String)
    suspend fun recordCompletion(gameId: String, result: GameResult)
    suspend fun deleteAll()
}
