package com.sterlingsworld.feature.symptomstriker

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
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.ArtworkTapTarget
import com.sterlingsworld.core.ui.components.BathroomMapButton

@Composable
fun SymptomStrikerScreen(onPlay: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_symptom_striker),
            contentDescription = "Symptom Striker RPG",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        BathroomMapButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        )
        // Positioned over the "ENTER GAUNTLET" button drawn into the artwork at ~bottom 15%
        ArtworkTapTarget(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .size(width = 340.dp, height = 300.dp),
            onTap = onPlay,
        )
    }
}
