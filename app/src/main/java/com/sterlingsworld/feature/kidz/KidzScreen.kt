package com.sterlingsworld.feature.kidz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.sterlingsworld.core.ui.theme.Accent
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Secondary
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.data.catalog.KidzCatalog
import com.sterlingsworld.domain.model.KidzActivity

@Composable
fun KidzScreen(
    onGameSelected: (gameId: String) -> Unit,
    onVideoSelected: (videoId: String) -> Unit,
) {
    val activities = KidzCatalog.activities

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(activities, key = { it.id }) { activity ->
            val isEnabled = when (activity) {
                is KidzActivity.KidzGame -> GameCatalog.isShipReady(activity.gameId)
                is KidzActivity.KidzVideo -> true
            }
            KidzActivityCard(
                activity = activity,
                isEnabled = isEnabled,
                onClick = {
                    when (activity) {
                        is KidzActivity.KidzGame -> onGameSelected(activity.gameId)
                        is KidzActivity.KidzVideo -> onVideoSelected(activity.video.id)
                    }
                },
            )
        }
    }
}

@Composable
private fun KidzActivityCard(activity: KidzActivity, isEnabled: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.65f)
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                val subtitle = when (activity) {
                    is KidzActivity.KidzGame ->
                        if (isEnabled) "Game" else "Not in the current playable build"
                    is KidzActivity.KidzVideo -> "Video"
                }
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
            val chipColor = when (activity) {
                is KidzActivity.KidzGame -> Accent.copy(alpha = 0.2f)
                is KidzActivity.KidzVideo -> Secondary.copy(alpha = 0.2f)
            }
            val chipLabel = when (activity) {
                is KidzActivity.KidzGame -> if (isEnabled) "Play" else "Unavailable"
                is KidzActivity.KidzVideo -> "Watch"
            }
            SuggestionChip(
                onClick = { if (isEnabled) onClick() },
                label = { Text(chipLabel, style = MaterialTheme.typography.labelMedium) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = chipColor,
                    labelColor = Primary,
                ),
            )
        }
    }
}
