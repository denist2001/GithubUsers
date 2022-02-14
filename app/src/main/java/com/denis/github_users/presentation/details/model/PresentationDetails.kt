package com.denis.github_users.presentation.details.model

data class PresentationDetails(
    val login: String,
    val avatar_url: String,
    val name: String?,
    val company: String?,
    val location: String?,
    val email: String?,
    val hireable: Boolean?,
    val bio: String?,
    val twitter_username: String?,
    val public_repos: Int?,
    val public_gists: Int?,
    val followers: Int?,
    val following: Int?,
    val created_at: String?,     //"2008-01-14T04:33:35Z"
    val updated_at: String?,     //"2008-01-14T04:33:35Z"
)
