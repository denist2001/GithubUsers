package com.denis.github_users.presentation.itemsList.model

/**
 *   {
 *      "login": "mojombo",
 *      "id": 1,
 *      "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4",
 *   }
 */
data class PresentationUserData(
    val login: String,
    val id: Int,
    val avatar_url: String,
)
