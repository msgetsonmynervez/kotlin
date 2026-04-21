// Module build file for the app module of the Frogger Compose game.
// This file applies the Android application and Kotlin plugins and
// configures Compose support and dependencies.

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.frogger"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.frogger"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    // Enable Jetpack Compose for this module
    buildFeatures {
        compose = true
    }
    composeOptions {
        // The Kotlin compiler extension version is tied to the Compose BOM.
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Use the latest Gradle plugin APIs where possible
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Use the Compose Bill of Materials to manage version alignment for all
    // Compose libraries. See the December 2025 Jetpack Compose release notes
    // which recommend using the BOM version 2025.12.00【261692908895787†L53-L60】.
    implementation(platform("androidx.compose:compose-bom:2025.12.00"))

    // Core Compose UI libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Lifecycle and activity support
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Debug tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}