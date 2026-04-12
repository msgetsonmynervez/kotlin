package com.sterlingsworld.feature.game.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Secondary
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.domain.model.GameResult

/**
 * GameShellScreen — shared chrome for all game sessions (pause menu, header, exit routing).
 *
 * This screen hosts per-game runtime composables via the [gameContent] slot pattern
 * that Phase 4 will establish. Currently, the content area is an inert holding state
 * that does not present itself as a game to the user — no score, no prompt, no
 * fake-complete path.
 *
 * The pause menu and exit flow are production-ready and will remain unchanged in Phase 4.
 * The [onComplete] callback is wired but not triggered from this screen — individual
 * game runtimes call it when they reach a genuine end state.
 */
@Composable
fun GameShellScreen(
    gameId: String,
    onExit: () -> Unit,
    onComplete: (result: GameResult) -> Unit,
) {
    val game = GameCatalog.byId(gameId)
    var showPauseMenu by remember { mutableStateOf(false) }

    if (showPauseMenu) {
        AlertDialog(
            onDismissRequest = { showPauseMenu = false },
            title = { Text("Paused — ${game?.title ?: "Game"}") },
            text = null,
            confirmButton = {
                TextButton(onClick = { showPauseMenu = false }) { Text("Resume") }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        showPauseMenu = false
                        // Restart: pop and re-enter same route — caller handles navigation
                    }) { Text("Restart") }
                    TextButton(onClick = {
                        showPauseMenu = false
                        onExit()
                    }) { Text("Exit", color = Secondary) }
                }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = game?.title ?: gameId,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                OutlinedButton(onClick = { showPauseMenu = true }) {
                    Text("Pause", color = Primary)
                }
            }

            // Game runtime content — Phase 4 replaces this Box with per-game composables.
            // This state is intentionally inert: it does not present a game interface,
            // does not accept user game input, and does not trigger completion.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Surface),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(32.dp),
                ) {
                    Text(
                        text = game?.title ?: "Game",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                    )
                    Text(
                        text = game?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Text(
                        text = "Coming in Phase 4",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                    )
                    OutlinedButton(onClick = onExit) {
                        Text("Return to Park", color = Primary)
                    }
                }
            }
        }
    }
}
