# Wheelie Spoon Rush

Wheelie Spoon Rush is a one‑button auto‑scrolling platformer built with
Kotlin and libGDX.  You play as a cartoon spoon riding a power
wheelchair through a whimsical world filled with household hazards,
collectibles and hidden spoons.  The goal of the first level is to
reach the finish line while gathering as many standard collectibles as
possible and discovering all of the secret spoon items.  Contact with
enemies does not kill you; instead you lose some collected items and
continue your run.

This repository contains the complete Android project for the first
vertical slice of the game.  The code is organised to allow future
expansion to additional levels and platforms.

## Project Structure

The project uses a standard libGDX multi‑module layout with a shared
`core` module and an Android application module:

```
wheelie-spoon-rush/
├── android/               # Android launcher and resources
│   ├── src/main/
│   │   ├── kotlin/com/wheeliespoon/rush/AndroidLauncher.kt
│   │   └── res/mipmap-*/ic_launcher.png  # App icon in multiple densities
│   ├── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── core/                  # Cross‑platform game logic written in Kotlin
│   ├── src/game/
│   │   ├── assets/        # Procedural assets (generated white pixel)
│   │   ├── audio/
│   │   │   └── AudioManager.kt
│   │   ├── config/
│   │   │   └── Constants.kt
│   │   ├── entities/      # Player, platform, enemy and collectible classes
│   │   ├── level/
│   │   │   └── GameWorld.kt
│   │   ├── save/
│   │   │   └── SaveManager.kt
│   │   ├── screens/
│   │   │   ├── GameScreen.kt
│   │   │   ├── TitleScreen.kt
│   │   │   ├── SettingsScreen.kt
│   │   │   └── ResultsScreen.kt
│   │   ├── ui/
│   │   │   └── BasicSkin.kt
│   │   └── GameApp.kt
│   ├── assets/sfx/        # Simple generated sound effects
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

### Core module

The `core` module contains all of the shared game logic.  It uses
libGDX’s base library and Kotlin’s standard library.  Because this
module is free of Android dependencies it can be reused for future
backends, such as iOS or desktop.

Key subsystems include:

| Subsystem | Description |
|---|---|
| **GameApp** | Entry point that sets up the SpriteBatch and AssetManager and switches between screens |
| **Constants** | Centralised tuning parameters for world dimensions, jump speeds, colours and counts |
| **Entities** | Classes for the player, platforms (including ramps and moving platforms), collectibles and enemies |
| **GameWorld** | Manages the level, updates all entities, handles collisions and tracks progress |
| **Screens** | Implement the title, game, settings and results screens using scene2d.ui |
| **AudioManager** | Loads and plays short sound effects for jumps, collectibles, enemy hits and UI clicks |
| **SaveManager** | Persists high scores, secret completion and settings via libGDX `Preferences`【859772538648162†L15-L50】 |

### Android module

The `android` module packages the game for Android devices.  It
contains a minimal launcher (`AndroidLauncher.kt`) that starts the
`GameApp`, a manifest describing the activity, and app icons at
multiple densities.  The Gradle script pulls in libGDX’s Android
backend and native binaries.  When building for other platforms you
would add additional modules alongside this one.

## Setup and Build Instructions

The project uses Gradle and the Android Gradle plugin.  To build and
run the game on an Android device you need:

* **Android Studio** 2023.1 or later with the Android SDK installed.
* **Java 8 JDK** or higher.
* An Android device or emulator running Android 5.0 (API level 21) or
  newer.

To build and install the game:

1. Clone or extract this repository to your machine.
2. Open Android Studio and choose **Open an Existing Project**.
3. Select the `wheelie-spoon-rush` directory.  Gradle will
   synchronise and download the required dependencies.
4. Connect an Android device or start an emulator.
5. Click **Run**.  Android Studio will compile the `core` and `android`
   modules and deploy the app.

Alternatively, from the command line you can run:

```sh
./gradlew android:assembleDebug
./gradlew android:installDebug
```

## Asset Notes

* **Textures:** The first slice uses a procedurally generated 1×1 pixel
  texture tinted different colours for the player, platforms, enemies
  and collectibles.  This keeps the art simple and readable while
  placeholders for proper artwork are developed.
* **Sound effects:** Short sine‑wave beeps are generated for jump,
  double jump, collectible pickup, enemy hit, finish and UI clicks.  These
  were produced programmatically during project creation and reside in
  `core/assets/sfx`.  Volume can be adjusted via the settings screen.
* **App icon:** A 512×512 illustration of a spoon riding a power
  wheelchair was generated specifically for this project and scaled to
  standard launcher sizes (`48×48` through `192×192`).

## Architecture Summary

Wheelie Spoon Rush is built around libGDX’s application and screen
framework.  The `GameApp` class holds the shared `SpriteBatch` and
`AssetManager` and switches between screens.  Each screen (title,
settings, game, results) uses a `Stage` with a `Table` layout to
arrange UI elements.

The game world itself lives in the `GameWorld` class.  At runtime the
`GameScreen` updates the world on each frame, adjusts the camera to
follow the auto‑scrolling player and draws the environment and HUD.
Platforms support flat, sloped and moving variants to create varied
terrain.  Collectibles and enemies are simple axis‑aligned objects that
interact with the player via bounding box intersection tests.  The
player supports forgiving jump mechanics including coyote time and jump
buffering to make mobile controls approachable.

High scores, secret discoveries and settings are persisted using
libGDX’s `Preferences` API, which stores key–value pairs on the
device【859772538648162†L15-L50】.  The reward scaffold sets a flag
whenever all secret spoons are collected so that cosmetic unlocks can be
added later.

## Adding Another Level

To add additional levels:

1. Create a new `GameWorld` subclass or parameterise `GameWorld` to
   accept level data.  Define your platforms, ramps, moving
   platforms, collectibles and enemies in the new level builder.
2. Update the `GameScreen` to instantiate the appropriate world when
   starting a run.  You might add a level selection screen or extend
   the title screen with level buttons.
3. Adjust `Constants.SECRET_SPOON_TOTAL` to reflect the number of
   secrets in the new level.
4. Consider loading level definitions from JSON or Tiled maps for
   easier iteration once the framework is established.

## Known Limitations

* The artwork consists of simple coloured rectangles; this vertical
  slice focuses on game feel and architecture rather than final art.
* Only a single handcrafted level is included.  Future versions can
  add more levels and vary themes without altering the core code.
* The UI uses a basic skin created at runtime.  For a production
  release you should replace this with a properly designed skin and
  fonts.
* Music playback is not yet implemented; a stub exists in the
  settings screen to prepare for future music integration.

## References

* The libGDX Preferences API stores small pieces of data such as user
  settings, scores and completion flags in a platform‑independent way【859772538648162†L15-L50】.
* libGDX’s input handling abstracts mouse and touch input across
  desktop and mobile devices【438113801169693†L15-L25】.
* Kotlin can be used seamlessly with libGDX and offers modern language
  features for building games【542530789169081†L15-L44】.