package io.dala.pandanow.presentation.screens.player

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import android.util.Rational
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import io.dala.pandanow.presentation.VideoPlayerViewModel
import io.dala.pandanow.presentation.components.SelectionCard
import io.dala.pandanow.presentation.components.VideoControllerUI
import io.dala.pandanow.presentation.navigation.VideoPlayerRoute
import io.dala.pandanow.utils.SystemSettings
import io.dala.pandanow.utils.getQualityDescription
import kotlinx.coroutines.delay

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(UnstableApi::class)
@Composable
fun  VideoPlayerScreen(details: VideoPlayerRoute, navController: NavController) {
    val viewModel: VideoPlayerViewModel = viewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    SystemSettings.hideSystemUI(context.findActivity())

    val player by viewModel.player.collectAsStateWithLifecycle()
    val isInPipMode by viewModel.isInPipMode.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val areControlsVisible by viewModel.areControlsVisible.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val currentQuality by viewModel.currentVideoQuality.collectAsState()
    val currentSubtitle by viewModel.currentSubtitle.collectAsState()
    var showQualityOptions by remember { mutableStateOf(false) }
    var showSubtitleOptions by remember { mutableStateOf(false) }
    val currentQualityDescription = getQualityDescription(currentQuality?.first ?: "Auto")

    var showSpeedOptions by remember { mutableStateOf(false) }
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }


    val currentSubtitleText by remember { mutableStateOf(details.subtitle) }

    // Set default orientation to landscape
    LaunchedEffect(Unit) {
        context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    LaunchedEffect(areControlsVisible) {
        if (!areControlsVisible) {
            showSpeedOptions = false
            showSubtitleOptions = false
            showQualityOptions = false
        }
    }

    LaunchedEffect(details.videoUrl) {
        viewModel.setMediaItem(details.videoUrl, details.subtitleUrl)
        viewModel.saveToHistory(details.title, details.subtitle, details.subtitleUrl)
    }

    LaunchedEffect(player, isPlaying) {
        while (isPlaying && player != null) {
            viewModel.saveCurrentPosition()
            delay(30000) // Update every 30 seconds
        }
    }
    // Handle lifecycle events
    DisposableEffect(lifecycleOwner) {
        viewModel.saveCurrentPosition()
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (context.findActivity()?.isInPictureInPictureMode == false) {
                        viewModel.pauseVideo()
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (context.findActivity()?.isInPictureInPictureMode == false) {
                        viewModel.setInPipMode(false)
                        viewModel.showControls()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            SystemSettings.showSystemUI(context.findActivity())
        }
    }

    // PiP controls
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DisposableEffect(isInPipMode, isPlaying) {

            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()

            context.findActivity()?.setPictureInPictureParams(params)
            onDispose { }
        }
    }

    LaunchedEffect(player) {
        player?.let {
            while (true) {
                currentPosition = it.currentPosition
                duration = it.duration.coerceAtLeast(0)
                delay(100)
            }
        }
    }

    LaunchedEffect(player, isPlaying) {
        while (isPlaying && player != null) {
            delay(30000) // Every 30 seconds
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = !isInPipMode) {
                viewModel.toggleControlsVisibility()
                if (!areControlsVisible) {
                    showSpeedOptions = false
                    showSubtitleOptions = false
                    showQualityOptions = false

                }
            }
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    useController = false
                    keepScreenOn = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    this.player = player
                }
            },
            update = { view ->
                view.resizeMode = resizeMode
                view.player = player
            },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = areControlsVisible && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VideoControllerUI(
                title = details.title,
                subTitle = currentSubtitleText,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                currentPosition = currentPosition,
                duration = duration,
                areControlsVisible = areControlsVisible,
                isInPipMode = isInPipMode,
                resizeMode = resizeMode,
                playbackSpeed = playbackSpeed,
                onPlayPause = { viewModel.togglePlayPause() },
                onSeekTo = { newPosition -> player?.seekTo(newPosition) },
                onSeekForward = { player?.seekForward() },
                currentQuality = currentQualityDescription,
                currentSubtitle = currentSubtitle?.first ?: "None",
                onSeekBackward = { player?.seekBack() },
                onToggleControls = { viewModel.toggleControlsVisibility() },
                onBackPress = { navController.popBackStack() },
                onChangeResizeMode = {
                    resizeMode = when (resizeMode) {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                        AspectRatioFrameLayout.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                onEnterPip = {
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .build()
                    context.findActivity()?.enterPictureInPictureMode(params)
                    viewModel.setInPipMode(true)
                },
                onRotate = {
                    context.findActivity()?.requestedOrientation =
                        if (context.findActivity()?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                },
                onSpeedChange = {
                    showSpeedOptions = !showSpeedOptions
                    showQualityOptions = false
                    showSubtitleOptions = false
                },
                onQualityChange = {
                    showQualityOptions = !showQualityOptions
                    showSpeedOptions = false
                    showSubtitleOptions = false
                },
                onSubtitleChange = {
                    showSubtitleOptions = !showSubtitleOptions
                    showSpeedOptions = false
                    showQualityOptions = false
                }
            )
        }
        // Speed control card
        AnimatedVisibility(
            visible = showSpeedOptions,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SelectionCard(
                title = "Playback Speed",
                options = listOf(
                    0.25f to "Very Slow",
                    0.5f to "Slow",
                    0.75f to "Slightly Slow",
                    1f to "Normal",
                    1.25f to "Slightly Fast",
                    1.5f to "Fast",
                    1.75f to "Very Fast",
                    2f to "Double Speed"
                ),
                currentSelection = playbackSpeed,
                onOptionSelected = { speed -> viewModel.setPlaybackSpeed(speed) },
                onDismiss = { showSpeedOptions = false }
            )
        }

        // Quality selection card
        AnimatedVisibility(
            visible = showQualityOptions,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SelectionCard(
                title = "Video Quality",
                options = viewModel.availableVideoQualities.value.map {
                    it.first to getQualityDescription(it.first)
                },
                currentSelection = currentQuality?.first ?: "Auto",
                onOptionSelected = { quality ->
                    viewModel.setVideoQuality(viewModel.availableVideoQualities.value.first { it.first == quality })
                },
                onDismiss = { showQualityOptions = false }
            )
        }

        // Subtitle selection card
        AnimatedVisibility(
            visible = showSubtitleOptions,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SelectionCard(
                title = "Subtitles",
                options = viewModel.availableSubtitles.value.map { it.first to it.first },
                currentSelection = currentSubtitle?.first ?: "None",
                onOptionSelected = { subtitle ->
                    viewModel.setSubtitle(viewModel.availableSubtitles.value.first { it.first == subtitle })
                },
                onDismiss = { showSubtitleOptions = false }
            )
        }

//        if (isBuffering) {
//            CircularProgressIndicator(
//                color = Color.White,
//                modifier = Modifier
//                    .align(Alignment.Center),
//            )
//        }

        errorMessage?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearError()
                            viewModel.retryPlayback()
                        }
                    ) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

class PipReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
    }

    @OptIn(UnstableApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                val viewModel = VideoPlayerViewModel.getInstance(context.applicationContext as Application)
                viewModel.togglePlayPause()
            }
        }
    }
}