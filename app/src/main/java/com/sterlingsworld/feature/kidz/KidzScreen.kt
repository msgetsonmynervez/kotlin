package com.sterlingsworld.feature.kidz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R

@Composable
fun KidzScreen(
    onGameSelected: (gameId: String) -> Unit = {},
    onVideoSelected: (videoId: String) -> Unit = {},
    onEnterKidz: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_kid_zone),
            contentDescription = "Kid Zone",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xD91B2631))
                .padding(horizontal = 60.dp, vertical = 16.dp)
                .clickable { onEnterKidz() },
        ) {
            Text("Enter", color = Color.White, fontSize = 18.sp)
        }
    }
}
