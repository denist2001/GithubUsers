package com.denis.github_users.presentation.theme

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/** Morphs the avatar between list and details; no-op when rendered outside the NavHost (e.g. tests). */
@Composable
fun Modifier.sharedAvatar(login: String): Modifier {
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val visibilityScope = LocalNavAnimatedVisibilityScope.current ?: return this
    return with(sharedScope) {
        this@sharedAvatar.sharedElement(
            rememberSharedContentState(key = "avatar-$login"),
            visibilityScope,
        )
    }
}
