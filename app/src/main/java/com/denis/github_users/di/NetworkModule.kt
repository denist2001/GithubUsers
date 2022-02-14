package com.denis.github_users.di

import com.denis.github_users.data.RepositoryService
import com.denis.github_users.data.repository.RepositoryImpl
import com.denis.github_users.domain.Repository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun getUrl(): String = "https://api.github.com"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, url: String): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideRepositoryService(retrofit: Retrofit): RepositoryService =
        retrofit.create(RepositoryService::class.java)

    @Provides
    @Singleton
    fun provideRepository(repository: RepositoryImpl): Repository = repository
}