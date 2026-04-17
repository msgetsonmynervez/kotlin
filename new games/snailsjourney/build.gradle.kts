plugins {
    // Apply the Kotlin JVM plugin to compile Kotlin sources.
    kotlin("jvm") version "1.9.21"
    application
}

repositories {
    mavenCentral()
    // LibGDX uses the JCenter repository. Although JCenter has been sunset, many
    // projects still mirror these artifacts to Maven Central. If a dependency
    // cannot be resolved, you may need to add a secondary repository mirror.
}

dependencies {
    implementation(kotlin("stdlib"))
    // Core LibGDX dependency provides all the classes used to build a game.
    implementation("com.badlogicgames.gdx:gdx:1.12.1")
    // Optional: include box2d or other extensions here if you extend the game later.
}

application {
    // Define the main class for the desktop launcher. The desktop launcher is
    // optional and can be removed when integrating into an Android app. It
    // allows you to run the game on the JVM for quick iteration.
    mainClass.set("com.example.snailsjourney.DesktopLauncher")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}