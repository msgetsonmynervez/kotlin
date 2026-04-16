package com.sterlingsworld.feature.kidz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val kidzGames = listOf(
    "Lumi's Star Quest" to "lumi_star_quest",
    "Doodle"            to "doodle",
    "Linebreaker"       to "linebreaker",
    "Nostalgia"         to "nostalgia",
)

@Composable
fun KidzGamesScreen(onGameSelected: (String) -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            kidzGames.forEach { (label, route) ->
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color(0xFF81C784).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .background(Color(0xFF81C784).copy(alpha = 0.15f))
                        .clickable { onGameSelected(route) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = Color(0xFF81C784), fontSize = 18.sp)
                }
            }
        }
    }
}
