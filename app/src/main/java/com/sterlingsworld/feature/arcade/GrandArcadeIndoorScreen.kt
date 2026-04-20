package com.sterlingsworld.feature.arcade

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myelin.game.android.NativeGameRegistry
import com.sterlingsworld.R
import com.sterlingsworld.core.navigation.Screen
import com.sterlingsworld.core.ui.components.BathroomMapButton
import com.sterlingsworld.core.ui.components.DashedCornerButton
import com.sterlingsworld.data.catalog.GameCatalog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ArcadeEntry(
    val gameId: String,
    val route: String,
    val isLive: Boolean,
    val description: String,
    @param:DrawableRes val thumbnailRes: Int,
)

private val arcadeGames = listOf(
    ArcadeEntry(
        gameId = "symptom-striker",
        route = Screen.SymptomStriker.route,
        isLive = true,
        description = "Flagship strategy battles with readable turns and crisp payoff.",
        thumbnailRes = R.drawable.bg_symptom_striker,
    ),
    ArcadeEntry(
        gameId = "cognitive-creamery",
        route = Screen.Creamery.route,
        isLive = true,
        description = "Memory and focus rounds staged like a warm neon brain-training parlor.",
        thumbnailRes = R.drawable.bg_cognitive_creamery,
    ),
    ArcadeEntry(
        gameId = NativeGameRegistry.GAME_ID_SPOON_GAUNTLET,
        route = Screen.Gauntlet.route,
        isLive = true,
        description = "Premium narrative runs with choices that visibly reshape the route.",
        thumbnailRes = R.drawable.bg_spoon_gauntlet,
    ),
    ArcadeEntry(
        gameId = "relaxation-retreat",
        route = Screen.RelaxationRetreat.route,
        isLive = true,
        description = "Short, restorative mini-games designed to reset the pace without friction.",
        thumbnailRes = R.drawable.bg_relaxation_retreat,
    ),
    ArcadeEntry(
        gameId = "aol",
        route = Screen.Aol.route,
        isLive = true,
        description = "A sharper battle-theme remix with fast retries and a louder attitude.",
        thumbnailRes = R.drawable.bg_aol,
    ),
    ArcadeEntry(
        gameId = NativeGameRegistry.GAME_ID_ACCESS_QUEST,
        route = Screen.GamePlayer.withId(NativeGameRegistry.GAME_ID_ACCESS_QUEST),
        isLive = false,
        description = "Mobility-first route planning with hazards, pacing, and comeback checkpoints.",
        thumbnailRes = R.drawable.bg_grand_arcadie,
    ),
    ArcadeEntry(
        gameId = NativeGameRegistry.GAME_ID_ACCESS_RACER,
        route = Screen.GamePlayer.withId(NativeGameRegistry.GAME_ID_ACCESS_RACER),
        isLive = false,
        description = "Assistive-ride racing built around readable handling and clean touch controls.",
        thumbnailRes = R.drawable.bg_grand_arcadie,
    ),
    ArcadeEntry(
        gameId = "Myelin Protocol",
        route = Screen.TechnicalDifficulties.route,
        isLive = false,
        description = "A deep protocol experience. Details coming soon.",
        thumbnailRes = R.drawable.bg_grand_arcadie,
    ),
    ArcadeEntry(
        gameId = "lucky-paws",
        route = Screen.LuckyPaws.route,
        isLive = false,
        description = "Reward reveals, cozy pet energy, and short replay-friendly runs.",
        thumbnailRes = R.drawable.bg_lucky_paws,
    ),
)

@Composable
fun GrandArcadeIndoorScreen(onGameSelected: (String) -> Unit = {}, onBack: () -> Unit = {}) {
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollBack by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 1 || listState.firstVisibleItemScrollOffset > 240
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_grand_arcade_indoor),
            contentDescription = "Arcade Interior",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.matchParentSize(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.80f)),
                        startY = 260f,
                    ),
                ),
        )
        DashedCornerButton(Modifier.align(Alignment.TopStart).padding(16.dp), onClick = onBack)
        BathroomMapButton(Modifier.align(Alignment.TopEnd).padding(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 90.dp,
                bottom = 28.dp + navBarBottom,
            ),
        ) {
            item {
                Spacer(modifier = Modifier.height(220.dp))
            }
            itemsIndexed(arcadeGames, key = { _, item -> item.gameId }) { index, entry ->
                NeonArcadeCard(
                    entry = entry,
                    index = index,
                    onPlay = { onGameSelected(entry.route) },
                )
            }
        }

        if (showScrollBack) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = navBarBottom + 18.dp)
                    .shadow(18.dp, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(Color(0xF0141414))
                    .border(1.5.dp, Color(0xCCF4B942), CircleShape)
                    .clickable {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Scroll to top",
                        tint = Color(0xFFF4B942),
                    )
                    Text(
                        text = "TOP",
                        color = Color(0xFFF4B942),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun NeonArcadeCard(
    entry: ArcadeEntry,
    index: Int,
    onPlay: () -> Unit,
) {
    val context = LocalContext.current
    val info = GameCatalog.byId(entry.gameId)
    val title = info?.title ?: entry.gameId
    val slideAnim = remember(entry.gameId) { Animatable(36f) }
    val alphaAnim = remember(entry.gameId) { Animatable(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borderBrush = if (entry.isLive) {
        Brush.linearGradient(listOf(Color(0xFF00F2FF), Color(0xFFFF007F)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF6B7280), Color(0xFF4B5563)))
    }

    LaunchedEffect(entry.gameId) {
        delay(index * 100L)
        launch {
            slideAnim.animateTo(0f, animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing))
        }
        alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = slideAnim.value
                alpha = alphaAnim.value
                scaleX = if (isPressed && entry.isLive) 1.02f else 1f
                scaleY = if (isPressed && entry.isLive) 1.02f else 1f
            }
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.75f))
            .border(
                BorderStroke(
                    width = 2.dp,
                    brush = borderBrush,
                ),
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(
                enabled = entry.isLive,
                interactionSource = interactionSource,
                indication = null,
            ) { },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.45f)),
            ) {
                Image(
                    painter = painterResource(id = entry.thumbnailRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    colorFilter = if (entry.isLive) null else grayscaleFilter(),
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = entry.description,
                    color = Color(0xFFB6B8C3),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (entry.isLive) {
                val playInteraction = remember { MutableInteractionSource() }
                val playPressed by playInteraction.collectIsPressedAsState()
                Box(
                    modifier = Modifier
                        .shadow(18.dp, CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF00F2FF), Color(0xFFFF007F))))
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0x5500F2FF),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f, size.height / 2f),
                            )
                        }
                        .clickable(
                            interactionSource = playInteraction,
                            indication = null,
                        ) {
                            context.vibrateArcade(20)
                            onPlay()
                        }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .graphicsLayer {
                            scaleX = if (playPressed) 0.98f else 1f
                            scaleY = if (playPressed) 0.98f else 1f
                        },
                ) {
                    Text(
                        text = "PLAY",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            } else {
                Text(
                    text = "LOCKED",
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        if (!entry.isLive) {
            Text(
                text = "COMING SOON",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.62f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}

private fun grayscaleFilter(): ColorFilter {
    return ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix().apply {
            setToSaturation(0f)
        },
    )
}

private fun Context.vibrateArcade(durationMs: Long) {
    val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    val duration = durationMs.coerceIn(1L, 80L)
    val target = vibrator ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        target.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        target.vibrate(duration)
    }
}
