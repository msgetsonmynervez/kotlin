package com.sterlingsworld.feature.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.core.media.StudioAvailability
import com.sterlingsworld.core.ui.theme.Accent
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Border
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Surface as SterlingBackground
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.domain.model.Album
import com.sterlingsworld.domain.model.Track

@Composable
fun StudioScreen() {
    val context = LocalContext.current
    val vm: StudioViewModel = viewModel(factory = StudioViewModel.Factory(context))
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Availability banner — shown when audio assets are not yet ready
        when (state.availability) {
            StudioAvailability.WAITING_FOR_ASSETS -> AudioStatusBanner("Music loading...")
            StudioAvailability.UNAVAILABLE -> AudioStatusBanner("Music unavailable in this build")
            StudioAvailability.READY -> Unit
        }

        // Now-playing transport bar — only shown when audio is ready and actually playing
        if (state.availability == StudioAvailability.READY && (state.currentTrackId != null || state.isPlaying)) {
            NowPlayingBar(
                isPlaying = state.isPlaying,
                currentTrackId = state.currentTrackId,
                onTogglePlayPause = vm::togglePlayPause,
                onNext = vm::next,
                onPrevious = vm::previous,
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = state.albums.size,
                key = { state.albums[it].id },
            ) { index ->
                AlbumCard(
                    album = state.albums[index],
                    currentTrackId = state.currentTrackId,
                    isPlaying = state.isPlaying,
                    enabled = state.availability == StudioAvailability.READY,
                    onTrackSelected = vm::playTrack,
                    onAlbumPlay = vm::playAlbum,
                )
            }
        }
    }
}

@Composable
private fun AudioStatusBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun NowPlayingBar(
    isPlaying: Boolean,
    currentTrackId: String?,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    val trackTitle = remember(currentTrackId) {
        if (currentTrackId != null) {
            com.sterlingsworld.data.catalog.StudioCatalog.allTracks
                .firstOrNull { it.id == currentTrackId }?.title ?: "Playing"
        } else "Playing"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Primary,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = trackTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.weight(1f),
            )
            Row {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = androidx.compose.ui.graphics.Color.White)
                }
                IconButton(onClick = onTogglePlayPause) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = androidx.compose.ui.graphics.Color.White,
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    currentTrackId: String?,
    isPlaying: Boolean,
    enabled: Boolean,
    onTrackSelected: (Track) -> Unit,
    onAlbumPlay: (Album) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val cardAlpha = if (enabled) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SterlingBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = album.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(
                        text = "${album.tracks.size} tracks - ${album.artist}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onAlbumPlay(album) },
                        enabled = enabled,
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play album", tint = if (enabled) Primary else TextMuted)
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = Primary,
                        )
                    }
                }
            }

            if (expanded) {
                HorizontalDivider(color = Border)
                album.tracks.forEachIndexed { index, track ->
                    TrackRow(
                        track = track,
                        position = index + 1,
                        isCurrentTrack = track.id == currentTrackId,
                        isPlaying = isPlaying && track.id == currentTrackId,
                        enabled = enabled,
                        onClick = { onTrackSelected(track) },
                    )
                    if (index < album.tracks.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Border.copy(alpha = 0.4f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    position: Int,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val textColor = if (isCurrentTrack) Primary else TextPrimary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .background(if (isCurrentTrack) Accent.copy(alpha = 0.08f) else androidx.compose.ui.graphics.Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isCurrentTrack && isPlaying) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Primary)
        } else {
            Text(
                text = position.toString().padStart(2, '0'),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
            )
        }
        Text(
            text = track.title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.weight(1f),
        )
    }
}
