# Access Racer – Mobility Kart Racing

An open‑source libGDX racing game written in Kotlin.  Access Racer is a
light‑weight, arcade‑style racing game designed with accessibility in mind
to be playable by people with multiple sclerosis (MS) or other mobility
impairments.  Instead of karts, players choose between mobility scooters
and power wheelchairs and compete on a single, simple race track.  The
project is structured as a standard libGDX multi‑module Gradle project
(`core` and `android` modules) and can be integrated into an existing
Android application.

## Features

* **Accessible controls:**  Large on‑screen buttons for steering and
  acceleration.  Controls are remappable, and steering sensitivity and
  game speed can be adjusted from the Options menu.  These features
  follow recommendations from the Game Accessibility Guidelines: players
  should be able to adjust game speed, control sensitivity and use
  simplified control schemes【295693586470493†L36-L45】.
* **Assist mode:**  Optional auto‑acceleration reduces the need for
  sustained input, which can help players with motor fatigue.  Auto
  acceleration is on by default and can be toggled off.
* **High contrast UI:**  The interface uses large buttons with clear
  labels and a high contrast colour palette to make interactive
  elements stand out【295693586470493†L36-L44】.
* **Vehicle choice:**  Two vehicles are available – the **Mobility
  Scooter** and the **Power Wheelchair**.  Their only difference is
  appearance (colours/shapes) to keep gameplay simple.
* **One track:**  A single oval track designed entirely with
  primitives (no external textures) ensures that the game runs on a
  wide range of devices.  The track has an outer and inner radius,
  forming a “ring” that the player must navigate around.  A finish
  line detects lap completion.

## Project structure

```
ms_racer_game/
├── README.md             – This file
├── LICENSE               – MIT License
├── settings.gradle       – Gradle settings (includes core and android modules)
├── build.gradle          – Root build script with repository definitions
├── gradle.properties     – Defines versions for libGDX and Kotlin
├── core/                 – Cross‑platform game code
│   ├── build.gradle      – Builds the core module (Kotlin)
│   └── src/main/kotlin/  – Kotlin source files
│       └── com/accessracer/game/
│           ├── AccessRacerGame.kt      – Application class
│           ├── GameSettings.kt         – Persistent settings model
│           └── screens/
│               ├── MainMenuScreen.kt   – Main menu UI
│               ├── VehicleSelectScreen.kt – Vehicle selection UI
│               ├── OptionsScreen.kt    – Options UI
│               └── RaceScreen.kt       – Core gameplay screen
├── android/
    ├── build.gradle      – Android specific build script
    └── src/main/java/com/accessracer/game/AndroidLauncher.kt – Android launcher
```

## Building

This repository does not include the Gradle wrapper because the
environment in which it was generated did not have Gradle installed.
To build the game yourself:

1. Install [Android Studio](https://developer.android.com/studio) and
   make sure it includes a recent Gradle version.
2. Clone or copy the `ms_racer_game` folder into your project.
3. From a terminal, run `gradle wrapper` in the project root to
   generate the wrapper scripts.
4. Open the project in Android Studio and run the `android` module on
   an emulator or device.

## Controls

During a race, the large on‑screen arrow buttons control steering.
If auto‑acceleration is disabled in the options, a large “Go” button
appears on the right side to control the throttle.  These
interactions can also be mapped to keyboard input (e.g. the left and
right arrow keys) when running on desktop.

## Accessibility considerations

The design of Access Racer draws from published game accessibility
guidelines.  Adjustable game speed and sensitivity help accommodate
different levels of motor control【295693586470493†L36-L45】.  Large,
well‑spaced buttons reduce the precision required to play【295693586470493†L36-L44】.
Auto‑acceleration limits the need for sustained button pressing, and
the track is intentionally simple to reduce cognitive load【295693586470493†L82-L85】.

## License

This project is released under the MIT License (see `LICENSE`), which
permits reuse, modification and distribution.  Attribution is
appreciated but not required.