# Gameplan: Debug and Fix AOL Launch & Related Issues

## 1. Analysis of AOL Launch Problem
Based on the codebase analysis, the "AOL" game is a WebView-based game. The reported "not launching" issue likely stems from:
- **Flag Conflicts**: Conflicting `shipReady` and `isLive` flags between `GameCatalog` and the UI.
- **Pathing/Asset Issues**: Potential issues with how the WebView loads `index.html`.
- **Architectural Misalignment**: Misleading comments suggesting `BattleModel` reuse (which is impossible for a WebView game) might have led to incorrect assumptions during recent refactors.

## 2. Proposed Fixes

### 2.1 Reconcile Game Flags
- Update `app/src/main/java/com/sterlingsworld/data/catalog/GameCatalog.kt` and the Arcade UI to use a single source of truth for "AOL" readiness.
- Ensure `shipReady` is consistently set to `true` if the game is intended to be playable.

### 2.2 Cleanup Misleading Documentation
- Remove comments in `BattleModel.kt` and other files claiming that `AOL` reuses Kotlin battle logic. This prevents future developer confusion.

### 2.3 Enhance WebView Robustness
- Add a `WebChromeClient` to `WebViewGame.kt` to capture console logs, which will help in future debugging of HTML/JS errors.
- Implement a basic error listener in `WebViewGame.kt` to show a "Technical Difficulties" screen if the `index.html` fails to load.

### 2.4 Navigation Hardening (Regression Fix)
- Verify the route for `aol` in `NavGraph.kt` and ensure it doesn't fall into the `InertGameContent` block due to incorrect `GameLaunchSpec` resolution.

### 2.5 Snails Journey Placeholder
- Since "Snail's Journey" is currently hitting a "Technical Difficulties" screen in `NavGraph.kt`, I will ensure it is correctly gated or pointed to its intended LibGDX implementation if ready, following the pattern of other native games.

## 3. Implementation Steps

1.  **Modify `GameCatalog.kt`**: Ensure `aol` has `shipReady = true`.
2.  **Modify `GrandArcadeIndoorScreen.kt`**: Ensure the "AOL" card uses the catalog's `shipReady` flag instead of a hardcoded `isLive`.
3.  **Update `WebViewGame.kt`**:
    - Add `WebChromeClient` for logging.
    - Add `onReceivedError` to `WebViewClient`.
4.  **Clean `BattleModel.kt`**: Remove incorrect reuse comments.
5.  **Verify `NavGraph.kt`**: Ensure the "AOL" route correctly resolves to `WebViewGame`.

## 4. Verification Plan
- Launch the app and navigate to the Arcade.
- Tap on "Armor of Light".
- Verify the WebView loads `index.html` correctly.
- Check Logcat for any JS console output.
