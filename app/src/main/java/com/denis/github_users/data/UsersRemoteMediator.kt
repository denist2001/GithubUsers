package com.denis.github_users.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.denis.github_users.data.db.UsersDB
import com.denis.github_users.data.model.UserDataEntity
import com.denis.github_users.data.repository.LoadingConfig.ITEMS_PER_PAGE
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UsersRemoteMediator @Inject constructor(
    private val networkService: RepositoryService,
    private val usersDB: UsersDB,
) : RemoteMediator<Int, UserDataEntity>() {
    private val userDataDao = usersDB.userDataDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserDataEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    lastItem.id
                }
            }
            val repositoryData =
                // TODO: probably, it's better to define network codes to show more precise errors to the user
                networkService.loadUsers(
                    since = loadKey,
                    per_page = ITEMS_PER_PAGE
                )
            usersDB.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    userDataDao.deleteRangeOfIds(loadKey, repositoryData.last().id)
                }
                userDataDao.insertAll(repositoryData)
            }

            MediatorResult.Success(
                endOfPaginationReached = repositoryData.size < ITEMS_PER_PAGE
            )
        } catch (exception: JsonSyntaxException) {
            MediatorResult.Error(Throwable("Result can not be parsed"))
        } catch (exception: IOException) {
            MediatorResult.Error(Throwable("IO error"))
        } catch (exception: HttpException) {
            MediatorResult.Error(Throwable("Network error"))
        } catch (exception: IllegalStateException) {
            MediatorResult.Error(Throwable("No more elements"))
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}
