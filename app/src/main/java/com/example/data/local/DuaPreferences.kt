package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DuaPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("dua_preferences", Context.MODE_PRIVATE)

    private val _favoritedIds = MutableStateFlow<Set<Int>>(getFavoritedIdsFromPrefs())
    val favoritedIds: StateFlow<Set<Int>> = _favoritedIds.asStateFlow()

    private val _bookmarkedIds = MutableStateFlow<Set<Int>>(getBookmarkedIdsFromPrefs())
    val bookmarkedIds: StateFlow<Set<Int>> = _bookmarkedIds.asStateFlow()

    private fun getFavoritedIdsFromPrefs(): Set<Int> {
        val stringSet = sharedPreferences.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    private fun getBookmarkedIdsFromPrefs(): Set<Int> {
        val stringSet = sharedPreferences.getStringSet(KEY_BOOKMARKS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun isFavorite(duaId: Int): Boolean {
        return _favoritedIds.value.contains(duaId)
    }

    fun isBookmarked(duaId: Int): Boolean {
        return _bookmarkedIds.value.contains(duaId)
    }

    fun toggleFavorite(duaId: Int) {
        val current = _favoritedIds.value.toMutableSet()
        if (current.contains(duaId)) {
            current.remove(duaId)
        } else {
            current.add(duaId)
        }
        _favoritedIds.value = current
        sharedPreferences.edit()
            .putStringSet(KEY_FAVORITES, current.map { it.toString() }.toSet())
            .apply()
    }

    fun toggleBookmark(duaId: Int) {
        val current = _bookmarkedIds.value.toMutableSet()
        if (current.contains(duaId)) {
            current.remove(duaId)
        } else {
            current.add(duaId)
        }
        _bookmarkedIds.value = current
        sharedPreferences.edit()
            .putStringSet(KEY_BOOKMARKS, current.map { it.toString() }.toSet())
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isGuest(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }

    fun getLoggedInEmail(): String {
        return sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, "") ?: ""
    }

    fun setLoggedIn(loggedIn: Boolean, email: String = "", isGuest: Boolean = false) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
            .putString(KEY_LOGGED_IN_EMAIL, email)
            .putBoolean(KEY_IS_GUEST, isGuest)
            .apply()
    }

    fun clearLoginState() {
        sharedPreferences.edit()
            .remove(KEY_IS_LOGGED_IN)
            .remove(KEY_LOGGED_IN_EMAIL)
            .remove(KEY_IS_GUEST)
            .apply()
    }

    companion object {
        private const val KEY_FAVORITES = "key_favorites"
        private const val KEY_BOOKMARKS = "key_bookmarks"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_LOGGED_IN_EMAIL = "key_logged_in_email"
        private const val KEY_IS_GUEST = "key_is_guest"
    }
}
