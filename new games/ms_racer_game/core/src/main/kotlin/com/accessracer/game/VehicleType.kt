package com.accessracer.game

import com.badlogic.gdx.graphics.Color

/**
 * Enumeration of available vehicles.  Each vehicle is associated with a
 * display name and a colour used to draw the player icon during the
 * race.  Additional attributes can be added later (e.g. speed or
 * handling differences) without changing selection logic.
 */
enum class VehicleType(val displayName: String, val color: Color) {
    /** Mobility scooter: red colour. */
    SCOOTER("Mobility Scooter", Color.RED),
    /** Power wheelchair: green colour. */
    WHEELCHAIR("Power Wheelchair", Color.GREEN)
}