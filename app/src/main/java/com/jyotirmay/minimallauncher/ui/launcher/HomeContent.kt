package com.jyotirmay.minimallauncher.ui.launcher

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
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Update clock every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
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
        // Clock
        Text(
            text = timeFormat.format(date),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Date
        Text(
            text = dateFormat.format(date),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
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
