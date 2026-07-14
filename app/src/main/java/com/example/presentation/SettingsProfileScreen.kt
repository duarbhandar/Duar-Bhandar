package com.example.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsProfileScreen(
    onAboutUsClick: () -> Unit,
    onAddNewDuaClick: () -> Unit,
    onLogoutClick: () -> Unit,
    userEmail: String,
    isGuestUser: Boolean,
    modifier: Modifier = Modifier
) {
    var notificationEnabled by remember { mutableStateOf(true) }
    var autoPlayAudio by remember { mutableStateOf(false) }
    var selectedFontSize by remember { mutableFloatStateOf(16f) }

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateIn = true
    }

    AnimatedVisibility(
        visible = animateIn,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
            initialOffsetY = { it / 10 },
            animationSpec = tween(500)
        )
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
                // 1. Premium Profile Header Card
                item {
                    ProfileHeaderCard(isTablet = isTablet, userEmail = userEmail, isGuestUser = isGuestUser)
                }

                // 2. Supplication Statistics Card
                item {
                    DuaStatsRow()
                }

                // 3. App Settings Section Header
                item {
                    Text(
                        text = "অ্যাপ্লিকেশন সেটিংস",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // 4. Settings Card Group
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column {
                            // Toggle 1: Daily Reminder
                            SettingsToggleTile(
                                icon = Icons.Rounded.NotificationsActive,
                                title = "প্রতিদিনের নোটিফিকেশন",
                                subtitle = "প্রতিদিন সকালে দোয়ার রিমাইন্ডার পান",
                                checked = notificationEnabled,
                                onCheckedChange = { notificationEnabled = it }
                            )

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Toggle 2: Audio Auto-play
                            SettingsToggleTile(
                                icon = Icons.Rounded.VolumeUp,
                                title = "অডিও স্বয়ংক্রিয় প্লেব্যাক",
                                subtitle = "দোয়ার পেজে ঢোকার সাথে সাথে অডিও বাজবে",
                                checked = autoPlayAudio,
                                onCheckedChange = { autoPlayAudio = it }
                            )

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Slider: Font Size Slider
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FormatSize,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "আরবি লেখার ফন্ট সাইজ",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${selectedFontSize.toInt()} sp",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Slider(
                                    value = selectedFontSize,
                                    onValueChange = { selectedFontSize = it },
                                    valueRange = 12f..28f,
                                    steps = 4,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                }

                // 5. Support & About Us Section Header
                item {
                    Text(
                        text = "সহায়তা ও আমাদের তথ্য",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // 6. Navigation items
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column {
                            // About Us item
                            SettingsNavigationTile(
                                icon = Icons.Rounded.Info,
                                title = "আমাদের সম্পর্কে (About Us)",
                                subtitle = "দোয়া ভান্ডার ও ইউসুফ ইসলামিক টিভি পরিচিতি",
                                onClick = onAboutUsClick,
                                modifier = Modifier.testTag("settings_about_us_tile")
                            )

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Contact info dummy
                            SettingsNavigationTile(
                                icon = Icons.Rounded.Lock,
                                title = "গোপনীয়তা নীতি ও নিরাপত্তা নীতি",
                                subtitle = "আপনার ডাটা নিরাপত্তা ও পলিসি বিবরণী",
                                onClick = onAboutUsClick // Navigates to About Us (which contains policy view)
                            )

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Add New Dua admin panel item
                            SettingsNavigationTile(
                                icon = Icons.Rounded.Add,
                                title = "নতুন দোয়া যোগ করুন (Add New Dua)",
                                subtitle = "সরাসরি অ্যাপের ডাটাবেসে নতুন দোয়া যুক্ত করুন",
                                onClick = onAddNewDuaClick,
                                modifier = Modifier.testTag("settings_add_dua_tile")
                            )

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Log Out tile
                            SettingsNavigationTile(
                                icon = Icons.Rounded.ExitToApp,
                                title = "লগ আউট করুন (Log Out)",
                                subtitle = if (isGuestUser) "অতিথি সেশন থেকে বিদায় নিন" else "আপনার অ্যাকাউন্ট সেশন থেকে লগ আউট করুন",
                                onClick = onLogoutClick,
                                modifier = Modifier.testTag("settings_logout_tile")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderCard(isTablet: Boolean, userEmail: String, isGuestUser: Boolean) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.horizontalGradient(colors = gradientColors))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar with circular glow
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 76.dp else 64.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = "User Avatar",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isGuestUser) "অতিথি ব্যবহারকারী" else "দ্বীনি ভাই/বোন",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isGuestUser) "দোয়ার সংগ্রহশালা উপভোগ করুন" else userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                // Decorative golden moon badge
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFDF94),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun DuaStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatsCard(
            icon = Icons.Rounded.WbSunny,
            tint = Color(0xFFFFB300),
            count = "১২ দিন",
            label = "আমাল স্ট্রিক",
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            icon = Icons.Rounded.Bookmark,
            tint = Color(0xFF00C853),
            count = "৮ টি",
            label = "সংরক্ষিত দোয়া",
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            icon = Icons.Rounded.Favorite,
            tint = Color(0xFFD50000),
            count = "৩৪ বার",
            label = "পঠিত জিকির",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatsCard(
    icon: ImageVector,
    tint: Color,
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsToggleTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
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

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsNavigationTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
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
