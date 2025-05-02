package io.dala.pandanow

import android.app.Application
import com.google.android.gms.ads.MobileAds
import io.dala.pandanow.utils.ads.AppOpenAdManager

class MyApplication : Application() {
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        appOpenAdManager = AppOpenAdManager(
            adUnit = "ca-app-pub-6822790457668840/1493768818",
            application = this
        )
    }
}