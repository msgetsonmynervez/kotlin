package com.sterlingsworld.feature.game.completion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.BathroomMapButton
import com.sterlingsworld.core.ui.theme.Accent
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.data.catalog.GameCatalog

@Composable
fun CompletionScreen(
    gameId: String = "",
    onReturnToPark: () -> Unit = {},
    onReplay: () -> Unit = {},
) {
    val game = GameCatalog.byId(gameId)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_completion_celebration),
            contentDescription = "Completion Celebration",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 40.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xCC1B1B1B))
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Run complete",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = game?.title ?: "Game Complete",
                style = MaterialTheme.typography.titleLarge,
                color = Accent,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Sterling says: Take what you need from this round.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted,
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onReplay,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Play Again", color = Primary)
                }
                Button(
                    onClick = onReturnToPark,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Back to Park", color = Surface)
                }
            }
        }
    }
}
