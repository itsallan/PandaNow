package io.dala.pandanow.presentation.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.dala.pandanow.utils.SettingsManager

/**
 * Manages the theme state for the app to enable real-time theme changes
 */
object ThemeStateManager {
    // Private mutable state that can be updated
    private val _themeMode = mutableStateOf("System Default")

    // Public immutable state that can be observed
    val themeMode: State<String> = _themeMode

    // Update the theme mode and trigger recomposition
    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    // Initialize from settings when the app starts
    fun initFromSettings(context: Context) {
        val settingsManager = SettingsManager.getInstance(context)
        _themeMode.value = settingsManager.getDarkMode()
    }
}

@Composable
fun rememberThemeState(context: Context): MutableState<String> {
    val settingsManager = remember { SettingsManager.getInstance(context) }
    val themeState = remember { mutableStateOf(settingsManager.getDarkMode()) }

    return themeState
}