package com.marlon.portalusuario.nauta.data.network

import cu.suitetecsa.sdk.nauta.domain.model.NautaUser
import cu.suitetecsa.sdk.nauta.domain.service.NautaClient
import cu.suitetecsa.sdk.nauta.domain.util.PasswordGenerator
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

    suspend fun userInformation() = withContext(Dispatchers.IO) { client.userInformation }

    suspend fun toUp(rechargeCode: String) {
        withContext(Dispatchers.IO) {
            client.toUpBalance(rechargeCode)
        }
    }

    suspend fun transfer(amount: Float, destinationAccount: String) {
        withContext(Dispatchers.IO) {
            client.transferBalance(amount, destinationAccount)
        }
    }

    suspend fun payQuote(amount: Float) {
        withContext(Dispatchers.IO) {
            client.payNautaHome(amount)
        }
    }

    suspend fun changePassword(newPassword: String) {
        withContext(Dispatchers.IO) {
            client.changePassword(newPassword)
        }
    }

    suspend fun changeEmailPassword(oldPassword: String, newPassword: String) {
        withContext(Dispatchers.IO) {
            client.changeEmailPassword(oldPassword, newPassword)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            client.isLoggedIn
        }
    }

    suspend fun connect(userName: String, password: String) {
        withContext(Dispatchers.IO) {
            client.setCredentials(userName, password)
            client.connect()
        }
    }

    suspend fun getRemainingTime(onResult: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            onResult(client.remainingTime)
        }
    }

    suspend fun getRemainingTime(): String {
        return withContext(Dispatchers.IO) {
            client.remainingTime
        }
    }

    suspend fun getConnectInfo(userName: String, password: String, postGet: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            client.setCredentials(userName, password)
            val infoConnect = client.connectInformation
            postGet(
                "$${((infoConnect[" account_info "]!! as Map<*, *>)[" credit "]!! as String)}".replace(
                    ".",
                    ","
                )
            )
        }
    }

    fun getDataSession(): Map<String, String> {
        return client.dataSession
    }

    fun setDataSession(value: Map<String, String>) {
        client.dataSession = value
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            client.disconnect()
        }
    }

    fun generatePassword(): String {
        return PasswordGenerator().generatePassword(12)
    }
}