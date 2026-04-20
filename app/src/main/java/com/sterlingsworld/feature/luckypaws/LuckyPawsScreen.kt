package com.sterlingsworld.feature.luckypaws

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.BathroomMapButton

@Composable
fun LuckyPawsScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_lucky_paws),
            contentDescription = "Lucky Paws Slots",
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
            Box(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xAA1A1A1A))
                    .padding(horizontal = 64.dp, vertical = 16.dp),
            ) {
                Text("Coming Soon", color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF9E7).copy(alpha = 0.9f))
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "TEST YOUR FORTUNE!",
                    color = Color(0xFF4E342E),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    "1 TOKEN PER SPIN!",
                    color = Color(0xFF6D4C41),
                    fontSize = 10.sp,
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.58f))
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                "UNAVAILABLE",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                letterSpacing = 2.sp,
            )
        }
    }
}
