# V1 Scope

Current v1 candidate for `C:\Users\Gutie\projects\android`.

## Playable

- `Lucky Paws`
- `Ghost`
- `Cognitive Creamery`
- `Symptom Striker`

These are the only titles currently treated as ship-ready for v1.

## Watchable

- Cinema videos through the shared `VideoPlayerScreen`
- Kidz videos through Storybook Land and Kidz Cinema into the shared `VideoPlayerScreen`

## Present But Unavailable

- Studio

Current Studio state:

- UI is present
- corpus is staged in the `:studio-audio` PAD module (126 tracks)
- catalog and transport UI are wired
- audio controls remain dependent on PAD runtime asset availability in the install path
- user-facing copy must continue to read as unavailable, not broken, until validation is complete

Studio audio uses install-time Play Asset Delivery (PAD) via the `:studio-audio` module.

Studio remains excluded from the playable v1 surface until:

- a Play-delivered install proves the assets resolve at runtime
- device playback validation passes

## Launchable In App But Excluded From Current Ship-Ready v1 Build

- Relaxation Retreat
- Spoon Gauntlet
- AOL
- Kidz Doodle Land
- Kidz Linebreaker
- Lumi's Star Quest
- Nostalgia

These games remain outside the current v1 promise even though the `new-ui` branch now gives them real attraction screens and packaged runtime entry points through `WebViewGame`.

## Other Included Surfaces

- Welcome
- park shell and tab navigation
- themed attraction screens for Arcade and Kidz flows
- settings and reset flow
- map

## Verification Baseline

- Android Studio sync works
- targeted native unit tests pass:
  - `GameCatalogTest`
  - `GameShellViewModelTest`
  - `LuckyPawsViewModelTest`
  - `GhostViewModelTest`
  - `CognitiveCreameryViewModelTest`
  - `SymptomStrikerViewModelTest`
- `assembleDebug` passes

## Remaining v1 gaps

- manual device or emulator verification for the four playable games
- manual smoke verification for the seven WebView-backed launchable titles
- Studio PAD runtime validation on a Play-installed build
- deliberate promotion decision for the next non-v1 title, most likely `AOL`
