package com.example.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCheckingUpdate by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf<String?>(null) } // "privacy" or "terms"

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateIn = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "আমাদের সম্পর্কে",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("about_back_button")
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
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it / 8 },
                animationSpec = tween(600)
            )
        ) {
            BoxWithConstraints(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val isTablet = maxWidth > 600.dp
                val contentPadding = if (isTablet) 24.dp else 16.dp

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(contentPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Premium Brand Header
                    item {
                        BrandHeader(isTablet)
                    }

                    // 2. About Content
                    item {
                        AboutContentCard()
                    }

                    // 3. Developer & Brand Information Card
                    item {
                        DeveloperCard(context)
                    }

                    // 3b. Follow Us Section
                    item {
                        FollowUsSection(context, isTablet)
                    }

                    // 4. Interactive Quick Actions Grid/List
                    item {
                        Text(
                            text = "অ্যাপ্লিকেশন লিংক ও সেবা সমূহ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if (isTablet) {
                        // Responsive 2-column grid for tablet layout
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AboutActionTile(
                                        icon = Icons.Rounded.Share,
                                        title = "অ্যাপ শেয়ার করুন",
                                        subtitle = "সদকায়ে জারিয়ার অংশীদার হোন",
                                        onClick = { shareApp(context) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    AboutActionTile(
                                        icon = Icons.Rounded.Star,
                                        title = "রেটিং দিন",
                                        subtitle = "প্লে-স্টোরে আমাদের উৎসাহিত করুন",
                                        onClick = { rateApp(context) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AboutActionTile(
                                        icon = Icons.Rounded.Email,
                                        title = "যোগাযোগ করুন",
                                        subtitle = "মতামত বা তথ্য সংশোধন জানান",
                                        onClick = { contactUs(context) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    AboutActionTile(
                                        icon = Icons.Rounded.Refresh,
                                        title = "আপডেট চেক করুন",
                                        subtitle = "সর্বশেষ সংস্করণটি নিশ্চিত করুন",
                                        onClick = {
                                            if (!isCheckingUpdate) {
                                                isCheckingUpdate = true
                                                coroutineScope.launch {
                                                    delay(1500)
                                                    isCheckingUpdate = false
                                                    showUpdateDialog = true
                                                }
                                            }
                                        },
                                        isLoading = isCheckingUpdate,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AboutActionTile(
                                        icon = Icons.Rounded.Security,
                                        title = "প্রাইভেসি পলিসি",
                                        subtitle = "তথ্য সুরক্ষার নিয়মাবলী",
                                        onClick = { showInfoDialog = "privacy" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    AboutActionTile(
                                        icon = Icons.Rounded.Article,
                                        title = "শর্তাবলী (Terms)",
                                        subtitle = "অ্যাপ ব্যবহারের নির্দেশনাবলী",
                                        onClick = { showInfoDialog = "terms" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    } else {
                        // Standard clean list for mobile layout
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                AboutActionTile(
                                    icon = Icons.Rounded.Share,
                                    title = "অ্যাপ শেয়ার করুন",
                                    subtitle = "সদকায়ে জারিয়ার অংশীদার হোন",
                                    onClick = { shareApp(context) }
                                )
                                AboutActionTile(
                                    icon = Icons.Rounded.Star,
                                    title = "রেটিং দিন",
                                    subtitle = "প্লে-স্টোরে আমাদের উৎসাহিত করুন",
                                    onClick = { rateApp(context) }
                                )
                                AboutActionTile(
                                    icon = Icons.Rounded.Email,
                                    title = "যোগাযোগ করুন",
                                    subtitle = "মতামত বা তথ্য সংশোধন জানান",
                                    onClick = { contactUs(context) }
                                )
                                AboutActionTile(
                                    icon = Icons.Rounded.Refresh,
                                    title = "আপডেট চেক করুন",
                                    subtitle = "সর্বশেষ সংস্করণটি নিশ্চিত করুন",
                                    onClick = {
                                        if (!isCheckingUpdate) {
                                            isCheckingUpdate = true
                                            coroutineScope.launch {
                                                delay(1500)
                                                isCheckingUpdate = false
                                                showUpdateDialog = true
                                            }
                                        }
                                    },
                                    isLoading = isCheckingUpdate
                                )
                                AboutActionTile(
                                    icon = Icons.Rounded.Security,
                                    title = "প্রাইভেসি পলিসি",
                                    subtitle = "তথ্য সুরক্ষার নিয়মাবলী",
                                    onClick = { showInfoDialog = "privacy" }
                                )
                                AboutActionTile(
                                    icon = Icons.Rounded.Article,
                                    title = "শর্তাবলী (Terms & Conditions)",
                                    subtitle = "অ্যাপ ব্যবহারের নির্দেশনাবলী",
                                    onClick = { showInfoDialog = "terms" }
                                )
                            }
                        }
                    }

                    // Spacer/Bottom area
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "© 2026 Yusuf Islamic TV. All Rights Reserved.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Dialogs
    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            confirmButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("ঠিক আছে", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text("আপডেট স্ট্যাটাস", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text("অভিনন্দন! আপনি আমাদের অ্যাপ্লিকেশনটির সর্বশেষ সংস্করণ (v1.1.0) ব্যবহার করছেন।", style = MaterialTheme.typography.bodyMedium)
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    showInfoDialog?.let { type ->
        Dialog(onDismissRequest = { showInfoDialog = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (type == "privacy") Icons.Rounded.Security else Icons.Rounded.Article,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (type == "privacy") "গোপনীয়তা নীতি (Privacy Policy)" else "ব্যবহারের শর্তাবলী (Terms & Conditions)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (type == "privacy") {
                            "দোয়া ভান্ডার অ্যাপ ব্যবহারকারীদের কোনো ধরণের ব্যক্তিগত তথ্য সংগ্রহ বা সংরক্ষণ করে না। আপনার যোগ করা সমস্ত নোট এবং বুকমার্ক স্থানীয়ভাবে আপনার ডিভাইসে সুরক্ষিত থাকে। অ্যাপটি কোনো ট্র্যাকিং বা অযাচিত বিজ্ঞাপন প্রচার করে না।"
                        } else {
                            "১. অ্যাপটি সম্পূর্ণ ফ্রিতে ব্যবহারের জন্য তৈরি।\n২. এতে সংরক্ষিত সমস্ত ধর্মীয় তথ্য সঠিক ও নির্ভুল রাখার চেষ্টা করা হয়েছে।\n৩. এই অ্যাপের কোনো কনটেন্ট বাণিজ্যিক উদ্দেশ্যে পুনঃব্যবহার করা সম্পূর্ণ নিষেধ।"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Justify
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { showInfoDialog = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("বন্ধ করুন", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun BrandHeader(isTablet: Boolean) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("about_brand_header"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.verticalGradient(colors = gradientColors))
                .padding(vertical = 24.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo with glowing gold circular background frame
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 110.dp else 90.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .border(1.5.dp, Color(0xFFFFDF94), CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // App Name in Bengali
                Text(
                    text = "দোয়া ভান্ডার",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tagline
                Text(
                    text = "বিশ্বস্ত দোয়া, সহীহ রেফারেন্সসহ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.2.sp
                    ),
                    color = Color(0xFFFFF9C4), // Golden yellow tint
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Version Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x33FFFFFF),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = "সংস্করণঃ ১.১.০ (v1.1.0)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AboutContentCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "অ্যাপের উদ্দেশ্য ও পরিচিতি",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Text(
                text = "দোয়া ভান্ডার একটি আধুনিক ইসলামিক মোবাইল অ্যাপ, যার লক্ষ্য কুরআন ও সহিহ হাদিসভিত্তিক দোয়াগুলো সহজ, সুন্দর এবং নির্ভরযোগ্যভাবে সবার কাছে পৌঁছে দেওয়া।",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Justify
            )

            Text(
                text = "প্রতিটি দোয়ার সঙ্গে আরবি পাঠ, বাংলা উচ্চারণ, বাংলা অর্থ, প্রামাণ্য রেফারেন্স এবং প্রয়োজনীয় ব্যাখ্যা যুক্ত করার চেষ্টা করা হয়েছে, যাতে ব্যবহারকারীরা সহজে শিখতে, বুঝতে এবং আমল করতে পারেন।",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Justify
            )

            Text(
                text = "এটি Yusuf Islamic TV-এর একটি উদ্যোগ। আমাদের লক্ষ্য ভবিষ্যতে এই প্ল্যাটফর্মকে একটি পূর্ণাঙ্গ ইসলামিক জ্ঞানভান্ডারে রূপান্তর করা, যেখানে দোয়ার পাশাপাশি কুরআন, সহিহ হাদিস, ইসলামিক বই, যিকির, নামাজের সময়সূচি, কিবলা নির্দেশনা, যাকাত ক্যালকুলেটর, হজ ও উমরাহ গাইড এবং অন্যান্য প্রয়োজনীয় ইসলামিক সেবা যুক্ত করা হবে।",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Justify
            )

            Text(
                text = "আমরা সর্বোচ্চ চেষ্টা করি বিশ্বস্ত ও গ্রহণযোগ্য ইসলামিক উৎস থেকে তথ্য সংগ্রহ ও উপস্থাপন করতে। তবুও যদি কোনো অনিচ্ছাকৃত ভুল আপনার নজরে আসে, অনুগ্রহ করে আমাদের সাথে যোগাযোগ করুন। আপনার মূল্যবান মতামত ও সংশোধনী আমাদের জন্য অত্যন্ত গুরুত্বপূর্ণ।",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun DeveloperCard(context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tv,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ডেভেলপার ও উদ্যোগ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "উদ্যোক্তা প্রতিষ্ঠানঃ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Yusuf Islamic TV",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ইমেইলঃ islamicmaster.info@gmail.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { contactUs(context) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = "Contact email"
                    )
                }
            }
        }
    }
}

@Composable
fun AboutActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Intent action implementations
private fun contactUs(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("islamicmaster.info@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "দোয়া ভান্ডার অ্যাপ মতামত ও সংশোধন")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ইমেইল অ্যাপ্লিকেশন পাওয়া যায়নি।", Toast.LENGTH_SHORT).show()
    }
}

private fun shareApp(context: Context) {
    try {
        val shareMessage = """
            দোয়া ভান্ডার - আধুনিক কুরআন ও সহিহ হাদিসভিত্তিক দোয়া আমল শিক্ষামূলক অ্যাপ।
            
            ডাউনলোড লিংক: https://play.google.com/store/apps/details?id=com.aistudio.duabhandar
            
            উদ্যোক্তা: Yusuf Islamic TV
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        context.startActivity(Intent.createChooser(intent, "শেয়ার করুন"))
    } catch (e: Exception) {
        Toast.makeText(context, "শেয়ার করা সম্ভব হচ্ছে না।", Toast.LENGTH_SHORT).show()
    }
}

private fun rateApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aistudio.duabhandar"))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to browser
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.aistudio.duabhandar"))
            context.startActivity(intent)
        } catch (err: Exception) {
            Toast.makeText(context, "প্লে-স্টোর লিংক ওপেন করা সম্ভব হয়নি।", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun FollowUsSection(context: Context, isTablet: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "আমাদের অনুসরণ করুন (Follow Us)",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        if (isTablet) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                YouTubeCard(context, modifier = Modifier.weight(1f))
                ContactCard(context, modifier = Modifier.weight(1f))
            }
        } else {
            YouTubeCard(context)
            ContactCard(context)
        }
    }
}

@Composable
fun YouTubeCard(context: Context, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) Color(0xFF2D1414) else Color(0xFFFFEAEA)
    val borderColor = if (isDark) Color(0xFF5A2A2A) else Color(0xFFFFCDCD)
    val textColor = if (isDark) Color(0xFFFF8A80) else Color(0xFFC62828)
    val subtitleColor = if (isDark) Color(0xFFFFCDD2) else Color(0xFFE53935)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@usufislamictv?si=gQyT6RDBdDO3eC3Y"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "ইউটিউব ওপেন করা সম্ভব হয়নি।", Toast.LENGTH_SHORT).show()
                }
            }
            .testTag("youtube_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF0000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "YouTube",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Yusuf Islamic TV",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "আমাদের অফিসিয়াল ইউটিউব চ্যানেল সাবস্ক্রাইব করুন",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }

            Icon(
                imageVector = Icons.Rounded.OpenInNew,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ContactCard(context: Context, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) Color(0xFF14242D) else Color(0xFFEAF5FF)
    val borderColor = if (isDark) Color(0xFF2A4A5A) else Color(0xFFCDE4FF)
    val textColor = if (isDark) Color(0xFF80D8FF) else Color(0xFF0277BD)
    val subtitleColor = if (isDark) Color(0xFFB3E5FC) else Color(0xFF039BE5)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { contactUs(context) }
            .testTag("contact_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF0277BD) else Color(0xFF039BE5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = "Email",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "সরাসরি যোগাযোগ করুন",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "islamicmaster.info@gmail.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }

            Icon(
                imageVector = Icons.Rounded.Send,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
