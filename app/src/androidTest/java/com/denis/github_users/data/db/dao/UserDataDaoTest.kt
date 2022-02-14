package com.denis.github_users.data.db.dao

import android.content.Context
import androidx.room.Room
import com.denis.github_users.DispatcherProvider
import com.denis.github_users.data.db.UsersDB
import com.denis.github_users.data.model.UserDataEntity
import com.denis.github_users.di.CoroutinesModule
import com.denis.github_users.di.DBModule
import com.denis.github_users.di.NetworkModule
import com.denis.github_users.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@HiltAndroidTest
@UninstallModules(NetworkModule::class, CoroutinesModule::class, DBModule::class)
class UserDataDaoTest {

    @Inject
    lateinit var db: UsersDB
    private lateinit var userDataDao: UserDataDao
    private val mockedRepository: Repository = mockk()

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp(): Unit = runBlocking {
        hiltRule.inject()
        userDataDao = db.userDataDao()
    }

    @Test
    fun getLimitedAmountOfEntities_whenDBContainsMoreEntities_shouldReturnRequestedNumberOfValues() =
        runBlocking {
            // fulfill database
            val userDataList = ArrayList<UserDataEntity>()
            for (index in 0..5) {
                userDataList.add(createUserDataEntityWithId(index * 2))  // 0, 2, 4, 6, 8, 10
            }
            userDataDao.insertAll(userDataList)
            // get result
            val size = 4
            val result = userDataDao.getUserDataEntities(size)
            // check result
            assertTrue(result.isNotEmpty())
            assertEquals(size, result.size)
            for (index in 0 until size) {
                assertEquals(result[index].id, index * 2)
            }
        }

    @Test
    fun getLimitedAmountOfEntities_whenDBContainsLessEntities_shouldReturnAllAvailableValues() =
        runBlocking {
            // fulfill database
            val userDataList = ArrayList<UserDataEntity>()
            val expectedSize = 4
            for (index in 0 until expectedSize) {
                userDataList.add(createUserDataEntityWithId(index * 2))  // 0, 2, 4, 6, 8, 10
            }
            userDataDao.insertAll(userDataList)
            // get result
            val size = 10
            val result = userDataDao.getUserDataEntities(size)
            // check result
            assertTrue(result.isNotEmpty())
            assertEquals(expectedSize, result.size)
            for (index in 0 until expectedSize) {
                assertEquals(result[index].id, index * 2)
            }
        }

    @Test
    fun getLimitedAmountOfEntities_whenDBDoesNotContainEntities_shouldReturnEmptyList() =
        runBlocking {
            // get result
            val size = 10
            val result = userDataDao.getUserDataEntities(size)
            // check result
            assertTrue(result.isEmpty())
        }

    @Test
    fun clearAll_whenDBContainsEntities_shouldRemoveAllValues() = runBlocking {
        // fulfill database
        val userDataList = ArrayList<UserDataEntity>()
        val expectedSize = 4
        for (index in 0 until expectedSize) {
            userDataList.add(createUserDataEntityWithId(index * 2))  // 0, 2, 4, 6, 8, 10
        }
        userDataDao.insertAll(userDataList)
        val checkResult = userDataDao.getUserDataEntities(expectedSize)
        assertTrue(checkResult.isNotEmpty())
        // clear data
        userDataDao.clearAll()
        // get result
        val result = userDataDao.getUserDataEntities(expectedSize)
        // check result
        assertTrue(result.isEmpty())
    }

    @Test
    fun clearAll_whenDBDoesNotContainEntities_shouldNotCrash() = runBlocking {
        val someSize = 10
        // check initial size
        val checkResult = userDataDao.getUserDataEntities(someSize)
        assertTrue(checkResult.isEmpty())
        // clear data
        userDataDao.clearAll()
        // get result
        val result = userDataDao.getUserDataEntities(someSize)
        // check result
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertAll_whenInsertedListIsEmpty_shouldNotCrash() = runBlocking {
        val someSize = 10
        // add empty list
        userDataDao.insertAll(emptyList())
        // get result
        val result = userDataDao.getUserDataEntities(someSize)
        // check result
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertAll_whenInsertedTheSameEntities_shouldRefreshExisted() = runBlocking {
        val someSize = 10
        // fulfill database
        val userDataList = ArrayList<UserDataEntity>()
        val dbEntity = createUserDataEntityWithId(0)
        val expectedSize = 4
        for (index in 0 until expectedSize) {
            userDataList.add(dbEntity)
        }
        // check initial data
        assertEquals(expectedSize, userDataList.size)
        // add empty list
        userDataDao.insertAll(userDataList)
        // get result
        val result = userDataDao.getUserDataEntities(someSize)
        // check result
        assertEquals(1, result.size)
        assertEquals(dbEntity, result[0])
    }

    @Test
    fun deleteRangeOfEntities_shouldRemoveOnlyCorrectEntities() = runBlocking {
        // fulfill database
        val expectedSize = 6
        val userDataList = ArrayList<UserDataEntity>()
        for (index in 0 until expectedSize) {
            userDataList.add(createUserDataEntityWithId(index * 2))  // 0, 2, 4, 6, 8, 10
        }
        userDataDao.insertAll(userDataList)
        // check initial data
        assertEquals(expectedSize, userDataList.size)
        // remove ids: 4 and 6
        userDataDao.deleteRangeOfIds(4, 6)
        // get result
        val result = userDataDao.getUserDataEntities(expectedSize)
        // check result
        assertEquals(4, result.size)
        assertEquals(0, result[0].id)
        assertEquals(2, result[1].id)
        assertEquals(8, result[2].id)
        assertEquals(10, result[3].id)
    }

    @Test
    fun deleteRangeOfEntities_whenRangeIsBiggerThenDBRange_shouldRemoveOnlyCorrectEntities() =
        runBlocking {
            // fulfill database
            val expectedSize = 6
            val userDataList = ArrayList<UserDataEntity>()
            for (index in 0 until expectedSize) {
                userDataList.add(createUserDataEntityWithId(index))  // 0, 1, 2, 3, 4, 5
            }
            userDataDao.insertAll(userDataList)
            // check initial data
            assertEquals(expectedSize, userDataList.size)
            // remove id 10
            userDataDao.deleteRangeOfIds(4, 100)
            // get result
            val result = userDataDao.getUserDataEntities(expectedSize)
            // check result
            assertEquals(4, result.size)
            assertEquals(0, result[0].id)
            assertEquals(1, result[1].id)
            assertEquals(2, result[2].id)
            assertEquals(3, result[3].id)
        }

    @Test
    fun deleteRangeOfEntities_whenRangeIsLessThenDBRange_shouldRemoveOnlyCorrectEntities() =
        runBlocking {
            // fulfill database
            val expectedSize = 6
            val userDataList = ArrayList<UserDataEntity>()
            for (index in 0 until expectedSize) {
                userDataList.add(createUserDataEntityWithId(index))  // 0, 1, 2, 3, 4, 5
            }
            userDataDao.insertAll(userDataList)
            // check initial data
            assertEquals(expectedSize, userDataList.size)
            // remove id 10
            userDataDao.deleteRangeOfIds(-100, 0)
            // get result
            val result = userDataDao.getUserDataEntities(expectedSize)
            // check result
            assertEquals(5, result.size)
            assertEquals(1, result[0].id)
            assertEquals(2, result[1].id)
            assertEquals(3, result[2].id)
            assertEquals(4, result[3].id)
            assertEquals(5, result[4].id)
        }

    @Test
    fun deleteRangeOfEntities_whenRangeIsBiggerAndLessThenDBRange_shouldRemoveOnlyCorrectEntities() =
        runBlocking {
            // fulfill database
            val expectedSize = 6
            val userDataList = ArrayList<UserDataEntity>()
            for (index in 0 until expectedSize) {
                userDataList.add(createUserDataEntityWithId(index * 2))  // 0, 2, 4, 6, 8, 10
            }
            userDataDao.insertAll(userDataList)
            // check initial data
            assertEquals(expectedSize, userDataList.size)
            // remove id 10
            userDataDao.deleteRangeOfIds(-100, 100)
            // get result
            val result = userDataDao.getUserDataEntities(expectedSize)
            // check result
            assertTrue(result.isEmpty())
        }

    private fun createUserDataEntityWithId(id: Int): UserDataEntity {
        return UserDataEntity(
            id = id,
            login = "mojombo",
            node_id = "MDQ6VXNlcjE=",
            avatar_url = "https://avatars.githubusercontent.com/u/1?v=4",
            gravatar_id = "",
            url = "https://api.github.com/users/mojombo",
            html_url = "https://github.com/mojombo",
            followers_url = "https://api.github.com/users/mojombo/followers",
            following_url = "https://api.github.com/users/mojombo/following{/other_user}",
            gists_url = "https://api.github.com/users/mojombo/gists{/gist_id}",
            starred_url = "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
            subscriptions_url = "https://api.github.com/users/mojombo/subscriptions",
            organizations_url = "https://api.github.com/users/mojombo/orgs",
            repos_url = "https://api.github.com/users/mojombo/repos",
            events_url = "https://api.github.com/users/mojombo/events{/privacy}",
            received_events_url = "https://api.github.com/users/mojombo/received_events",
            type = "User",
            site_admin = false
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    inner class DataBaseModuleForTests {

        @Provides
        fun providesRepository(): Repository = mockedRepository

        @Provides
        @Singleton
        fun provideDBInstance(@ApplicationContext appContext: Context): UsersDB =
            Room.inMemoryDatabaseBuilder(appContext, UsersDB::class.java)
                .allowMainThreadQueries()
                .build()

        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider = object : DispatcherProvider {
            private val testDispatcher = StandardTestDispatcher()

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
