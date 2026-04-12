package com.sterlingsworld.core.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.sterlingsworld.R
import com.sterlingsworld.data.catalog.StudioCatalog

/**
 * StudioPlaybackService — foreground Media3 MediaSessionService for Studio audio playback.
 *
 * Lifecycle:
 *  - Created when the first MediaController binds (typically from StudioViewModel).
 *  - Runs as a foreground service while audio is playing, allowing background playback.
 *  - Released when the last controller unbinds and no playback is active.
 *
 * Queue: the full 126-track catalog is loaded on creation. Tracks are resolved
 * from the Android assets directory using the "asset:///" URI scheme via
 * ExoPlayer's built-in AssetDataSource.
 */
class StudioPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true,
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        val mediaItems = StudioCatalog.allTracks.map { track ->
            MediaItem.Builder()
                .setMediaId(track.id)
                .setUri("file:///android_asset/${track.assetPath}")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setAlbumTitle(
                            StudioCatalog.albumById(track.albumId)?.title ?: track.albumId
                        )
                        .setArtist("Sterling Sound Team")
                        .setTrackNumber(track.trackNumber)
                        .build()
                )
                .build()
        }

        player.setMediaItems(mediaItems)
        player.prepare()

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_studio),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(R.string.notification_channel_studio_desc)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "studio_playback"
    }
}
