package com.sterlingsworld.domain.model

data class Track(
    val id: String,
    val title: String,
    /** Path relative to the Android assets directory, e.g. "audio/music/music-track-01.mp3" */
    val assetPath: String,
    val trackNumber: Int,
    val albumId: String,
)
