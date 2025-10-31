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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.xr.compose.material3.ExperimentalMaterial3XrApi
import androidx.xr.compose.material3.HorizontalFloatingToolbar
import compose.icons.TablerIcons
import compose.icons.tablericons.History
import compose.icons.tablericons.Settings
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.presentation.HomeViewModel
import io.dala.pandanow.presentation.navigation.HistoryRoute
import io.dala.pandanow.presentation.navigation.SettingsRoute
import io.dala.pandanow.presentation.navigation.VideoPlayerRoute
import io.dala.pandanow.presentation.screens.home.components.AddVideoScreen
import io.dala.pandanow.presentation.screens.home.components.ContinueWatchingCard
import io.dala.pandanow.presentation.screens.home.components.CreatePlaylistScreen
import io.dala.pandanow.presentation.screens.history.components.EmptyHistoryState
import io.dala.pandanow.presentation.screens.history.components.HistoryVideoItem
import io.dala.pandanow.presentation.screens.home.components.PlaylistCard
import io.dala.pandanow.presentation.screens.home.components.SectionTitle
import io.dala.pandanow.presentation.utils.formatFilenameToTitle
import org.koin.androidx.compose.koinViewModel
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3XrApi::class
)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()
    val videoHistory by viewModel.videoHistory.collectAsState()
    val playlists by viewModel.playlists.collectAsState() // New playlist state


    var showAddVideoScreen by rememberSaveable { mutableStateOf(false) }
    var videoUrl by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var subtitle by rememberSaveable { mutableStateOf("") }
    var subtitleUrl by rememberSaveable { mutableStateOf("") }
    var videoUrlError by rememberSaveable { mutableStateOf(false) }
    var showAdvancedOptions by rememberSaveable { mutableStateOf(false) }
    var autoExtractMetadata by rememberSaveable { mutableStateOf(true) }

    var showCreatePlaylistScreen by rememberSaveable { mutableStateOf(false) }

    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
        viewModel.loadPlaylists()
    }

    LaunchedEffect(videoUrl) {
        if (autoExtractMetadata && videoUrl.isNotEmpty()) {
            try {
                val decodedUrl = URLDecoder.decode(videoUrl, StandardCharsets.UTF_8.toString())
                val uri = URI(decodedUrl)
                val path = uri.path

                if (title.isEmpty() && path != null) {
                    val filename = path.substring(path.lastIndexOf('/') + 1)
                        .substringBeforeLast(".")

                    title = formatFilenameToTitle(filename)
                }
            } catch (e: Exception) {
                val simpleName = videoUrl.substringAfterLast('/')
                    .substringBeforeLast('.')
                    .replace("%20", " ")
                    .replace("%21", " ")

                if (title.isEmpty() && simpleName.isNotEmpty()) {
                    title = formatFilenameToTitle(simpleName)
                }
            }
        }
    }

    val continueWatchingVideo = remember(videoHistory) {
        videoHistory.firstOrNull { it.lastPosition > 0 && it.lastPosition < it.duration }
    }

    val navigateToVideoPlayer = { finalVideoUrl: String, finalTitle: String, finalSubtitle: String?, finalSubtitleUrl: String?, playlistId: String? ->
        navController.navigate(
            VideoPlayerRoute(
                videoUrl = finalVideoUrl,
                title = finalTitle,
                subtitle = finalSubtitle,
                subtitleUrl = finalSubtitleUrl,
                playlistId = playlistId
            )
        )
    }

    val resetAddVideoState = {
        videoUrl = ""
        title = ""
        subtitle = ""
        subtitleUrl = ""
        videoUrlError = false
        showAdvancedOptions = false
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
                    val finalTitle = title.ifEmpty {
                        "Untitled Video"
                    }

                    val historyItem = VideoHistoryItem(
                        videoUrl = videoUrl,
                        title = finalTitle,
                        subtitle = subtitle.takeIf { it.isNotEmpty() },
                        subtitleUrl = subtitleUrl.takeIf { it.isNotEmpty() },
                        lastPosition = 0L,
                        duration = 0L,
                        timestamp = System.currentTimeMillis()
                    )

                    viewModel.saveNewVideoToHistory(historyItem)

                    showAddVideoScreen = false

                    navigateToVideoPlayer(
                        videoUrl,
                        finalTitle,
                        subtitle.takeIf { it.isNotEmpty() },
                        subtitleUrl.takeIf { it.isNotEmpty() },
                        null
                    )
                }
            }
        )
    } else if (showCreatePlaylistScreen) {
        CreatePlaylistScreen(onBack = { showCreatePlaylistScreen = false })
    } else {
        Scaffold(
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = Color.Unspecified,
                        navigationIconContentColor = Color.Unspecified,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = Color.Unspecified
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate(HistoryRoute) }) {
                            Icon(TablerIcons.History, contentDescription = "Watch History")
                        }
                        IconButton(onClick = { navController.navigate(SettingsRoute) }) {
                            Icon(TablerIcons.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                HorizontalFloatingToolbar(
                    expanded = expanded,
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { expanded = !expanded },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = if (expanded) "Close menu" else "Expand menu")
                        }
                    },
                    colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                    content = {
                        IconButton(
                            onClick = {
                                expanded = false
                                resetAddVideoState()
                                showCreatePlaylistScreen = true
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.QueueMusic,
                                contentDescription = "Create Playlist"
                            )
                        }

                        IconButton(
                            onClick = {
                                expanded = false
                                resetAddVideoState()
                                showAddVideoScreen = true
                            }
                        ) {
                            Icon(
                                Icons.Filled.VideoFile,
                                contentDescription = "Add Video"
                            )
                        }
                    },
                )
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (videoHistory.isEmpty() && playlists.isEmpty()) {
                    EmptyHistoryState(
                        onAddVideo = {
                            resetAddVideoState()
                            showAddVideoScreen = true
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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
                                            navigateToVideoPlayer(video.videoUrl, video.title, video.subtitle, video.subtitleUrl, null)
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        if (playlists.isNotEmpty()) {
                            item {
                                SectionTitle(title = "Playlists")
                            }

                            items(playlists) { playlist ->
                                PlaylistCard(
                                    playlist = playlist,
                                    onClick = {
                                        val firstVideo = playlist.videos.firstOrNull()
                                        if (firstVideo != null) {
                                            navController.navigate(
                                                VideoPlayerRoute(
                                                    videoUrl = firstVideo.videoUrl,
                                                    title = playlist.name,
                                                    subtitle = null,
                                                    subtitleUrl = null,
                                                    playlistId = playlist.id,
                                                    initialVideoIndex = 0
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}