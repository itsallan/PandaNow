package io.dala.pandanow

import android.app.Application
import io.dala.pandanow.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModules)
        }
    }
}