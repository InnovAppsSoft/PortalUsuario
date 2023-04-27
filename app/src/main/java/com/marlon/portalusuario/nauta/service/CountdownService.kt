package com.marlon.portalusuario.nauta.service

import com.marlon.portalusuario.nauta.data.network.NautaService

interface CountdownService {
    fun subscribe(subscriber: CountdownSubscriber)
    fun unsubscribe(subscriber: CountdownSubscriber)
    fun setParams(reservedTime: Int, nautaService: NautaService)
    fun startTimer()
}