package io.dala.pandanow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dala.pandanow.domain.models.Playlist
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.usecase.ClearAllVideoHistoryUseCase
import io.dala.pandanow.domain.usecase.CreatePlaylistUseCase
import io.dala.pandanow.domain.usecase.GetAllVideoHistoryUseCase
import io.dala.pandanow.domain.usecase.GetPlaylistsUseCase
import io.dala.pandanow.domain.usecase.SaveToHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllVideoHistoryUseCase: GetAllVideoHistoryUseCase,
    private val clearAllVideoHistoryUseCase: ClearAllVideoHistoryUseCase,
    private val saveToHistoryUseCase: SaveToHistoryUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase
) : ViewModel() {

    private val _videoHistory = MutableStateFlow<List<VideoHistoryItem>>(emptyList())
    val videoHistory: StateFlow<List<VideoHistoryItem>> = _videoHistory.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    init {
        loadHistory()
        loadPlaylists()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _videoHistory.value = getAllVideoHistoryUseCase()
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.value = getPlaylistsUseCase()
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

    fun createNewPlaylist(name: String, videos: List<VideoHistoryItem>) {
        viewModelScope.launch {
            createPlaylistUseCase(name, videos)
            loadPlaylists()
        }
    }
}