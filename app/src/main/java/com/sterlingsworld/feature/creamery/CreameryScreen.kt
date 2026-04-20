package com.sterlingsworld.feature.creamery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.ArtworkTapTarget
import com.sterlingsworld.core.ui.components.BathroomMapButton

@Composable
fun CreameryScreen(onPlay: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_cognitive_creamery),
            contentDescription = "Cognitive Creamery",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        // Play button overlaid on the "Start Quest" sign drawn in the center of the artwork
        ArtworkTapTarget(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 50.dp)
                .size(width = 340.dp, height = 300.dp),
            onTap = onPlay,
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E241F).copy(alpha = 0.9f))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "TREAT YOUR BRAIN - DISCOVER NEW FLAVOR WORLDS!",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "ATTRACTION NO. 42 - THE FLAVOR QUEST",
                    color = Color(0xFFB0A080),
                    fontSize = 10.sp,
                )
            }
        }
    }
}
