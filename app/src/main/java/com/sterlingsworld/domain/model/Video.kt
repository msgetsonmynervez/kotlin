package com.sterlingsworld.domain.model

data class Video(
    val id: String,
    val title: String,
    val durationLabel: String,
    /** Path relative to the Android assets directory, e.g. "video/main/main-video-01.mp4" */
    val assetPath: String,
)
