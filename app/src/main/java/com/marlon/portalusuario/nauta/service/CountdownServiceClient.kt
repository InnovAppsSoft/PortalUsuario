package com.marlon.portalusuario.nauta.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.marlon.portalusuario.nauta.data.network.NautaService
import kotlin.properties.Delegates

class CountdownServiceClient(private val context: Context) {
    private lateinit var countdownService: CountdownService
    private lateinit var nautaService: NautaService
    private var reservedTime by Delegates.notNull<Int>()
    private val isCountdownServiceBound = MutableLiveData<Boolean>()
    val isBound: LiveData<Boolean> = isCountdownServiceBound

    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as CountdownServiceImpl.CountdownBinder
            countdownService = binder.getService()
            countdownService.setParams(reservedTime, nautaService)
            countdownService.startTimer()
            isCountdownServiceBound.postValue(true)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isCountdownServiceBound.postValue(false)
        }
    }

    fun serviceConnect(): Boolean {
        val intent = Intent(context, CountdownServiceImpl::class.java)
        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun serviceSubscribe(subscriber: CountdownSubscriber) {
        countdownService.subscribe(subscriber)
    }

    fun serviceUnsubscribe(subscriber: CountdownSubscriber) {
        countdownService.unsubscribe(subscriber)
    }

    fun setParams(reservedTime: Int, nautaService: NautaService) {
        this.reservedTime = reservedTime
        this.nautaService = nautaService
    }

    fun serviceDisconnect() {
        isCountdownServiceBound.value?.let {
            if (it) {
                println("ME DESVINCULE")
                context.unbindService(serviceConnection)
            }
        }
    }
}