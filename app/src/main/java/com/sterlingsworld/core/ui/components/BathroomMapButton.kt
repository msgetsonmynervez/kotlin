package com.sterlingsworld.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.core.ui.theme.Accent
import com.sterlingsworld.core.util.openNearbyBathroomMap

@Composable
fun BathroomMapButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xF0141414))
            .border(width = 1.5.dp, color = Accent.copy(alpha = 0.7f), shape = CircleShape)
            .clickable { openNearbyBathroomMap(context) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "WC",
            color = Accent,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
        )
    }
}
