package com.sterlingsworld.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun openNearbyBathroomMap(context: Context) {
    val intents = listOf(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=accessible restroom near me"),
        ),
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/accessible+restroom+near+me"),
        ),
    ).map { candidate ->
        candidate.apply {
            if (context !is android.app.Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    val packageManager = context.packageManager
    val launchableIntent = intents.firstOrNull { it.resolveActivity(packageManager) != null } ?: return

    runCatching { context.startActivity(launchableIntent) }
        .recoverCatching {
            if (it is ActivityNotFoundException) null else throw it
        }
}
