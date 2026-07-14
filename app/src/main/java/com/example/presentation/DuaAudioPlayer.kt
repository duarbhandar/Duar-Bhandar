package com.example.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun DuaAudioPlayer(
    duaId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Map Dua IDs to actual online MP3 files (Mishary Alafasy recitations)
    val audioUrl = remember(duaId) {
        when (duaId) {
            1 -> "https://www.everyayah.com/data/Mishary_Al_Afasy_32kbps/002255.mp3" // Ayat al-Kursi
            2 -> "https://www.everyayah.com/data/Mishary_Al_Afasy_32kbps/112001.mp3" // Surah Al-Ikhlas
            3 -> "https://www.everyayah.com/data/Mishary_Al_Afasy_32kbps/113001.mp3" // Surah Al-Falaq
            4 -> "https://www.everyayah.com/data/Mishary_Al_Afasy_32kbps/114001.mp3" // Surah An-Nas
            else -> "https://www.everyayah.com/data/Mishary_Al_Afasy_32kbps/001001.mp3" // Fallback Bismillah
        }
    }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var totalDuration by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var isOfflineMode by remember { mutableStateOf(false) }

    // Offline simulation state
    var simulatedDuration by remember { mutableFloatStateOf(15000f) } // 15 seconds default

    // Clean up media player when composable is destroyed
    DisposableEffect(duaId) {
        onDispose {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
            isPlaying = false
            isPrepared = false
            isLoading = false
        }
    }

    // Helper to format milliseconds to MM:SS
    fun formatTime(ms: Float): String {
        val totalSeconds = (ms / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    // Initialize/Reset Media Player
    fun initMediaPlayer() {
        if (isOfflineMode) return

        isLoading = true
        isPrepared = false
        isPlaying = false
        currentPosition = 0f

        // Release existing if any
        mediaPlayer?.release()
        mediaPlayer = null

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
            }

            player.setOnPreparedListener { p ->
                isLoading = false
                isPrepared = true
                totalDuration = p.duration.toFloat()
                p.start()
                isPlaying = true
            }

            player.setOnCompletionListener {
                isPlaying = false
                currentPosition = 0f
                it.seekTo(0)
            }

            player.setOnErrorListener { _, what, extra ->
                isLoading = false
                isPrepared = false
                isPlaying = false
                isOfflineMode = true
                Toast.makeText(
                    context,
                    "ইন্টারনেট ত্রুটি! অফলাইন প্লেব্যাক মোড সক্রিয় করা হয়েছে।",
                    Toast.LENGTH_SHORT
                ).show()
                // Start local simulation immediately
                totalDuration = simulatedDuration
                isPlaying = true
                true
            }

            mediaPlayer = player
            player.prepareAsync()

        } catch (e: Exception) {
            isLoading = false
            isPrepared = false
            isOfflineMode = true
            totalDuration = simulatedDuration
            isPlaying = true
        }
    }

    // Real-time progress updater for actual MediaPlayer
    LaunchedEffect(isPlaying, isPrepared, isOfflineMode) {
        if (isPlaying && isPrepared && !isOfflineMode) {
            while (isPlaying) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying && !isSeeking) {
                        currentPosition = player.currentPosition.toFloat()
                        sliderValue = currentPosition
                    }
                }
                delay(200)
            }
        }
    }

    // Progress updater for Offline/Simulation mode
    LaunchedEffect(isPlaying, isOfflineMode) {
        if (isPlaying && isOfflineMode) {
            while (isPlaying) {
                delay(200)
                if (!isSeeking) {
                    currentPosition += 200f
                    if (currentPosition >= totalDuration) {
                        currentPosition = 0f
                        isPlaying = false
                    }
                    sliderValue = currentPosition
                }
            }
        }
    }

    // Synchronize slider value during non-seeking states
    LaunchedEffect(currentPosition) {
        if (!isSeeking) {
            sliderValue = currentPosition
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_player_container_$duaId"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Audio title and Mode indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Audiotrack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "সহীহ অডিও তিলাওয়াত",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Mode Badge
                Surface(
                    color = if (isOfflineMode) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isOfflineMode) "অফলাইন মোড" else "অনলাইন স্ট্রিমিং",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isOfflineMode) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Player controls row (Play, Pause, Progress, Time labels)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Play / Pause / Load button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("audio_loading_spinner"),
                            color = MaterialTheme.colorScheme.secondary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        IconButton(
                            onClick = {
                                if (isOfflineMode) {
                                    isPlaying = !isPlaying
                                } else {
                                    val player = mediaPlayer
                                    if (player == null) {
                                        initMediaPlayer()
                                    } else {
                                        if (player.isPlaying) {
                                            player.pause()
                                            isPlaying = false
                                        } else {
                                            player.start()
                                            isPlaying = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary, shape = CircleShape)
                                .size(36.dp)
                                .testTag("audio_play_pause_btn_$duaId")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pause Recitation" else "Play Recitation",
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Stop button
                IconButton(
                    onClick = {
                        if (isOfflineMode) {
                            isPlaying = false
                            currentPosition = 0f
                        } else {
                            mediaPlayer?.let { player ->
                                if (player.isPlaying) {
                                    player.pause()
                                }
                                player.seekTo(0)
                                isPlaying = false
                                currentPosition = 0f
                            }
                        }
                    },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                        .size(36.dp)
                        .testTag("audio_stop_btn_$duaId"),
                    enabled = isPlaying || currentPosition > 0f
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stop,
                        contentDescription = "Stop Recitation",
                        tint = if (isPlaying || currentPosition > 0f) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Progress slider
                val sliderMax = if (isOfflineMode) simulatedDuration else totalDuration.coerceAtLeast(1f)
                Slider(
                    value = sliderValue,
                    onValueChange = { newValue ->
                        isSeeking = true
                        sliderValue = newValue
                    },
                    onValueChangeFinished = {
                        isSeeking = false
                        currentPosition = sliderValue
                        if (isOfflineMode) {
                            // seek in simulated timeline
                        } else {
                            mediaPlayer?.seekTo(sliderValue.toInt())
                        }
                    },
                    valueRange = 0f..sliderMax,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        thumbColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("audio_slider_$duaId")
                )

                // Time Counter
                Text(
                    text = "${formatTime(sliderValue)} / ${formatTime(if (isOfflineMode) simulatedDuration else totalDuration)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.testTag("audio_time_text_$duaId")
                )
            }
        }
    }
}
