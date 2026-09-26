package com.jyotirmay.minimallauncher.ui.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jyotirmay.minimallauncher.data.model.LauncherApp

/**
 * Search screen with auto-focused text field and live-filtered results.
 * Visually consistent with the minimal dark launcher.
 */
@Composable
fun SearchScreen(
    query: String,
    results: List<LauncherApp>,
    favouritePackages: Set<String>,
    onQueryChange: (String) -> Unit,
    onAppTap: (LauncherApp) -> Unit,
    onAppLongPress: (LauncherApp) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        // Search field
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Search apps",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { /* no-op, live filtering */ }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .focusRequester(focusRequester)
        )

        // Results
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(
                items = results,
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
