package com.denis.github_users.di

import android.content.Context
import com.denis.github_users.data.db.UsersDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DBModule {
    @Provides
    @Singleton
    fun provideDBInstance(@ApplicationContext appContext: Context) = UsersDB.getDatabase(appContext)
}