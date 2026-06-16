package com.marlon.portalusuario.testhelpers

import android.telephony.TelephonyManager
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.model.SimCard

class FakeSimCardCollector(
    private val simCards: List<SimCard> = emptyList(),
) : SimCardCollector {
    override fun collect(): List<SimCard> = simCards

    companion object {
        fun createSimCard(
            displayName: String = "Test SIM",
            phoneNumber: String = "12345678",
            slotIndex: Int = 0,
            subscriptionId: Int = 0,
        ): SimCard {
            val telephonyManager =
                TelephonyManager::class.java
                    .getDeclaredConstructor()
                    .apply { isAccessible = true }
                    .newInstance()
            return SimCard(
                displayName = displayName,
                phoneNumber = phoneNumber,
                slotIndex = slotIndex,
                subscriptionId = subscriptionId,
                telephony = telephonyManager,
            )
        }
    }
}
