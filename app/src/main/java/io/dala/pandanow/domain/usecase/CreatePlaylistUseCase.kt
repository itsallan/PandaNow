package io.dala.pandanow.domain.usecase

import io.dala.pandanow.domain.models.Playlist
import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.repository.PlaylistRepository

class CreatePlaylistUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(name: String, videos: List<VideoHistoryItem>): Playlist {
        return repository.createPlaylist(name, videos)
    }
}