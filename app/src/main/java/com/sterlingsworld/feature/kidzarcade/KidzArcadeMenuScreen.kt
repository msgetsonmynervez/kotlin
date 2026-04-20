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
import com.sterlingsworld.core.ui.components.BathroomMapButton
import com.sterlingsworld.core.ui.components.DashedCornerButton

private data class KidzArcadeEntry(val label: String, val sublabel: String)

private val kidzArcadeGames = listOf(
    KidzArcadeEntry("Lumi's Star Quest", "Fantasy adventure"),
    KidzArcadeEntry("Doodle Land",       "Save Doodle Land!"),
    KidzArcadeEntry("Nostalgia",         "Retro breakout"),
    KidzArcadeEntry("Linebreaker",       "Arcade puzzling"),
    KidzArcadeEntry("Snail's Journey",   "A gentle stroll"),
)

@Composable
fun KidzArcadeMenuScreen(onMenuItemClick: (Int) -> Unit = {}, onBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_kidz_arcade),
            contentDescription = "Kidz Arcade Menu",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        DashedCornerButton(Modifier.align(Alignment.TopStart).padding(16.dp), onClick = onBack)
        BathroomMapButton(Modifier.align(Alignment.TopEnd).padding(16.dp))

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            kidzArcadeGames.forEachIndexed { index, entry ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                        .background(Color(0xFF111111).copy(alpha = 0.72f))
                        .clickable { onMenuItemClick(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = entry.label,
                            color = Color.White.copy(alpha = 0.90f),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = entry.sublabel,
                            color = Color.White.copy(alpha = 0.50f),
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}
