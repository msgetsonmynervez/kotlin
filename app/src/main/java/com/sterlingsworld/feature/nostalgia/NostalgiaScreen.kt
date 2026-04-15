package com.sterlingsworld.feature.nostalgia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun NostalgiaScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center,
    ) {
        Text("Nostalgia", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
