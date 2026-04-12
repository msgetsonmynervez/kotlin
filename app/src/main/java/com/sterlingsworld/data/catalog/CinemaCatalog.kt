package com.sterlingsworld.data.catalog

import com.sterlingsworld.domain.model.Video

object CinemaCatalog {

    val videos: List<Video> = listOf(
        Video(
            id = "main-video-01",
            title = "A Calm Tour Through Sterling Park",
            durationLabel = "04:30",
            assetPath = "video/main/main-video-01.mp4",
        ),
    )

    fun byId(id: String): Video? = videos.firstOrNull { it.id == id }
}
