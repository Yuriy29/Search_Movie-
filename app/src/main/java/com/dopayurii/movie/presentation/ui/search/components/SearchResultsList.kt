package com.dopayurii.movie.presentation.ui.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.dopayurii.movie.data.model.MovieSummary

@Composable
fun SearchResultsList(
    movies: LazyPagingItems<MovieSummary>,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1️⃣ Initial load / refresh states
        when (val refreshState = movies.loadState.refresh) {
            is LoadState.Loading -> {
                if (movies.itemCount == 0) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            is LoadState.Error -> {
                if (movies.itemCount == 0) {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = refreshState.error.localizedMessage ?: "Failed to load results",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = { movies.retry() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            is LoadState.NotLoading -> {
                if (movies.itemCount == 0) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No results found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        // 2️⃣ Display items using stable keys
        items(
            count = movies.itemCount,
            key = { index -> "${movies[index]?.imdbId}_${index}" },
            contentType = movies.itemContentType { "movie" }
        ) { index ->
            movies[index]?.let { movie ->
                SearchResultItem(
                    movie = movie,
                    onClick = { onMovieClick(movie.imdbId) }
                )
            }
        }

        // 3️⃣ Footer: Loading indicator
        item(
            key = "append_loader",
            contentType = "loader"
        ) {
            if (movies.loadState.append is LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // 4️⃣ Footer: Error when loading more
        item(
            key = "append_error",
            contentType = "error"
        ) {
            val error = movies.loadState.append as? LoadState.Error
            if (error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = error.error.localizedMessage ?: "Failed to load more",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { movies.retry() },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        // 5️⃣ Bottom spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
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
