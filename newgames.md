# New Games Integration Summary

This document is now a completion summary for the `new game stage` integrations that landed in the main app.

## Integrated Titles

- `Spoons and Stairs`
- `Busy Streets` (`frogger`)

## Current App Locations

Code:

- `app/src/main/java/com/sterlingsworld/feature/game/games/spoonsandstairs/`
- `app/src/main/java/com/sterlingsworld/feature/game/games/frogger/`

Registration:

- `app/src/main/java/com/sterlingsworld/feature/game/EmbeddedGameRegistry.kt`
- `app/src/main/java/com/sterlingsworld/data/catalog/GameCatalog.kt`
- `app/src/main/java/com/sterlingsworld/data/catalog/GameLaunchCatalog.kt`

Assets:

- `app/src/main/res/drawable/`

## Integration Outcome

- Both games were brought into the canonical Android app path
- Both games now launch through the shared game player route
- Both games participate in the app's shared shell flow instead of living as isolated staging work
- Both games are currently surfaced as preview titles rather than ship-ready catalog entries

## Busy Streets Notes

- `FroggerViewModel.kt` and `FroggerGame.kt` are integrated into the app package structure
- Traffic and raft sprite assets are now present in `res/drawable`
- The current preview uses integrated object art but still keeps the existing player/frog composable treatment

## Spoons And Stairs Notes

- The former staging logic and assets were moved into the app's `spoonsandstairs` package
- The current version is launchable from the arcade preview surface

## Current Status

The integration work itself is complete.

The remaining work for both titles is polish, balancing, and any future promotion from preview status to ship-ready status.
