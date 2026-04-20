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
 * Audio delivery:
 *  - Paths are resolved via [StudioAudioLocator], which handles the debug-fallback vs.
 *    PAD-delivery distinction.
 *  - If no assets are accessible, the player queue is not populated and [audioAvailability]
 *    is set to [StudioAvailability.UNAVAILABLE]. The service remains bound so the
 *    MediaController can connect, but playback actions will no-op silently.
 *  - Do NOT call player.prepare() without a populated queue — that would leave the player
 *    in an indeterminate state with broken URIs.
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

        val locator = StudioAudioLocator(this)
        val (resolvedTracks, availability) = locator.resolveAll()

        StudioMediaStateHolder.update(availability)

        if (availability == StudioAvailability.READY || availability == StudioAvailability.WAITING_FOR_ASSETS) {
            // Build queue from whichever tracks resolved (all of them when READY,
            // a subset if somehow partially available).
            val resolvedById = resolvedTracks.toMap()
            val mediaItems = StudioCatalog.playableTracks.mapNotNull { track ->
                val uri = resolvedById[track.id] ?: return@mapNotNull null
                MediaItem.Builder()
                    .setMediaId(track.id)
                    .setUri(uri)
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

            if (mediaItems.isNotEmpty()) {
                player.setMediaItems(mediaItems)
                player.prepare()
            }
        }
        // If UNAVAILABLE: player is built but no queue is set. The service stays alive
        // so the MediaController can bind. Playback actions will be ignored by the player.

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
        // Reset to WAITING so a future service start begins with the correct initial state.
        StudioMediaStateHolder.update(StudioAvailability.WAITING_FOR_ASSETS)
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
