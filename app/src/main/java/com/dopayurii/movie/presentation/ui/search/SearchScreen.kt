package com.dopayurii.movie.presentation.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.ehsanmsz.mszprogressindicator.progressindicator.BallPulseProgressIndicator
import com.dopayurii.movie.domain.model.SearchUiState
import com.dopayurii.movie.presentation.navigation.navigateToDetailsScreen
import com.dopayurii.movie.presentation.ui.search.components.SearchBar
import com.dopayurii.movie.presentation.ui.search.components.SearchResultsList

@Composable
fun SearchRoute(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToDetails = { movieId ->
            navController.navigateToDetailsScreen(movieId)
        }
    )
}

/**
 * Stateless Search screen composable.
 */
@Composable
fun SearchScreen(
    uiState: SearchUiState,
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
            onSearchSubmit = { onEvent(SearchUiEvent.OnSearchSubmit(it)) },
            onClearClick = { onEvent(SearchUiEvent.OnClearSearch) }
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                uiState.isLoading && uiState.searchResults.isEmpty() -> SearchScreenLoading()
                uiState.errorMessage != null && uiState.searchResults.isEmpty() ->
                    SearchScreenError(uiState.errorMessage)
                uiState.searchResults.isEmpty() && uiState.query.isEmpty() -> SearchScreenInitial()
                uiState.searchResults.isEmpty() -> SearchScreenNoResults()
                else -> SearchResultsList(
                    results = uiState.searchResults,
                    totalResults = uiState.totalResults,
                    isLoadingMore = uiState.isLoadingMore,
                    hasMoreResults = uiState.hasMoreResults,
                    onMovieClick = onNavigateToDetails,
                    onLoadMoreClick = { onEvent(SearchUiEvent.OnLoadMore) }
                )
            }
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

@Composable
private fun SearchScreenNoResults() {
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
            text = "No results found",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = "Try different keywords or check your spelling",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SearchScreenError(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun SearchScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BallPulseProgressIndicator()
    }
}
