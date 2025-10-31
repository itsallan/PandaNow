package io.dala.pandanow.domain.usecase

import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.repository.PlaylistRepository

class GetPlaylistVideosUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String): List<VideoHistoryItem> {
        return repository.getPlaylistVideos(playlistId)
    }
}