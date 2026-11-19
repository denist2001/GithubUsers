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
import com.denis.github_users.presentation.details.DETAILS_ERROR_TAG
import com.denis.github_users.presentation.details.DetailsScreen
import com.denis.github_users.presentation.details.PICTURE_TAG
import com.denis.github_users.presentation.details.USER_NAME_TAG
import com.denis.github_users.presentation.itemsList.PROGRESS_TAG
import com.denis.github_users.testUtils.waitUntilTagDisappears
import com.denis.github_users.testUtils.waitUntilTagExists

@ExperimentalCoroutinesApi
@UninstallModules(NetworkModule::class, CoroutinesModule::class, DBModule::class)
@HiltAndroidTest
class DetailsUserTest : BaseTestClass() {

    override val launchOnSetUp = false

    override fun setMockResponses() {
    }

    override fun launchScreen() {
        composeRule.setContent {
            GithubUsersTheme {
                DetailsScreen(login = "mojombo")
            }
        }
    }

    @Test
    fun showUserDetails_whenServerDataIsCorrect() {
        setNetworkDispatcher("usersDetails.json", 200)
        launchScreen()
        composeRule.waitUntilTagExists(PICTURE_TAG)
        composeRule.onNodeWithTag(PICTURE_TAG).assertIsDisplayed()
        composeRule.onNodeWithTag(USER_NAME_TAG).assertIsDisplayed()
        composeRule.waitUntilTagDisappears(PROGRESS_TAG)
    }

    @Test
    fun showUserDetails_whenServerDataIsNotCorrect_shouldNotCrash() {
        setNetworkDispatcher("usersDetailsWithoutPictureUrl.json", 200)
        launchScreen()
        composeRule.waitUntilTagDisappears(PROGRESS_TAG)
        // Missing avatar_url is reported through the error dialog instead of crashing
        composeRule.onNodeWithTag(DETAILS_ERROR_TAG).assertIsDisplayed()
        composeRule.onNodeWithTag(PICTURE_TAG).assertDoesNotExist()
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