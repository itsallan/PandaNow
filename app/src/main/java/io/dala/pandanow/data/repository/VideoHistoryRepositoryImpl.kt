package io.dala.pandanow.data.repository

import io.dala.pandanow.data.source.HistoryDataSource
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.repository.VideoHistoryRepository

class VideoHistoryRepositoryImpl(
    private val localDataSource: HistoryDataSource
) : VideoHistoryRepository {

    override suspend fun getSavedPosition(url: String): Long {
        return localDataSource.getSavedPosition(url)
    }

    override suspend fun savePosition(url: String, position: Long, duration: Long) {
        localDataSource.savePosition(url, position)
        localDataSource.updateVideoProgress(url, position, duration)
    }

    override suspend fun saveVideoToHistory(item: VideoHistoryItem) {
        localDataSource.saveVideoToHistory(item)
    }

    override suspend fun getAllHistory(): List<VideoHistoryItem> {
        return localDataSource.getAllHistory()
    }

    override suspend fun deleteHistoryItem(url: String) {
        localDataSource.deleteHistoryItem(url)
    }

    override suspend fun clearAllHistory() {
        localDataSource.clearAllHistory()
    }
}