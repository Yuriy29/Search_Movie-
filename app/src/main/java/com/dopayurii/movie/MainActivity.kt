package com.dopayurii.movie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.dopayurii.movie.presentation.navigation.MovieNavHost
import com.dopayurii.movie.presentation.ui.theme.MovieExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the MovieExplorer application.
 * Sets up the Compose UI with navigation and theming.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MovieExplorerTheme {
                SetSystemBarColours()

                Scaffold(modifier = Modifier.fillMaxSize().navigationBarsPadding().systemBarsPadding()) { pv ->
                    MovieNavHost(navController, pv)
                }
            }
        }
    }
}

/**
 * Sets the system bar colors to match the app's theme.
 * Adjusts status bar and navigation bar colors based on current theme's background color.
 * Also controls light/dark icon appearance based on background luminance.
 */
@Composable
private fun SetSystemBarColours() {
    val context = LocalContext.current
    val window = (context as ComponentActivity).window
    window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()//LocalExtraColors.current.topBar.toArgb()
    window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()//LocalExtraColors.current.bottomBar.toArgb()
    val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f

    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useDarkIcons
        isAppearanceLightNavigationBars = useDarkIcons
    }
}

