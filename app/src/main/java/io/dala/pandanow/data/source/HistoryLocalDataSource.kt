package io.dala.pandanow.data.source

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.dala.pandanow.domain.models.VideoHistoryItem
import androidx.core.content.edit

class HistoryLocalDataSource(
    private val context: Context,
    private val gson: Gson
) : HistoryDataSource {

    private val positionPreferences: SharedPreferences = context.getSharedPreferences("VideoPositions", Context.MODE_PRIVATE)
    private val historyPreferences: SharedPreferences = context.getSharedPreferences("VideoHistory", Context.MODE_PRIVATE)
    private val HISTORY_KEY = "history_list"

    override fun getSavedPosition(url: String): Long {
        return positionPreferences.getLong(url, 0L)
    }

    override fun savePosition(url: String, position: Long) {
        positionPreferences.edit { putLong(url, position) }
    }

    override fun updateVideoProgress(url: String, lastPosition: Long, duration: Long) {
        val historyList = getAllHistory().toMutableList()
        val existingIndex = historyList.indexOfFirst { it.videoUrl == url }

        if (existingIndex != -1) {
            val updatedItem = historyList[existingIndex].copy(
                lastPosition = lastPosition,
                duration = duration,
                timestamp = System.currentTimeMillis()
            )
            historyList.removeAt(existingIndex)
            historyList.add(0, updatedItem)
            saveHistoryList(historyList)
        }
    }

    override fun saveVideoToHistory(item: VideoHistoryItem) {
        val historyList = getAllHistory().toMutableList()
        val existingIndex = historyList.indexOfFirst { it.videoUrl == item.videoUrl }

        if (existingIndex != -1) {
            historyList.removeAt(existingIndex)
        }

        historyList.add(0, item.copy(timestamp = System.currentTimeMillis()))
        saveHistoryList(historyList)
    }

    override fun getAllHistory(): List<VideoHistoryItem> {
        val json = historyPreferences.getString(HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<VideoHistoryItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun deleteHistoryItem(url: String) {
        val historyList = getAllHistory().toMutableList()
        historyList.removeAll { it.videoUrl == url }
        saveHistoryList(historyList)
    }

    override fun clearAllHistory() {
        historyPreferences.edit { remove(HISTORY_KEY) }
    }

    private fun saveHistoryList(list: List<VideoHistoryItem>) {
        val json = gson.toJson(list)
        historyPreferences.edit { putString(HISTORY_KEY, json) }
    }
}