package com.sterlingsworld.feature.studio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.core.media.StudioAvailability
import com.sterlingsworld.core.ui.theme.Accent
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.domain.model.Album
import com.sterlingsworld.domain.model.AlbumAvailability
import com.sterlingsworld.domain.model.Track

private val MenuCard = Color(0xE61A1A1A)
private val MenuCardStrong = Color(0xFF2A2A2A)
private val MenuDivider = Color(0x33F4B942)

@Composable
fun StudioScreen() {
    val context = LocalContext.current
    val vm: StudioViewModel = viewModel(factory = StudioViewModel.Factory(context))
    val state by vm.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_music_land),
            contentDescription = "Studio Selection",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.18f),
                            Color.Black.copy(alpha = 0.52f),
                            Color(0xFF121212),
                        ),
                    ),
                ),
        )

        Column(modifier = Modifier.fillMaxSize()) {
            when (state.availability) {
                StudioAvailability.WAITING_FOR_ASSETS -> AudioStatusBanner("Music loading...")
                StudioAvailability.UNAVAILABLE -> AudioStatusBanner("Music unavailable in this build")
                StudioAvailability.READY -> Unit
            }

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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Now Spinning",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Choose an album before entering playback.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                }

                items(
                    count = state.albums.size,
                    key = { state.albums[it].id },
                ) { index ->
                    AlbumCard(
                        album = state.albums[index],
                        currentTrackId = state.currentTrackId,
                        isPlaying = state.isPlaying,
                        playbackReady = state.availability == StudioAvailability.READY,
                        onTrackSelected = vm::playTrack,
                        onAlbumPlay = vm::playAlbum,
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioStatusBanner(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = Color(0xCC1F1F1F),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.82f),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color(0xF0141414),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = trackTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
            Row {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = Accent)
                }
                IconButton(onClick = onTogglePlayPause) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Accent,
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = Accent)
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
    playbackReady: Boolean,
    onTrackSelected: (Track) -> Unit,
    onAlbumPlay: (Album) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val availableNow = album.availability == AlbumAvailability.AVAILABLE_NOW
    val enabled = playbackReady && availableNow
    val cardAlpha = if (enabled) 1f else 0.56f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MenuCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MenuCardStrong),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(34.dp),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${album.tracks.size} tracks - ${album.artist}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted.copy(alpha = 0.95f),
                    )
                    if (!availableNow) {
                        Text(
                            text = "Download later",
                            style = MaterialTheme.typography.labelLarge,
                            color = Accent,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onAlbumPlay(album) },
                        enabled = enabled,
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = "Play album",
                            tint = if (enabled) Accent else Color.White.copy(alpha = 0.38f),
                        )
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = Accent,
                        )
                    }
                }
            }

            if (expanded) {
                HorizontalDivider(color = MenuDivider)
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
                            color = MenuDivider,
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
    val textColor = if (isCurrentTrack) Accent else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .background(if (isCurrentTrack) Accent.copy(alpha = 0.10f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isCurrentTrack && isPlaying) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Accent)
        } else {
            Text(
                text = position.toString().padStart(2, '0'),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
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
