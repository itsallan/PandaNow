package io.dala.pandanow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.usecase.ClearAllVideoHistoryUseCase
import io.dala.pandanow.domain.usecase.GetAllVideoHistoryUseCase
import io.dala.pandanow.domain.usecase.SaveToHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllVideoHistoryUseCase: GetAllVideoHistoryUseCase,
    private val clearAllVideoHistoryUseCase: ClearAllVideoHistoryUseCase,
    private val saveToHistoryUseCase: SaveToHistoryUseCase
) : ViewModel() {

    private val _videoHistory = MutableStateFlow<List<VideoHistoryItem>>(emptyList())
    val videoHistory: StateFlow<List<VideoHistoryItem>> = _videoHistory.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _videoHistory.value = getAllVideoHistoryUseCase()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearAllVideoHistoryUseCase()
            _videoHistory.value = emptyList()
        }
    }

    fun saveNewVideoToHistory(item: VideoHistoryItem) {
        viewModelScope.launch {
            saveToHistoryUseCase(item)
            loadHistory()
        }
    }
}