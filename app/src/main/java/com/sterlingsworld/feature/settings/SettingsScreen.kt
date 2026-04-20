package com.sterlingsworld.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

private val SettingsCardShape = RoundedCornerShape(22.dp)
private val SettingsCardColor = Color(0x99000000)
private val SettingsCardBorder = Color.White.copy(alpha = 0.12f)
private val SettingsOverlay = Brush.verticalGradient(
    colors = listOf(Color(0xCC000000), Color(0x66000000), Color(0xCC000000)),
)
private val ToggleOn = Color(0xFF34C759)

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
    val voiceoverEnabled by vm.voiceoverEnabled.collectAsStateWithLifecycle()
    val hapticEnabled by vm.hapticEnabled.collectAsStateWithLifecycle()
    var showAbout by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_guest_relations),
            contentDescription = "Settings",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SettingsOverlay),
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
            label = "Bathroom Finder",
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .padding(top = 88.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(20.dp))

            SettingsToggleCard(
                label = "Sound",
                icon = Icons.Filled.VolumeUp,
                checked = soundEnabled,
                onCheckedChange = vm::setSoundEnabled,
            )
            Spacer(modifier = Modifier.height(14.dp))
            SettingsToggleCard(
                label = "Voiceover",
                icon = Icons.Filled.Mic,
                checked = voiceoverEnabled,
                onCheckedChange = vm::setVoiceoverEnabled,
            )
            Spacer(modifier = Modifier.height(14.dp))
            SettingsToggleCard(
                label = "Haptic Feedback",
                icon = Icons.Filled.PhoneAndroid,
                checked = hapticEnabled,
                onCheckedChange = vm::setHapticEnabled,
            )
            Spacer(modifier = Modifier.height(14.dp))
            SettingsActionCard(
                label = "About Sterling's World",
                icon = Icons.Filled.Info,
                onClick = { showAbout = true },
            )
        }
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("About Sterling's World") },
            text = {
                Text(
                    "Sterling's World is a park-style wellness app with arcade games, music, cinema, and kid-safe experiences.",
                )
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text("Close")
                }
            },
        )
    }
}

@Composable
private fun SettingsToggleCard(
    label: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        shape = SettingsCardShape,
        colors = CardDefaults.cardColors(containerColor = SettingsCardColor),
        border = BorderStroke(1.dp, SettingsCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                IconBadge(icon = icon)
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ToggleOn,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF5F6368),
                ),
            )
        }
    }
}

@Composable
private fun SettingsActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Card(
        shape = SettingsCardShape,
        colors = CardDefaults.cardColors(containerColor = SettingsCardColor),
        border = BorderStroke(1.dp, SettingsCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                IconBadge(icon = icon)
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9AA0A6),
            )
        }
    }
}

@Composable
private fun IconBadge(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(Color.White.copy(alpha = 0.08f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}
