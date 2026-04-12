# MeetSterling Android Kotlin Rebuild — Phase Status

## Phase 1: Foundation — COMPLETE
**Completed:** 2026-04-12

- Gradle KTS project, version catalog, build variants (debug/qa/release)
- Release signing via Gradle properties — no secrets in source
- ProGuard rules wired
- Domain models, content catalogs (10 games, 126 tracks, 4 videos)
- DataStore (preferences) + Room (game progress) data layer
- Compose theme sourced from RN palette
- Navigation: typed Screen sealed class, full NavGraph, ParkScaffold (bottom tabs)
- `MeetSterlingApplication` (app-level DI)
- `MainActivity` (edge-to-edge, start dest from DataStore)

---

## Phase 2: Shell — COMPLETE
**Completed:** 2026-04-12

All 5 park tab screens, Welcome, Settings, Map, Completion, VideoPlayer shell, GameShellScreen chrome, IdleAwareRoot/IdleOverlay.

---

## Sprint 2 — COMPLETE
**Completed:** 2026-04-12

### Gradle wrapper bootstrap
- `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar` added
- Java: `C:/Program Files/Android/Android Studio/jbr`
- **`.\gradlew assembleDebug` — BUILD SUCCESSFUL (3m 21s, 39 tasks)**
- **`.\gradlew test` — BUILD SUCCESSFUL (57s, all unit tests pass)**
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk` — 156 MB (video assets staged, audio not yet)

### Package hygiene
- `com.meetsterling` package fully removed (3 hook-injected files deleted)
- Only `com.sterlingsworld` remains
- No encoding/mojibake issues found in source files

### Launcher resources
- `drawable/ic_launcher_background.xml` — brand cream fill (#F7F1E3)
- `drawable/ic_launcher_foreground.xml` — Sterling "S" vector monogram in primary green
- Adaptive-icon XMLs deployed to all 5 mipmap density buckets (mdpi → xxxhdpi)
- `allowBackup=false`, `android:theme=@style/Theme.MeetSterling` in manifest

### Asset directory structure (app/src/main/assets/)
```
assets/
  images/
    mascot/
      sterling_main.png      (IMG_3778.PNG — copied from RN)
      sterling_wave.png      (IMG_3779.PNG — copied from RN, used on Welcome screen)
    map/
      park_map.png           (IMG_3780.PNG — copied from RN, used on Map screen)
    zones/
      zone_arcade.png        (IMG_3781.PNG)
      zone_cinema.png        (IMG_3782.PNG)
      zone_studio.png        (IMG_3784.PNG)
      zone_kidz.png          (IMG_3785.PNG)
  video/
    main/
      main-video-01.mp4      (8.7 MB — staged, playable)
    kidz/
      kids-video-01.mp4  \
      kids-video-02.mp4   >  (119 MB total — staged, playable)
      kids-video-03.mp4  /
  audio/
    music/
      dark_side_of_the_spoon/   (directory created, MP3s NOT YET COPIED)
      groove/                   (directory created, MP3s NOT YET COPIED)
      neural_garden/            (directory created, MP3s NOT YET COPIED)
      (root-level tracks 01-63 NOT YET COPIED)
```

**Audio blocked — deliberate.** RN audio total: 536 MB. Bundling all audio would reproduce the 754 MB APK problem. Decision required before staging audio:
  - Option A: Bundle all audio → ~700 MB AAB (Play Store requires <150 MB, use Play Asset Delivery)
  - Option B: Play Asset Delivery (PAD) — stream audio packs on install
  - Option C: Reduce audio via re-encoding at lower bitrate
  This is the primary Phase 6 packaging decision.

### Real asset rendering
- `WelcomeScreen` — displays `sterling_wave.png` from assets via `BitmapFactory` + `ImageBitmap`
- `MapScreen` — displays `park_map.png` from assets, sized to image aspect ratio
- Both screens have graceful fallbacks if asset is missing (no crash)
- `AssetImage.kt` utility in `core/util/` for future use

### Media — real implementation
**Studio (`StudioPlaybackService`):**
- Real Media3 `MediaSessionService` with `ExoPlayer`
- Loads full 126-track queue on create from `StudioCatalog` via `file:///android_asset/` URIs
- Audio focus handled (`handleAudioFocus = true`)
- `handleAudioBecomingNoisy = true` (pause on headphone unplug)
- Foreground notification channel registered on create
- **Blocked for playback:** audio MP3 files not yet staged in assets/

**Studio (`StudioViewModel` + `StudioScreen`):**
- `StudioViewModel` holds real `MediaController` bound to `StudioPlaybackService`
- `StateFlow` for `isPlaying`, `currentTrackId`, `controllerReady`
- `StudioScreen` shows real now-playing transport bar (play/pause/next/previous) when active
- Track rows highlight active track; album header has play-album button
- All play actions route through real `MediaController` — no UI-only fakes

**Video (`VideoPlayerScreen`):**
- Real `ExoPlayer` instance with `AndroidView { PlayerView }`
- Loads video from `file:///android_asset/` URI
- Auto-plays on enter, auto-navigates back on `STATE_ENDED`
- `DisposableEffect` releases player on composable exit — no leak
- Video files are staged → ready to test on device now

### Game shell
- **Removed:** "Complete (dev)" fake button from `GameShellScreen`
- **Removed:** all user-visible fake-game UI text/prompts
- Shell is now an inert holding state: displays game title + description, offers only "Return to Park"
- Pause menu (resume/restart/exit) is real and wired
- `onComplete` callback preserved for Phase 4 game runtimes to call

---

## Phase 3: Media — COMPLETE (with audio staging blocked)
**Remaining:**
- Stage 126 audio MP3s once delivery strategy is decided (see above)
- Device smoke test: Studio playback end-to-end (requires audio files)
- Device smoke test: Video playback for all 4 videos

---

## Phase 4: Game Platform Foundation — NOT STARTED
**Scope:**
- `GameShellScreen` → real ViewModel (pause state machine, restart, exit)
- Progress repository integration (recordSessionStart, recordCompletion)
- Suite architecture foundations: BattleSuite, MiniGameSuite, ArcadeSuite, NarrativeSuite, RewardSuite
- Accessibility hooks
- Haptics (`VibrationEffect`)

---

## Phase 5: Individual Games — NOT STARTED

Requires answer to: **What does v1 game behavior mean?**

| Game | Suite | Decision |
|---|---|---|
| Cognitive Creamery | MiniGameSuite | TBD |
| Relaxation Retreat | MiniGameSuite | TBD |
| Spoon Gauntlet | NarrativeSuite | TBD |
| Symptom Striker | BattleSuite | TBD |
| Lucky Paws | RewardSuite | TBD |
| AOL | BattleSuite | TBD |
| Kidz Doodle Land | MiniGameSuite | TBD |
| Kidz Linebreaker | ArcadeSuite | TBD |
| Lumi's Star Quest | BattleSuite | TBD |
| Nostalgia | ArcadeSuite | TBD |

---

## Phase 6: Hardening — NOT STARTED
Key decision required here: audio delivery strategy (see Asset section above) before AAB size review can be completed.
