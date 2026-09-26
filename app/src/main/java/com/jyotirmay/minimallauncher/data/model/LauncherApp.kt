package com.jyotirmay.minimallauncher.data.model

import android.graphics.drawable.Drawable

/**
 * Represents a launchable application installed on the device.
 * The icon is loaded once and cached; it is NOT serialized.
 */
data class LauncherApp(
    val packageName: String,
    val activityName: String?,
    val label: String,
    val icon: Drawable,
    /** First alphabetic character of the label, uppercased, or '#' for non-alpha. */
    val letterKey: Char
)
