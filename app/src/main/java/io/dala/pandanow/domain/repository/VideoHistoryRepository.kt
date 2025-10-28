package io.dala.pandanow.domain.repository

import io.dala.pandanow.domain.models.VideoHistoryItem

interface VideoHistoryRepository {
    suspend fun getSavedPosition(url: String): Long
    suspend fun savePosition(url: String, position: Long, duration: Long)
    suspend fun saveVideoToHistory(item: VideoHistoryItem)
    suspend fun getAllHistory(): List<VideoHistoryItem>
    suspend fun deleteHistoryItem(url: String)
    suspend fun clearAllHistory()
}