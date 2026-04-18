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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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

        mediaSession = MediaSession.Builder(this, player).build()

        serviceScope.launch {
            val (resolvedTracks, availability) = withContext(Dispatchers.IO) {
                val locator = StudioAudioLocator(this@StudioPlaybackService)
                locator.resolveAll()
            }

            _audioAvailability.value = availability

            if (availability == StudioAvailability.READY || availability == StudioAvailability.WAITING_FOR_ASSETS) {
                // Build queue from whichever tracks resolved (all of them when READY,
                // a subset if somehow partially available).
                val resolvedById = resolvedTracks.toMap()
                val mediaItems = StudioCatalog.allTracks.mapNotNull { track ->
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
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        // Reset to WAITING so a future service start begins with the correct initial state.
        _audioAvailability.value = StudioAvailability.WAITING_FOR_ASSETS
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

        private val _audioAvailability = MutableStateFlow(StudioAvailability.WAITING_FOR_ASSETS)

        /**
         * Current audio availability state, written by the service and collected by
         * [com.sterlingsworld.feature.studio.StudioViewModel].
         *
         * Initialized to [StudioAvailability.WAITING_FOR_ASSETS] so the UI shows a
         * loading/waiting state before the service has had a chance to probe assets.
         */
        val audioAvailability: StateFlow<StudioAvailability> = _audioAvailability.asStateFlow()
    }
}
