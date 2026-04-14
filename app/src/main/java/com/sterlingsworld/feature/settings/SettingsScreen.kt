package com.sterlingsworld.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R

@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    var soundEnabled by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_guest_relations),
            contentDescription = "Settings",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
            Text("Sound", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
