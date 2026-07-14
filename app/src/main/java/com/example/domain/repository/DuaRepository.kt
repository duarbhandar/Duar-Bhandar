package com.example.domain.repository

import com.example.domain.model.Dua
import kotlinx.coroutines.flow.Flow

interface DuaRepository {
    fun getAllDuas(): Flow<List<Dua>>
    fun getDuasByCategory(categoryId: Int): Flow<List<Dua>>
    fun getFeaturedDuas(): Flow<List<Dua>>
    fun getBookmarkedDuas(): Flow<List<Dua>>
    suspend fun updateBookmark(duaId: Int, isBookmarked: Boolean)
    suspend fun updateNotes(duaId: Int, notes: String?)
    fun searchDuas(query: String): Flow<List<Dua>>
    suspend fun insertDua(dua: Dua)
}
