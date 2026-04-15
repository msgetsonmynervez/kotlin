package com.sterlingsworld.feature.kidzcinema

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.ActionButton
import com.sterlingsworld.core.ui.components.DashedCornerButton

@Composable
fun KidzCinemaScreen(
    videoId: String = "kids-video-01",
    onPlayVideo: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_kidz_cinema),
            contentDescription = "Kidz Cinema Showtime Adventure",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        DashedCornerButton(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            onClick = onBack,
        )
        DashedCornerButton(Modifier.align(Alignment.TopEnd).padding(16.dp))

        // Transparent tap zone over the theater screen in the artwork
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-60).dp)
                .fillMaxWidth(0.6f)
                .aspectRatio(16f / 10f)
                .clickable { onPlayVideo() },
            contentAlignment = Alignment.Center,
        )

        ActionButton(
            label = "Back to Stories",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp),
            onClick = onBack,
        )
    }
}
