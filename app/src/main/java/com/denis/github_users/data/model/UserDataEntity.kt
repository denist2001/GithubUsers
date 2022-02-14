package com.denis.github_users.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *   {
 *      "login": "mojombo",
 *      "id": 1,
 *      "node_id": "MDQ6VXNlcjE=",
 *      "avatar_url": "https://avatars.githubusercontent.com/u/1?v=4",
 *      "gravatar_id": "",
 *      "url": "https://api.github.com/users/mojombo",
 *      "html_url": "https://github.com/mojombo",
 *      "followers_url": "https://api.github.com/users/mojombo/followers",
 *      "following_url": "https://api.github.com/users/mojombo/following{/other_user}",
 *      "gists_url": "https://api.github.com/users/mojombo/gists{/gist_id}",
 *      "starred_url": "https://api.github.com/users/mojombo/starred{/owner}{/repo}",
 *      "subscriptions_url": "https://api.github.com/users/mojombo/subscriptions",
 *      "organizations_url": "https://api.github.com/users/mojombo/orgs",
 *      "repos_url": "https://api.github.com/users/mojombo/repos",
 *      "events_url": "https://api.github.com/users/mojombo/events{/privacy}",
 *      "received_events_url": "https://api.github.com/users/mojombo/received_events",
 *      "type": "User",
 *      "site_admin": false
 *   }
 */
@Entity(tableName = "user_data")
data class UserDataEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "login")
    @NonNull
    val login: String,

    @ColumnInfo(name = "node_id")
    @NonNull
    val node_id: String,

    @ColumnInfo(name = "avatar_url")
    @NonNull
    val avatar_url: String,

    @ColumnInfo(name = "gravatar_id")
    @NonNull
    val gravatar_id: String,

    @ColumnInfo(name = "url")
    @NonNull
    val url: String,

    @ColumnInfo(name = "html_url")
    @NonNull
    val html_url: String,

    @ColumnInfo(name = "followers_url")
    @NonNull
    val followers_url: String,

    @ColumnInfo(name = "following_url")
    @NonNull
    val following_url: String,

    @ColumnInfo(name = "gists_url")
    @NonNull
    val gists_url: String,

    @ColumnInfo(name = "starred_url")
    @NonNull
    val starred_url: String,

    @ColumnInfo(name = "subscriptions_url")
    @NonNull
    val subscriptions_url: String,

    @ColumnInfo(name = "organizations_url")
    @NonNull
    val organizations_url: String,

    @ColumnInfo(name = "repos_url")
    @NonNull
    val repos_url: String,

    @ColumnInfo(name = "events_url")
    @NonNull
    val events_url: String,

    @ColumnInfo(name = "received_events_url")
    @NonNull
    val received_events_url: String,

    @ColumnInfo(name = "type")
    @NonNull
    val type: String,

    @ColumnInfo(name = "site_admin")
    @NonNull
    val site_admin: Boolean,
)
