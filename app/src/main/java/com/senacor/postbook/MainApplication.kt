package com.senacor.postbook

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Thread.setDefaultUncaughtExceptionHandler { _, throwable -> Timber.e(throwable) }
    }
}