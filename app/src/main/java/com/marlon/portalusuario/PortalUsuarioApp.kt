package com.marlon.portalusuario

import android.app.Application
import com.arr.bugsend.utils.HandlerUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PortalUsuarioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(HandlerUtil(this))
    }
}
