package com.sterlingsworld.feature.nostalgia

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.core.ui.components.BathroomMapButton

@Composable
fun NostalgiaScreen(onPlay: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020A02)),
    ) {
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "BREAKOUT",
                color = Color(0xFF33FF33),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
            Text(
                "NOSTALGIA",
                color = Color(0xFF33FF33).copy(alpha = 0.45f),
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0A3A0A))
                    .clickable { onPlay() }
                    .padding(horizontal = 64.dp, vertical = 14.dp),
            ) {
                Text("Play", color = Color(0xFF33FF33), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF020A02))
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Nostalgia",
                    color = Color(0xFF33FF33),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Retro breakout — bounce, collect, retry",
                    color = Color(0xFF33FF33).copy(alpha = 0.50f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}
