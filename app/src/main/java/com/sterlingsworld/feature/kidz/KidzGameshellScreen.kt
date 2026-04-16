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
import com.sterlingsworld.core.ui.components.BathroomMapButton

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
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Side — Games Land
            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onGamesLand() }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1A1A).copy(alpha = 0.88f))
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Games Land", color = Color(0xFF81C784), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1A1A1A).copy(alpha = 0.88f))
                        .padding(horizontal = 32.dp, vertical = 14.dp),
                ) {
                    Text("Enter \u2192", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            // Right Side — Storybook Land
            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onStorybookLand() }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A1A35).copy(alpha = 0.88f))
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Storybook Land", color = Color(0xFFB39DDB), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2A1A35).copy(alpha = 0.88f))
                        .padding(horizontal = 32.dp, vertical = 14.dp),
                ) {
                    Text("Enter \u2192", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
