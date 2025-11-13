package com.olympplanner.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.olympplanner.app.data.local.AppDatabase
import com.olympplanner.app.domain.repository.NoteRepository
import com.olympplanner.app.domain.repository.TaskRepository
import com.olympplanner.app.ui.components.BottomNavigationBar
import com.olympplanner.app.ui.components.OlympBackground
import com.olympplanner.app.ui.navigation.NavGraph
import com.olympplanner.app.ui.theme.OlympPlannerTheme
import com.olympplanner.app.ui.theme.ThemePreferences
import com.olympplanner.app.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize repositories
        val database = AppDatabase.getInstance(applicationContext)
        val taskRepository = TaskRepository(database.taskDao())
        val noteRepository = NoteRepository(database.noteDao())
        val themePreferences = ThemePreferences(applicationContext)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(themePreferences)
            )
            val themeSettings by themeViewModel.themeSettings.collectAsState()

            OlympPlannerTheme(
                themeMode = themeSettings.mode,
                accentSaturation = themeSettings.accentSaturation
            ) {
                MainScreen(
                    taskRepository = taskRepository,
                    noteRepository = noteRepository,
                    themeViewModel = themeViewModel,
                    themeSettings = themeSettings
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    taskRepository: TaskRepository,
    noteRepository: NoteRepository,
    themeViewModel: ThemeViewModel,
    themeSettings: com.olympplanner.app.ui.theme.ThemeSettings
) {
    val navController = rememberNavController()

    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(taskRepository)
    )

    val noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(noteRepository)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Background with Olymp elements
        OlympBackground(
            themeMode = themeSettings.mode,
            showColumns = themeSettings.showColumns,
            glowIntensity = themeSettings.glowIntensity
        )

        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavGraph(
                    navController = navController,
                    taskViewModel = taskViewModel,
                    noteViewModel = noteViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}

