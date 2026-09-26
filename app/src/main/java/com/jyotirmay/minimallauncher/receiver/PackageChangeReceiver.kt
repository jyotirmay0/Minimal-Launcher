package com.jyotirmay.minimallauncher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives package install/uninstall/update broadcasts.
 * Notifies the launcher to refresh its app list via a callback.
 */
class PackageChangeReceiver : BroadcastReceiver() {

    companion object {
        var onPackageChanged: (() -> Unit)? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                onPackageChanged?.invoke()
            }
        }
    }
}
