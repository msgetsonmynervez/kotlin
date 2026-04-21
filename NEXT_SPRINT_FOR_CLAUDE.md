# Current Follow-Up Notes

This branch is no longer in the earlier "next sprint" planning state. The app is in a final working snapshot with a smaller follow-up list.

## Verified Baseline

- app builds from the repo root
- debug app installs to emulator
- startup path was revalidated after a clean rebuild fixed a stale broken APK artifact
- current Kotlin navigation shell is intact
- native game shell and completion flow are in place
- Media3-backed video playback path is in place
- Studio catalog is wired with real album grouping

## Current App Truth

Ship-ready:

- `Ghost`
- `Cognitive Creamery`
- `Symptom Striker`
- `Spoon Gauntlet`
- `Relaxation Retreat`

Preview-live:

- `Busy Streets`
- `Spoons and Stairs`

Not ship-ready:

- `AOL`
- `Lucky Paws`
- `Access Quest`
- `Access Racer`
- `Snail's Journey`

## Remaining Follow-Up Work

1. Manual smoke pass across surfaced routes.
2. Studio PAD/runtime validation on the intended install path.
3. Bottom-nav and route-policy cleanup.
4. Preview polish decisions for `Busy Streets` and `Spoons and Stairs`.

## Documentation Rule

Future changes must keep these states aligned across:

- `GameCatalog`
- surfaced UI availability
- top-level repo markdown files
