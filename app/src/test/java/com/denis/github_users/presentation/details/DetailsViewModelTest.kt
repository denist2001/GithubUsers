package com.denis.github_users.presentation.details

import android.content.Context
import app.cash.turbine.test
import com.denis.github_users.R
import com.denis.github_users.domain.Converter
import com.denis.github_users.domain.Repository
import com.denis.github_users.testUtils.CoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*

class DetailsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    val mockedRepository: Repository = mockk()
    val mockedConverter: Converter = mockk()
    val mockedContext: Context = mockk()

    val wrongUserName = "wrong user name"

    private val stateFlows = mutableListOf<LoadingState>()

    val subject = DetailsViewModel(mockedRepository, mockedConverter, mockedContext)

    @Before
    fun setUp() {
        every { mockedContext.getString(R.string.wrong_user_name) } returns wrongUserName
    }

    @Ignore("Additional investigation needed. I have to somehow close flow.")
    @Test
    fun `startLoading when userLogin is null should return error`() {
        runBlocking {
            subject.startLoading(null)
            subject.stateFlow.test {
                // check if only one error appears
                val item = expectMostRecentItem()
                assertEquals(LoadingState.Error("Wrong user name"), item)
                awaitComplete()
            }
            verify(atLeast = 1) { mockedContext.getString(R.string.wrong_user_name) }
        }
    }

    @After
    fun finishTest() {
        stateFlows.clear()
    }
}