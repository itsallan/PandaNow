package io.dala.pandanow.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.Settings
import io.dala.pandanow.data.VideoHistoryItem
import io.dala.pandanow.presentation.navigation.VideoPlayerRoute
import io.dala.pandanow.presentation.screens.home.components.AddVideoScreen
import io.dala.pandanow.presentation.screens.home.components.ContinueWatchingCard
import io.dala.pandanow.presentation.screens.home.components.EmptyHistoryState
import io.dala.pandanow.presentation.screens.home.components.HistoryVideoItem
import io.dala.pandanow.presentation.screens.home.components.SectionTitle
import io.dala.pandanow.utils.VideoHistoryManager
import io.dala.pandanow.utils.formatFilenameToTitle
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current

    // State
    var showAddVideoScreen by remember { mutableStateOf(false) }
    var videoUrl by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var subtitleUrl by remember { mutableStateOf("") }

    var videoUrlError by remember { mutableStateOf(false) }
    var showAdvancedOptions by remember { mutableStateOf(false) }
    var autoExtractMetadata by remember { mutableStateOf(true) }

    // Video history from SharedPreferences
    val historyManager = remember { VideoHistoryManager.getInstance(context) }
    var videoHistory by remember { mutableStateOf(historyManager.getVideoHistory()) }

    // Refreshes the history when the screen is shown
    LaunchedEffect(Unit) {
        videoHistory = historyManager.getVideoHistory()
    }

    // Auto extract metadata when URL changes
    LaunchedEffect(videoUrl) {
        if (autoExtractMetadata && videoUrl.isNotEmpty()) {
            try {
                // Extract a title from the URL
                val decodedUrl = URLDecoder.decode(videoUrl, StandardCharsets.UTF_8.toString())
                val uri = URI(decodedUrl)
                val path = uri.path

                if (title.isEmpty() && path != null) {
                    // Get filename from path
                    val filename = path.substring(path.lastIndexOf('/') + 1)
                        .substringBeforeLast(".")

                    // Format the filename to a readable title
                    title = formatFilenameToTitle(filename)
                }
            } catch (e: Exception) {
                // If URL parsing fails, try a simpler approach
                val simpleName = videoUrl.substringAfterLast('/')
                    .substringBeforeLast('.')
                    .replace("%20", " ")

                if (title.isEmpty() && simpleName.isNotEmpty()) {
                    title = formatFilenameToTitle(simpleName)
                }
            }
        }
    }

    val continueWatchingVideo = remember(videoHistory) {
        videoHistory.firstOrNull { it.lastPosition > 0 && it.lastPosition < it.duration }
    }

    if (showAddVideoScreen) {
        AddVideoScreen(
            videoUrl = videoUrl,
            onVideoUrlChange = {
                videoUrl = it
                videoUrlError = false
            },
            title = title,
            onTitleChange = { title = it },
            subtitle = subtitle,
            onSubtitleChange = { subtitle = it },
            subtitleUrl = subtitleUrl,
            onSubtitleUrlChange = { subtitleUrl = it },
            autoExtractMetadata = autoExtractMetadata,
            onAutoExtractChange = { autoExtractMetadata = it },
            showAdvancedOptions = showAdvancedOptions,
            onToggleAdvancedOptions = { showAdvancedOptions = !showAdvancedOptions },
            videoUrlError = videoUrlError,
            onBack = { showAddVideoScreen = false },
            onAdd = {
                videoUrlError = videoUrl.isEmpty()

                if (!videoUrlError) {
                    // Generate title if empty
                    val finalTitle = if (title.isEmpty()) {
                        "Untitled Video"
                    } else {
                        title
                    }

                    // Save to history first so it appears immediately
                    val historyItem = VideoHistoryItem(
                        videoUrl = videoUrl,
                        title = finalTitle,
                        subtitle = subtitle.takeIf { it.isNotEmpty() },
                        subtitleUrl = subtitleUrl.takeIf { it.isNotEmpty() },
                        timestamp = System.currentTimeMillis()
                    )
                    historyManager.saveVideoToHistory(historyItem)

                    // Refresh history and close screen
                    videoHistory = historyManager.getVideoHistory()
                    showAddVideoScreen = false

                    // Navigate to player
                    navController.navigate(
                        VideoPlayerRoute(
                            videoUrl = videoUrl,
                            title = finalTitle,
                            subtitle = subtitle.takeIf { it.isNotEmpty() },
                            subtitleUrl = subtitleUrl.takeIf { it.isNotEmpty() }
                        )
                    )
                }
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "PandaNow",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(TablerIcons.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        // Reset form fields when opening
                        videoUrl = ""
                        title = ""
                        subtitle = ""
                        subtitleUrl = ""
                        videoUrlError = false
                        showAdvancedOptions = false
                        showAddVideoScreen = true
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Video") }
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (videoHistory.isEmpty()) {
                    // Empty state
                    EmptyHistoryState(
                        onAddVideo = {
                            videoUrl = ""
                            title = ""
                            subtitle = ""
                            subtitleUrl = ""
                            videoUrlError = false
                            showAdvancedOptions = false
                            showAddVideoScreen = true
                        }
                    )
                } else {
                    // Show history
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Continue watching section (if applicable)
                        continueWatchingVideo?.let { video ->
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SectionTitle(title = "Continue Watching")
                                    ContinueWatchingCard(
                                        video = video,
                                        onPlayClick = {
                                            navController.navigate(
                                                VideoPlayerRoute(
                                                    videoUrl = video.videoUrl,
                                                    title = video.title,
                                                    subtitle = video.subtitle,
                                                    subtitleUrl = video.subtitleUrl
                                                )
                                            )
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // History section
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SectionTitle(title = "Watch History")

                                IconButton(
                                    onClick = {
                                        historyManager.clearHistory()
                                        videoHistory = emptyList()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Clear History",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        items(videoHistory) { video ->
                            HistoryVideoItem(
                                video = video,
                                onClick = {
                                    navController.navigate(
                                        VideoPlayerRoute(
                                            videoUrl = video.videoUrl,
                                            title = video.title,
                                            subtitle = video.subtitle,
                                            subtitleUrl = video.subtitleUrl
                                        )
                                    )
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                        }
                    }
                }
            }
        }
    }
}