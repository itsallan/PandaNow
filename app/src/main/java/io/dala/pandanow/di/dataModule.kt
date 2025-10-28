package io.dala.pandanow.di

import androidx.media3.common.util.UnstableApi
import com.google.gson.Gson
import io.dala.pandanow.data.repository.VideoHistoryRepositoryImpl
import io.dala.pandanow.data.source.CacheLocalDataSource
import io.dala.pandanow.data.source.HistoryDataSource
import io.dala.pandanow.data.source.HistoryLocalDataSource
import io.dala.pandanow.data.source.VideoCacheDataSource
import io.dala.pandanow.domain.repository.VideoHistoryRepository
import org.koin.dsl.module

@UnstableApi
val dataModule = module {
    single { Gson() }

    single<VideoCacheDataSource> { CacheLocalDataSource(context = get()) }

    single<HistoryDataSource> {
        HistoryLocalDataSource(
            context = get(),
            gson = get()
        )
    }

    single<VideoHistoryRepository> { VideoHistoryRepositoryImpl(localDataSource = get()) }
}