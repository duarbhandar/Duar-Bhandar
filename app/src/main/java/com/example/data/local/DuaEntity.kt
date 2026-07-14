package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Dua

@Entity(tableName = "duas")
data class DuaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val arabic: String,
    val pronunciation: String,
    val translation: String,
    val reference: String,
    val categoryId: Int,
    val isBookmarked: Boolean = false,
    val isFeatured: Boolean = false,
    val notes: String? = null
) {
    fun toDomain(): Dua = Dua(
        id = id,
        title = title,
        arabic = arabic,
        pronunciation = pronunciation,
        translation = translation,
        reference = reference,
        categoryId = categoryId,
        isBookmarked = isBookmarked,
        isFeatured = isFeatured,
        notes = notes
    )

    companion object {
        fun fromDomain(dua: Dua): DuaEntity = DuaEntity(
            id = dua.id,
            title = dua.title,
            arabic = dua.arabic,
            pronunciation = dua.pronunciation,
            translation = dua.translation,
            reference = dua.reference,
            categoryId = dua.categoryId,
            isBookmarked = dua.isBookmarked,
            isFeatured = dua.isFeatured,
            notes = dua.notes
        )
    }
}
