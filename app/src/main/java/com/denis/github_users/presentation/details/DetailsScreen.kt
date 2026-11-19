package com.denis.github_users.presentation.details

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.denis.github_users.R
import com.denis.github_users.presentation.details.model.PresentationDetails
import com.denis.github_users.presentation.itemsList.PROGRESS_TAG
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

const val PICTURE_TAG = "picture"
const val USER_NAME_TAG = "user_name"
const val DETAILS_ERROR_TAG = "details_error"

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
            LoadingState.Started -> CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag(PROGRESS_TAG),
            )

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
private fun DetailsContent(details: PresentationDetails) {
    val unknown = stringResource(R.string.unknown)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        AsyncImage(
            model = details.avatar_url,
            contentDescription = details.login,
            placeholder = painterResource(R.drawable.person_150),
            error = painterResource(R.drawable.person_150),
            fallback = painterResource(R.drawable.person_150),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .testTag(PICTURE_TAG),
        )
        Text(
            text = details.name ?: details.login,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.testTag(USER_NAME_TAG),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.align(Alignment.Start),
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
            CountText(R.string.public_repos, details.public_repos, unknown)
            CountText(R.string.public_gists, details.public_gists, unknown)
            CountText(R.string.followers, details.followers, unknown)
            CountText(R.string.following, details.following, unknown)
            Text(stringResource(R.string.created, details.created_at ?: unknown))
            Text(stringResource(R.string.updated, details.updated_at ?: unknown))
        }
        details.bio?.let { bio ->
            Text(
                text = stringResource(R.string.bio_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(text = bio, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun CountText(@StringRes formatRes: Int, count: Int?, unknown: String) {
    val text = if (count != null) {
        stringResource(formatRes, count)
    } else {
        stringResource(formatRes, 0).substringBefore(":") + ": " + unknown
    }
    Text(text)
}
