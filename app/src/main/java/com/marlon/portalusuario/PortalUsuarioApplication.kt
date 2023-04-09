package com.marlon.portalusuario

import android.app.Application

class PortalUsuarioApplication : Application() {

    companion object {
        lateinit var sessionPref: Pref
    }

    override fun onCreate() {
        super.onCreate()
        sessionPref = Pref(applicationContext)
    }
}