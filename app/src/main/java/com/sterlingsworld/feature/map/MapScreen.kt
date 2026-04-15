package com.sterlingsworld.feature.map

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R
import com.sterlingsworld.core.navigation.Screen

@Composable
fun MapScreen(onNavigateToZone: (route: String) -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_theme_park_map),
            contentDescription = "Theme Park Map",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // Music Land — left center
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, bottom = 40.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xD92D2D2D))
                .padding(horizontal = 48.dp, vertical = 20.dp)
                .clickable { onNavigateToZone(Screen.Studio.route) },
        ) {
            Text("Music Land", color = Color.White, fontSize = 22.sp)
        }
        // Movie Land — right center
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp, bottom = 80.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xD92D2D2D))
                .padding(horizontal = 48.dp, vertical = 20.dp)
                .clickable { onNavigateToZone(Screen.Cinema.route) },
        ) {
            Text("Movie Land", color = Color.White, fontSize = 22.sp)
        }
        // Games Land — bottom left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 120.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xD92D2D2D))
                .padding(horizontal = 48.dp, vertical = 20.dp)
                .clickable { onNavigateToZone(Screen.Arcade.route) },
        ) {
            Text("Games Land", color = Color.White, fontSize = 22.sp)
        }
        // Kid Zone — bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 40.dp, bottom = 160.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xD92D2D2D))
                .padding(horizontal = 48.dp, vertical = 20.dp)
                .clickable { onNavigateToZone(Screen.Kidz.route) },
        ) {
            Text("Kid Zone", color = Color.White, fontSize = 22.sp)
        }
    }
}
