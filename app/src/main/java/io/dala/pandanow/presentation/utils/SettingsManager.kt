package io.dala.pandanow.presentation.utils


import android.content.Context
import android.content.SharedPreferences

/**
 * Manages app settings using SharedPreferences
 */
class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SETTINGS_PREFERENCES, Context.MODE_PRIVATE
    )

    // Auto-extract metadata setting
    fun getAutoExtractMetadata(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTO_EXTRACT_METADATA, true)
    }

    fun setAutoExtractMetadata(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_EXTRACT_METADATA, value).apply()
    }

    // Theme settings
    fun getDarkMode(): String {
        return sharedPreferences.getString(KEY_DARK_MODE, "System Default") ?: "System Default"
    }

    fun setDarkMode(value: String) {
        sharedPreferences.edit().putString(KEY_DARK_MODE, value).apply()
    }

    companion object {
        private const val SETTINGS_PREFERENCES = "app_settings"

        private const val KEY_AUTO_EXTRACT_METADATA = "auto_extract_metadata"
        private const val KEY_DARK_MODE = "dark_mode"

        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }
}