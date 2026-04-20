package com.sterlingsworld.feature.relaxationretreat

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
fun RelaxationRetreatScreen(onPlay: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_relaxation_retreat),
            contentDescription = "Relaxation Retreat",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
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
                    .background(Color(0xEE111111))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Relaxation Retreat",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "A calm corner for gentle reset activities",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}
