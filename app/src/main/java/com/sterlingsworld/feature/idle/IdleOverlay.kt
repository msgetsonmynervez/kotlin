package com.sterlingsworld.feature.idle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sterlingsworld.core.ui.theme.Overlay
import com.sterlingsworld.core.ui.theme.Surface

@Composable
fun IdleOverlay(onResume: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Overlay)
            .clickable(onClick = onResume),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "Still here?",
                style = MaterialTheme.typography.displayLarge,
                color = Surface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Sterling is resting. Tap anywhere to continue.",
                style = MaterialTheme.typography.bodyLarge,
                color = Surface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }
}
