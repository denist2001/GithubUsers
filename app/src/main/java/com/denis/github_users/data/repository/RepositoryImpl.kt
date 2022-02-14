package com.denis.github_users.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.denis.github_users.data.RepositoryService
import com.denis.github_users.data.UsersRemoteMediator
import com.denis.github_users.data.db.UsersDB
import com.denis.github_users.data.model.UserDataDetails
import com.denis.github_users.data.model.UserDataEntity
import com.denis.github_users.data.repository.LoadingConfig.ITEMS_PER_PAGE
import com.denis.github_users.domain.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val usersDB: UsersDB,
    private val usersRemoteMediator: UsersRemoteMediator,
    private val repositoryService: RepositoryService,
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun loadItem(number: Int?): Flow<PagingData<UserDataEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = true,
            ),
            remoteMediator = usersRemoteMediator
        ) {
            usersDB.userDataDao().pagingSource(1)
        }.flow
    }

    override suspend fun loadUserDetails(userLogin: String): UserDataDetails {
        return repositoryService.loadUserDetails(username = userLogin)
    }
}