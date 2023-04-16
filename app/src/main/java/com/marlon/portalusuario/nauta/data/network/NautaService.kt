package com.marlon.portalusuario.nauta.data.network

import cu.suitetecsa.sdk.nauta.domain.model.NautaUser
import cu.suitetecsa.sdk.nauta.domain.service.NautaClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NautaService @Inject constructor(
    private val client: NautaClient
) {

    suspend fun getCaptcha(): ByteArray {
        return withContext(Dispatchers.IO) {
            client.captchaImage
        }
    }

    suspend fun login(userName: String, password: String, captchaCode: String): NautaUser {
        return withContext(Dispatchers.IO) {
            client.setCredentials(userName, password)
            client.login(captchaCode)
        }
    }

    suspend fun toUp(rechargeCode: String) {
        withContext(Dispatchers.IO) {
            client.toUpBalance(rechargeCode)
        }
    }

    suspend fun transfer(amount: Float, destinationAccount: String) {
        withContext(Dispatchers.IO) {
            client.transferBalance(amount,destinationAccount)
        }
    }

    suspend fun connect(userName: String, password: String) {
        withContext(Dispatchers.IO) {
            client.setCredentials(userName, password)
            client.connect()
        }
    }

    suspend fun getRemainingTime(): String {
        return withContext(Dispatchers.IO) {
            client.remainingTime
        }
    }

    suspend fun getDataSession(): Map<String, String> {
        return withContext(Dispatchers.IO) {
            client.dataSession
        }
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            client.disconnect()
        }
    }
}