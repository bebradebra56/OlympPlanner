package com.olympplanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.olympplanner.app.ui.viewmodel.NoteViewModel
import com.olympplanner.app.ui.viewmodel.NoteViewModelFactory
import com.olympplanner.app.ui.viewmodel.TaskViewModel
import com.olympplanner.app.ui.viewmodel.TaskViewModelFactory
import com.olympplanner.app.ui.viewmodel.ThemeViewModel
import com.olympplanner.app.ui.viewmodel.ThemeViewModelFactory

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

