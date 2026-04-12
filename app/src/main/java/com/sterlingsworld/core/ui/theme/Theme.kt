package com.sterlingsworld.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SterlingColorScheme = lightColorScheme(
    primary            = Primary,
    onPrimary          = Surface,
    primaryContainer   = SurfaceStrong,
    onPrimaryContainer = TextPrimary,
    secondary          = Secondary,
    onSecondary        = Surface,
    tertiary           = Accent,
    onTertiary         = TextPrimary,
    background         = Background,
    onBackground       = TextPrimary,
    surface            = Surface,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceStrong,
    onSurfaceVariant   = TextMuted,
    outline            = Border,
    error              = ErrorColor,
    onError            = Surface,
)

private val KidzColorScheme = lightColorScheme(
    primary            = KidzPrimary,
    onPrimary          = KidzSurface,
    primaryContainer   = KidzSurfaceStrong,
    onPrimaryContainer = KidzTextPrimary,
    secondary          = KidzSecondary,
    onSecondary        = KidzSurface,
    tertiary           = KidzAccent,
    onTertiary         = KidzTextPrimary,
    background         = KidzBackground,
    onBackground       = KidzTextPrimary,
    surface            = KidzSurface,
    onSurface          = KidzTextPrimary,
    surfaceVariant     = KidzSurfaceStrong,
    onSurfaceVariant   = KidzTextMuted,
    outline            = KidzBorder,
    error              = ErrorColor, // Share error color
    onError            = KidzSurface,
)

@Composable
fun MeetSterlingTheme(
    isKidz: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isKidz) KidzColorScheme else SterlingColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = SterlingTypography,
        content     = content,
    )
}
