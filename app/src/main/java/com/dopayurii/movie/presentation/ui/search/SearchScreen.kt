package com.dopayurii.movie.presentation.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.presentation.navigation.navigateToDetailsScreen
import com.dopayurii.movie.presentation.ui.model.SearchUiState
import com.dopayurii.movie.presentation.ui.search.components.SearchBar
import com.dopayurii.movie.presentation.ui.search.components.SearchResultsList

@Composable
fun SearchRoute(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movies = viewModel.searchResults.collectAsLazyPagingItems()

    SearchScreen(
        uiState = uiState,
        movies = movies,
        onEvent = viewModel::onEvent,
        onNavigateToDetails = { movieId ->
            navController.navigateToDetailsScreen(movieId)
        }
    )
}

/**
 * Stateless Search screen composable using Paging 3.
 */
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    movies: LazyPagingItems<MovieSummary>,
    onEvent: (SearchUiEvent) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SearchBar(
            query = uiState.query,
            onQueryChange = { onEvent(SearchUiEvent.OnQueryChange(it)) },
            onSearchSubmit = { onEvent(SearchUiEvent.OnQueryChange(it)) },
            onClearClick = { onEvent(SearchUiEvent.OnClearSearch) }
        )

        // Show initial state when query is too short (less than 2 chars)
        if (uiState.query.length < 2) {
            SearchScreenInitial()
        } else {
            SearchResultsList(
                movies = movies,
                onMovieClick = onNavigateToDetails
            )
        }
    }
}

@Composable
private fun SearchScreenInitial() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = "Search for movies, series, and episodes",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
