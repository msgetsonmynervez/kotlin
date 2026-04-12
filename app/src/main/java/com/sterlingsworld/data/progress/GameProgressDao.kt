package com.sterlingsworld.data.progress

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProgressDao {

    @Query("SELECT * FROM game_progress WHERE gameId = :gameId")
    fun observeProgress(gameId: String): Flow<GameProgressEntity?>

    @Query("SELECT * FROM game_progress")
    fun observeAll(): Flow<List<GameProgressEntity>>

    @Query("SELECT * FROM game_progress WHERE gameId = :gameId")
    suspend fun getProgress(gameId: String): GameProgressEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: GameProgressEntity)

    @Update
    suspend fun update(entity: GameProgressEntity)

    @Query("DELETE FROM game_progress")
    suspend fun deleteAll()
}
