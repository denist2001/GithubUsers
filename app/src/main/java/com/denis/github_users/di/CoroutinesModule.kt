package com.denis.github_users.di

import com.denis.github_users.DefaultDispatcherProvider
import com.denis.github_users.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CoroutinesModule {
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider
}