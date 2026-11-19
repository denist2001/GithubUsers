package com.denis.github_users.presentation.itemsList

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
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
import com.denis.github_users.presentation.theme.glass
import com.denis.github_users.presentation.theme.sharedAvatar
import com.denis.github_users.presentation.theme.shimmer
import kotlinx.coroutines.delay

const val ITEMS_LIST_TAG = "items_list"
const val PROGRESS_TAG = "progress"
const val ERROR_TAG = "error"
private const val CLICK_THROTTLE_MILLIS = 1000L
private const val STAGGERED_ITEMS = 8
private const val STAGGER_DELAY_MILLIS = 70L
private const val PRESSED_SCALE = 0.94f
private const val SKELETON_COUNT = 6
private const val SPINNER_MILLIS = 900
private val CardShape = RoundedCornerShape(24.dp)
private val ShineColors = listOf(Color(0xFF03DAC5), Color(0xFF8E5CFF), Color(0xFFFF5C9E), Color(0xFF03DAC5))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel = hiltViewModel(),
) {
    val items = viewModel.itemsFlow.collectAsLazyPagingItems()
    val throttledClick = rememberThrottledClick(onUserClick)
    val refreshState = items.loadState.refresh
    val isRefreshing = refreshState is LoadState.Loading && items.itemCount > 0
    val pullState = rememberPullToRefreshState()
    // Keys of items that already played their entrance, so scrolling back doesn't replay it
    val appeared = remember { mutableSetOf<Any>() }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { items.refresh() },
        state = pullState,
        indicator = { GlassRefreshIndicator(pullState, isRefreshing, Modifier.align(Alignment.TopCenter)) },
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
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
                        entranceDelayMillis = if (user.id in appeared || index >= STAGGERED_ITEMS) {
                            0L
                        } else {
                            index * STAGGER_DELAY_MILLIS
                        },
                        shouldAnimateEntrance = appeared.add(user.id),
                        onClick = { throttledClick(user.login) },
                        modifier = Modifier
                            .animateItem()
                            .testTag("item_$index"),
                    )
                }
            }
        }
        when {
            refreshState is LoadState.Loading && items.itemCount == 0 -> SkeletonList()

            refreshState is LoadState.Error -> MessageText(
                text = refreshState.error.message ?: stringResource(R.string.unknown_issue),
                modifier = Modifier.align(Alignment.Center),
            )

            refreshState is LoadState.NotLoading && items.itemCount == 0 -> MessageText(
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
        color = Color.White,
        modifier = modifier
            .padding(16.dp)
            .glass(CardShape)
            .padding(24.dp)
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
    entranceDelayMillis: Long,
    shouldAnimateEntrance: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val entrance = remember { Animatable(if (shouldAnimateEntrance) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (entrance.value < 1f) {
            delay(entranceDelayMillis)
            entrance.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow))
        }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_SCALE else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press_scale",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .graphicsLayer {
                val progress = entrance.value
                alpha = progress.coerceIn(0f, 1f)
                translationY = (1f - progress) * 80.dp.toPx()
                val scale = pressScale * (0.9f + 0.1f * progress)
                scaleX = scale
                scaleY = scale
            }
            .glass(CardShape)
            .clickable(interactionSource = interactionSource, indication = ripple(), onClick = onClick)
            .height(120.dp),
    ) {
        // Oversized faded initial gives each card its own watermark
        Text(
            text = user.login.take(1).uppercase(),
            fontSize = 150.sp,
            fontWeight = FontWeight.Black,
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp, y = 8.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            AsyncImage(
                model = user.avatar_url,
                contentDescription = user.login,
                placeholder = painterResource(R.drawable.person_150),
                error = painterResource(R.drawable.person_150),
                fallback = painterResource(R.drawable.person_150),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .sharedAvatar(user.login)
                    .clip(CircleShape)
                    .border(2.dp, Brush.sweepGradient(ShineColors), CircleShape),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.login, user.login),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.id_number, user.id),
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun SkeletonList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .testTag(PROGRESS_TAG),
    ) {
        repeat(SKELETON_COUNT) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .glass(CardShape)
                    .padding(16.dp),
            ) {
                Box(
                    Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .shimmer(),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        Modifier
                            .size(width = 140.dp, height = 18.dp)
                            .clip(CircleShape)
                            .shimmer(),
                    )
                    Box(
                        Modifier
                            .size(width = 70.dp, height = 14.dp)
                            .clip(CircleShape)
                            .shimmer(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GlassRefreshIndicator(state: PullToRefreshState, isRefreshing: Boolean, modifier: Modifier = Modifier) {
    val spin by rememberInfiniteTransition(label = "refresh").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(SPINNER_MILLIS, easing = LinearEasing)),
        label = "refresh_spin",
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                val fraction = state.distanceFraction.coerceIn(0f, 1.5f)
                translationY = fraction * 72.dp.toPx() - 56.dp.toPx()
                alpha = fraction.coerceIn(0f, 1f)
                rotationZ = if (isRefreshing) spin else fraction * 360f
            }
            .size(48.dp)
            .glass(CircleShape),
    ) {
        Canvas(modifier = Modifier.size(28.dp)) {
            drawArc(
                brush = Brush.sweepGradient(ShineColors),
                startAngle = 0f,
                sweepAngle = 280f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
            )
        }
    }
}
