package com.marlon.portalusuario.nauta.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import com.marlon.portalusuario.nauta.data.network.NautaService
import cu.suitetecsa.sdk.nauta.domain.util.timeStringToSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

class CountdownServiceImpl : Service(), CountdownService, CoroutineScope {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var nautaService: NautaService
    private var reservedTime by Delegates.notNull<Int>()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    private val subscribers: MutableList<CountdownSubscriber> = mutableListOf()

    override fun onBind(intent: Intent?): IBinder {
        return CountdownBinder()
    }

    override fun onCreate() {
        super.onCreate()
        job = SupervisorJob()
    }

    override fun startTimer() {
        launch {
            val timeLeft = getLeftTime()

            countDownTimer = object : CountDownTimer(timeLeft, 1000) {
                override fun onTick(p0: Long) {
                    notifyTimeLeft(p0)
                }

                override fun onFinish() {
                    notifyTimerFinished()
                    disconnect(5)
                }

            }.start()
        }
    }

    private suspend fun getLeftTime(): Long {
        return withContext(Dispatchers.IO){
            val remainingTime = timeStringToSeconds(getRemainingTime(5))
            println(remainingTime)

            if (reservedTime != 0) {
                (kotlin.math.abs(remainingTime) - kotlin.math.abs(reservedTime)).toLong() * 1000
            } else {
                kotlin.math.abs(remainingTime.toLong() * 1000)
            }
        }
    }

    private fun disconnect(retries: Int = 1) {
        launch {
            try {
                nautaService.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                if (retries > 0) {
                    val newRetries = retries - 1
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000)
                    }
                    disconnect(newRetries)
                }
            }
        }
    }

    private suspend fun getRemainingTime(retries: Int = 1): String {
        return withContext(Dispatchers.IO) {
            try {
                nautaService.getRemainingTime()
            } catch (e: Exception) {
                e.printStackTrace()
                if (retries > 0) {
                    val newRetries = retries - 1
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000)
                    }
                    getRemainingTime(newRetries)
                } else {
                    "00:00:00"
                }
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        stopTimer()
        super.onDestroy()
    }

    override fun subscribe(subscriber: CountdownSubscriber) {
        subscribers.add(subscriber)
    }

    override fun unsubscribe(subscriber: CountdownSubscriber) {
        subscribers.remove(subscriber)
    }

    override fun setParams(reservedTime: Int, nautaService: NautaService) {
        this.reservedTime = reservedTime
        this.nautaService = nautaService
    }

    private fun stopTimer() {
        countDownTimer.cancel()
    }

    private fun notifyTimeLeft(time: Long) {
        time.let {
            subscribers.forEach { subscriber ->
                subscriber.onTimeLeftChanged(it)
            }
        }
    }

    private fun notifyTimerFinished() {
        subscribers.forEach { subscriber ->
            subscriber.onTimerFinished()
        }
    }

    inner class CountdownBinder : Binder() {
        fun getService(): CountdownServiceImpl = this@CountdownServiceImpl
    }
}