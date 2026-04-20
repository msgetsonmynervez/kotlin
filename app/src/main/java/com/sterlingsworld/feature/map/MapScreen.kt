package com.sterlingsworld.feature.map

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.sterlingsworld.R
import com.sterlingsworld.core.navigation.Screen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class MapZone(
    val route: String,
    val topFraction: Float,
    val heightFraction: Float,
    val horizontalPadding: Dp,
    val focusOffsetY: Dp,
)

private val mapZones = listOf(
    MapZone(route = Screen.Studio.route, topFraction = 0.22f, heightFraction = 0.18f, horizontalPadding = 16.dp, focusOffsetY = 44.dp),
    MapZone(route = Screen.Cinema.route, topFraction = 0.41f, heightFraction = 0.18f, horizontalPadding = 16.dp, focusOffsetY = 44.dp),
    MapZone(route = Screen.Arcade.route, topFraction = 0.60f, heightFraction = 0.18f, horizontalPadding = 16.dp, focusOffsetY = 44.dp),
    MapZone(route = Screen.Kidz.route, topFraction = 0.79f, heightFraction = 0.18f, horizontalPadding = 16.dp, focusOffsetY = 44.dp),
)

@Composable
fun MapScreen(onNavigateToZone: (route: String) -> Unit = {}) {
    val translateY = remember { Animatable(-90f) }
    val alpha = remember { Animatable(0f) }
    val rustle = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 45) }

    DisposableEffect(Unit) {
        onDispose { rustle.release() }
    }

    LaunchedEffect(Unit) {
        rustle.startTone(ToneGenerator.TONE_PROP_BEEP2, 45)
        delay(35)
        rustle.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        coroutineScope {
            launch {
                translateY.animateTo(0f, animationSpec = tween(520, easing = FastOutSlowInEasing))
            }
            launch {
                alpha.animateTo(1f, animationSpec = tween(420, easing = FastOutSlowInEasing))
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val heightPx = with(density) { maxHeight.toPx() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = translateY.value
                    this.alpha = alpha.value
                },
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_theme_park_map),
                contentDescription = "Theme Park Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            mapZones.forEach { zone ->
                val topOffset = with(density) { (heightPx * zone.topFraction).toDp() }
                MapZoneHotspot(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(zone.heightFraction)
                        .offset(y = topOffset)
                        .padding(horizontal = zone.horizontalPadding),
                    focusOffsetY = zone.focusOffsetY,
                    onTap = { onNavigateToZone(zone.route) },
                )
            }
        }
    }
}

@Composable
private fun MapZoneHotspot(
    modifier: Modifier = Modifier,
    focusOffsetY: Dp,
    onTap: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap,
            ),
    ) {
        if (isPressed) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(4.dp)
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, Color.White.copy(alpha = 0.30f), RoundedCornerShape(26.dp)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-focusOffsetY))
                    .size(width = 160.dp, height = 44.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.16f))
                    .border(1.dp, Color.White.copy(alpha = 0.38f), RoundedCornerShape(24.dp))
                    .blur(2.dp),
            )
        }
    }
}
