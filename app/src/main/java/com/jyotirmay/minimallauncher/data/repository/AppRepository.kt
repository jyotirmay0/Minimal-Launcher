package com.jyotirmay.minimallauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.jyotirmay.minimallauncher.data.model.LauncherApp
import com.jyotirmay.minimallauncher.domain.LetterMapper

/**
 * Discovers and caches all launchable apps on the device.
 * Queries PackageManager only on initialization and explicit refresh.
 */
class AppRepository(private val context: Context) {

    @Volatile
    private var cachedApps: List<LauncherApp>? = null

    fun getApps(): List<LauncherApp> {
        cachedApps?.let { return it }
        return refreshApps()
    }

    @Synchronized
    fun refreshApps(): List<LauncherApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        val ownPackage = context.packageName

        val apps = resolveInfos
            .filter { it.activityInfo.packageName != ownPackage }
            .mapNotNull { resolveInfo ->
                try {
                    val activityInfo = resolveInfo.activityInfo
                    val label = resolveInfo.loadLabel(pm).toString()
                    val icon = activityInfo.loadIcon(pm)
                    LauncherApp(
                        packageName = activityInfo.packageName,
                        activityName = activityInfo.name,
                        label = label,
                        icon = icon,
                        letterKey = LetterMapper.letterForApp(label)
                    )
                } catch (e: Exception) {
                    null
                }
            }
            .sortedBy { it.label.lowercase() }

        cachedApps = apps
        return apps
    }

    fun invalidateCache() {
        cachedApps = null
    }
}
