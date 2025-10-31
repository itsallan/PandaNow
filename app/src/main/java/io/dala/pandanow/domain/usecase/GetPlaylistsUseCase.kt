package io.dala.pandanow.domain.usecase

import io.dala.pandanow.domain.models.Playlist
import io.dala.pandanow.domain.repository.PlaylistRepository

class GetPlaylistsUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(): List<Playlist> {
        return repository.getPlaylists()
    }
}