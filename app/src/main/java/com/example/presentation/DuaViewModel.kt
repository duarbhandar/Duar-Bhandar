package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.DuaPreferences
import com.example.domain.model.Category
import com.example.domain.model.Dua
import com.example.domain.repository.DuaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DuaViewModel(
    private val repository: DuaRepository,
    private val preferences: DuaPreferences
) : ViewModel() {

    private val _isLoggedInState = MutableStateFlow(preferences.isLoggedIn())
    val isLoggedInState: StateFlow<Boolean> = _isLoggedInState.asStateFlow()

    private val _isGuestState = MutableStateFlow(preferences.isGuest())
    val isGuestState: StateFlow<Boolean> = _isGuestState.asStateFlow()

    private val _loggedInEmailState = MutableStateFlow(preferences.getLoggedInEmail())
    val loggedInEmailState: StateFlow<String> = _loggedInEmailState.asStateFlow()

    fun loginUser(email: String) {
        preferences.setLoggedIn(loggedIn = true, email = email, isGuest = false)
        _isLoggedInState.value = true
        _isGuestState.value = false
        _loggedInEmailState.value = email
    }

    fun loginAsGuest() {
        preferences.setLoggedIn(loggedIn = false, email = "", isGuest = true)
        _isLoggedInState.value = false
        _isGuestState.value = true
        _loggedInEmailState.value = ""
    }

    fun logoutUser() {
        preferences.clearLoginState()
        _isLoggedInState.value = false
        _isGuestState.value = false
        _loggedInEmailState.value = ""
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    // Dynamically update favorites and bookmark statuses inside a single mapped flow
    private val mappedDuasFlow = combine(
        repository.getAllDuas(),
        preferences.favoritedIds,
        preferences.bookmarkedIds
    ) { duas, favIds, bookmarkIds ->
        duas.map { dua ->
            dua.copy(
                isFavorite = favIds.contains(dua.id),
                isBookmarked = bookmarkIds.contains(dua.id)
            )
        }
    }

    // Dynamically calculate category counts from the live list of Duas
    val categories: StateFlow<List<Category>> = mappedDuasFlow
        .map { duas ->
            val baseCategories = listOf(
                Category(1, "সকাল-সন্ধ্যা", "wb_twilight", 0),
                Category(2, "নামাজ ও ইবাদত", "spa", 0), // SPA represents spiritual/meditation
                Category(3, "বিপদ ও রোগমুক্তি", "health_and_safety", 0),
                Category(4, "দৈনন্দিন জীবন", "home", 0),
                Category(5, "ক্ষমা ও তওবা", "volunteer_activism", 0),
                Category(6, "সফর ও ভ্রমণ", "flight", 0)
            )
            baseCategories.map { cat ->
                cat.copy(count = duas.count { it.categoryId == cat.id })
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val featuredDuas: StateFlow<List<Dua>> = mappedDuasFlow
        .map { duas -> duas.filter { it.isFeatured } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bookmarkedDuas: StateFlow<List<Dua>> = mappedDuasFlow
        .map { duas -> duas.filter { it.isBookmarked } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoritedDuas: StateFlow<List<Dua>> = mappedDuasFlow
        .map { duas -> duas.filter { it.isFavorite } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredDuas: StateFlow<List<Dua>> = combine(
        _selectedCategoryId,
        _searchQuery,
        mappedDuasFlow
    ) { categoryId, query, mappedDuas ->
        if (query.isNotEmpty()) {
            mappedDuas.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.pronunciation.contains(query, ignoreCase = true) ||
                it.translation.contains(query, ignoreCase = true)
            }
        } else if (categoryId != null) {
            mappedDuas.filter { it.categoryId == categoryId }
        } else {
            mappedDuas
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun toggleBookmark(duaId: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            preferences.toggleBookmark(duaId)
            repository.updateBookmark(duaId, preferences.isBookmarked(duaId))
        }
    }

    fun toggleFavorite(duaId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            preferences.toggleFavorite(duaId)
        }
    }

    fun updateNotes(duaId: Int, notes: String?) {
        viewModelScope.launch {
            repository.updateNotes(duaId, notes)
        }
    }

    fun insertDua(
        title: String,
        arabic: String,
        pronunciation: String,
        translation: String,
        reference: String,
        categoryId: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val newDua = Dua(
                id = 0,
                title = title,
                arabic = arabic,
                pronunciation = pronunciation,
                translation = translation,
                reference = reference,
                categoryId = categoryId,
                isBookmarked = false,
                isFeatured = false,
                notes = null,
                isFavorite = false
            )
            repository.insertDua(newDua)
            onSuccess()
        }
    }
}

class DuaViewModelFactory(
    private val repository: DuaRepository,
    private val preferences: DuaPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DuaViewModel::class.java)) {
            return DuaViewModel(repository, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
