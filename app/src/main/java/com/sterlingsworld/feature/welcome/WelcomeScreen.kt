package com.sterlingsworld.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.MeetSterlingApplication
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.SurfaceStrong
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.core.util.rememberAssetBitmap

@Composable
fun WelcomeScreen(onEnterPark: () -> Unit) {
    val app = LocalContext.current.applicationContext as MeetSterlingApplication
    val vm: WelcomeViewModel = viewModel(
        factory = WelcomeViewModel.Factory(app.preferencesRepository),
    )
    val state by vm.uiState.collectAsStateWithLifecycle()

    val mascotBitmap = rememberAssetBitmap("images/mascot/sterling_wave.png")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .clickable(enabled = !state.isLoading) {
                vm.onEnterPark()
                onEnterPark()
            },
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(color = Primary)
        } else {
            Column(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            color = if (mascotBitmap == null) SurfaceStrong else Background,
                            shape = RoundedCornerShape(16.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (mascotBitmap != null) {
                        Image(
                            bitmap = mascotBitmap,
                            contentDescription = "Sterling the mascot",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    } else {
                        // Fallback only shown if asset copy is incomplete
                        Text(
                            text = "Sterling",
                            color = Primary,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }

                Text(
                    text = "Welcome to MeetSterling",
                    color = TextPrimary,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = state.message,
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )

                Button(
                    onClick = {
                        vm.onEnterPark()
                        onEnterPark()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "Enter the Park",
                        color = Surface,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
