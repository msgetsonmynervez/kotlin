# MeetSterling Android Kotlin Rebuild - Phase Status

This repository at `C:\Users\Gutie\projects\kotlin-clean` is the main build and the canonical shipping path for the Android app.

## Phase 1: Foundation - COMPLETE
**Completed:** 2026-04-12

- Gradle KTS project and Android app module are in place
- Core domain models, catalogs, navigation shell, application wiring, and theme are established
- Native Android project is the canonical app path

---

## Phase 2: Shell - COMPLETE WITH ACTIVE CLEANUP

- Welcome, map, settings, completion, cinema, studio, arcade, and kidz surfaces exist
- the Kidz entry path now lands directly on the storybooks-or-games chooser
- attraction and zone surfaces exist for:
  - `GrandArcadeIndoor`
  - `Lucky Paws`
  - `Symptom Striker`
  - `Creamery`
  - `Gauntlet`
  - `Relaxation Retreat`
  - `AOL`
  - `KidzArcadeMenu`
  - `StorybookLand`
  - `KidzCinema`
  - `Doodle`
  - `Linebreaker`
  - `Lumi's Star Quest`
  - `Nostalgia`
- artwork tap targets replaced the old pill CTA pattern on landing pages

Open shell cleanup:

- global bottom-nav behavior still needs route-by-route hardening
- route policy and shell chrome still need cleanup to remove drift from recent refactors

---

## Phase 3: Media - COMPLETE WITH RUNTIME VALIDATION STILL OPEN

- Cinema and Kidz video assets are staged and routed through `VideoPlayerScreen`
- `VideoPlayerScreen` uses Media3/ExoPlayer against bundled app assets
- Storybook Land and Kidz Cinema route selected Kidz videos into the shared player
- Studio audio corpus is staged in the `:studio-audio` PAD module
- Studio now exposes a real album model:
  - available now: `Dark Side of the Spoon`, `Stand Up`, `Neural Garden`
  - download later: `Groove`, `Sterling Main Library`
- Studio UI marks deferred albums as download-later and playback is restricted to the intended live albums

Open media validation:

- device smoke test for all routed videos
- device validation for Studio runtime asset access on the intended install path

---

## Phase 4: Game Platform Foundation - COMPLETE WITH MIXED RUNTIME MODES

Current runtime foundations in place:

- `GameShellScreen` + `GameShellViewModel` with pause, restart, exit, and completion flow
- progress repository integration for session start, restart, and completion recording
- real completion routing from playable games back into the shared completion surface
- native family foundations under `feature/game/games/`
- HTML/WebView runtime is wired for packaged asset games under `app/src/main/assets/games/`
- attraction detail screens route into either native runtimes or packaged WebView games through the shared `game_player/{gameId}` entry point

Still open at the platform level:

- broader cross-family runtime polish
- accessibility and haptics pass
- battle-family follow-on reuse hardening
- navigation cleanup to keep shell behavior consistent across the app

---

## Phase 5: Individual Games - IN PROGRESS

### Currently live in app

| Game | State |
|---|---|
| Ghost | Live |
| Cognitive Creamery | Live |
| Symptom Striker | Live |
| Spoon Gauntlet | Live |
| Relaxation Retreat | Live |
| AOL | Live |

### Currently unavailable or intentionally deferred

| Game | State |
|---|---|
| Lucky Paws | Coming soon / unavailable |
| Access Quest | Not live in surfaced app flow |
| Access Racer | Not live in surfaced app flow |
| Snail's Journey | Not live in surfaced app flow |

### Kidz titles present in app

| Game | State |
|---|---|
| Kidz Doodle Land | Present |
| Kidz Linebreaker | Present |
| Lumi's Star Quest | Present |
| Nostalgia | Present |

---

## Phase 6: Hardening - IN PROGRESS

Current verified baseline:

- targeted native unit tests pass:
  - `GameCatalogTest`
  - `GameShellViewModelTest`
  - `LuckyPawsViewModelTest`
  - `GhostViewModelTest`
  - `CognitiveCreameryViewModelTest`
  - `SymptomStrikerViewModelTest`
- `:app:assembleDebug` passes

Still open:

- manual device or emulator pass for the currently live game routes
- manual smoke pass for packaged WebView-backed titles
- Studio runtime validation on the intended install path
- bottom navigation and route-policy cleanup
- broader regression sweep so docs, catalog state, and surfaced UI stay aligned
