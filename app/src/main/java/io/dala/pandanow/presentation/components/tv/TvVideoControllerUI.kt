package io.dala.pandanow.presentation.components.tv

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.PhotoSizeSelectSmall
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowBack
import compose.icons.tablericons.Language
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.PlayerSkipBack
import compose.icons.tablericons.Speedboat
import compose.icons.tablericons.ZodiacAquarius
import io.dala.pandanow.presentation.components.ads.AdmobBanner
import io.dala.pandanow.presentation.components.formatDuration
import io.dala.pandanow.utils.getSpeedDescription
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun TvVideoControllerUI(
    title: String,
    subTitle: String?,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPosition: Long,
    duration: Long,
    areControlsVisible: Boolean,
    isInPipMode: Boolean,
    resizeMode: Int,
    playbackSpeed: Float,
    currentQuality: String,
    currentSubtitle: String,
    onPlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onToggleControls: () -> Unit,
    onBackPress: () -> Unit,
    onChangeResizeMode: () -> Unit,
    onEnterPip: () -> Unit,
    onRotate: () -> Unit,
    onSpeedChange: () -> Unit,
    onQualityChange: () -> Unit,
    onSubtitleChange: () -> Unit,
) {
    val backButtonFocus = remember { FocusRequester() }
    val subtitleFocus = remember { FocusRequester() }
    val qualityFocus = remember { FocusRequester() }
    val speedFocus = remember { FocusRequester() }
    val seekBackwardFocus = remember { FocusRequester() }
    val playPauseFocus = remember { FocusRequester() }
    val seekForwardFocus = remember { FocusRequester() }
    val pipFocus = remember { FocusRequester() }
    val rotateFocus = remember { FocusRequester() }
    val resizeModeFocus = remember { FocusRequester() }

    LaunchedEffect(areControlsVisible) {
        if (areControlsVisible && !isInPipMode) {
            try {
                delay(100)
                playPauseFocus.requestFocus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { if (!isInPipMode) onToggleControls() }
    ) {
        AnimatedVisibility(
            visible = areControlsVisible && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        var isBackButtonFocused by remember { mutableStateOf(false) }

                        IconButton(
                            onClick = onBackPress,
                            modifier = Modifier
                                .focusRequester(backButtonFocus)
                                .focusProperties {
                                    right = subtitleFocus
                                    down = seekBackwardFocus
                                }
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = if (isBackButtonFocused) 0.3f else 0.1f))
                                .size(40.dp)
                                .onFocusChanged { isBackButtonFocused = it.isFocused }
                        ) {
                            Icon(
                                imageVector = TablerIcons.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = title,
                                color = Color.White,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall
                            )
                            if (subTitle != null) {
                                Text(
                                    text = subTitle,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var isSubtitleFocused by remember { mutableStateOf(false) }
                        var isQualityFocused by remember { mutableStateOf(false) }
                        var isSpeedFocused by remember { mutableStateOf(false) }

                        TVControlButton(
                            icon = TablerIcons.Language,
                            label = currentSubtitle,
                            onClick = onSubtitleChange,
                            modifier = Modifier
                                .focusRequester(subtitleFocus)
                                .focusProperties {
                                    left = backButtonFocus
                                    right = qualityFocus
                                    down = seekBackwardFocus
                                }
                                .onFocusChanged { isSubtitleFocused = it.isFocused },
                            isFocused = isSubtitleFocused
                        )

                        TVControlButton(
                            icon = TablerIcons.ZodiacAquarius,
                            label = currentQuality,
                            onClick = onQualityChange,
                            modifier = Modifier
                                .focusRequester(qualityFocus)
                                .focusProperties {
                                    left = subtitleFocus
                                    right = speedFocus
                                    down = playPauseFocus
                                }
                                .onFocusChanged { isQualityFocused = it.isFocused },
                            isFocused = isQualityFocused
                        )

                        TVControlButton(
                            icon = TablerIcons.Speedboat,
                            label = getSpeedDescription(playbackSpeed),
                            onClick = onSpeedChange,
                            modifier = Modifier
                                .focusRequester(speedFocus)
                                .focusProperties {
                                    left = qualityFocus
                                    down = seekForwardFocus
                                }
                                .onFocusChanged { isSpeedFocused = it.isFocused },
                            isFocused = isSpeedFocused
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    var isSeekBackwardFocused by remember { mutableStateOf(false) }
                    var isPlayPauseFocused by remember { mutableStateOf(false) }
                    var isSeekForwardFocused by remember { mutableStateOf(false) }

                    TVRoundIconButton(
                        onClick = onSeekBackward,
                        modifier = Modifier
                            .focusRequester(seekBackwardFocus)
                            .focusProperties {
                                up = backButtonFocus
                                right = playPauseFocus
                                down = pipFocus
                            }
                            .onFocusChanged { isSeekBackwardFocused = it.isFocused },
                        isFocused = isSeekBackwardFocused
                    ) {
                        Icon(TablerIcons.PlayerSkipBack, contentDescription = "Seek Backward", tint = Color.White)
                    }

                    if (isBuffering) {
                        //CircularProgressIndicator(color = Color.White)
                    } else {
                        TVRoundIconButton(
                            onClick = onPlayPause,
                            modifier = Modifier
                                .focusRequester(playPauseFocus)
                                .focusProperties {
                                    up = qualityFocus
                                    left = seekBackwardFocus
                                    right = seekForwardFocus
                                    down = rotateFocus
                                }
                                .size(70.dp)
                                .onFocusChanged { isPlayPauseFocused = it.isFocused },
                            isFocused = isPlayPauseFocused
                        ) {
                            Icon(
                                if (isPlaying) TablerIcons.PlayerPause else TablerIcons.PlayerPlay,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    TVRoundIconButton(
                        onClick = onSeekForward,
                        modifier = Modifier
                            .focusRequester(seekForwardFocus)
                            .focusProperties {
                                up = speedFocus
                                left = playPauseFocus
                                down = resizeModeFocus
                            }
                            .onFocusChanged { isSeekForwardFocused = it.isFocused },
                        isFocused = isSeekForwardFocused
                    ) {
                        Icon(Icons.Default.Forward10, contentDescription = "Seek Forward", tint = Color.White)
                    }
                }

                // Bottom controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Slider(
                        value = currentPosition.toFloat(),
                        onValueChange = { onSeekTo(it.toLong()) },
                        valueRange = 0f..duration.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatDuration(currentPosition)} / ${formatDuration(duration)}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                        )

                        Box(
                            modifier = Modifier.weight(2f),
                            contentAlignment = Alignment.Center
                        ) {
                            AdmobBanner(
                                modifier = Modifier
                                    .heightIn(min = 50.dp, max = 60.dp)
                                    .fillMaxWidth(0.7f)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(22.dp),
                        ) {
                            var isPipFocused by remember { mutableStateOf(false) }
                            var isRotateFocused by remember { mutableStateOf(false) }
                            var isResizeModeFocused by remember { mutableStateOf(false) }

                            TVRoundIconButton(
                                onClick = onEnterPip,
                                modifier = Modifier
                                    .focusRequester(pipFocus)
                                    .focusProperties {
                                        up = seekBackwardFocus
                                        right = rotateFocus
                                    }
                                    .onFocusChanged { isPipFocused = it.isFocused },
                                isFocused = isPipFocused
                            ) {
                                Icon(Icons.Default.PictureInPicture, contentDescription = "Enter PIP Mode", tint = Color.White)
                            }

                            TVRoundIconButton(
                                onClick = onRotate,
                                modifier = Modifier
                                    .focusRequester(rotateFocus)
                                    .focusProperties {
                                        up = playPauseFocus
                                        left = pipFocus
                                        right = resizeModeFocus
                                    }
                                    .onFocusChanged { isRotateFocused = it.isFocused },
                                isFocused = isRotateFocused
                            ) {
                                Icon(Icons.Default.ScreenRotation, contentDescription = "Rotate Screen", tint = Color.White)
                            }

                            TVRoundIconButton(
                                onClick = onChangeResizeMode,
                                modifier = Modifier
                                    .focusRequester(resizeModeFocus)
                                    .focusProperties {
                                        up = seekForwardFocus
                                        left = rotateFocus
                                    }
                                    .onFocusChanged { isResizeModeFocused = it.isFocused },
                                isFocused = isResizeModeFocused
                            ) {
                                Icon(
                                    imageVector = when (resizeMode) {
                                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> Icons.Default.FitScreen
                                        AspectRatioFrameLayout.RESIZE_MODE_FILL -> Icons.Default.Fullscreen
                                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> Icons.Default.ZoomIn
                                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH -> Icons.Default.PhotoSizeSelectSmall
                                        AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT -> Icons.Default.PhotoSizeSelectLarge
                                        else -> Icons.Default.FitScreen
                                    },
                                    contentDescription = "Resize Mode",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}