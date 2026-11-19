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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.denis.github_users.presentation.itemsList.ItemsScreen
import com.denis.github_users.presentation.itemsList.PROGRESS_TAG
import com.denis.github_users.testUtils.waitUntilTextExists

@ExperimentalCoroutinesApi
@UninstallModules(NetworkModule::class, CoroutinesModule::class, DBModule::class)
@HiltAndroidTest
class MalformedUsersListTest : BaseTestClass() {

    // Each test configures its own mock response before launching the screen
    override val launchOnSetUp = false

    override fun setMockResponses() {
    }

    override fun launchScreen() {
        composeRule.setContent {
            GithubUsersTheme {
                ItemsScreen(onUserClick = {})
            }
        }
    }

    @Test
    fun showUsers_whenEmptyListComes_shouldShowEmptyScreenAndHideProgressBar() {
        assertMessageShown("empty.json", 200, "List is empty.")
    }

    @Test
    fun showUsers_whenMalformedListComes_shouldShowErrorAndHideProgressBar() {
        assertMessageShown("malformedList.json", 200, "IO error")
    }

    @Test
    fun showUsers_whenMalformedUserIdComes_shouldShowErrorAndHideProgressBar() {
        assertMessageShown("malformedUsersId.json", 200, "IO error")
    }

    @Test
    fun showUsers_whenMalformedUserLoginComes_shouldShowErrorAndHideProgressBar() {
        assertMessageShown("malformedUsersLogin.json", 200, "IO error")
    }

    @Test
    fun showUsers_whenUserCorrectButNetworkErrorComes_shouldShowErrorAndHideProgressBar() {
        assertMessageShown("user.json", 404, "Network error")
    }

    private fun assertMessageShown(fileName: String, responseCode: Int, message: String) {
        setNetworkDispatcher(fileName, responseCode)
        launchScreen()
        composeRule.waitUntilTextExists(message)
        composeRule.onNodeWithText(message).assertIsDisplayed()
        composeRule.onNodeWithTag(PROGRESS_TAG).assertDoesNotExist()
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