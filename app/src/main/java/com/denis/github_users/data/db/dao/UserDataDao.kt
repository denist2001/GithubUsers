package com.denis.github_users.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.denis.github_users.data.model.UserDataEntity

@Dao
abstract class UserDataDao {
    @Query("SELECT * FROM user_data ORDER BY id LIMIT :size")
    abstract suspend fun getUserDataEntities(size: Int): List<UserDataEntity>

    @Query("DELETE FROM user_data")
    abstract suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(list: List<UserDataEntity>)

    @Query("SELECT * FROM user_data WHERE id >= :id")
    abstract fun pagingSource(id: Int): PagingSource<Int, UserDataEntity>

    @Query("DELETE FROM user_data WHERE id >= :from AND id <= :to")
    abstract fun deleteRangeOfIds(from: Int, to: Int)
}