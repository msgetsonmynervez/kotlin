package com.sterlingsworld.feature.kidz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun KidzGameshellScreen(
    onGamesLand: () -> Unit = {},
    onStorybookLand: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_kidz_gameshell),
            contentDescription = "Kidz Gameshell Split Map",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Side — Games Land
            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onGamesLand() }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF212121).copy(alpha = 0.9f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("SECTION 1:", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("GAMES LAND!", color = Color.White, fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray.copy(alpha = 0.85f))
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                ) {
                    Text("Label", color = Color.White)
                }
            }
            // Right Side — Storybook Land
            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onStorybookLand() }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF424242).copy(alpha = 0.9f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("SECTION 2:", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("STORYBOOK LAND!", color = Color.White, fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray.copy(alpha = 0.85f))
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                ) {
                    Text("Label", color = Color.White)
                }
            }
        }
    }
}
