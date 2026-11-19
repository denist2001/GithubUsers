package com.denis.github_users.testUtils

import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

fun UiDevice.checkViewByText(expectedText: String, timeoutInMs: Long = 1000): Boolean {
    val uiObject = findObject(UiSelector().text(expectedText))
    return uiObject.waitForExists(timeoutInMs)
}
