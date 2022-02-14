package com.denis.github_users.testUtils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.awaitility.Awaitility
import org.awaitility.core.ConditionTimeoutException
import org.hamcrest.Matcher
import org.junit.Assert
import java.util.concurrent.TimeUnit

fun waitUntilViewWithId(
    viewId: Int,
    timeoutInSec: Long = 3,
    matcher: Matcher<View>
) {
    try {
        Awaitility.await().atMost(timeoutInSec, TimeUnit.SECONDS).untilAsserted {
            onView(withId(viewId)).check(matches(matcher))
        }
    } catch (e: ConditionTimeoutException) {
        Assert.fail("View with id: $viewId doesn't match $matcher in $timeoutInSec seconds")
    }
}

fun waitUntilViewWithText(
    text: String,
    timeoutInSec: Long = 3,
    matcher: Matcher<View>
) {
    try {
        Awaitility.await().atMost(timeoutInSec, TimeUnit.SECONDS).untilAsserted {
            onView(withText(text)).check(matches(matcher))
        }
    } catch (e: ConditionTimeoutException) {
        Assert.fail("View with text: $text doesn't match $matcher in $timeoutInSec seconds")
    }
}

fun waitUntilProgressBarAppears(
    viewId: Int,
    timeoutInMs: Long = 3
) {
    try {
        getUiObject(viewId).waitForExists(timeoutInMs)
    } catch (e: ConditionTimeoutException) {
        Assert.fail("View with id: $viewId doesn't appear in $timeoutInMs seconds")
    }
}

fun waitUntilProgressBarDisappears(
    viewId: Int,
    timeoutInMs: Long = 3
) {
    try {
        getUiObject(viewId).waitUntilGone(timeoutInMs)
    } catch (e: ConditionTimeoutException) {
        Assert.fail("View with id: $viewId doesn't disappear in $timeoutInMs seconds")
    }
}