package com.dopayurii.movie.presentation.navigation

/**
 * Sealed class representing navigation destinations in the app.
 *
 * @property title Human-readable screen title
 * @property route Navigation route string
 */
sealed class Screen(val title: String, val route: String) {
    /**
     * Search screen destination - the app's entry point.
     */
    data object SearchScreen : Screen("Search", "search")

    /**
     * Details screen destination for showing movie information.
     */
    data object DetailsScreen : Screen("Details", "details")
}
