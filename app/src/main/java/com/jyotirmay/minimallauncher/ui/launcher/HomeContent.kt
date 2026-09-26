package com.jyotirmay.minimallauncher.ui.launcher

import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jyotirmay.minimallauncher.data.model.LauncherApp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Home content showing live clock, date, and favourite apps.
 */
@Composable
fun HomeContent(
    favouriteApps: List<LauncherApp>,
    favouritePackages: Set<String>,
    onAppTap: (LauncherApp) -> Unit,
    onAppLongPress: (LauncherApp) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Update clock on minute boundary to save battery and CPU cycles
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            val nextMinuteDelay = 60_000L - (currentTime % 60_000L)
            delay(nextMinuteDelay.coerceAtLeast(1000L))
        }
    }

    val date = remember(currentTime) { Date(currentTime) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEE d MMM", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 80.dp, end = 56.dp)
    ) {
        // Clock - tap to open Clock/Alarms
        Text(
            text = timeFormat.format(date),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable {
                try {
                    val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    try {
                        val fallback = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_LAUNCHER)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(fallback)
                    } catch (_: Exception) {}
                }
            }
        )

        // Date - tap to open Calendar
        Text(
            text = dateFormat.format(date),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.clickable {
                try {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_CALENDAR)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Favourite apps list
        favouriteApps.forEach { app ->
            AppRow(
                icon = app.icon,
                label = app.label,
                isFavourite = app.packageName in favouritePackages,
                onTap = { onAppTap(app) },
                onLongPress = { onAppLongPress(app) }
            )
        }
    }
}
