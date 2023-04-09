package com.marlon.portalusuario

import android.app.Application
import cu.suitetecsa.sdk.nauta.data.repository.DefaultNautaSession
import cu.suitetecsa.sdk.nauta.data.repository.JSoupNautaScrapper
import cu.suitetecsa.sdk.nauta.domain.service.NautaClient

class PortalUsuarioApplication : Application() {
    companion object {
        lateinit var sessionPref: Pref
        lateinit var client: NautaClient
    }

    override fun onCreate() {
        super.onCreate()
        val session = DefaultNautaSession()
        val scrapper = JSoupNautaScrapper(session)
        client = NautaClient(scrapper)
        sessionPref = Pref(applicationContext)
    }
}