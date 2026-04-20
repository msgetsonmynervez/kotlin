package com.sterlingsworld.feature.linebreaker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.ArtworkTapTarget
@Composable
fun LinebreakerScreen(onPlay: () -> Unit = {}) {
    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_linebreaker),
            contentDescription = "Linebreaker Game",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        com.sterlingsworld.core.ui.components.BathroomMapButton(
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFEBE9).copy(alpha = 0.95f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "ARCADE PUZZLING AT ITS FINEST!",
                    color = Color(0xFF3E2723),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    "PLAYABLE NOW!",
                    color = Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
