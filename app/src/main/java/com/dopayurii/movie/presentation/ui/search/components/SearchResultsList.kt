package com.dopayurii.movie.presentation.ui.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ehsanmsz.mszprogressindicator.progressindicator.BallPulseProgressIndicator
import com.dopayurii.movie.data.model.MovieSummary

/**
 * Displays a scrollable list of search results with pagination support.
 *
 * @param results List of movie summaries to display
 * @param totalResults Total number of results available
 * @param isLoadingMore Whether more results are being loaded
 * @param hasMoreResults Whether there are more results to load
 * @param onMovieClick Callback when a movie is clicked
 * @param onLoadMoreClick Callback when "Load More" is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun SearchResultsList(
    results: List<MovieSummary>,
    totalResults: Int,
    isLoadingMore: Boolean,
    hasMoreResults: Boolean,
    onMovieClick: (String) -> Unit,
    onLoadMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(results, key = { index, movie -> "${movie.imdbId}_$index" }) { _, movie ->
            SearchResultItem(
                movie = movie,
                onClick = { onMovieClick(movie.imdbId) }
            )
        }

        if (hasMoreResults) {
            item {
                LoadMoreButton(
                    isLoading = isLoadingMore,
                    onClick = onLoadMoreClick,
                    loadedCount = results.size,
                    totalCount = totalResults
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    movie: MovieSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalDivider(thickness = 0.5.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        MoviePoster(
            posterUrl = movie.poster,
            id = movie.imdbId,
            width = 50.dp,
            onClick = {}
        )
        Spacer(Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontSize = 18.sp, fontStyle = FontStyle.Italic)) {
                        append(movie.title)
                    }
                    withStyle(
                        SpanStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        append("\n${movie.year} • ${movie.type}")
                    }
                },
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun LoadMoreButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    loadedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (isLoading) {
            BallPulseProgressIndicator()
        } else {
            Button(onClick = onClick) {
                Text("Load More ($loadedCount of $totalCount)")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Showing $loadedCount of $totalCount results",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
