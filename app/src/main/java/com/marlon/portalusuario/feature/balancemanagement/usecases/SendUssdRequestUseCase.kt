package com.marlon.portalusuario.feature.balancemanagement.usecases

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.sim.model.SimCard
import cu.suitetecsa.sdk.ussd.model.UssdResponse
import cu.suitetecsa.sdk.ussd.uitls.sendUssdRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SendUssdRequestUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
    suspend operator fun invoke(ussdCode: String, simCard: SimCard): UssdResponse =
        simCard.sendUssdRequest(context = context, ussdCode = ussdCode)
}
