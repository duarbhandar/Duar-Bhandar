package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.DuaDatabase
import com.example.data.local.DuaPreferences
import com.example.data.repository.DuaRepositoryImpl
import com.example.presentation.DuaApp
import com.example.presentation.DuaViewModel
import com.example.presentation.DuaViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Local SQLite Room database initialization
        val database = DuaDatabase.getDatabase(this)
        val repository = DuaRepositoryImpl(database.duaDao())
        val preferences = DuaPreferences(applicationContext)

        // Inject repository and preferences into ViewModel via Factory
        val viewModel = ViewModelProvider(
            this,
            DuaViewModelFactory(repository, preferences)
        )[DuaViewModel::class.java]

        setContent {
            MyApplicationTheme {
                DuaApp(viewModel = viewModel)
            }
        }
    }
}
