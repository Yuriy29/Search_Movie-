package com.dopayurii.movie.presentation.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dopayurii.movie.R
import com.dopayurii.movie.data.model.MovieType

/**
 * Composable that displays a movie poster image with optional type badge.
 *
 * @param posterUrl URL of the poster image to load
 * @param id Unique identifier for the movie
 * @param movieType Type of content (movie, series, episode) for the badge color
 * @param showMovieType Whether to show a type badge overlay
 * @param width Width of the poster image
 * @param onClick Callback when the poster is clicked, receives the movie id
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun MoviePoster(posterUrl: String, id: String, movieType: MovieType = MovieType.MOVIE, showMovieType: Boolean = false, width: Dp = 100.dp, onClick: ((id: String) -> Unit)? = null, modifier: Modifier = Modifier) {
    Box(
        modifier
            .width(width)
            .shadow(
                width * 0.15f,
                spotColor = Color.Black,
                shape = RoundedCornerShape(width * 0.1f)
            )
            .clip(RoundedCornerShape(width * 0.1f))
            .clickable(enabled = onClick != null) {
                if (onClick != null){
                    onClick(id)
                }
            }
    ) {
        val context = LocalContext.current

        val density = LocalDensity.current
        val imageRequest: ImageRequest = remember(posterUrl, width, density) {
            with(density) {
                ImageRequest.Builder(context).data(posterUrl).size(width.roundToPx(), (width*1.8f).roundToPx()).crossfade(500).build()
            }
        }

        AsyncImage(
            model = imageRequest, null,
            modifier = Modifier
                .width(width)
                .height(width * 1.8f)
                .clip(RoundedCornerShape(width * 0.10f)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.gggr),
            error = painterResource(R.drawable.placeholder),
            filterQuality = FilterQuality.Low,

        )
        if (showMovieType){
            Row(
                modifier = Modifier
                    .background(movieType.movieTypeColor())
                    .fillMaxWidth()
            ) {
                Text(
                    movieType.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

