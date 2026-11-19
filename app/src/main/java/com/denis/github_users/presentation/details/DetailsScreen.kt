package com.denis.github_users.presentation.details

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.denis.github_users.R
import com.denis.github_users.presentation.details.model.PresentationDetails
import com.denis.github_users.presentation.itemsList.PROGRESS_TAG
import com.denis.github_users.presentation.theme.glass
import com.denis.github_users.presentation.theme.sharedAvatar
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

const val PICTURE_TAG = "picture"
const val USER_NAME_TAG = "user_name"
const val DETAILS_ERROR_TAG = "details_error"
private const val COUNT_UP_MILLIS = 1_200
private val PanelShape = RoundedCornerShape(24.dp)
private val RingColors = listOf(Color(0xFF03DAC5), Color(0xFF8E5CFF), Color(0xFFFF5C9E), Color(0xFF03DAC5))

@Composable
fun DetailsScreen(
    login: String?,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    var state by remember { mutableStateOf<LoadingState>(LoadingState.Started) }
    var isErrorDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(login) {
        // stateFlow has no replay, so subscribe before triggering the load
        launch(start = CoroutineStart.UNDISPATCHED) {
            viewModel.stateFlow.collect { state = it }
        }
        viewModel.startLoading(login)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val current = state) {
            LoadingState.Started -> {
                // Keep the avatar on screen so the shared-element transition has a target while loading
                login?.let { Avatar(url = null, login = it, modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)) }
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag(PROGRESS_TAG),
                )
            }

            is LoadingState.Loaded -> DetailsContent(current.userDataDetails)

            is LoadingState.Error -> if (!isErrorDismissed) {
                AlertDialog(
                    modifier = Modifier.testTag(DETAILS_ERROR_TAG),
                    onDismissRequest = { isErrorDismissed = true },
                    text = { Text(current.error) },
                    confirmButton = {
                        TextButton(onClick = { isErrorDismissed = true }) {
                            Text(stringResource(android.R.string.ok))
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun Avatar(url: String?, login: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        contentDescription = login,
        placeholder = painterResource(R.drawable.person_150),
        error = painterResource(R.drawable.person_150),
        fallback = painterResource(R.drawable.person_150),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(150.dp)
            .sharedAvatar(login)
            .clip(CircleShape)
            .border(4.dp, Brush.sweepGradient(RingColors), CircleShape),
    )
}

@Composable
private fun DetailsContent(details: PresentationDetails) {
    val unknown = stringResource(R.string.unknown)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Avatar(
            url = details.avatar_url,
            login = details.login,
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag(PICTURE_TAG),
        )
        Text(
            text = details.name ?: details.login,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.testTag(USER_NAME_TAG),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            StatTile(R.string.stat_repos, details.public_repos, Modifier.weight(1f))
            StatTile(R.string.stat_gists, details.public_gists, Modifier.weight(1f))
            StatTile(R.string.stat_followers, details.followers, Modifier.weight(1f))
            StatTile(R.string.stat_following, details.following, Modifier.weight(1f))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .glass(PanelShape)
                .padding(20.dp),
        ) {
            Text(stringResource(R.string.company, details.company ?: unknown))
            Text(stringResource(R.string.location, details.location ?: unknown))
            Text(stringResource(R.string.email, details.email ?: unknown))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.hireable))
                Image(
                    painter = painterResource(
                        if (details.hireable == true) R.drawable.checked else R.drawable.on_hold
                    ),
                    contentDescription = null,
                )
            }
            Text(stringResource(R.string.twitter_username, details.twitter_username ?: unknown))
            Text(stringResource(R.string.created, details.created_at ?: unknown))
            Text(stringResource(R.string.updated, details.updated_at ?: unknown))
        }
        details.bio?.let { bio ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .glass(PanelShape)
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.bio_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = bio, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun StatTile(@StringRes labelRes: Int, count: Int?, modifier: Modifier = Modifier) {
    val animatedCount = remember(count) { Animatable(0f) }
    LaunchedEffect(count) {
        count?.let { animatedCount.animateTo(it.toFloat(), tween(COUNT_UP_MILLIS, easing = FastOutSlowInEasing)) }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .glass(RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 4.dp),
    ) {
        Text(
            text = count?.let { animatedCount.value.roundToInt().toString() } ?: "–",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.75f),
            maxLines = 1,
        )
    }
}
