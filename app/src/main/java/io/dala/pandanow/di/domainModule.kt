package io.dala.pandanow.di

import io.dala.pandanow.domain.usecase.ClearAllVideoHistoryUseCase
import io.dala.pandanow.domain.usecase.GetAllVideoHistoryUseCase
import io.dala.pandanow.domain.usecase.GetSavedPositionUseCase
import io.dala.pandanow.domain.usecase.SaveCurrentPositionUseCase
import io.dala.pandanow.domain.usecase.SaveToHistoryUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetSavedPositionUseCase(repository = get()) }
    factory { SaveCurrentPositionUseCase(repository = get()) }
    factory { SaveToHistoryUseCase(repository = get()) }
    factory { GetAllVideoHistoryUseCase(repository = get()) }
    factory { ClearAllVideoHistoryUseCase(repository = get()) }
}