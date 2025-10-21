package io.dala.pandanow.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri

fun generateVideoThumbnail(
    context: Context,
    videoUrl: String,
    position: Long
): Bitmap? {
    return try {
        val mediaMetadataRetriever = MediaMetadataRetriever()

        if (videoUrl.startsWith("http")) {
            mediaMetadataRetriever.setDataSource(videoUrl, HashMap())
        } else {
            mediaMetadataRetriever.setDataSource(context, videoUrl.toUri())
        }
        mediaMetadataRetriever.getFrameAtTime(position * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}