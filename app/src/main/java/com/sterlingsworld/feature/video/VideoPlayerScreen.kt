package com.sterlingsworld.feature.video

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sterlingsworld.core.ui.components.DashedCornerButton
import com.sterlingsworld.data.catalog.CinemaCatalog
import com.sterlingsworld.data.catalog.KidzCatalog

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    videoId: String = "",
    source: String = "",
    onBack: () -> Unit = {},
) {
    val video = when (source) {
        "cinema" -> CinemaCatalog.byId(videoId)
        "kidz" -> KidzCatalog.videos().firstOrNull { it.video.id == videoId }?.video
        else -> null
    }

    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }

    LaunchedEffect(videoId, source) {
        if (video != null) {
            val uri = Uri.parse("asset:///${video.assetPath}")
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        AndroidView(
            factory = { ctx -> PlayerView(ctx).also { it.player = player } },
            modifier = Modifier.fillMaxSize(),
        )

        DashedCornerButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClick = onBack,
        )

        if (video != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp, start = 80.dp, end = 16.dp)
                    .background(Color(0xAA000000))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {
            Text(
                text = "Video unavailable",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
            )
        }
    }
}
