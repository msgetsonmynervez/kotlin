package com.sterlingsworld.domain.model

data class GameProgress(
    val gameId: String,
    val completionCount: Int = 0,
    val bestScore: Int = 0,
    val bestStars: Int = 0,
    val restartCount: Int = 0,
    val playCount: Int = 0,
    val lastPlayedAt: String? = null,
    val lastCompletedAt: String? = null,
)
