package com.olympplanner.app.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

enum class ThemeMode {
    DAY, NIGHT
}

data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.DAY,
    val glowIntensity: Float = 0.5f,
    val accentSaturation: Float = 1.0f,
    val showColumns: Boolean = true
)

class ThemePreferences(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val GLOW_INTENSITY = floatPreferencesKey("glow_intensity")
        val ACCENT_SATURATION = floatPreferencesKey("accent_saturation")
        val SHOW_COLUMNS = booleanPreferencesKey("show_columns")
    }

    val themeSettings: Flow<ThemeSettings> = context.dataStore.data.map { preferences ->
        ThemeSettings(
            mode = ThemeMode.valueOf(
                preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.DAY.name
            ),
            glowIntensity = preferences[PreferencesKeys.GLOW_INTENSITY] ?: 0.5f,
            accentSaturation = preferences[PreferencesKeys.ACCENT_SATURATION] ?: 1.0f,
            showColumns = preferences[PreferencesKeys.SHOW_COLUMNS] ?: true
        )
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun updateGlowIntensity(intensity: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GLOW_INTENSITY] = intensity.coerceIn(0f, 1f)
        }
    }

    suspend fun updateAccentSaturation(saturation: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCENT_SATURATION] = saturation.coerceIn(0f, 1f)
        }
    }

    suspend fun updateShowColumns(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COLUMNS] = show
        }
    }
}

