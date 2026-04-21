# Arcade Update: Busy Streets And Spoons And Stairs

This document replaces the earlier prototype-quality review as the current status snapshot for the arcade preview pair in this branch.

## Current Status

Both games are now integrated into the main Android app and reachable from the arcade experience as preview titles:

- `Busy Streets` (`frogger`)
- `Spoons and Stairs`

Source locations:

- `app/src/main/java/com/sterlingsworld/feature/game/games/frogger/*`
- `app/src/main/java/com/sterlingsworld/feature/game/games/spoonsandstairs/*`
- `app/src/main/java/com/sterlingsworld/feature/arcade/GrandArcadeIndoorScreen.kt`

## What Changed

- Both games were moved out of staging work and into the canonical app path
- Arcade routing now exposes them as playable previews
- `Busy Streets` now uses integrated sprite artwork for traffic and raft obstacles
- Game shell integration, result handling, and shared app routing are in place

## Current Readiness

These two titles are:

- integrated
- launchable
- represented in the arcade UI as previews
- not yet marked ship-ready in `GameCatalog`

That is the intended current state of the branch.

## Why They Are Still Previews

They work as part of the app, but they are still held below the ship-ready tier because they need more polish than the five current ship-ready catalog games.

Current polish gaps:

- stronger feedback / juice
- more authored progression tuning
- clearer onboarding and reward cadence
- a more complete presentation pass so they feel fully promoted rather than preview-tier

## Busy Streets Specific Note

`Busy Streets` no longer relies only on flat placeholder object rendering for its traffic/river obstacles. The current preview uses integrated Frogger art assets for:

- left-moving car
- right-moving car
- raft

The frog/player art is still using the existing composable presentation because the available `hop.PNG` source was not a usable frog sprite.

## Recommended Next Step

If these titles are promoted beyond preview, the next work should be a polish pass, not another integration pass.
