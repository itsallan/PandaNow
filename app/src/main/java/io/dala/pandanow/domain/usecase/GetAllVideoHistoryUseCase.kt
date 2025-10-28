package io.dala.pandanow.domain.usecase

import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.repository.VideoHistoryRepository

class GetAllVideoHistoryUseCase(
    private val repository: VideoHistoryRepository
) {
    suspend operator fun invoke(): List<VideoHistoryItem> {
        return repository.getAllHistory()
    }
}