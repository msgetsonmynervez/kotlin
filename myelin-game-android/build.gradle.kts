import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Sync
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
}

val gdxNativesArm32 by configurations.creating
val gdxNativesArm64 by configurations.creating
val gdxNativesX86 by configurations.creating
val gdxNativesX64 by configurations.creating

fun nativeFileTrees(configuration: Configuration) =
    configuration.incoming.artifactView { }.files.elements.map { artifacts ->
        artifacts.map { zipTree(it.asFile) }
    }

val extractGdxNatives by tasks.registering(Sync::class) {
    into(layout.buildDirectory.dir("generated/gdx-jniLibs"))

    from(nativeFileTrees(gdxNativesArm32)) {
        include("libgdx.so")
        into("armeabi-v7a")
    }
    from(nativeFileTrees(gdxNativesArm64)) {
        include("libgdx.so")
        into("arm64-v8a")
    }
    from(nativeFileTrees(gdxNativesX86)) {
        include("libgdx.so")
        into("x86")
    }
    from(nativeFileTrees(gdxNativesX64)) {
        include("libgdx.so")
        into("x86_64")
    }
}

android {
    namespace = "com.myelin.game.android"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            assets.directories.add("../myelin-game-core/src/main/assets")
            assets.directories.add("src/main/assets")
            jniLibs.directories.add(layout.buildDirectory.dir("generated/gdx-jniLibs").get().asFile.invariantSeparatorsPath)
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.named("preBuild") {
    dependsOn(extractGdxNatives)
}

dependencies {
    implementation(project(":myelin-game-core"))
    implementation("com.badlogicgames.gdx:gdx:1.13.0")
    implementation("com.badlogicgames.gdx:gdx-backend-android:1.13.0")
    gdxNativesArm32("com.badlogicgames.gdx:gdx-platform:1.13.0:natives-armeabi-v7a")
    gdxNativesArm64("com.badlogicgames.gdx:gdx-platform:1.13.0:natives-arm64-v8a")
    gdxNativesX86("com.badlogicgames.gdx:gdx-platform:1.13.0:natives-x86")
    gdxNativesX64("com.badlogicgames.gdx:gdx-platform:1.13.0:natives-x86_64")
}
