package com.dopayurii.movie.presentation.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ehsanmsz.mszprogressindicator.progressindicator.BallPulseProgressIndicator
import com.dopayurii.movie.R
import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.domain.model.DetailsUiState

/**
 * Route composable that wires up the ViewModel.
 */
@Composable
fun DetailsRoute(
    navController: NavController,
    movieId: String,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailsScreen(
        uiState = uiState,
        onNavigateBack = { navController.navigateUp() }
    )
}

/**
 * Stateless Details screen composable.
 */
@Composable
fun DetailsScreen(
    uiState: DetailsUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> DetailsLoading(modifier)
        uiState.errorMessage != null && uiState.movie == null ->
            DetailsError(message = uiState.errorMessage, modifier)
        uiState.movie != null -> DetailsContent(
            movie = uiState.movie,
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }
}

@Composable
private fun DetailsLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BallPulseProgressIndicator()
    }
}

@Composable
private fun DetailsError(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
    }
}

@Composable
private fun DetailsContent(
    movie: Movie,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Poster - full screen width, no rounded corners
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = movie.poster,
                contentDescription = "${movie.title} poster",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
                placeholder = painterResource(R.drawable.gggr),
                error = painterResource(R.drawable.placeholder)
            )

            // Back button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.9f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = Color.Black
                )
            }
        }

        // Content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${movie.title} (${movie.yearFrom}${movie.yearTo?.let { " - $it" } ?: ""})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            movie.imdbRating?.let { rating ->
                Text(
                    text = "IMDb: $rating/10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF616161)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            DetailSection(title = "Genres", content = movie.genre.joinToString(", "))
            DetailSection(title = "Director", content = movie.directors.joinToString(", "))
            DetailSection(title = "Writer", content = movie.writers.joinToString(", "))
            DetailSection(title = "Actors", content = movie.actors.joinToString(", "))
            DetailSection(title = "Languages", content = movie.language.joinToString(", "))
            DetailSection(title = "Countries", content = movie.country.joinToString(", "))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = movie.plot,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF757575)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
