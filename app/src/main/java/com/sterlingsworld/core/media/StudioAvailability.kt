package com.sterlingsworld.core.media

/**
 * Represents the Studio audio playback availability state.
 *
 * The service emits this on startup after probing for assets; the ViewModel
 * surfaces it in [StudioUiState] so the UI can react honestly.
 */
enum class StudioAvailability {
    /** Asset-pack content has been confirmed accessible and the player queue is loaded. */
    READY,

    /** Service has started but has not yet finished probing asset availability. */
    WAITING_FOR_ASSETS,

    /** Audio assets are not accessible in this build environment (debug without corpus,
     *  or a production build where the pack failed to install). Playback is not possible. */
    UNAVAILABLE,
}
