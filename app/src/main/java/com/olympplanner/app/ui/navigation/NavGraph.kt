package com.olympplanner.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.olympplanner.app.ui.screens.*
import com.olympplanner.app.ui.viewmodel.NoteViewModel
import com.olympplanner.app.ui.viewmodel.TaskViewModel
import com.olympplanner.app.ui.viewmodel.ThemeViewModel

sealed class Screen(val route: String) {
    object Week : Screen("week")
    object Day : Screen("day")
    object Notes : Screen("notes")
    object Themes : Screen("themes")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    noteViewModel: NoteViewModel,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Week.route
    ) {
        composable(Screen.Week.route) {
            WeekScreen(
                viewModel = taskViewModel,
                onNavigateToDay = { date ->
                    taskViewModel.setSelectedDate(date)
                    navController.navigate(Screen.Day.route)
                }
            )
        }
        
        composable(Screen.Day.route) {
            DayScreen(viewModel = taskViewModel)
        }
        
        composable(Screen.Notes.route) {
            NotesScreen(viewModel = noteViewModel)
        }
        
        composable(Screen.Themes.route) {
            ThemesScreen(viewModel = themeViewModel)
        }
    }
}

