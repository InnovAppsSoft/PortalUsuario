package cu.suitetecsa.nautanav.service

import cu.suitetecsa.nautanav.data.network.NautaService

interface CountdownService {
    fun subscribe(subscriber: CountdownSubscriber)
    fun unsubscribe(subscriber: CountdownSubscriber)
    fun setParams(reservedTime: Int, nautaService: NautaService)
    fun startTimer()
}