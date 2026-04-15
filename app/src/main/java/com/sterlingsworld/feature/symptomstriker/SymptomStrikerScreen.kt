package com.sterlingsworld.feature.symptomstriker

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sterlingsworld.R

@Composable
fun SymptomStrikerScreen(onPlay: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_symptom_striker),
            contentDescription = "Symptom Striker RPG",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xD92D2D2D))
                .clickable { onPlay() }
                .padding(horizontal = 64.dp, vertical = 16.dp),
        ) {
            Text("Play", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
