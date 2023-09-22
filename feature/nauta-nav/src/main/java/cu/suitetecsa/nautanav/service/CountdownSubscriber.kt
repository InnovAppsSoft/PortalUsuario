package cu.suitetecsa.nautanav.service

interface CountdownSubscriber {
    fun onTimeLeftChanged(timeLeftInMillis: Long)
    fun onTimerFinished()
}