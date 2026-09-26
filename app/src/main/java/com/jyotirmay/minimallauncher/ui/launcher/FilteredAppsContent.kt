package com.jyotirmay.minimallauncher.ui.launcher

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jyotirmay.minimallauncher.data.model.LauncherApp

/**
 * Content shown when a letter is selected: letter header + matching apps.
 */
@Composable
fun FilteredAppsContent(
    letter: Char,
    apps: List<LauncherApp>,
    favouritePackages: Set<String>,
    onAppTap: (LauncherApp) -> Unit,
    onAppLongPress: (LauncherApp) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 80.dp, end = 56.dp)
    ) {
        // Letter header
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "No apps",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn {
                items(
                    items = apps,
                    key = { it.packageName }
                ) { app ->
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
    }
}
