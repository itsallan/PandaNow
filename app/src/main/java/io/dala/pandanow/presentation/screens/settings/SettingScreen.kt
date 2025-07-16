package io.dala.pandanow.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VideoCameraBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.dala.pandanow.presentation.theme.ThemeStateManager
import io.dala.pandanow.utils.SettingsManager
import io.dala.pandanow.utils.VideoHistoryManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager.getInstance(context) }
    val historyManager = remember { VideoHistoryManager.getInstance(context) }

    var autoExtractMetadata by remember { mutableStateOf(settingsManager.getAutoExtractMetadata()) }
    var darkMode by remember { mutableStateOf(settingsManager.getDarkMode()) }

    // UI state
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showDarkModeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Video Settings Section
                SettingsSectionHeader(title = "Video Settings", icon = Icons.Default.VideoCameraBack)

                SettingsToggle(
                    title = "Auto-extract metadata",
                    description = "Automatically extract title and metadata from video URLs",
                    isChecked = autoExtractMetadata,
                    onCheckedChange = { newValue ->
                        autoExtractMetadata = newValue
                        settingsManager.setAutoExtractMetadata(newValue)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Appearance Settings
                SettingsSectionHeader(title = "Appearance", icon = Icons.Default.ColorLens)

                SettingsItem(
                    title = "Theme",
                    description = "Change the app theme",
                    value = darkMode,
                    onClick = { showDarkModeDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Data Management
                SettingsSectionHeader(title = "Data Management", icon = Icons.Default.Delete)

                SettingsItem(
                    title = "Clear History",
                    description = "Remove all videos from watch history",
                    value = "",
                    onClick = { showClearHistoryDialog = true },
                    showDivider = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // About
                SettingsSectionHeader(title = "About", icon = Icons.Default.Info)

                SettingsItem(
                    title = "About PandaNow",
                    description = "Version 1.0.0",
                    value = "",
                    onClick = { showAboutDialog = true },
                    showDivider = false
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Dialogs
            if (showClearHistoryDialog) {
                AlertDialog(
                    onDismissRequest = { showClearHistoryDialog = false },
                    title = { Text("Clear History") },
                    text = { Text("Are you sure you want to clear your watch history? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                historyManager.clearHistory()
                                showClearHistoryDialog = false
                            }
                        ) {
                            Text("Clear")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearHistoryDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showDarkModeDialog) {
                val themes = listOf("System Default", "Light", "Dark")

                AlertDialog(
                    onDismissRequest = { showDarkModeDialog = false },
                    title = { Text("App Theme") },
                    text = {
                        Column {
                            themes.forEach { theme ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            darkMode = theme
                                            settingsManager.setDarkMode(theme)
                                            ThemeStateManager.setThemeMode(theme)
                                            showDarkModeDialog = false
                                        }
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = darkMode == theme,
                                        onClick = {
                                            darkMode = theme
                                            settingsManager.setDarkMode(theme)
                                            ThemeStateManager.setThemeMode(theme)
                                            showDarkModeDialog = false
                                        }
                                    )
                                    Text(
                                        text = theme,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showDarkModeDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showAboutDialog) {
                AlertDialog(
                    onDismissRequest = { showAboutDialog = false },
                    title = { Text("About PandaNow") },
                    text = {
                        Column {
                            Text("PandaNow Video Player")
                            Text("Version 1.0.0")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("A modern video player for all your media needs.")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("© 2025 PandaNow")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAboutDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsToggle(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }

        Divider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    value: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(0.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showDivider) {
            Divider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}