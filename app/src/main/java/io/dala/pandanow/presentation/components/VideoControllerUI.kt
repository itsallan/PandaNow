package io.dala.pandanow.presentation.components


import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import io.dala.pandanow.presentation.components.phone.PhoneVideoControllerUI
import io.dala.pandanow.presentation.components.tv.TvVideoControllerUI

@Composable
fun VideoControllerUI(
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
    val isTV = LocalConfiguration.current.uiMode and Configuration.UI_MODE_TYPE_MASK ==
            Configuration.UI_MODE_TYPE_TELEVISION

    if (isTV) {
        TvVideoControllerUI(
            title = title,
            subTitle = subTitle,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            currentPosition = currentPosition,
            duration = duration,
            areControlsVisible = areControlsVisible,
            isInPipMode = isInPipMode,
            resizeMode = resizeMode,
            playbackSpeed = playbackSpeed,
            currentQuality = currentQuality,
            currentSubtitle = currentSubtitle,
            onPlayPause = onPlayPause,
            onSeekTo = onSeekTo,
            onSeekForward = onSeekForward,
            onSeekBackward = onSeekBackward,
            onToggleControls = onToggleControls,
            onBackPress = onBackPress,
            onChangeResizeMode = onChangeResizeMode,
            onEnterPip = onEnterPip,
            onRotate = onRotate,
            onSpeedChange = onSpeedChange,
            onQualityChange = onQualityChange,
            onSubtitleChange = onSubtitleChange,
        )
    } else {
        PhoneVideoControllerUI(
            title = title,
            subTitle = subTitle,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            currentPosition = currentPosition,
            duration = duration,
            areControlsVisible = areControlsVisible,
            isInPipMode = isInPipMode,
            resizeMode = resizeMode,
            playbackSpeed = playbackSpeed,
            currentQuality = currentQuality,
            currentSubtitle = currentSubtitle,
            onPlayPause = onPlayPause,
            onSeekTo = onSeekTo,
            onSeekForward = onSeekForward,
            onSeekBackward = onSeekBackward,
            onToggleControls = onToggleControls,
            onBackPress = onBackPress,
            onChangeResizeMode = onChangeResizeMode,
            onEnterPip = onEnterPip,
            onRotate = onRotate,
            onSpeedChange = onSpeedChange,
            onQualityChange = onQualityChange,
            onSubtitleChange = onSubtitleChange,
        )
    }
}

@SuppressLint("DefaultLocale")
fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    val hours = durationMs / (1000 * 60 * 60)
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
