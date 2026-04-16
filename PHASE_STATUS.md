# MeetSterling Android Kotlin Rebuild - Phase Status

## Phase 1: Foundation - COMPLETE
**Completed:** 2026-04-12

- Gradle KTS project and Android app module are in place
- Core domain models, catalogs, navigation shell, application wiring, and theme are established
- Native Android project is the canonical app path

---

## Phase 2: Shell - COMPLETE
**Completed:** 2026-04-12

- Welcome, park shell, settings, map, completion, cinema, studio, arcade, and kidz surfaces exist
- Shared navigation and game shell chrome are wired
- Themed attraction surfaces now sit on top of the shell:
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

---

## Phase 3: Media - COMPLETE WITH VALIDATION PENDING

- Cinema and Kidz video assets are staged and routed through `VideoPlayerScreen`
- `VideoPlayerScreen` now uses Media3/ExoPlayer against bundled app assets
- Storybook Land and Kidz Cinema now route selected Kidz videos into the shared player
- Studio audio corpus is staged in the `:studio-audio` PAD module
- Studio now has catalog and transport UI wiring, but availability still depends on PAD runtime asset resolution
- Studio remains present but unavailable until PAD runtime validation is proven on a Play-installed build

Open media validation:

- device smoke test for all 4 videos
- device validation for Studio PAD runtime asset access

---

## Phase 4: Game Platform Foundation - COMPLETE WITH MIXED RUNTIME MODES

Current runtime foundations now in place:

- `GameShellScreen` + `GameShellViewModel` with real pause, restart, exit, and completion flow
- progress repository integration for session start, restart, and completion recording
- real completion routing from playable games back into the shared completion surface
- native family foundations under `feature/game/games/`
  - Reward: `Lucky Paws`
  - Narrative: `Ghost`
  - Mini-game: `Cognitive Creamery`
  - Battle: `Symptom Striker`
- HTML/WebView game runtime is wired for packaged asset games under `app/src/main/assets/games/`
- attraction detail screens route into either native runtimes or packaged WebView games through the shared `game_player/{gameId}` entry point

Still open at the platform level:

- broader cross-family runtime polish beyond the current v1 ship-ready set
- accessibility and haptics pass
- battle-family follow-on reuse for `AOL` and `Lumi's Star Quest`

---

## Phase 5: Individual Games - IN PROGRESS

### Ship-ready native games

| Game | Suite | State |
|---|---|---|
| Lucky Paws | RewardSuite | Ship-ready |
| Ghost | NarrativeSuite | Ship-ready |
| Cognitive Creamery | MiniGameSuite | Ship-ready |
| Symptom Striker | BattleSuite | Ship-ready |

### Launchable in app but not ship-ready for v1

These titles now have dedicated attraction screens and route into packaged HTML/WebView game assets, but are still outside the current ship-ready v1 set.

| Game | Suite | State |
|---|---|---|
| Relaxation Retreat | MiniGameSuite | Launchable, not ship-ready |
| Spoon Gauntlet | NarrativeSuite | Launchable, not ship-ready |
| AOL | BattleSuite | Launchable, not ship-ready |
| Kidz Doodle Land | MiniGameSuite | Launchable, not ship-ready |
| Kidz Linebreaker | ArcadeSuite | Launchable, not ship-ready |
| Lumi's Star Quest | BattleSuite | Launchable, not ship-ready |
| Nostalgia | ArcadeSuite | Launchable, not ship-ready |

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

- manual device or emulator pass for the current four playable games
- manual smoke pass for the seven packaged WebView titles
- Studio PAD runtime validation on a Play-installed build
- release packaging, signing, and broader regression sweep across native and WebView-backed game paths
