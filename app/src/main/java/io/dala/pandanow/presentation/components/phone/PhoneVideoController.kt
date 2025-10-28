package io.dala.pandanow.presentation.components.phone

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.PhotoSizeSelectSmall
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import io.dala.pandanow.presentation.components.formatDuration
import io.dala.pandanow.presentation.utils.getSpeedDescription

@OptIn(UnstableApi::class)
@Composable
fun PhoneVideoControllerUI(
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
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

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
                // Top controls (back button, video title, and control buttons)
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
                    if (isPortrait) {
                        Column {
                            // Back button and title
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = onBackPress,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
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

                            // Control buttons below title in portrait
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ControlButton(
                                    icon = Icons.Default.ClosedCaption,
                                    label = currentSubtitle,
                                    onClick = onSubtitleChange
                                )
                                ControlButton(
                                    icon = Icons.Default.HighQuality,
                                    label = currentQuality,
                                    onClick = onQualityChange
                                )
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onBackPress,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
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
                        // Control buttons at top right
                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ControlButton(
                                icon = Icons.Default.ClosedCaption,
                                label = currentSubtitle,
                                onClick = onSubtitleChange
                            )
                            ControlButton(
                                icon = Icons.Default.HighQuality,
                                label = currentQuality,
                                onClick = onQualityChange
                            )
                            ControlButton(
                                icon = Icons.Default.Speed,
                                label = getSpeedDescription(playbackSpeed),
                                onClick = onSpeedChange
                            )
                        }
                    }
                }

                // Center controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoundIconButton(onClick = onSeekBackward) {
                        Icon(
                            Icons.Default.Replay10,
                            contentDescription = "Seek Backward",
                            tint = Color.White
                        )
                    }
                    if (isBuffering) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        RoundIconButton(
                            onClick = onPlayPause,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    RoundIconButton(onClick = onSeekForward) {
                        Icon(
                            Icons.Default.Forward10,
                            contentDescription = "Seek Forward",
                            tint = Color.White
                        )
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(22.dp),
                        ) {
                            RoundIconButton(onClick = onEnterPip) {
                                Icon(
                                    Icons.Default.PictureInPicture,
                                    contentDescription = "Enter PIP Mode",
                                    tint = Color.White
                                )
                            }
                            RoundIconButton(onClick = onRotate) {
                                Icon(
                                    Icons.Default.ScreenRotation,
                                    contentDescription = "Rotate Screen",
                                    tint = Color.White
                                )
                            }
                            RoundIconButton(onClick = onChangeResizeMode) {
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