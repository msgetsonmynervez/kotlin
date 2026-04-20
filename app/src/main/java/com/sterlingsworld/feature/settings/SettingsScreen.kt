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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.core.di.LocalAppContainer
import com.sterlingsworld.core.ui.components.BathroomMapButton
import com.sterlingsworld.core.ui.components.DashedCornerButton

@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    val container = LocalAppContainer.current
    val vm: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            prefs = container.preferencesRepository,
            progress = container.gameProgressRepository,
        ),
    )
    val soundEnabled by vm.soundEnabled.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_guest_relations),
            contentDescription = "Settings",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        DashedCornerButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClick = onBack,
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Switch(checked = soundEnabled, onCheckedChange = vm::setSoundEnabled)
            Text("Sound", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
