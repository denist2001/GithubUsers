package com.denis.github_users.domain

import com.denis.github_users.data.model.UserDataDetails
import com.denis.github_users.data.model.UserDataEntity
import com.denis.github_users.testUtils.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ConverterTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private val subject = Converter(coroutinesTestRule.testDispatcherProvider)

    @Test
    fun `when id is negative should create presentation object`() = runBlocking {
        val sourceValue = createUserDataEntityWithId(
            id = -100,
            url = "",
        )
        val result = subject.convertFromEntityToPresentation(sourceValue)
        assertEquals(-100, result.id)
    }

    @Test
    fun `when id is minimal int value should create presentation object`() = runBlocking {
        val sourceValue = createUserDataEntityWithId(
            id = Int.MIN_VALUE,
            url = "",
        )
        val result = subject.convertFromEntityToPresentation(sourceValue)
        assertEquals(Int.MIN_VALUE, result.id)
    }

    @Test
    fun `when id is positive int value should create presentation object`() = runBlocking {
        val sourceValue = createUserDataEntityWithId(
            id = 1000,
            url = "",
        )
        val result = subject.convertFromEntityToPresentation(sourceValue)
        assertEquals(1000, result.id)
    }

    @Test
    fun `when id is maximal int value should create presentation object`() = runBlocking {
        val sourceValue = createUserDataEntityWithId(
            id = Int.MAX_VALUE,
            url = "",
        )
        val result = subject.convertFromEntityToPresentation(sourceValue)
        assertEquals(Int.MAX_VALUE, result.id)
    }

    @Test
    fun `when title contains special symbols should create presentation object`() = runBlocking {
        val sourceValue = createUserDataEntityWithId(
            id = 1,
            login = "±!@#$%ˆ&*()_+}{|:?><˜\"",
        )
        val result = subject.convertFromEntityToPresentation(sourceValue)
        assertEquals("±!@#$%ˆ&*()_+}{|:?><˜\"", result.login)
    }

    @Test
    fun `when avatar_url contains special symbols should create presentation object`() =
        runBlocking {
            val sourceValue = createUserDataEntityWithId(
                id = 1,
                avatar_url = "±!@#$%ˆ&*()_+}{|:?><˜\"",
            )
            val result = subject.convertFromEntityToPresentation(sourceValue)
            assertEquals("±!@#$%ˆ&*()_+}{|:?><˜\"", result.avatar_url)
        }

    @Test
    fun `from entity when id is negative should create presentation object`() = runBlocking {
        val sourceValue = createUserDataDetails(
            id = -100,
        )
        val result = subject.convertRepositoryUserDetailsToPresentation(sourceValue)
        assertNotNull(result)
    }

    @Test
    fun `from entity when id is minimal int value should create presentation object`() =
        runBlocking {
            val sourceValue = createUserDataDetails(
                id = Int.MIN_VALUE,
            )
            val result = subject.convertRepositoryUserDetailsToPresentation(sourceValue)
            assertNotNull(result)
        }

    @Test
    fun `from entity when id is positive int value should create presentation object`() =
        runBlocking {
            val sourceValue = createUserDataDetails(
                id = 1000,
                url = "",
            )
            val result = subject.convertRepositoryUserDetailsToPresentation(sourceValue)
            assertNotNull(result)
        }

    @Test
    fun `from entity when id is maximal int value should create presentation object`() =
        runBlocking {
            val sourceValue = createUserDataDetails(
                id = Int.MAX_VALUE,
            )
            val result = subject.convertRepositoryUserDetailsToPresentation(sourceValue)
            assertNotNull(result)
        }

    @Test
    fun `from entity when login contains special symbols should create presentation object`() =
        runBlocking {
            val sourceValue = createUserDataDetails(
                id = 1,
                login = "±!@#$%ˆ&*()_+}{|:?><˜\"",
            )
            val result = subject.convertRepositoryUserDetailsToPresentation(sourceValue)
            assertEquals("±!@#$%ˆ&*()_+}{|:?><˜\"", result.login)
        }

    @Test
    fun `from entity should cut created date`() =
        runBlocking {
            val sourceValue = createUserDataDetails(
                id = 1,
            )
            val result = subject.convertRepositoryUserDetailsToPresentation(sourceValue)
            assertEquals("2008-01-14", result.created_at)
        }

    @Test
    fun `from entity should cut updated date`() =
        runBlocking {
            val sourceValue = createUserDataDetails(
                id = 1,
            )
            val result = subject.convertRepositoryUserDetailsToPresentation(sourceValue)
            assertEquals("2008-01-14", result.updated_at)
        }

    private fun createUserDataDetails(
        id: Int,
        login: String = "mojombo",
        node_id: String = "MDQ6VXNlcjE=",
        avatar_url: String = "https://avatars.githubusercontent.com/u/1?v=4",
        gravatar_id: String = "",
        url: String = "https://api.github.com/users/mojombo",
        html_url: String = "https://github.com/mojombo",
        followers_url: String = "https://api.github.com/users/mojombo/followers",
        following_url: String = "https://api.github.com/users/mojombo/following{/other_user}",
        gists_url: String = "https://api.github.com/users/mojombo/gists{/gist_id}",
        starred_url: String = "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
        subscriptions_url: String = "https://api.github.com/users/mojombo/subscriptions",
        organizations_url: String = "https://api.github.com/users/mojombo/orgs",
        repos_url: String = "https://api.github.com/users/mojombo/repos",
        events_url: String = "https://api.github.com/users/mojombo/events{/privacy}",
        received_events_url: String = "https://api.github.com/users/mojombo/received_events",
        type: String = "User",
        site_admin: Boolean = false,
        name: String = "monalisa octocat",
        company: String = "GitHub",
        blog: String = "https://github.com/blog",
        location: String = "San Francisco",
        email: String = "octocat@github.com",
        hireable: Boolean = false,
        bio: String = "There once was...",
        twitter_username: String = "monatheoctocat",
        public_repos: Int = 2,
        public_gists: Int = 1,
        followers: Int = 20,
        following: Int = 0,
        created_at: String = "2008-01-14T04:33:35Z",
        updated_at: String = "2008-01-14T04:33:35Z"
    ): UserDataDetails {
        return UserDataDetails(
            id = id,
            login = login,
            node_id = node_id,
            avatar_url = avatar_url,
            gravatar_id = gravatar_id,
            url = url,
            html_url = html_url,
            followers_url = followers_url,
            following_url = following_url,
            gists_url = gists_url,
            starred_url = starred_url,
            subscriptions_url = subscriptions_url,
            organizations_url = organizations_url,
            repos_url = repos_url,
            events_url = events_url,
            received_events_url = received_events_url,
            type = type,
            site_admin = site_admin,
            name = "monalisa octocat",
            company = "GitHub",
            blog = "https://github.com/blog",
            location = "San Francisco",
            email = "octocat@github.com",
            hireable = false,
            bio = "There once was...",
            twitter_username = "monatheoctocat",
            public_repos = 2,
            public_gists = 1,
            followers = 20,
            following = 0,
            created_at = "2008-01-14T04:33:35Z",
            updated_at = "2008-01-14T04:33:35Z"
        )
    }

    private fun createUserDataEntityWithId(
        id: Int,
        login: String = "mojombo",
        node_id: String = "MDQ6VXNlcjE=",
        avatar_url: String = "https://avatars.githubusercontent.com/u/1?v=4",
        gravatar_id: String = "",
        url: String = "https://api.github.com/users/mojombo",
        html_url: String = "https://github.com/mojombo",
        followers_url: String = "https://api.github.com/users/mojombo/followers",
        following_url: String = "https://api.github.com/users/mojombo/following{/other_user}",
        gists_url: String = "https://api.github.com/users/mojombo/gists{/gist_id}",
        starred_url: String = "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
        subscriptions_url: String = "https://api.github.com/users/mojombo/subscriptions",
        organizations_url: String = "https://api.github.com/users/mojombo/orgs",
        repos_url: String = "https://api.github.com/users/mojombo/repos",
        events_url: String = "https://api.github.com/users/mojombo/events{/privacy}",
        received_events_url: String = "https://api.github.com/users/mojombo/received_events",
        type: String = "User",
        site_admin: Boolean = false
    ): UserDataEntity {
        return UserDataEntity(
            id = id,
            login = login,
            node_id = node_id,
            avatar_url = avatar_url,
            gravatar_id = gravatar_id,
            url = url,
            html_url = html_url,
            followers_url = followers_url,
            following_url = following_url,
            gists_url = gists_url,
            starred_url = starred_url,
            subscriptions_url = subscriptions_url,
            organizations_url = organizations_url,
            repos_url = repos_url,
            events_url = events_url,
            received_events_url = received_events_url,
            type = type,
            site_admin = site_admin
        )
    }
}