package io.dala.pandanow.domain.repository

import io.dala.pandanow.domain.models.Playlist
import io.dala.pandanow.domain.models.VideoHistoryItem

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, videos: List<VideoHistoryItem>): Playlist
    suspend fun getPlaylists(): List<Playlist>
    suspend fun getPlaylistVideos(id: String): List<VideoHistoryItem>
    suspend fun deletePlaylist(id: String)
    suspend fun updateVideos(playlistId: String, newVideos: List<VideoHistoryItem>)
}