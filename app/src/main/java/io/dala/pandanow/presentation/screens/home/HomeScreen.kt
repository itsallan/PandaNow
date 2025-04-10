package io.dala.pandanow.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.Settings
import io.dala.pandanow.presentation.navigation.VideoPlayerRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var videoUrl by remember { mutableStateOf("https://cdn.flowplayer.com/a30bd6bc-f98b-47bc-abf5-97633d4faea0/hls/de3f6ca7-2db3-4689-8160-0f574a5996ad/playlist.m3u8") }
    var title by remember { mutableStateOf("test") }
    var subtitle by remember { mutableStateOf("test 2") }
    var subtitleUrl by remember { mutableStateOf("") }

    var videoUrlError by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "PandaNow") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(TablerIcons.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = {
                        videoUrl = it
                        videoUrlError = it.isEmpty()
                    },
                    label = { Text("Video URL (Required)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = videoUrlError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                        keyboardType = KeyboardType.Uri
                    ),
                    supportingText = {
                        if (videoUrlError) {
                            Text("Video URL is required")
                        }
                    }
                )

                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = it.isEmpty()
                    },
                    label = { Text("Video Title (Required)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    supportingText = {
                        if (titleError) {
                            Text("Title is required")
                        }
                    }
                )

                // Subtitle (Description) Field
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    )
                )

                Row {
                // Subtitle URL Field
                OutlinedTextField(
                    value = subtitleUrl,
                    onValueChange = { subtitleUrl = it },
                    label = { Text("Subtitle URL (Optional)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                        keyboardType = KeyboardType.Uri
                    )
                )

                Button(
                    onClick = {
                        // Validate required fields
                        videoUrlError = videoUrl.isEmpty()
                        titleError = title.isEmpty()

                        if (!videoUrlError && !titleError) {
                            navController.navigate(VideoPlayerRoute(
                                videoUrl = videoUrl,
                                title = title,
                                subtitle = subtitle.takeIf { it.isNotEmpty() },
                                subtitleUrl = subtitleUrl.takeIf { it.isNotEmpty() }
                            ))
                        }
                    },
                    modifier = Modifier.weight(1f).padding(all = 12.dp)
                        .height(56.dp),
                    enabled = videoUrl.isNotEmpty() && title.isNotEmpty()
                ) {
                    Text("Play")
                }
            }
            }

        }
    }
}