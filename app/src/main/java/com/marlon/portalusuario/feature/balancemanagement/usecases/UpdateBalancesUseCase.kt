package com.marlon.portalusuario.feature.balancemanagement.usecases

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.marlon.portalusuario.feature.balancemanagement.data.repository.BalancesRepository
import com.marlon.portalusuario.feature.balancemanagement.domain.util.toDomain
import cu.suitetecsa.sdk.sim.model.SimCard
import cu.suitetecsa.sdk.ussd.uitls.parseBonusBalance
import cu.suitetecsa.sdk.ussd.uitls.parseDailyData
import cu.suitetecsa.sdk.ussd.uitls.parseMailData
import cu.suitetecsa.sdk.ussd.uitls.parseMainBalance
import cu.suitetecsa.sdk.ussd.uitls.parseMainData
import cu.suitetecsa.sdk.ussd.uitls.parseMainSms
import cu.suitetecsa.sdk.ussd.uitls.parseMainVoice
import kotlinx.coroutines.delay
import javax.inject.Inject

private const val DELAY_TIME = 3000L

class UpdateBalancesUseCase @Inject constructor(
    private val sendUssdRequestUseCase: SendUssdRequestUseCase,
    private val balancesRepository: BalancesRepository,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
    suspend operator fun invoke(simCard: SimCard, notifyActionRunning: (String) -> Unit) {
        notifyActionRunning("Consultando saldo...")
        val main = sendUssdRequestUseCase("*222#", simCard).parseMainBalance()

        if (main.data.data != null || main.data.dataLte != null) {
            delay(DELAY_TIME)
            notifyActionRunning("Consultando datos...")
            val dataResponse = sendUssdRequestUseCase("*222*328#", simCard)
            main.data = dataResponse.parseMainData()
            main.dailyData = dataResponse.parseDailyData()
            main.mailData = dataResponse.parseMailData()
        }

        main.voice.mainVoice?.let {
            delay(DELAY_TIME)
            notifyActionRunning("Consultando minutos...")
            main.voice = sendUssdRequestUseCase("*222*869#", simCard).parseMainVoice()
        }

        main.sms.mainSms?.let {
            delay(DELAY_TIME)
            notifyActionRunning("Consultando sms...")
            main.sms = sendUssdRequestUseCase("*222*767#", simCard).parseMainSms()
        }
        balancesRepository.saveMainBalance(main.toDomain(simCard.serialNumber))

        delay(DELAY_TIME)
        notifyActionRunning("Consultando bonos...")
        val bonus = sendUssdRequestUseCase("*222*266#", simCard).parseBonusBalance()

        balancesRepository.saveBonusBalance(bonus.toDomain(simCard.serialNumber))
    }
}