// Top‑level build file for the Frogger Compose game.
// This file declares build plugins and global configuration that
// are shared across all modules (in this case just the app module).

plugins {
    // Apply the Android application plugin to the project. The
    // version here should match the installed Android Gradle Plugin.
    id("com.android.application") version "8.2.0" apply false
    // Apply the Kotlin Android plugin to enable Kotlin support.
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// Optionally define repositories that will be used for all modules.
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}