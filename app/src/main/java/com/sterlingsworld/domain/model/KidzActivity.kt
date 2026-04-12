package com.sterlingsworld.domain.model

sealed class KidzActivity {
    abstract val id: String
    abstract val title: String

    data class KidzGame(
        override val id: String,
        override val title: String,
        val gameId: String,
    ) : KidzActivity()

    data class KidzVideo(
        override val id: String,
        override val title: String,
        val video: Video,
    ) : KidzActivity()
}
