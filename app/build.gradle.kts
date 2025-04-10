plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization")
}

android {
    namespace = "io.dala.pandanow"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.dala.pandanow"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    // Media & Video
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    //Icons
    implementation(libs.composeIcons.tablerIcons)
    implementation(libs.androidx.material.icons.extended)
    // TV Support
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    //serialization
    implementation(libs.kotlinx.serialization.json)
    //Ads
    implementation(libs.play.services.ads)
    //InAppUpdate
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    //DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.process)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}