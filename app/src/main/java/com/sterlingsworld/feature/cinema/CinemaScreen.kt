package com.sterlingsworld.feature.cinema

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.SurfaceStrong
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.data.catalog.CinemaCatalog
import com.sterlingsworld.domain.model.Video

@Composable
fun CinemaScreen(onVideoSelected: (videoId: String) -> Unit) {
    val videos = CinemaCatalog.videos

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(videos, key = { it.id }) { video ->
            VideoCard(video = video, onClick = { onVideoSelected(video.id) })
        }
    }
}

@Composable
private fun VideoCard(video: Video, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            // Thumbnail placeholder — Phase 3 will wire ExoPlayer thumbnails
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(SurfaceStrong),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "▶",
                    style = MaterialTheme.typography.displayLarge,
                    color = Primary,
                )
            }
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = video.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                if (video.durationLabel.isNotEmpty()) {
                    Text(text = video.durationLabel, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                }
            }
        }
    }
}
