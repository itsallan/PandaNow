package io.dala.pandanow.presentation.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.content.Context
import java.io.File

/**
 * Extracts a thumbnail from a video file at the specified path
 * @param context The application context
 * @param videoPath The path to the video file
 * @param frameTime The time position in microseconds where to extract the frame (default: 1000000 = 1 second)
 * @return A Bitmap containing the thumbnail, or null if extraction failed
 */
fun getVideoThumbnail(context: Context, videoFile: File, frameTime: Long = 1000000): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFile.absolutePath)
        val bitmap = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        retriever.release()
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}