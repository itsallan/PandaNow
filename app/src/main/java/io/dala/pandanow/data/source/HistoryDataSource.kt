package io.dala.pandanow.data.source

import io.dala.pandanow.domain.models.VideoHistoryItem

interface HistoryDataSource {
    fun getSavedPosition(url: String): Long
    fun savePosition(url: String, position: Long)
    fun updateVideoProgress(url: String, lastPosition: Long, duration: Long)
    fun saveVideoToHistory(item: VideoHistoryItem)
    fun getAllHistory(): List<VideoHistoryItem>
    fun deleteHistoryItem(url: String)
    fun clearAllHistory()
}