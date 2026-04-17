package com.sterlingsworld.feature.welcome

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.ActionButton
import com.sterlingsworld.core.ui.components.BathroomMapButton

@Composable
fun WelcomeScreen(
    onEnterPark: () -> Unit = {},
    onStartMyelinProtocol: (Context) -> Unit = {},
) {
    val context = androidx.compose.ui.platform.LocalContext.current

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
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 88.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ActionButton(
                label = "Start Myelin Protocol",
                onClick = { onStartMyelinProtocol(context) },
            )
            ActionButton(
                label = "Enter",
                onClick = onEnterPark,
            )
        }
    }
}
