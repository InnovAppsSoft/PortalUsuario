package com.marlon.portalusuario.nauta.service

interface CountdownSubscriber {
    fun onTimeLeftChanged(timeLeftInMillis: Long)
    fun onTimerFinished()
}