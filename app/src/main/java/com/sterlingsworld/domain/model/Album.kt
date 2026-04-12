package com.sterlingsworld.domain.model

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val description: String,
    val tracks: List<Track>,
)
