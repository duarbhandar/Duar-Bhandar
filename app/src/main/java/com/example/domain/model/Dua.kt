package com.example.domain.model

data class Dua(
    val id: Int = 0,
    val title: String,
    val arabic: String,
    val pronunciation: String,
    val translation: String,
    val reference: String,
    val categoryId: Int,
    val isBookmarked: Boolean = false,
    val isFeatured: Boolean = false,
    val notes: String? = null,
    val isFavorite: Boolean = false
)
