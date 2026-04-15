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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.DashedCornerButton

private val arcadeGames = listOf(
    "Lucky Paws"         to "lucky_paws",
    "Gauntlet"           to "gauntlet",
    "Symptom Striker"    to "symptom_striker",
    "Creamery"           to "creamery",
    "Relaxation Retreat" to "relaxation_retreat",
)

@Composable
fun GrandArcadeIndoorScreen(onGameSelected: (String) -> Unit = {}) {
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
            arcadeGames.forEach { (label, route) ->
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF4285F4).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .background(Color(0xFF4285F4).copy(alpha = 0.15f))
                        .clickable { onGameSelected(route) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = Color(0xFF00CCFF), fontSize = 18.sp)
                }
            }
        }
    }
}
