package com.dopayurii.movie

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for MovieExplorer.
 * Serves as the Hilt dependency injection entry point for the app.
 */
@HiltAndroidApp
class MovieApplication : Application()
