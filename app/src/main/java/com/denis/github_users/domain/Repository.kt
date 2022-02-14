package com.denis.github_users.domain

import androidx.paging.PagingData
import com.denis.github_users.data.model.UserDataDetails
import com.denis.github_users.data.model.UserDataEntity
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun loadItem(number: Int?): Flow<PagingData<UserDataEntity>>

    suspend fun loadUserDetails(userLogin: String): UserDataDetails
}