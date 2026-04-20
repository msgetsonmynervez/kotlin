package com.sterlingsworld.domain.model

enum class AlbumAvailability {
    AVAILABLE_NOW,
    DOWNLOAD_LATER,
}

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val description: String,
    val tracks: List<Track>,
    val availability: AlbumAvailability = AlbumAvailability.AVAILABLE_NOW,
)
