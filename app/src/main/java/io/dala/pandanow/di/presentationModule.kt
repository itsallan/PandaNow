package io.dala.pandanow.di

import androidx.media3.common.util.UnstableApi
import io.dala.pandanow.presentation.HomeViewModel
import io.dala.pandanow.presentation.VideoPlayerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@UnstableApi
val presentationModule = module {
    viewModel {
        VideoPlayerViewModel(
            application = androidApplication(),
            videoCacheDataSource = get(),
            getSavedPositionUseCase = get(),
            saveCurrentPositionUseCase = get(),
            saveToHistoryUseCase = get()
        )
    }
    viewModel {
        HomeViewModel(
            getAllVideoHistoryUseCase = get(),
            clearAllVideoHistoryUseCase = get(),
            saveToHistoryUseCase = get()
        )
    }
}