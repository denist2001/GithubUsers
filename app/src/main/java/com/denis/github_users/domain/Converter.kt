package com.denis.github_users.domain

import com.denis.github_users.DispatcherProvider
import com.denis.github_users.data.model.UserDataDetails
import com.denis.github_users.data.model.UserDataEntity
import com.denis.github_users.presentation.details.model.PresentationDetails
import com.denis.github_users.presentation.itemsList.model.PresentationUserData
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Converter @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend fun convertFromEntityToPresentation(userDataEntity: UserDataEntity): PresentationUserData {
        return withContext(dispatcherProvider.io()) {
            return@withContext PresentationUserData(
                login = userDataEntity.login,
                id = userDataEntity.id,
                avatar_url = userDataEntity.avatar_url,
            )
        }
    }

    suspend fun convertRepositoryUserDetailsToPresentation(loadUserDetails: UserDataDetails): PresentationDetails {
        return withContext(dispatcherProvider.io()) {
            PresentationDetails(
                login = loadUserDetails.login,
                avatar_url = loadUserDetails.avatar_url,
                name = loadUserDetails.name,
                company = loadUserDetails.company,
                location = loadUserDetails.location,
                email = loadUserDetails.email,
                hireable = loadUserDetails.hireable,
                bio = loadUserDetails.bio,
                twitter_username = loadUserDetails.twitter_username,
                public_repos = loadUserDetails.public_repos,
                public_gists = loadUserDetails.public_gists,
                followers = loadUserDetails.followers,
                following = loadUserDetails.following,
                created_at = loadUserDetails.created_at.substring(
                    0,
                    10
                ),     //"2008-01-14T04:33:35Z"
                updated_at = loadUserDetails.updated_at.substring(
                    0,
                    10
                ),     //"2008-01-14T04:33:35Z"
            )
        }
    }
}