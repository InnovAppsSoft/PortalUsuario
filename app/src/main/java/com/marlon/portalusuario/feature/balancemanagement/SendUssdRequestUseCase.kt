package com.marlon.portalusuario.feature.balancemanagement

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.marlon.portalusuario.feature.balancemanagement.data.repository.SimCardsRepository
import cu.suitetecsa.sdk.ussd.model.UssdResponse
import cu.suitetecsa.sdk.ussd.uitls.sendUssdRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SendUssdRequestUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val simCardsRepository: SimCardsRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(allOf = [Manifest.permission.CALL_PHONE])
    suspend operator fun invoke(ussdCode: String): UssdResponse =
        simCardsRepository.getCurrentSimCard().sendUssdRequest(context = context, ussdCode = ussdCode)
}