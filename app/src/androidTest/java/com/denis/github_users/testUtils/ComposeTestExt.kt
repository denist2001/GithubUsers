package com.denis.github_users.testUtils

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText

private const val DEFAULT_TIMEOUT_MS = 15_000L

fun ComposeTestRule.waitUntilTagExists(tag: String, timeoutMs: Long = DEFAULT_TIMEOUT_MS) =
    waitUntil(timeoutMs) { onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }

fun ComposeTestRule.waitUntilTagDisappears(tag: String, timeoutMs: Long = DEFAULT_TIMEOUT_MS) =
    waitUntil(timeoutMs) { onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty() }

fun ComposeTestRule.waitUntilTextExists(text: String, timeoutMs: Long = DEFAULT_TIMEOUT_MS) =
    waitUntil(timeoutMs) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
