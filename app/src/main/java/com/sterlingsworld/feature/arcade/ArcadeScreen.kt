package com.sterlingsworld.feature.arcade

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
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.domain.model.GameDefinition
import com.sterlingsworld.domain.model.GameSection

@Composable
fun ArcadeScreen(onGameSelected: (gameId: String) -> Unit) {
    val games = GameCatalog.bySection(GameSection.GAMES)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(games, key = { it.id }) { game ->
            GameCard(
                game = game,
                isEnabled = GameCatalog.isShipReady(game.id),
                onClick = { onGameSelected(game.id) },
            )
        }
    }
}

@Composable
fun GameCard(
    game: GameDefinition,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.65f)
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (isEnabled) game.accentLabel else "Unavailable",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Accent.copy(alpha = 0.2f),
                        labelColor = Primary,
                    ),
                )
            }
            Text(
                text = game.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
            Text(
                text = if (isEnabled) {
                    "~${game.estimatedDurationSec / 60} min - ${game.difficulty.name.lowercase().replace('_', ' ')}"
                } else {
                    "Not in the current playable build"
                },
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
            )
        }
    }
}
