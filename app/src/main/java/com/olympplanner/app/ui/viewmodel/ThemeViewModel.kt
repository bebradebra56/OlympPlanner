package com.olympplanner.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olympplanner.app.ui.theme.ThemeMode
import com.olympplanner.app.ui.theme.ThemePreferences
import com.olympplanner.app.ui.theme.ThemeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val themePreferences: ThemePreferences) : ViewModel() {

    val themeSettings = themePreferences.themeSettings
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ThemeSettings()
        )

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.updateThemeMode(mode)
        }
    }

    fun updateGlowIntensity(intensity: Float) {
        viewModelScope.launch {
            themePreferences.updateGlowIntensity(intensity)
        }
    }

    fun updateAccentSaturation(saturation: Float) {
        viewModelScope.launch {
            themePreferences.updateAccentSaturation(saturation)
        }
    }

    fun updateShowColumns(show: Boolean) {
        viewModelScope.launch {
            themePreferences.updateShowColumns(show)
        }
    }
}

