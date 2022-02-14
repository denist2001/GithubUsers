package com.denis.github_users

import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.denis.github_users.data.db.UsersDB
import com.denis.github_users.data.db.dao.UserDataDao
import com.denis.github_users.testUtils.getStringFrom
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.IOException
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
abstract class BaseTestClass {
    @Inject
    lateinit var db: UsersDB
    lateinit var userDataDao: UserDataDao

    lateinit var mockServer: MockWebServer
    val device: UiDevice by lazy {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var intentsTestRule = IntentsTestRule(HiltTestActivity::class.java)

    @get:Rule(order = 2)
    var grantPermissionRule = GrantPermissionRule.grant(
        "android.permission.INTERNET"
    )

    @Before
    fun setUp(): Unit = runBlocking {
        hiltRule.inject()
        mockServer = MockWebServer()
        mockServer.start(8080)
        setMockResponses()
        userDataDao = db.userDataDao()
        userDataDao.clearAll()
        launchFragment()
    }

    fun setNetworkDispatcher(fileName: String, responseCode: Int) {
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val response = MockResponse()
                    .setResponseCode(responseCode)
                    .setBody(getStringFrom(fileName))
                return response
            }
        }
    }

    abstract fun setMockResponses()

    abstract fun launchFragment()

    @After
    @Throws(IOException::class)
    fun closeDb() {
        device.setOrientationNatural()
        db.close()
        mockServer.shutdown()
    }
}
