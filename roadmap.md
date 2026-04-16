# MeetSterling Android Roadmap

Current canonical app:

- `C:\Users\Gutie\projects\android`

Reference-only legacy repo:

- `C:\Users\Gutie\projects\Sterlingsworld`

## Where We Started

The original shipping path was a React Native project with native Android and iOS shells around it. That repo had become the wrong base for release work:

- native build issues were being caused by RN dependencies and tooling drift
- media and game assets needed to live in the native Android app
- the long-term goal was a native Android app, not continued RN maintenance

The project was therefore re-centered on the native Android repo and the RN repo was treated as source material only.

## What Is Done

### Native app foundation

- Native Android project is the canonical app
- Gradle-based Android build is working again
- Typed navigation, shell screens, theme, and app wiring are in place
- The park shell now branches into dedicated attraction screens instead of only flat hub surfaces
- App builds successfully with `:app:assembleDebug`

### Asset migration

- Video assets are staged in the native app
- Music corpus is staged in the `:studio-audio` PAD module
- Game asset corpus has been copied into the native Android project
- `Ghost` was explicitly included in the app
- HTML game assets now live under `app/src/main/assets/games/` and are launched through `WebViewGame`

### Playable games

These are now real native playable surfaces:

- `Lucky Paws`
- `Ghost`
- `Cognitive Creamery`
- `Symptom Striker`

### Shared runtime

- `GameShellScreen` and `GameShellViewModel` now handle pause, resume, restart, exit, and completion
- progress events are recorded through the native progress path
- replay and completion routing are wired through the shared navigation flow
- completion copy is neutral enough to avoid treating a losing run as an explicit win
- `VideoPlayerScreen` now uses Media3/ExoPlayer for Cinema and Kidz asset playback
- Studio has a real catalog and transport UI path, but it is still gated by PAD runtime availability
- non-native titles now route through packaged HTML/WebView game assets instead of dead-end catalog entries

## Verification already completed

The following are currently green:

- Android Studio sync
- targeted native unit tests
  - `GameCatalogTest`
  - `GameShellViewModelTest`
  - `LuckyPawsViewModelTest`
  - `GhostViewModelTest`
  - `CognitiveCreameryViewModelTest`
  - `SymptomStrikerViewModelTest`
- `:app:assembleDebug`

## Current v1 Surface

### Ship-ready playable

- `Lucky Paws`
- `Ghost`
- `Cognitive Creamery`
- `Symptom Striker`

### Watchable

- Cinema videos through the shared player
- Kidz videos through Storybook Land and Kidz Cinema into the shared player

### Present but not yet available

- Studio

Studio remains blocked on PAD runtime validation in a Play-installed build.

### Launchable in app but not ship-ready for v1

These titles now have themed attraction screens and packaged runtime entry points, but are still outside the current v1 ship-ready promise.

- `Relaxation Retreat`
- `Spoon Gauntlet`
- `AOL`
- `Kidz Doodle Land`
- `Kidz Linebreaker`
- `Lumi's Star Quest`
- `Nostalgia`

## What Is Next

## 1. Manual QA pass

This is the highest-priority next step.

Use:

- [MANUAL_QA_CHECKLIST.md](C:\Users\Gutie\projects\android\MANUAL_QA_CHECKLIST.md)

Priority order:

1. `Symptom Striker` full win path
2. `Symptom Striker` loss path
3. `Ghost` full run
4. completion replay and back-to-park flow
5. pause and restart behavior in at least two games
6. `Lucky Paws` and `Cognitive Creamery` completion sanity

Goal:

- confirm that the verified code paths also behave correctly on emulator or device
- confirm that the attraction-screen routing and the WebView-backed titles load cleanly

## 2. Fix anything found in manual QA

Only after the manual pass.

If failures appear, fix them before starting another implementation sprint.
The important rule now is:

- do not stack new features on top of broken current flows

## 3. Validate Studio PAD runtime

This is the main remaining media unknown.

Need to prove:

- Studio audio assets resolve correctly at runtime
- playback works in a Play-installed build
- the current "present but unavailable" state can be upgraded honestly if validation passes

## 4. Next feature sprint

If manual QA is clean, the next logical implementation sprint is:

- `AOL`

Reason:

- `Symptom Striker` already established the first battle-family foundation
- `AOL` is already live in the new UI and is the highest-leverage follow-on if we want to reuse that battle work while it is fresh

If battle reuse turns out to need more cleanup first, then the fallback next sprint is:

- battle-family hardening before `AOL`

## 5. Remaining game rollout

After `AOL`, the likely order is:

1. `Lumi's Star Quest`
2. one lighter non-battle game such as `Relaxation Retreat`
3. the remaining catalog based on reuse value and scope discipline

This order should stay flexible if QA or product priorities expose a better sequence.

## 6. Release hardening

After the current playable set and media are verified:

- broader device smoke testing
- release packaging review
- signing and distribution prep
- final v1 scope cleanup

## Rules For Tomorrow

- Start with manual QA, not a new feature
- Send only failures or odd behavior back into the fix loop
- Keep the native Android repo as the only shipping target
- Do not reopen RN work unless a missing asset or source reference requires it

## Current Restart Point

If work resumes tomorrow, the first action should be:

- run the manual checklist and report failures only

Priority order:

- `Symptom Striker` win path
- `Symptom Striker` loss path
- `Ghost` full run
- completion replay and back-to-park flow

If no failures appear:

- begin the next long sprint centered on `AOL`
