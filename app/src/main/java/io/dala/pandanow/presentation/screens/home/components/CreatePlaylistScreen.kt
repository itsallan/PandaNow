package io.dala.pandanow.presentation.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.presentation.HomeViewModel
import io.dala.pandanow.presentation.utils.formatFilenameToTitle
import org.koin.androidx.compose.koinViewModel
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(onBack: () -> Unit) {
    val viewModel: HomeViewModel = koinViewModel()

    var playlistName by rememberSaveable { mutableStateOf("") }
    var videoUrlsInput by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf(false) }
    var urlsError by rememberSaveable { mutableStateOf(false) }

    val handleCreate = {
        nameError = playlistName.isBlank()

        val rawUrls = videoUrlsInput.lines()
            .map { it.trim() }
            .filter { it.startsWith("http", ignoreCase = true) }

        urlsError = rawUrls.isEmpty()

        if (!nameError && !urlsError) {

            val videos = rawUrls.map { url ->
                val extractedTitle = extractVideoTitleFromUrl(url)
                VideoHistoryItem(
                    videoUrl = url,
                    title = extractedTitle.ifEmpty { "Untitled Video" },
                    subtitle = null,
                    subtitleUrl = null,
                    lastPosition = 0L,
                    duration = 0L,
                    timestamp = System.currentTimeMillis()
                )
            }

            if (videos.isNotEmpty()) {
                viewModel.createNewPlaylist(playlistName, videos)
                onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create New Playlist") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = handleCreate,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Create Playlist") },
                expanded = playlistName.isNotEmpty() && videoUrlsInput.isNotEmpty()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Define a name and paste one video link per line below.",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Playlist Name Field
            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it; nameError = false },
                label = { Text("Playlist Name") },
                isError = nameError,
                supportingText = { if (nameError) Text("Name cannot be empty") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Video URLs Input Field
            OutlinedTextField(
                value = videoUrlsInput,
                onValueChange = { videoUrlsInput = it; urlsError = false },
                label = { Text("Video Links (One URL per line)") },
                placeholder = { Text("Paste video URLs here...") },
                isError = urlsError,
                supportingText = { if (urlsError) Text("Please enter at least one valid video link starting with 'http'.") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                minLines = 8,
                maxLines = 16,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private fun extractVideoTitleFromUrl(videoUrl: String): String {
    var title = ""
    try {
        val decodedUrl = URLDecoder.decode(videoUrl, StandardCharsets.UTF_8.toString())
        val uri = URI(decodedUrl)
        val path = uri.path

        if (path != null) {
            val filename = path.substring(path.lastIndexOf('/') + 1)
                .substringBeforeLast(".")
            title = formatFilenameToTitle(filename)
        }
    } catch (e: Exception) {
        val simpleName = videoUrl.substringAfterLast('/')
            .substringBeforeLast('.')
            .replace("%20", " ")
            .replace("%21", " ")
        if (simpleName.isNotEmpty()) {
            title = formatFilenameToTitle(simpleName)
        }
    }
    return title
}