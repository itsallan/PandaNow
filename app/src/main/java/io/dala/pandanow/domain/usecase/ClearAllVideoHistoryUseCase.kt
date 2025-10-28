package io.dala.pandanow.domain.usecase

import io.dala.pandanow.domain.repository.VideoHistoryRepository

class ClearAllVideoHistoryUseCase(
    private val repository: VideoHistoryRepository
) {
    suspend operator fun invoke() {
        repository.clearAllHistory()
    }
}