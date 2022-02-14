package com.denis.github_users

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.denis.github_users.data.RepositoryService
import com.denis.github_users.data.db.UsersDB
import com.denis.github_users.data.repository.RepositoryImpl
import com.denis.github_users.di.CoroutinesModule
import com.denis.github_users.di.DBModule
import com.denis.github_users.di.NetworkModule
import com.denis.github_users.domain.Repository
import com.denis.github_users.presentation.itemsList.ItemsFragment
import com.denis.github_users.presentation.itemsList.innerItem.ItemViewHolder
import com.denis.github_users.testUtils.launchFragmentInHiltContainer
import com.denis.github_users.testUtils.waitUntilProgressBarAppears
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@UninstallModules(NetworkModule::class, CoroutinesModule::class, DBModule::class)
@HiltAndroidTest
class UsersListTest : BaseTestClass() {

    private lateinit var navController: NavController
    override fun setMockResponses() {
        setNetworkDispatcher("10users.json", 200)
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
    fun progressBar_shouldAppearDuringStartAndDisappearWhenListDownloaded() = runBlocking {
        waitUntilProgressBarAppears(R.id.progress_pb, 1000)
        assertEquals(10, userDataDao.getUserDataEntities(100).size)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
    }

    @Test
    fun showUsers_shouldAppearInOrderOfIds() = runBlocking {
        waitUntilProgressBarAppears(R.id.progress_pb, 1000)
        assertEquals(10, userDataDao.getUserDataEntities(100).size)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        val ids = listOf(1, 2, 3, 4, 5, 6, 7, 17, 18, 19)
        ids.forEachIndexed { index, id ->
            checkRecyclerItemAndScrollToPos(index, id)
        }
    }

    @Test
    fun showUsers_whenScreenRotates_shouldNotDoRequest() = runBlocking {
        assertNotNull(mockServer.takeRequest())
        waitUntilProgressBarAppears(R.id.progress_pb, 1000)
        assertEquals(10, userDataDao.getUserDataEntities(100).size)
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        device.unfreezeRotation()
        device.setOrientationLeft()
        assertNull(mockServer.takeRequest(1, TimeUnit.SECONDS))
    }

    @Test
    fun clickOnUser_shouldGoToDetailsFragment() = runBlocking {
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        onView(withId(R.id.items_list_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemViewHolder>(
                0,
                click()
            )
        )
        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            when (destination.id) {
                R.id.detailsFragment -> assertEquals("mojombo", bundle?.getString("login"))
                else -> assertNull(bundle?.getString("login"))
            }
        }
    }

    @Test
    fun doubleClickOnUser_shouldGoToDetailsFragment() = runBlocking {
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        onView(withId(R.id.items_list_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemViewHolder>(
                0,
                doubleClick()
            )
        )
        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            when (destination.id) {
                R.id.detailsFragment -> assertEquals("mojombo", bundle?.getString("login"))
                else -> assertNull(bundle?.getString("login"))
            }
        }
    }

    @Test
    fun longClickOnUser_shouldGoToDetailsFragment() = runBlocking {
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        onView(withId(R.id.items_list_rv)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ItemViewHolder>(
                0,
                longClick()
            )
        )
        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            when (destination.id) {
                R.id.detailsFragment -> assertEquals("mojombo", bundle?.getString("login"))
                else -> assertNull(bundle?.getString("login"))
            }
        }
    }

    @Test
    fun clickOnDifferentUsers_shouldGoToDetailsFragment() = runBlocking {
        waitUntilViewWithId(R.id.items_list_rv, 15, isDisplayed())
        waitUntilProgressBarDisappears(R.id.progress_pb, 1000)
        launch {
            onView(withId(R.id.items_list_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemViewHolder>(
                    0,
                    click()
                )
            )
        }
        launch {
            onView(withId(R.id.items_list_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition<ItemViewHolder>(
                    1,
                    click()
                )
            )
        }
        navController.addOnDestinationChangedListener { controller, destination, bundle ->
            when (destination.id) {
                R.id.detailsFragment -> assertNotNull(bundle?.getString("login"))
                else -> assertNull(bundle?.getString("login"))
            }
        }
    }

    private fun checkRecyclerItemAndScrollToPos(index: Int, id: Int) {
        onView(
            withId(
                R.id.items_list_rv
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.items_list_rv))
            .perform(scrollToPosition<ItemViewHolder>(index))
        onView(withText("ID: $id")).check(matches(isDisplayed()))
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