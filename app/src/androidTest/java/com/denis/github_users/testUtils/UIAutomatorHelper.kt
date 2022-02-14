package com.denis.github_users.testUtils

import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector

fun UiDevice.checkViewByText(expectedText: String, timeoutInMs: Long = 1000): Boolean {
    val uiObject = findObject(UiSelector().text(expectedText))
    return uiObject.waitForExists(timeoutInMs)
}

fun getUiObject(viewId: Int): UiObject {
    val resourceId = InstrumentationRegistry.getTargetContext().resources.getResourceName(viewId)
    val selector = UiSelector().resourceId(resourceId)
    return UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation())
        .findObject(selector)
}