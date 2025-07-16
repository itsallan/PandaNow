package io.dala.pandanow.utils

import io.dala.pandanow.data.VideoHistoryItem

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class VideoHistoryManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "VideoHistory", Context.MODE_PRIVATE
    )

    private val json = Json { ignoreUnknownKeys = true }

    fun saveVideoToHistory(video: VideoHistoryItem) {
        val history = getVideoHistory().toMutableList()
        history.removeIf { it.videoUrl == video.videoUrl }
        history.add(0, video)
        val limitedHistory = history.take(10)

        sharedPreferences.edit().apply {
            putString(VIDEO_HISTORY_KEY, json.encodeToString(limitedHistory))
            apply()
        }
    }

    fun getVideoHistory(): List<VideoHistoryItem> {
        val historyJson = sharedPreferences.getString(VIDEO_HISTORY_KEY, "[]") ?: "[]"
        return try {
            json.decodeFromString(historyJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun updateVideoProgress(videoUrl: String, position: Long, duration: Long) {
        val history = getVideoHistory().toMutableList()
        val videoIndex = history.indexOfFirst { it.videoUrl == videoUrl }

        if (videoIndex != -1) {
            val video = history[videoIndex]
            val updatedVideo = video.copy(
                lastPosition = position,
                duration = duration,
                timestamp = System.currentTimeMillis()
            )

            history[videoIndex] = updatedVideo

            sharedPreferences.edit().apply {
                putString(VIDEO_HISTORY_KEY, json.encodeToString(history))
                apply()
            }
        }
    }

    fun clearHistory() {
        sharedPreferences.edit().apply {
            remove(VIDEO_HISTORY_KEY)
            apply()
        }
    }

    companion object {
        private const val VIDEO_HISTORY_KEY = "video_history"

        @Volatile
        private var instance: VideoHistoryManager? = null

        fun getInstance(context: Context): VideoHistoryManager {
            return instance ?: synchronized(this) {
                instance ?: VideoHistoryManager(context.applicationContext).also { instance = it }
            }
        }
    }
}