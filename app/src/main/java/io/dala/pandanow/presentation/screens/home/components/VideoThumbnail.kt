package io.dala.pandanow.presentation.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.presentation.utils.generateVideoThumbnail

@Composable
fun VideoThumbnail(
    video: VideoHistoryItem,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val thumbnailBitmap = remember(video.videoUrl, video.lastPosition) {
        generateVideoThumbnail(context, video.videoUrl, video.lastPosition)
    }

    if (thumbnailBitmap != null) {
        Image(
            bitmap = thumbnailBitmap.asImageBitmap(),
            contentDescription = video.title,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        // Fallback UI
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}