package com.sterlingsworld.feature.aol

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
fun AolScreen(onPlay: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_aol),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "ARMOR OF LIGHT",
                color = Color(0xFFE8DCC8),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
            )
            Text(
                "AOL",
                color = Color(0xFFE8DCC8).copy(alpha = 0.45f),
                fontSize = 13.sp,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        // Play button aligned to the archway portal at center with a downward offset
        ArtworkTapTarget(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 72.dp)
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
                    .background(Color(0xFF0D0D1A).copy(alpha = 0.95f))
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Armor of Light",
                    color = Color(0xFFE8DCC8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "A battle of light and faith",
                    color = Color(0xFFE8DCC8).copy(alpha = 0.50f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}
