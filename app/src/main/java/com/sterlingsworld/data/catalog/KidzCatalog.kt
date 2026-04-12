package com.sterlingsworld.data.catalog

import com.sterlingsworld.domain.model.KidzActivity
import com.sterlingsworld.domain.model.Video

object KidzCatalog {

    val activities: List<KidzActivity> = listOf(
        KidzActivity.KidzGame(
            id = "kids-game-01",
            title = "Kidz Doodle Land",
            gameId = "kidz-doodle-land",
        ),
        KidzActivity.KidzGame(
            id = "kids-game-02",
            title = "Kidz Linebreaker",
            gameId = "kidz-linebreaker",
        ),
        KidzActivity.KidzGame(
            id = "kids-game-03",
            title = "Lumi's Star Quest",
            gameId = "lumis-star-quest",
        ),
        KidzActivity.KidzGame(
            id = "kids-game-04",
            title = "Nostalgia",
            gameId = "nostalgia",
        ),
        KidzActivity.KidzVideo(
            id = "kids-video-01",
            title = "Friendly Shapes",
            video = Video(
                id = "kids-video-01",
                title = "Friendly Shapes",
                durationLabel = "",
                assetPath = "video/kidz/kids-video-01.mp4",
            ),
        ),
        KidzActivity.KidzVideo(
            id = "kids-video-02",
            title = "Cloud Counting",
            video = Video(
                id = "kids-video-02",
                title = "Cloud Counting",
                durationLabel = "",
                assetPath = "video/kidz/kids-video-02.mp4",
            ),
        ),
        KidzActivity.KidzVideo(
            id = "kids-video-03",
            title = "Tiny Parade",
            video = Video(
                id = "kids-video-03",
                title = "Tiny Parade",
                durationLabel = "",
                assetPath = "video/kidz/kids-video-03.mp4",
            ),
        ),
    )

    fun games(): List<KidzActivity.KidzGame> = activities.filterIsInstance<KidzActivity.KidzGame>()
    fun videos(): List<KidzActivity.KidzVideo> = activities.filterIsInstance<KidzActivity.KidzVideo>()
}
