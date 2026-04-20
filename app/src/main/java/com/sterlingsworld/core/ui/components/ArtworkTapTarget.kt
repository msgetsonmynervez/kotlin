package com.sterlingsworld.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@Composable
fun ArtworkTapTarget(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTap: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier.clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onTap,
        ),
    ) {
        if (isPressed) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.dp, Color.White.copy(alpha = 0.34f), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .blur(4.dp),
            )
        }
    }
}
