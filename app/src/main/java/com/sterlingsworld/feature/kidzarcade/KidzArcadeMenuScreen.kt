package com.sterlingsworld.feature.kidzarcade

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.sterlingsworld.core.ui.components.DashedCornerButton

private val kidzArcadeGames = listOf(
    "Lumi's Star Quest",
    "Doodle Land",
    "Linebreaker",
    "Nostalgia",
)

@Composable
fun KidzArcadeMenuScreen(onMenuItemClick: (Int) -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_kidz_arcade),
            contentDescription = "Kidz Arcade Menu",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        DashedCornerButton(Modifier.align(Alignment.TopStart).padding(16.dp))
        DashedCornerButton(Modifier.align(Alignment.TopEnd).padding(16.dp))

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            kidzArcadeGames.forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color(0xFF42A5F5), RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.05f))
                        .clickable { onMenuItemClick(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = Color(0xFF42A5F5),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
