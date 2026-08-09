package com.gemastik.hemora

import android.app.Application
import com.gemastik.hemora.core.di.AppContainer
import com.gemastik.hemora.core.di.DefaultAppContainer

class HemoraApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
