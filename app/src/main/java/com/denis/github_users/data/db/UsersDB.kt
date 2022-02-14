package com.denis.github_users.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.denis.github_users.data.db.dao.UserDataDao
import com.denis.github_users.data.model.UserDataEntity

@Database(entities = [UserDataEntity::class], version = 1, exportSchema = false)
abstract class UsersDB : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile
        private var instance: UsersDB? = null

        fun getDatabase(context: Context): UsersDB =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, UsersDB::class.java, "githubusers")
                .fallbackToDestructiveMigration()
                .build()
    }
}
