package com.sterlingsworld.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun openNearbyBathroomMap(context: Context) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:0,0?q=public restroom"),
    )

    if (context !is android.app.Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val packageManager = context.packageManager
    if (intent.resolveActivity(packageManager) == null) return

    runCatching { context.startActivity(intent) }
        .recoverCatching {
            if (it is ActivityNotFoundException) null else throw it
        }
}
