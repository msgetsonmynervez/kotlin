package com.sterlingsworld.feature.arcade

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.BathroomMapButton
import com.sterlingsworld.core.ui.components.DashedCornerButton

private data class ArcadeEntry(val label: String, val route: String, val isLive: Boolean)

private val arcadeGames = listOf(
    ArcadeEntry("Lucky Paws",         "lucky_paws",         isLive = true),
    ArcadeEntry("Symptom Striker",    "symptom_striker",    isLive = true),
    ArcadeEntry("Creamery",           "creamery",           isLive = true),
    ArcadeEntry("Gauntlet",           "gauntlet",           isLive = true),
    ArcadeEntry("Relaxation Retreat", "relaxation_retreat", isLive = true),
    ArcadeEntry("AOL",                "aol",                isLive = true),
    ArcadeEntry("Access Quest",       "access_quest",       isLive = false),
    ArcadeEntry("Wheelie Spoon Rush", "wheelie_spoon_rush", isLive = false),
    ArcadeEntry("MS Racer",           "ms_racer",           isLive = false),
    ArcadeEntry("Snail's Journey",    "snails_journey",     isLive = false),
)

@Composable
fun GrandArcadeIndoorScreen(onGameSelected: (String) -> Unit = {}, onBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_grand_arcade_indoor),
            contentDescription = "Arcade Interior",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        DashedCornerButton(Modifier.align(Alignment.TopStart).padding(16.dp), onClick = onBack)
        BathroomMapButton(Modifier.align(Alignment.TopEnd).padding(16.dp))
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            arcadeGames.forEach { entry ->
                val borderColor = if (entry.isLive)
                    Color(0xFF4285F4)
                else
                    Color.White.copy(alpha = 0.25f)
                val bgColor = if (entry.isLive)
                    Color(0xFF0D2B55).copy(alpha = 0.85f)
                else
                    Color(0xFF111111).copy(alpha = 0.70f)
                val textColor = if (entry.isLive) Color(0xFF5BC8FF) else Color.White.copy(alpha = 0.45f)

                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, borderColor, RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .let { m ->
                            if (entry.isLive) m.clickable { onGameSelected(entry.route) } else m
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (entry.isLive) {
                        Text(entry.label, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(entry.label, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("Coming Soon", color = Color.White.copy(alpha = 0.30f), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
