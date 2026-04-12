package com.sterlingsworld.data.progress

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey
    val gameId: String,
    val completionCount: Int = 0,
    val bestScore: Int = 0,
    val bestStars: Int = 0,
    val restartCount: Int = 0,
    val playCount: Int = 0,
    val lastPlayedAt: String? = null,
    val lastCompletedAt: String? = null,
)
