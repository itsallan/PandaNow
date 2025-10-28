package io.dala.pandanow.domain.usecase
import io.dala.pandanow.domain.repository.VideoHistoryRepository

class GetSavedPositionUseCase(
    private val repository: VideoHistoryRepository
) {
    suspend operator fun invoke(url: String): Long {
        return repository.getSavedPosition(url)
    }
}