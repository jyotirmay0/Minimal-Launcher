package com.jyotirmay.minimallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jyotirmay.minimallauncher.ui.launcher.LauncherScreen
import com.jyotirmay.minimallauncher.ui.theme.MinimalLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinimalLauncherTheme {
                LauncherScreen()
            }
        }
    }

    /**
     * As a Home launcher, pressing Back should not finish the activity.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Do nothing — launcher stays on screen
    }
}