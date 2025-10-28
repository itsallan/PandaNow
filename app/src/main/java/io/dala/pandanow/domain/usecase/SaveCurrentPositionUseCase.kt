package io.dala.pandanow.domain.usecase

import io.dala.pandanow.domain.repository.VideoHistoryRepository

class SaveCurrentPositionUseCase(
    private val repository: VideoHistoryRepository
) {
    suspend operator fun invoke(url: String, position: Long, duration: Long) {
        repository.savePosition(url, position, duration)
    }
}