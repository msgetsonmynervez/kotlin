package com.sterlingsworld.domain.model

enum class GameSection { GAMES, KIDZ }
enum class GameZone { ARCADE, KIDZ }
enum class GameSuite { BATTLE, MINI_GAME, ARCADE, NARRATIVE, REWARD }
enum class GameDifficulty { RELAXED, STANDARD, CHALLENGE, KIDS_EASY }
enum class GameType { BATTLE, STORY, ARCADE, PUZZLE, MEMORY, FOCUS, CALM, REWARD, KIDS, STRATEGY }

data class GameDefinition(
    val id: String,
    val title: String,
    val section: GameSection,
    val zone: GameZone,
    val suite: GameSuite,
    val description: String,
    val objective: String,
    val gameTypes: List<GameType>,
    val difficulty: GameDifficulty,
    val estimatedDurationSec: Int,
    val partyEligible: Boolean,
    val accentLabel: String,
    val shipReady: Boolean = false,
)

data class GameResult(
    val completed: Boolean,
    val score: Int,
    val stars: Int,
    val durationMs: Long,
    val perfect: Boolean,
)
