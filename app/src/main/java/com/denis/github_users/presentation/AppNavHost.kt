package com.denis.github_users.presentation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.denis.github_users.presentation.details.DetailsScreen
import com.denis.github_users.presentation.itemsList.ItemsScreen
import com.denis.github_users.presentation.theme.LocalNavAnimatedVisibilityScope
import com.denis.github_users.presentation.theme.LocalSharedTransitionScope

private const val ITEMS_ROUTE = "items"
private const val LOGIN_ARG = "login"
private const val DETAILS_ROUTE = "details/{$LOGIN_ARG}"

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = ITEMS_ROUTE,
            ) {
                composable(ITEMS_ROUTE) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        ItemsScreen(onUserClick = { login -> navController.navigate("details/$login") })
                    }
                }
                composable(
                    route = DETAILS_ROUTE,
                    arguments = listOf(navArgument(LOGIN_ARG) { type = NavType.StringType }),
                ) { entry ->
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        DetailsScreen(login = entry.arguments?.getString(LOGIN_ARG))
                    }
                }
            }
        }
    }
}
