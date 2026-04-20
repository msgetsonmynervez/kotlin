package com.sterlingsworld.core.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-scoped state holder for Studio audio availability.
 *
 * Written exclusively by [StudioPlaybackService]; read by [StudioViewModel].
 * Keeping state here breaks the direct class-level dependency from ViewModel to Service.
 */
object StudioMediaStateHolder {

    private val _audioAvailability = MutableStateFlow(StudioAvailability.WAITING_FOR_ASSETS)

    val audioAvailability: StateFlow<StudioAvailability> = _audioAvailability.asStateFlow()

    internal fun update(state: StudioAvailability) {
        _audioAvailability.value = state
    }
}
