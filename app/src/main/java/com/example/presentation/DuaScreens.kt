package com.example.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.NoteAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.domain.model.Category
import com.example.domain.model.Dua
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaApp(viewModel: DuaViewModel) {
    var currentTab by remember { mutableIntStateOf(0) }
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    var isViewingAboutUs by remember { mutableStateOf(false) }
    var isViewingAdminPanel by remember { mutableStateOf(false) }

    val titleText = when {
        selectedCategory != null -> {
            val categories by viewModel.categories.collectAsStateWithLifecycle()
            categories.find { it.id == selectedCategory }?.name ?: "দোয়া ভান্ডার"
        }
        currentTab == 0 -> "দোয়া ভান্ডার"
        currentTab == 1 -> "বুকমার্কস"
        currentTab == 2 -> "পছন্দের দোয়া"
        currentTab == 3 -> "অনুসন্ধান"
        currentTab == 4 -> "সেটিংস ও প্রোফাইল"
        else -> "দোয়া ভান্ডার"
    }

    val isLoggedIn by viewModel.isLoggedInState.collectAsStateWithLifecycle()
    val isGuest by viewModel.isGuestState.collectAsStateWithLifecycle()
    val userEmail by viewModel.loggedInEmailState.collectAsStateWithLifecycle()

    if (!isLoggedIn && !isGuest) {
        AuthScreen(viewModel = viewModel)
    } else {
        if (isViewingAboutUs) {
            AboutUsScreen(onBackClick = { isViewingAboutUs = false })
        } else if (isViewingAdminPanel) {
            AdminPanelScreen(
                viewModel = viewModel,
                onBackClick = { isViewingAdminPanel = false }
            )
        } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.testTag("top_bar_title")
                        )
                    },
                    navigationIcon = {
                        if (selectedCategory != null) {
                            IconButton(
                                onClick = { viewModel.selectCategory(null) },
                                modifier = Modifier.testTag("back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = "Back to categories"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0 && selectedCategory == null,
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = 0
                        },
                        icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                        label = { Text("হোম") },
                        modifier = Modifier.testTag("nav_home_tab")
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = 1
                        },
                        icon = { Icon(Icons.Rounded.Bookmark, contentDescription = "Bookmarks") },
                        label = { Text("বুকমার্ক") },
                        modifier = Modifier.testTag("nav_bookmark_tab")
                    )
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = 2
                        },
                        icon = { Icon(Icons.Rounded.Favorite, contentDescription = "Favorites") },
                        label = { Text("পছন্দের") },
                        modifier = Modifier.testTag("nav_favorites_tab")
                    )
                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = 3
                        },
                        icon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                        label = { Text("খুঁজুন") },
                        modifier = Modifier.testTag("nav_search_tab")
                    )
                    NavigationBarItem(
                        selected = currentTab == 4,
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = 4
                        },
                        icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                        label = { Text("সেটিংস") },
                        modifier = Modifier.testTag("nav_settings_tab")
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    selectedCategory != null -> {
                        CategoryDetailScreen(
                            viewModel = viewModel,
                            categoryId = selectedCategory!!
                        )
                    }
                    currentTab == 0 -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onCategorySelect = { catId -> viewModel.selectCategory(catId) }
                        )
                    }
                    currentTab == 1 -> {
                        BookmarksScreen(viewModel = viewModel)
                    }
                    currentTab == 2 -> {
                        FavoritesScreen(viewModel = viewModel)
                    }
                    currentTab == 3 -> {
                        SearchScreen(viewModel = viewModel)
                    }
                    currentTab == 4 -> {
                        SettingsProfileScreen(
                            onAboutUsClick = { isViewingAboutUs = true },
                            onAddNewDuaClick = { isViewingAdminPanel = true },
                            onLogoutClick = { viewModel.logoutUser() },
                            userEmail = userEmail,
                            isGuestUser = isGuest
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun HomeScreen(
    viewModel: DuaViewModel,
    onCategorySelect: (Int) -> Unit
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val featuredDuas by viewModel.featuredDuas.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Banner Section
        item {
            DynamicBanner()
        }

        // Verse of the Day Section
        item {
            VerseOfTheDayCard()
        }

        // Categories Title
        item {
            Text(
                text = "দোয়া বিভাগ সমূহ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFDFB15B) else Color(0xFF0F3D32)
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Category Grid
        item {
            CategoryGridSection(
                categories = categories,
                onCategorySelect = onCategorySelect
            )
        }

        // Featured Duas Title
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFFDFB15B) else Color(0xFFC59B27),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "গুরুত্বपूर्ण বা ফিচার্ড দোয়া",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFDFB15B) else Color(0xFF0F3D32)
                    )
                )
            }
        }

        // Featured Duas list
        if (featuredDuas.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("কোনো ফিচার্ড দোয়া পাওয়া যায়নি।", color = Color.Gray)
                }
            }
        } else {
            items(featuredDuas, key = { it.id }) { dua ->
                DuaCard(
                    dua = dua,
                    onBookmarkToggle = { viewModel.toggleBookmark(dua.id, !dua.isBookmarked) },
                    onFavoriteToggle = { viewModel.toggleFavorite(dua.id, !dua.isFavorite) },
                    onNotesUpdate = { notes -> viewModel.updateNotes(dua.id, notes) }
                )
            }
        }
    }
}

@Composable
fun VerseOfTheDayCard() {
    val verses = listOf(
        "“তোমরা আমাকে ডাকো, আমি তোমাদের ডাকে সাড়া দেবো।”\n— সূরা গাফির: ৬০",
        "“যখন আমার বান্দারা আমার সম্পর্কে জিজ্ঞেস করে, আমি তো কাছেই আছি।”\n— সূরা আল-বাকারাহ: ১৮৬",
        "“হে ঈমানدارগণ! তোমরা আল্লাহর কাছে খাঁটি তওবা করো।”\n— সূরা আত-তাহরীম: ৮",
        "“নিশ্চয়ই কষ্টের সাথেই স্বস্তি রয়েছে।”\n— সূরা আল-ইনশিরাহ: ৫",
        "“আমার বান্দাদের জানিয়ে দাও যে, আমি অবশ্যই পরম ক্ষমাশীল, পরম দয়ালু।”\n— সূরা আল-হিজর: ৪৯"
    )

    var currentVerseIndex by remember { mutableIntStateOf(0) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)
            visible = false
            delay(400)
            currentVerseIndex = (currentVerseIndex + 1) % verses.size
            visible = true
            delay(1600)
        }
    }

    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0D251F) else Color(0xFFF1F6F4)
    val goldColor = if (isDark) Color(0xFFDFB15B) else Color(0xFFC59B27)
    val textColor = if (isDark) Color(0xFFE6EFEA) else Color(0xFF0A2D25)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("verse_of_the_day_card"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, goldColor.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Spa,
                    contentDescription = null,
                    tint = goldColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "আজকের আয়াত ও বাণী",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = goldColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = verses[currentVerseIndex],
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 22.sp
                    ),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun DynamicBanner() {
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .testTag("dynamic_banner"),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, if (isDark) Color(0xFFDFB15B) else Color(0xFFC59B27)), // Gold border matching the logo
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0A241E) else Color(0xFFFAF7F0)), // Emerald green backing in dark mode, cream beige in light mode
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner_1783964823677),
                contentDescription = "Dua Bhandar Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

data class CategoryThemeColors(
    val containerColor: Color,
    val iconBgColor: Color,
    val iconColor: Color,
    val textColor: Color,
    val countColor: Color,
    val borderColor: Color
)

@Composable
fun getCategoryThemeColors(categoryId: Int): CategoryThemeColors {
    val isDark = isSystemInDarkTheme()
    return if (isDark) {
        CategoryThemeColors(
            containerColor = Color(0xFF0D231E), // Beautiful deep desaturated emerald green
            iconBgColor = Color(0xFFDFB15B).copy(alpha = 0.15f), // Gold tinted highlight
            iconColor = Color(0xFFDFB15B), // Gold
            textColor = Color(0xFFE6EFEA), // Soft cream-emerald text
            countColor = Color(0xFFDFB15B), // Gold
            borderColor = Color(0xFFDFB15B).copy(alpha = 0.7f) // Gold border
        )
    } else {
        CategoryThemeColors(
            containerColor = Color(0xFFF1F6F4), // Light desaturated emerald green
            iconBgColor = Color(0xFF0F3D32).copy(alpha = 0.1f), // Deep emerald green background tint
            iconColor = Color(0xFF0F3D32), // Deep emerald green
            textColor = Color(0xFF0F3D32), // Deep emerald green text
            countColor = Color(0xFFC59B27), // Gold count text
            borderColor = Color(0xFFC59B27).copy(alpha = 0.6f) // Gold border
        )
    }
}

@Composable
fun CategoryGridSection(
    categories: List<Category>,
    onCategorySelect: (Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val width = maxWidth
        val columns = when {
            width > 800.dp -> 4
            width > 600.dp -> 3
            else -> 2
        }
        val rows = (categories.size + columns - 1) / columns
        val rowHeight = 125.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight * rows),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (i in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (j in 0 until columns) {
                        val index = i * columns + j
                        if (index < categories.size) {
                            val category = categories[index]
                            Box(modifier = Modifier.weight(1f)) {
                                CategoryGridItem(
                                    category = category,
                                    onClick = { onCategorySelect(category.id) }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryGridItem(
    category: Category,
    onClick: () -> Unit
) {
    val icon = when (category.iconName) {
        "wb_twilight" -> Icons.Rounded.WbSunny
        "spa" -> Icons.Rounded.Spa
        "health_and_safety" -> Icons.Rounded.LocalHospital
        "home" -> Icons.Rounded.Home
        "volunteer_activism" -> Icons.Rounded.VolunteerActivism
        "flight" -> Icons.Rounded.Flight
        else -> Icons.Rounded.MenuBook
    }

    val themeColors = getCategoryThemeColors(category.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("category_card_${category.id}"),
        colors = CardDefaults.cardColors(
            containerColor = themeColors.containerColor
        ),
        border = BorderStroke(1.2.dp, themeColors.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(themeColors.iconBgColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = themeColors.iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.1.sp
                ),
                color = themeColors.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = themeColors.iconBgColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(
                    text = "${category.count} টি দোয়া",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = themeColors.countColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryDetailScreen(
    viewModel: DuaViewModel,
    categoryId: Int
) {
    val duas by viewModel.filteredDuas.collectAsStateWithLifecycle()

    if (duas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("এই ক্যাটাগরিতে কোনো দোয়া পাওয়া যায়নি।", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(duas, key = { it.id }) { dua ->
                DuaCard(
                    dua = dua,
                    onBookmarkToggle = { viewModel.toggleBookmark(dua.id, !dua.isBookmarked) },
                    onFavoriteToggle = { viewModel.toggleFavorite(dua.id, !dua.isFavorite) },
                    onNotesUpdate = { notes -> viewModel.updateNotes(dua.id, notes) }
                )
            }
        }
    }
}

@Composable
fun BookmarksScreen(viewModel: DuaViewModel) {
    val bookmarkedDuas by viewModel.bookmarkedDuas.collectAsStateWithLifecycle()

    if (bookmarkedDuas.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.BookmarkBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "কোনো বুকমার্ক করা দোয়া নেই",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "দোয়ার পাশে থাকা বুকমার্ক বাটনে ক্লিক করে দোয়া সংরক্ষণ করুন।",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookmarkedDuas, key = { it.id }) { dua ->
                DuaCard(
                    dua = dua,
                    onBookmarkToggle = { viewModel.toggleBookmark(dua.id, !dua.isBookmarked) },
                    onFavoriteToggle = { viewModel.toggleFavorite(dua.id, !dua.isFavorite) },
                    onNotesUpdate = { notes -> viewModel.updateNotes(dua.id, notes) }
                )
            }
        }
    }
}

@Composable
fun FavoritesScreen(viewModel: DuaViewModel) {
    val favoritedDuas by viewModel.favoritedDuas.collectAsStateWithLifecycle()

    if (favoritedDuas.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "কোনো পছন্দের দোয়া নেই",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "দোয়ার পাশে থাকা লাভ (হার্ট) বাটনে ক্লিক করে প্রিয় দোয়াগুলো এখানে যোগ করুন।",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favoritedDuas, key = { it.id }) { dua ->
                DuaCard(
                    dua = dua,
                    onBookmarkToggle = { viewModel.toggleBookmark(dua.id, !dua.isBookmarked) },
                    onFavoriteToggle = { viewModel.toggleFavorite(dua.id, !dua.isFavorite) },
                    onNotesUpdate = { notes -> viewModel.updateNotes(dua.id, notes) }
                )
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: DuaViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.filteredDuas.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("search_text_input"),
            placeholder = { Text("দোয়ার নাম, উচ্চারণ বা অর্থ দিয়ে খুঁজুন...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.onSearchQueryChanged("") },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Search Results List
        if (searchQuery.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "খুঁজুন",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "যেকোনো শব্দ টাইপ করে দোয়া ভান্ডারের সমস্ত দোয়া অনুসন্ধান করুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "দুঃখিত, কোনো ম্যাচিং দোয়া পাওয়া যায়নি!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(searchResults, key = { it.id }) { dua ->
                    DuaCard(
                        dua = dua,
                        onBookmarkToggle = { viewModel.toggleBookmark(dua.id, !dua.isBookmarked) },
                        onFavoriteToggle = { viewModel.toggleFavorite(dua.id, !dua.isFavorite) },
                        onNotesUpdate = { notes -> viewModel.updateNotes(dua.id, notes) }
                    )
                }
            }
        }
    }
}

@Composable
fun DuaCard(
    dua: Dua,
    onBookmarkToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onNotesUpdate: (String?) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isEditingNotes by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf(dua.notes ?: "") }

    // TTS implementation
    var isPlaying by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale("bn", "BD")
                ttsInstance?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        isPlaying = false
                    }
                    override fun onError(utteranceId: String?) {
                        isPlaying = false
                    }
                })
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance?.stop()
            ttsInstance?.shutdown()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .testTag("dua_card_${dua.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title and controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dua.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 17.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("favorite_btn_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (dua.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (dua.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_btn_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (dua.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Toggle Bookmark",
                            tint = if (dua.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Show Less" else "Show More"
                        )
                    }
                }
            }

            // Short summary of translation when collapsed to let user scan
            if (!isExpanded) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dua.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!dua.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFF9C4).copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NoteAdd,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "আমার নোটঃ ${dua.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5D4037),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Expanded detail section
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Arabic Text Box with beautiful Islamic display styling
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = dua.arabic,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                lineHeight = 38.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pronunciation section
                    Text(
                        text = "উচ্চারণঃ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dua.pronunciation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Translation section
                    Text(
                        text = "অর্থঃ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dua.translation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audio Player Component
                    DuaAudioPlayer(duaId = dua.id)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Personal Notes Module
                    Text(
                        text = "আমার ব্যক্তিগত নোটঃ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (isEditingNotes) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(12.dp)
                        ) {
                            OutlinedTextField(
                                value = noteText,
                                onValueChange = { noteText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                placeholder = { Text("এখানে আপনার ব্যক্তিগত নোট লিখুন...") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        isEditingNotes = false
                                        noteText = dua.notes ?: ""
                                    },
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("বাতিল", style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val finalNote = noteText.trim().ifBlank { null }
                                        onNotesUpdate(finalNote)
                                        isEditingNotes = false
                                    },
                                    modifier = Modifier.height(36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("সংরক্ষণ", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    } else {
                        if (!dua.notes.isNullOrBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF9C4)) // Post-it yellow styling
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dua.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF5D4037),
                                    modifier = Modifier.weight(1f)
                                )
                                Row {
                                    IconButton(
                                        onClick = { isEditingNotes = true },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Edit,
                                            contentDescription = "Edit Note",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            onNotesUpdate(null)
                                            noteText = ""
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Delete Note",
                                            tint = Color.Red,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = { isEditingNotes = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.NoteAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ব্যক্তিগত নোট বা আমল ডায়েরি লিখুন", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reference Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সূত্রঃ",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dua.reference,
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Utility Buttons: TTS Play, Copy, Share
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val speech = tts
                                    if (speech != null) {
                                        if (isPlaying) {
                                            speech.stop()
                                            isPlaying = false
                                        } else {
                                            // Speak in Bengali
                                            val textToSpeak = "${dua.title}. উচ্চারণঃ ${dua.pronunciation}. অনুবাদঃ ${dua.translation}."
                                            speech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "DuaSpeech")
                                            isPlaying = true
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VolumeUp,
                                    contentDescription = "Pronounce Audio",
                                    tint = if (isPlaying) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(
                                        "Dua",
                                        "${dua.title}\n\n${dua.arabic}\n\nউচ্চারণঃ ${dua.pronunciation}\n\nঅর্থঃ ${dua.translation}\n\nসূত্রঃ ${dua.reference}"
                                    )
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "দোয়া ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = "Copy Dua to Clipboard",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "${dua.title}\n\n${dua.arabic}\n\nউচ্চারণঃ ${dua.pronunciation}\n\nঅর্থঃ ${dua.translation}\n\nসূত্রঃ ${dua.reference}"
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share Dua",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
