package com.sterlingsworld.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import com.sterlingsworld.core.ui.theme.Accent

@Composable
fun DashedCornerButton(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Box(
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xF0141414))
            .border(width = 1.5.dp, color = Accent.copy(alpha = 0.7f), shape = CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = if (onClick != null) "Back" else null,
            tint = Accent,
            modifier = Modifier.size(24.dp),
        )
    }
}
