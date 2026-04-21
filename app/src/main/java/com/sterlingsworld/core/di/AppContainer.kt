package com.sterlingsworld.core.di

import androidx.compose.runtime.staticCompositionLocalOf
import com.sterlingsworld.domain.repository.AppPreferencesRepository
import com.sterlingsworld.domain.repository.GameProgressRepository

data class AppContainer(
    val preferencesRepository: AppPreferencesRepository,
    val gameProgressRepository: GameProgressRepository,
)

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("LocalAppContainer not provided — wrap your content with CompositionLocalProvider")
}
