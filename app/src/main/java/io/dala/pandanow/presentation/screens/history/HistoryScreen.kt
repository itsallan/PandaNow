package io.dala.pandanow.presentation.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.presentation.HomeViewModel
import io.dala.pandanow.presentation.navigation.VideoPlayerRoute
import io.dala.pandanow.presentation.screens.home.components.ContinueWatchingCard
import io.dala.pandanow.presentation.screens.home.components.SectionTitle
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import io.dala.pandanow.presentation.screens.history.components.EmptyHistoryState
import io.dala.pandanow.presentation.screens.history.components.HistoryVideoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()
    val videoHistory by viewModel.videoHistory.collectAsState()

    val navigateToVideoPlayer = { video: VideoHistoryItem ->
        navController.navigate(
            VideoPlayerRoute(
                videoUrl = video.videoUrl,
                title = video.title,
                subtitle = video.subtitle,
                subtitleUrl = video.subtitleUrl,
                playlistId = null
            )
        )
    }

//    val continueWatchingVideo = remember(videoHistory) {
//        videoHistory.firstOrNull { it.lastPosition > 0 && it.lastPosition < it.duration }
//    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Watch History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (videoHistory.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearHistory() }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Clear History",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (videoHistory.isEmpty()) {
                EmptyHistoryState(
                    onAddVideo = { navController.popBackStack() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    continueWatchingVideo?.let { video ->
//                        item {
//                            Column(
//                                modifier = Modifier.fillMaxWidth(),
//                                verticalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                SectionTitle(title = "Continue Watching")
//                                ContinueWatchingCard(
//                                    video = video,
//                                    onPlayClick = { navigateToVideoPlayer(video) }
//                                )
//                            }
//                            Spacer(modifier = Modifier.height(16.dp))
//                        }
//                    }

//                    item {
//                        SectionTitle(title = "History")
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }

                    items(videoHistory) { video ->
                        HistoryVideoItem(
                            video = video,
                            onClick = { navigateToVideoPlayer(video) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}