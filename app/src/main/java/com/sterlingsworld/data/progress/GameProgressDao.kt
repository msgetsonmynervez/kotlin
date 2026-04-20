package com.sterlingsworld.data.progress

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GameProgressDao {

    @Query("SELECT * FROM game_progress WHERE gameId = :gameId")
    abstract fun observeProgress(gameId: String): Flow<GameProgressEntity?>

    @Query("SELECT * FROM game_progress")
    abstract fun observeAll(): Flow<List<GameProgressEntity>>

    @Query("DELETE FROM game_progress")
    abstract suspend fun deleteAll()

    // ── Low-level primitives used by @Transaction methods below ──────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertOrIgnore(entity: GameProgressEntity)

    @Query("UPDATE game_progress SET playCount = playCount + 1, lastPlayedAt = :now WHERE gameId = :gameId")
    protected abstract suspend fun incrementPlayCount(gameId: String, now: String)

    @Query("UPDATE game_progress SET restartCount = restartCount + 1 WHERE gameId = :gameId")
    protected abstract suspend fun incrementRestartCount(gameId: String)

    @Query("""
        UPDATE game_progress SET
            completionCount = completionCount + 1,
            bestScore = MAX(bestScore, :score),
            bestStars = MAX(bestStars, :stars),
            lastPlayedAt = :now,
            lastCompletedAt = :now
        WHERE gameId = :gameId
    """)
    protected abstract suspend fun incrementCompletion(gameId: String, score: Int, stars: Int, now: String)

    // ── Atomic composite operations ───────────────────────────────────────────

    @Transaction
    open suspend fun atomicSessionStart(gameId: String, now: String) {
        insertOrIgnore(GameProgressEntity(gameId = gameId))
        incrementPlayCount(gameId, now)
    }

    @Transaction
    open suspend fun atomicRestart(gameId: String) {
        insertOrIgnore(GameProgressEntity(gameId = gameId))
        incrementRestartCount(gameId)
    }

    @Transaction
    open suspend fun atomicCompletion(gameId: String, score: Int, stars: Int, now: String) {
        insertOrIgnore(GameProgressEntity(gameId = gameId))
        incrementCompletion(gameId, score, stars, now)
    }
}
