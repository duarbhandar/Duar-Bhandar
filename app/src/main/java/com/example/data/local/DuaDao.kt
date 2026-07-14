package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaDao {
    @Query("SELECT * FROM duas ORDER BY id ASC")
    fun getAllDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE categoryId = :categoryId ORDER BY id ASC")
    fun getDuasByCategory(categoryId: Int): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE isFeatured = 1 ORDER BY id ASC")
    fun getFeaturedDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM duas WHERE isBookmarked = 1 ORDER BY id ASC")
    fun getBookmarkedDuas(): Flow<List<DuaEntity>>

    @Query("UPDATE duas SET isBookmarked = :isBookmarked WHERE id = :duaId")
    suspend fun updateBookmark(duaId: Int, isBookmarked: Boolean)

    @Query("UPDATE duas SET notes = :notes WHERE id = :duaId")
    suspend fun updateNotes(duaId: Int, notes: String?)

    @Query("SELECT * FROM duas WHERE title LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%' OR pronunciation LIKE '%' || :query || '%'")
    fun searchDuas(query: String): Flow<List<DuaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuas(duas: List<DuaEntity>)

    @Query("SELECT COUNT(*) FROM duas")
    suspend fun countDuas(): Int
}
