package com.sterlingsworld.feature.doodle

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
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.ArtworkTapTarget
import com.sterlingsworld.core.ui.components.BathroomMapButton

@Composable
fun DoodleScreen(onPlay: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_doodle_land),
            contentDescription = "Doodle Land",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArtworkTapTarget(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .size(width = 340.dp, height = 300.dp),
                onTap = onPlay,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3C4043).copy(alpha = 0.95f))
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "COME SAVE DOODLE LAND! USE YOUR CREATIVITY!",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
