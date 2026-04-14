package com.sterlingsworld.feature.arcade

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.background
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.DashedCornerButton

@Composable
fun GrandArcadeIndoorScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_grand_arcade_indoor),
            contentDescription = "Arcade Interior",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        DashedCornerButton(Modifier.align(Alignment.TopStart).padding(16.dp))
        DashedCornerButton(Modifier.align(Alignment.TopEnd).padding(16.dp))
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(5) {
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF4285F4).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .background(Color(0xFF4285F4).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Label", color = Color(0xFF00CCFF), fontSize = 18.sp)
                }
            }
        }
    }
}
