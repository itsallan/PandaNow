package io.dala.pandanow.data.repository

import io.dala.pandanow.data.source.PlaylistDataSource
import io.dala.pandanow.domain.models.Playlist
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val localDataSource: PlaylistDataSource
) : PlaylistRepository {

    override suspend fun createPlaylist(name: String, videos: List<VideoHistoryItem>): Playlist = withContext(Dispatchers.IO) {
        localDataSource.createPlaylist(name, videos)
    }

    override suspend fun getPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        localDataSource.getPlaylists()
    }

    override suspend fun getPlaylistVideos(id: String): List<VideoHistoryItem> = withContext(Dispatchers.IO) {
        localDataSource.getPlaylistById(id)?.videos ?: emptyList()
    }

    override suspend fun deletePlaylist(id: String) = withContext(Dispatchers.IO) {
        localDataSource.deletePlaylist(id)
    }

    override suspend fun updateVideos(playlistId: String, newVideos: List<VideoHistoryItem>) = withContext(Dispatchers.IO) {
        localDataSource.updateVideos(playlistId, newVideos)
    }
}