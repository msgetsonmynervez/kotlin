package com.sterlingsworld.feature.studio

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.sterlingsworld.core.media.StudioAvailability
import com.sterlingsworld.core.media.StudioMediaStateHolder
import com.sterlingsworld.core.media.StudioPlaybackService

import com.sterlingsworld.data.catalog.StudioCatalog
import com.sterlingsworld.domain.model.Album
import com.sterlingsworld.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudioUiState(
    val albums: List<Album> = StudioCatalog.albums,
    val isPlaying: Boolean = false,
    val currentTrackId: String? = null,
    val controllerReady: Boolean = false,
    val availability: StudioAvailability = StudioAvailability.WAITING_FOR_ASSETS,
)

class StudioViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    private var controllerFuture = buildControllerFuture()
    private var controller: MediaController? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _uiState.value = _uiState.value.copy(currentTrackId = mediaItem?.mediaId)
        }
    }

    init {
        // Collect audio availability from the shared state holder (written by the service)
        // and reflect it in the UI state.
        viewModelScope.launch {
            StudioMediaStateHolder.audioAvailability.collect { availability ->
                _uiState.value = _uiState.value.copy(availability = availability)
            }
        }
    }

    private fun buildControllerFuture() = MediaController
        .Builder(context, SessionToken(context, ComponentName(context, StudioPlaybackService::class.java)))
        .buildAsync()
        .also { future ->
            future.addListener(
                {
                    try {
                        val ctrl = future.get()
                        controller = ctrl
                        ctrl.addListener(playerListener)
                        _uiState.value = _uiState.value.copy(
                            controllerReady = true,
                            isPlaying = ctrl.isPlaying,
                            currentTrackId = ctrl.currentMediaItem?.mediaId,
                        )
                    } catch (_: Exception) {
                        // Service not yet started; playback actions will no-op until reconnected.
                    }
                },
                ContextCompat.getMainExecutor(context),
            )
        }

    fun playTrack(track: Track) {
        if (_uiState.value.availability != StudioAvailability.READY) return
        val ctrl = controller ?: return
        val index = StudioCatalog.playableTracks.indexOfFirst { it.id == track.id }
        if (index >= 0) {
            ctrl.seekToDefaultPosition(index)
            ctrl.play()
        }
    }

    fun playAlbum(album: Album) {
        if (!StudioCatalog.availableAlbums.any { it.id == album.id }) return
        playTrack(album.tracks.firstOrNull() ?: return)
    }

    fun togglePlayPause() {
        if (_uiState.value.availability != StudioAvailability.READY) return
        val ctrl = controller ?: return
        if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
    }

    fun next() {
        if (_uiState.value.availability != StudioAvailability.READY) return
        controller?.seekToNextMediaItem()
    }

    fun previous() {
        if (_uiState.value.availability != StudioAvailability.READY) return
        controller?.seekToPreviousMediaItem()
    }

    override fun onCleared() {
        controller?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture)
        super.onCleared()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            StudioViewModel(context.applicationContext) as T
    }
}
