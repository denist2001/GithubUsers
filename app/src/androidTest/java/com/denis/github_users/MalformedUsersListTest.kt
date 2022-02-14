package com.denis.github_users

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.denis.github_users.data.RepositoryService
import com.denis.github_users.data.db.UsersDB
import com.denis.github_users.data.repository.RepositoryImpl
import com.denis.github_users.di.CoroutinesModule
import com.denis.github_users.di.DBModule
import com.denis.github_users.di.NetworkModule
import com.denis.github_users.domain.Repository
import com.denis.github_users.presentation.itemsList.ItemsFragment
import com.denis.github_users.testUtils.checkViewByText
import com.denis.github_users.testUtils.launchFragmentInHiltContainer
import com.denis.github_users.testUtils.waitUntilProgressBarDisappears
import com.denis.github_users.testUtils.waitUntilViewWithId
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
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@UninstallModules(NetworkModule::class, CoroutinesModule::class, DBModule::class)
@HiltAndroidTest
class MalformedUsersListTest : BaseTestClass() {

    private lateinit var navController: NavController
    override fun setMockResponses() {
        // I have to set dispatcher for concrete cases
    }

    override fun launchFragment() {
        launchFragmentInHiltContainer<ItemsFragment> {
            //preparation for starting fragment
            navController = TestNavHostController(requireActivity())
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @Test
    fun showUsers_whenEmptyListComes_shouldShowEmptyScreenAndHideProgressBar() {
        setNetworkDispatcher("empty.json", 200)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        assertTrue(device.checkViewByText("List is empty."))
    }

    @Test
    fun showUsers_whenMalformedListComes_shouldShowEmptyScreenAndHideProgressBar() {
        setNetworkDispatcher("malformedList.json", 200)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        assertTrue(device.checkViewByText("IO error"))
    }

    @Test
    fun showUsers_whenMalformedUserIdComes_shouldShowEmptyScreenAndHideProgressBar() {
        setNetworkDispatcher("malformedUsersId.json", 200)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        assertTrue(device.checkViewByText("IO error"))
    }

    @Test
    fun showUsers_whenMalformedUserLoginComes_shouldShowEmptyScreenAndHideProgressBar() {
        setNetworkDispatcher("malformedUsersLogin.json", 200)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        assertTrue(device.checkViewByText("IO error"))
    }

    @Test
    fun showUsers_whenUserCorrectButNetworkErrorComes_shouldShowEmptyScreenAndHideProgressBar() {
        setNetworkDispatcher("user.json", 404)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        assertTrue(device.checkViewByText("Network error"))
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