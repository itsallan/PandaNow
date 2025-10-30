package io.dala.pandanow.domain.usecase

import io.dala.pandanow.domain.models.VideoHistoryItem
import io.dala.pandanow.domain.repository.VideoHistoryRepository

class SaveToHistoryUseCase(
    private val repository: VideoHistoryRepository
) {
    suspend operator fun invoke(item: VideoHistoryItem) {
        repository.saveVideoToHistory(item)
    }
}