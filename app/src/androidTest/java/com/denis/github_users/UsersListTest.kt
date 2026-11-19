package com.denis.github_users

import android.content.Context
import androidx.room.Room
import com.denis.github_users.data.RepositoryService
import com.denis.github_users.data.db.UsersDB
import com.denis.github_users.data.repository.RepositoryImpl
import com.denis.github_users.di.CoroutinesModule
import com.denis.github_users.di.DBModule
import com.denis.github_users.di.NetworkModule
import com.denis.github_users.domain.Repository
import com.denis.github_users.presentation.theme.GithubUsersTheme
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import com.denis.github_users.presentation.itemsList.ITEMS_LIST_TAG
import com.denis.github_users.presentation.itemsList.ItemsScreen
import com.denis.github_users.presentation.itemsList.PROGRESS_TAG
import com.denis.github_users.testUtils.waitUntilTagDisappears
import com.denis.github_users.testUtils.waitUntilTagExists
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import java.util.Collections
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@UninstallModules(NetworkModule::class, CoroutinesModule::class, DBModule::class)
@HiltAndroidTest
class UsersListTest : BaseTestClass() {

    private val clickedLogins: MutableList<String> = Collections.synchronizedList(mutableListOf())

    override fun setMockResponses() {
        setNetworkDispatcher("10users.json", 200)
    }

    override fun launchScreen() {
        composeRule.setContent {
            GithubUsersTheme {
                ItemsScreen(onUserClick = { clickedLogins += it })
            }
        }
    }

    @Test
    fun progressBar_shouldAppearDuringStartAndDisappearWhenListDownloaded() = runBlocking {
        waitUntilListLoaded()
        assertEquals(10, userDataDao.getUserDataEntities(100).size)
        composeRule.onNodeWithTag(PROGRESS_TAG).assertDoesNotExist()
    }

    @Test
    fun showUsers_shouldAppearInOrderOfIds() = runBlocking {
        waitUntilListLoaded()
        assertEquals(10, userDataDao.getUserDataEntities(100).size)
        val ids = listOf(1, 2, 3, 4, 5, 6, 7, 17, 18, 19)
        ids.forEachIndexed { index, id ->
            composeRule.onNodeWithTag(ITEMS_LIST_TAG).performScrollToIndex(index)
            composeRule.onNodeWithText("ID: $id").assertIsDisplayed()
        }
    }

    @Test
    fun showUsers_whenScreenRotates_shouldNotDoRequest() = runBlocking {
        assertNotNull(mockServer.takeRequest())
        waitUntilListLoaded()
        assertEquals(10, userDataDao.getUserDataEntities(100).size)
        device.unfreezeRotation()
        device.setOrientationLeft()
        assertNull(mockServer.takeRequest(1, TimeUnit.SECONDS))
    }

    @Test
    fun clickOnUser_shouldGoToDetails() {
        waitUntilListLoaded()
        composeRule.onNodeWithTag("item_0").performClick()
        composeRule.waitForIdle()
        assertEquals(listOf("mojombo"), clickedLogins)
    }

    @Test
    fun doubleClickOnUser_shouldGoToDetailsOnlyOnce() {
        waitUntilListLoaded()
        composeRule.onNodeWithTag("item_0").performTouchInput { doubleClick() }
        composeRule.waitForIdle()
        assertEquals(listOf("mojombo"), clickedLogins)
    }

    @Test
    fun longClickOnUser_shouldGoToDetails() {
        waitUntilListLoaded()
        composeRule.onNodeWithTag("item_0").performTouchInput { longClick() }
        composeRule.waitForIdle()
        assertEquals(listOf("mojombo"), clickedLogins)
    }

    @Test
    fun clickOnDifferentUsers_shouldGoToDetailsOnlyOnce() {
        waitUntilListLoaded()
        composeRule.onNodeWithTag("item_0").performClick()
        composeRule.onNodeWithTag("item_1").performClick()
        composeRule.waitForIdle()
        assertEquals(1, clickedLogins.size)
        assertTrue(clickedLogins.first().isNotEmpty())
    }

    private fun waitUntilListLoaded() {
        composeRule.waitUntilTagExists("item_0")
        composeRule.waitUntilTagDisappears(PROGRESS_TAG)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    inner class NetworkModuleForTests {
        @Provides
        @Singleton
        fun getUrl(): String = "http://localhost:8080"

        @Provides
        @Singleton
        fun provideGson() = GsonBuilder()
            .setLenient()
            .create()

        @Provides
        @Singleton
        fun provideRetrofit(gson: Gson, url: String) = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        @Provides
        @Singleton
        fun provideRepositoryService(retrofit: Retrofit) =
            retrofit.create(RepositoryService::class.java)

        @Provides
        @Singleton
        fun provideRepository(repository: RepositoryImpl): Repository = repository

        @Provides
        @Singleton
        fun provideDBInstance(@ApplicationContext appContext: Context) =
            Room.inMemoryDatabaseBuilder(appContext, UsersDB::class.java)
                .allowMainThreadQueries()
                .build()

        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider = object : DispatcherProvider {

            private val testDispatcher = UnconfinedTestDispatcher()

            override fun io(): CoroutineDispatcher {
                return testDispatcher
            }

            override fun default(): CoroutineDispatcher {
                return testDispatcher
            }

            override fun main(): CoroutineDispatcher {
                return testDispatcher
            }
        }
    }
}