package com.example.presentation

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: DuaViewModel,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    // Premium Emerald and Gold Colors
    val primaryBg = if (isDark) Color(0xFF191C1B) else Color(0xFFF4FBF7)
    val cardBg = if (isDark) Color(0xFF1C2220) else Color(0xFFFFFFFF)
    val emeraldPrimary = if (isDark) Color(0xFF5EDFBF) else Color(0xFF006C59)
    val emeraldContainer = if (isDark) Color(0xFF005142) else Color(0xFF7CF8DA)
    val goldColor = if (isDark) Color(0xFFDFB15B) else Color(0xFFC59B27)
    val textColor = if (isDark) Color(0xFFE1E3E0) else Color(0xFF191C1B)
    val textMuted = if (isDark) Color(0xFFBEC9C5) else Color(0xFF3F4946)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(primaryBg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                emeraldPrimary,
                                if (isDark) Color(0xFF00382D) else Color(0xFF002019)
                            )
                        )
                    )
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(cardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Spa,
                        contentDescription = "App Logo",
                        tint = goldColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "দোয়া ভান্ডার",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = emeraldPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "আপনার প্রতিদিনের দ্বীনি সঙ্গী ও দোয়ার সংগ্রহশালা",
                style = MaterialTheme.typography.bodyMedium,
                color = textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card"),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.2.dp, goldColor.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Custom switch tab between Login and Sign Up
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(emeraldPrimary.copy(alpha = 0.1f))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isSignUpMode) emeraldPrimary else Color.Transparent)
                                .clickable { isSignUpMode = false }
                                .testTag("login_tab_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "লগইন",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (!isSignUpMode) cardBg else emeraldPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSignUpMode) emeraldPrimary else Color.Transparent)
                                .clickable { isSignUpMode = true }
                                .testTag("signup_tab_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "নিবন্ধন",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSignUpMode) cardBg else emeraldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("ইমেইল অ্যাড্রেস") },
                        placeholder = { Text("example@email.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Email,
                                contentDescription = null,
                                tint = emeraldPrimary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = emeraldPrimary,
                            unfocusedBorderColor = textMuted.copy(alpha = 0.5f),
                            focusedLabelColor = emeraldPrimary,
                            cursorColor = emeraldPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("পাসওয়ার্ড") },
                        placeholder = { Text("৬+ অক্ষরের পাসওয়ার্ড") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = emeraldPrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = emeraldPrimary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = emeraldPrimary,
                            unfocusedBorderColor = textMuted.copy(alpha = 0.5f),
                            focusedLabelColor = emeraldPrimary,
                            cursorColor = emeraldPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    if (isSignUpMode) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Confirm Password Field
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("পাসওয়ার্ড নিশ্চিত করুন") },
                            placeholder = { Text("পুনরায় পাসওয়ার্ড লিখুন") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.LockReset,
                                    contentDescription = null,
                                    tint = emeraldPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = emeraldPrimary
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = emeraldPrimary,
                                unfocusedBorderColor = textMuted.copy(alpha = 0.5f),
                                focusedLabelColor = emeraldPrimary,
                                cursorColor = emeraldPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_password_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Action Button (Login or Sign Up)
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "অনুগ্রহ করে সব ক্ষেত্র পূরণ করুন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                Toast.makeText(context, "সঠিক ইমেইল অ্যাড্রেস লিখুন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password.length < 6) {
                                Toast.makeText(context, "পাসওয়ার্ড অন্তত ৬ অক্ষরের হতে হবে", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (isSignUpMode) {
                                if (password != confirmPassword) {
                                    Toast.makeText(context, "পাসওয়ার্ড মিলছে না", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                // Simulate Successful Sign Up and Log in
                                viewModel.loginUser(email)
                                Toast.makeText(context, "নিবন্ধন সফল হয়েছে!", Toast.LENGTH_LONG).show()
                            } else {
                                // Simulate Successful Login
                                viewModel.loginUser(email)
                                Toast.makeText(context, "লগইন সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_auth_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = emeraldPrimary,
                            contentColor = cardBg
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (isSignUpMode) "নিবন্ধন সম্পন্ন করুন" else "লগইন করুন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Continue as Guest Option
                    TextButton(
                        onClick = {
                            viewModel.loginAsGuest()
                            Toast.makeText(context, "অতিথি হিসেবে প্রবেশ করছেন", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("continue_as_guest_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "অতিথি হিসেবে প্রবেশ করুন",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = goldColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = goldColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
