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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.MeetSterlingApplication
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Secondary
import com.sterlingsworld.data.catalog.GameCatalog
import com.sterlingsworld.domain.model.GameResult

@Composable
fun GameShellScreen(
    gameId: String,
    onExit: () -> Unit,
    onRestart: () -> Unit,
    onComplete: (result: GameResult) -> Unit,
    gameContent: @Composable (onComplete: (GameResult) -> Unit) -> Unit,
) {
    val app = LocalContext.current.applicationContext as MeetSterlingApplication
    val vm: GameShellViewModel = viewModel(
        factory = GameShellViewModel.Factory(
            gameId = gameId,
            progressRepository = app.gameProgressRepository,
        ),
    )
    val uiState by vm.uiState.collectAsState()
    val game = GameCatalog.byId(gameId)
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(vm, lifecycleOwner) {
        vm.events.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect { event ->
            when (event) {
                GameShellEvent.Exit -> onExit()
                GameShellEvent.Restart -> onRestart()
                is GameShellEvent.Complete -> onComplete(event.result)
            }
        }
    }

    if (uiState.gamePhase == GamePhase.PAUSED) {
        AlertDialog(
            onDismissRequest = vm::onResume,
            title = { Text("Paused - ${game?.title ?: "Game"}") },
            text = null,
            confirmButton = {
                TextButton(onClick = vm::onResume) {
                    Text("Resume")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = vm::onRestart) {
                        Text("Restart")
                    }
                    TextButton(onClick = vm::onExit) {
                        Text("Exit", color = Secondary)
                    }
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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = game?.title ?: gameId,
                    style = MaterialTheme.typography.titleMedium,
                )
                OutlinedButton(onClick = vm::onPause) {
                    Text("Pause", color = Primary)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                gameContent { result -> vm.onComplete(result) }
            }
        }
    }
}
