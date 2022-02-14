package com.denis.github_users.data

import com.denis.github_users.data.model.UserDataDetails
import com.denis.github_users.data.model.UserDataEntity
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RepositoryService {
    @GET("/users")
    suspend fun loadUsers(
        @Header("Accept") header: String = "application/vnd.github.v3+json", // Setting to application/vnd.github.v3+json is recommended.
        @Query("since") since: Int,                                          // A user ID. Only return users with an ID greater than this ID.
        @Query("per_page") per_page: Int,                                    // Results per page (max 100) Default: 30
    ): List<UserDataEntity>

    @GET("/users/{username}")
    suspend fun loadUserDetails(
        @Header("Accept") header: String = "application/vnd.github.v3+json", // Setting to application/vnd.github.v3+json is recommended.
        @Path("username") username: String,
    ): UserDataDetails
}