# Current Shipping Scope

Canonical repo:

- `C:\Users\Gutie\projects\kotlin-clean`

This file reflects the current shipping/preview split in the final working branch.

## Ship-Ready Titles

- `Ghost`
- `Cognitive Creamery`
- `Symptom Striker`
- `Spoon Gauntlet`
- `Relaxation Retreat`

These are the titles currently treated as ship-ready in `GameCatalog`.

## Preview-Live Titles

- `Busy Streets`
- `Spoons and Stairs`

These titles are integrated and playable in the arcade UI, but they remain preview-tier and are not marked ship-ready in `GameCatalog`.

## Present But Not Ship-Ready

- `AOL`
- `Lucky Paws`
- `Access Quest`
- `Access Racer`
- `Snail's Journey`

## Media Surface

- Cinema videos through the shared `VideoPlayerScreen`
- Kidz videos through Storybook Land and Kidz Cinema into the shared `VideoPlayerScreen`
- Studio UI and catalog are present, but Studio runtime readiness is still conditional on PAD/install-path validation

## Kidz Surface Present In App

- `Kidz Doodle Land`
- `Kidz Linebreaker`
- `Lumi's Star Quest`
- `Nostalgia`

## Verification Baseline

- `:app:assembleDebug`
- `:app:installDebug`
- emulator relaunch after clean rebuild and reinstall
- targeted native unit coverage remains in repo for catalog and gameplay view models

## Current Remaining Gaps

- Studio runtime validation on the intended install path
- manual smoke verification across surfaced routes
- bottom-nav and shell consistency cleanup
- future promotion decision for `Busy Streets` and `Spoons and Stairs`
