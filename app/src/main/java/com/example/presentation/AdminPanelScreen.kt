package com.example.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: DuaViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Form states
    var title by remember { mutableStateOf("") }
    var arabic by remember { mutableStateOf("") }
    var pronunciation by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    
    // Categories list (Id to Name)
    val categoriesList = listOf(
        1 to "সকাল-সন্ধ্যা (Morning & Evening)",
        2 to "নামাজ ও সালাত (Salah & Prayer)",
        3 to "দৈনন্দিন জীবন (Daily Life)",
        4 to "রোগ-ব্যাধি ও আরোগ্য (Health & Healing)",
        5 to "গুনাহ মাফ ও তওবা (Forgiveness & Repentance)",
        6 to "ভ্রমণ ও সফর (Traveling)"
    )
    
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Validation/Error states
    var titleError by remember { mutableStateOf(false) }
    var arabicError by remember { mutableStateOf(false) }
    var pronunciationError by remember { mutableStateOf(false) }
    var translationError by remember { mutableStateOf(false) }

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateIn = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "নতুন দোয়া যোগ করুন",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        AnimatedVisibility(
            visible = animateIn,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                initialOffsetY = { it / 10 },
                animationSpec = tween(500)
            ),
            modifier = Modifier.padding(innerPadding)
        ) {
            BoxWithConstraints(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val isTablet = maxWidth > 600.dp
                val contentPadding = if (isTablet) 24.dp else 16.dp

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(contentPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title info card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "এখানে তথ্য পূরণ করে আপনি ডাটাবেসে সরাসরি নতুন দোয়া যুক্ত করতে পারেন।",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Form container card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Title Field
                                Column {
                                    Text(
                                        text = "দোয়ার শিরোনাম *",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = title,
                                        onValueChange = {
                                            title = it
                                            titleError = false
                                        },
                                        placeholder = { Text("উদা: ঘুমানোর পূর্বের দোয়া") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_title_input"),
                                        isError = titleError,
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true,
                                        leadingIcon = {
                                            Icon(Icons.Rounded.Title, contentDescription = null)
                                        }
                                    )
                                    if (titleError) {
                                        Text(
                                            text = "শিরোনাম আবশ্যক",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }

                                // 2. Category Selector Dropdown
                                Column {
                                    Text(
                                        text = "দোয়ার ক্যাটাগরি *",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = categoriesList[selectedCategoryIndex].second,
                                            onValueChange = {},
                                            readOnly = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { dropdownExpanded = true }
                                                .testTag("admin_category_dropdown"),
                                            shape = RoundedCornerShape(8.dp),
                                            trailingIcon = {
                                                IconButton(onClick = { dropdownExpanded = true }) {
                                                    Icon(
                                                        imageVector = if (dropdownExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                                        contentDescription = "Expand categories"
                                                    )
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Rounded.Category, contentDescription = null)
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = dropdownExpanded,
                                            onDismissRequest = { dropdownExpanded = false },
                                            modifier = Modifier
                                                .fillMaxWidth(0.9f)
                                                .background(MaterialTheme.colorScheme.surface)
                                        ) {
                                            categoriesList.forEachIndexed { index, pair ->
                                                DropdownMenuItem(
                                                    text = { Text(pair.second) },
                                                    onClick = {
                                                        selectedCategoryIndex = index
                                                        dropdownExpanded = false
                                                    },
                                                    modifier = Modifier.testTag("category_item_$index")
                                                )
                                            }
                                        }
                                    }
                                }

                                // 3. Arabic Text Field
                                Column {
                                    Text(
                                        text = "আরবি উচ্চারণ/টেক্সট *",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = arabic,
                                        onValueChange = {
                                            arabic = it
                                            arabicError = false
                                        },
                                        placeholder = { Text("اللَّهُمَّ بِاسْمِكَ أَمُوتُ وَأَحْيَا") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_arabic_input"),
                                        isError = arabicError,
                                        shape = RoundedCornerShape(8.dp),
                                        minLines = 2,
                                        leadingIcon = {
                                            Icon(Icons.Rounded.MenuBook, contentDescription = null)
                                        }
                                    )
                                    if (arabicError) {
                                        Text(
                                            text = "আরবি টেক্সট আবশ্যক",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }

                                // 4. Pronunciation Field
                                Column {
                                    Text(
                                        text = "বাংলা উচ্চারণ *",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = pronunciation,
                                        onValueChange = {
                                            pronunciation = it
                                            pronunciationError = false
                                        },
                                        placeholder = { Text("উদা: আল্লাহুম্মা বিসমিকা আমুতু ওয়া আহয়া।") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_pronunciation_input"),
                                        isError = pronunciationError,
                                        shape = RoundedCornerShape(8.dp),
                                        minLines = 2,
                                        leadingIcon = {
                                            Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null)
                                        }
                                    )
                                    if (pronunciationError) {
                                        Text(
                                            text = "বাংলা উচ্চারণ আবশ্যক",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }

                                // 5. Meaning Field
                                Column {
                                    Text(
                                        text = "বাংলা অর্থ *",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = translation,
                                        onValueChange = {
                                            translation = it
                                            translationError = false
                                        },
                                        placeholder = { Text("উদা: হে আল্লাহ! আপনার নাম নিয়ে আমি মৃত্যুবরণ করি এবং জীবিত হই।") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_translation_input"),
                                        isError = translationError,
                                        shape = RoundedCornerShape(8.dp),
                                        minLines = 2,
                                        leadingIcon = {
                                            Icon(Icons.Rounded.Translate, contentDescription = null)
                                        }
                                    )
                                    if (translationError) {
                                        Text(
                                            text = "অর্থ আবশ্যক",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }

                                // 6. Reference Field
                                Column {
                                    Text(
                                        text = "রেফারেন্স বা উৎস",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = reference,
                                        onValueChange = { reference = it },
                                        placeholder = { Text("উদা: বুখারী: ৬৩২৪") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_reference_input"),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true,
                                        leadingIcon = {
                                            Icon(Icons.Rounded.BookmarkBorder, contentDescription = null)
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Save Button
                                Button(
                                    onClick = {
                                        // Simple form validation
                                        if (title.isBlank()) titleError = true
                                        if (arabic.isBlank()) arabicError = true
                                        if (pronunciation.isBlank()) pronunciationError = true
                                        if (translation.isBlank()) translationError = true

                                        if (!titleError && !arabicError && !pronunciationError && !translationError) {
                                            keyboardController?.hide()
                                            viewModel.insertDua(
                                                title = title.trim(),
                                                arabic = arabic.trim(),
                                                pronunciation = pronunciation.trim(),
                                                translation = translation.trim(),
                                                reference = reference.trim().ifEmpty { "সংগৃহীত" },
                                                categoryId = categoriesList[selectedCategoryIndex].first,
                                                onSuccess = {
                                                    Toast.makeText(
                                                        context,
                                                        "নতুন দোয়াটি সফলভাবে যুক্ত করা হয়েছে!",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    onBackClick()
                                                }
                                            )
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "দয়া করে সব আবশ্যক ক্ষেত্র পূরণ করুন।",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("admin_save_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(Icons.Rounded.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "দোয়াটি সংরক্ষণ করুন",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
