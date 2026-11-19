package com.denis.github_users.presentation.itemsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.denis.github_users.R
import com.denis.github_users.presentation.itemsList.model.PresentationUserData

const val ITEMS_LIST_TAG = "items_list"
const val PROGRESS_TAG = "progress"
const val ERROR_TAG = "error"
private const val CLICK_THROTTLE_MILLIS = 1000L

@Composable
fun ItemsScreen(
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel = hiltViewModel(),
) {
    val items = viewModel.itemsFlow.collectAsLazyPagingItems()
    val throttledClick = rememberThrottledClick(onUserClick)
    val refreshState = items.loadState.refresh

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag(ITEMS_LIST_TAG),
        ) {
            items(
                count = items.itemCount,
                key = items.itemKey { it.id },
            ) { index ->
                items[index]?.let { user ->
                    UserItem(
                        user = user,
                        onClick = { throttledClick(user.login) },
                        modifier = Modifier.testTag("item_$index"),
                    )
                }
            }
        }
        when {
            refreshState is LoadState.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag(PROGRESS_TAG),
            )

            refreshState is LoadState.Error -> MessageText(
                text = refreshState.error.message ?: stringResource(R.string.unknown_issue),
                modifier = Modifier.align(Alignment.Center),
            )

            items.itemCount == 0 -> MessageText(
                text = stringResource(R.string.list_is_empty),
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun MessageText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .padding(16.dp)
            .testTag(ERROR_TAG),
    )
}

@Composable
private fun rememberThrottledClick(onClick: (String) -> Unit): (String) -> Unit {
    val lastClick = remember { longArrayOf(0L) }
    return { login ->
        val now = System.currentTimeMillis()
        if (now - lastClick[0] >= CLICK_THROTTLE_MILLIS) {
            lastClick[0] = now
            onClick(login)
        }
    }
}

@Composable
private fun UserItem(
    user: PresentationUserData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(168.dp)
            .padding(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
        ) {
            AsyncImage(
                model = user.avatar_url,
                contentDescription = user.login,
                placeholder = painterResource(R.drawable.person_150),
                error = painterResource(R.drawable.person_150),
                fallback = painterResource(R.drawable.person_150),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(R.string.login, user.login),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primaryContainer,
                )
                Text(
                    text = stringResource(R.string.id_number, user.id),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
