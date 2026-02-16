package com.dopayurii.movie.presentation.navigation

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dopayurii.movie.presentation.ui.details.DetailsRoute
import com.dopayurii.movie.presentation.ui.search.SearchRoute

/**
 * Navigation host composable that sets up the app's navigation graph.
 * Defines routes for Search and Details screens with custom animations.
 *
 * @param navController Navigation controller for managing navigation state
 * @param paddingValues Padding values to apply to the navigation host content
 */
@Composable
fun MovieNavHost(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.SearchScreen.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(
            route = Screen.SearchScreen.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(1000, delayMillis = 20)
                ) + fadeIn(animationSpec = tween(100, delayMillis = 200))
            },
            popEnterTransition = {
                fadeIn(tween(1000))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(1000)
                ) + fadeOut(animationSpec = tween(1000))
            }
        ) {
            SearchRoute(navController)
        }

        composable(
            route = Screen.DetailsScreen.route + "/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType }),
            enterTransition = {
                fadeIn(tween(700, easing = EaseIn))
            },
            popExitTransition = {
                fadeOut(tween(800, easing = EaseOut))
            }
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            DetailsRoute(
                navController = navController,
                movieId = movieId
            )
        }
    }
}

/**
 * Extension function to navigate to the Details screen with a movie ID.
 *
 * @param id IMDB ID of the movie to display details for
 */
fun NavController.navigateToDetailsScreen(id: String) {
    this.navigate("${Screen.DetailsScreen.route}/${id}")
}



