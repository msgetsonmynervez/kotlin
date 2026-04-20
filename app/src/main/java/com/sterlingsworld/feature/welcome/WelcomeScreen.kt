package com.sterlingsworld.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sterlingsworld.R
import com.sterlingsworld.core.di.LocalAppContainer
import com.sterlingsworld.core.ui.components.ArtworkTapTarget
import com.sterlingsworld.core.ui.components.BathroomMapButton

@Composable
fun WelcomeScreen(
    onEnterPark: () -> Unit = {},
) {
    val container = LocalAppContainer.current
    val vm: WelcomeViewModel = viewModel(
        factory = WelcomeViewModel.Factory(container.preferencesRepository),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_welcome_entrance),
            contentDescription = "Welcome Entrance",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        ArtworkTapTarget(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(width = 280.dp, height = 240.dp),
            onTap = {
                vm.onEnterPark()
                onEnterPark()
            },
        )
    }
}
