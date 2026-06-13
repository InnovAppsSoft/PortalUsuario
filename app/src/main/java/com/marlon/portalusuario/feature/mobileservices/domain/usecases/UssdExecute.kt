package com.marlon.portalusuario.feature.mobileservices.domain.usecases

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.suitetecsa.sdk.android.model.SimCard
import io.github.suitetecsa.sdk.android.utils.makeCall
import javax.inject.Inject

open class UssdExecute
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : IUssdExecute {
        @RequiresPermission(Manifest.permission.CALL_PHONE)
        override operator fun invoke(
            simCard: SimCard,
            ussdCode: String,
        ) {
            simCard.makeCall(context, ussdCode)
        }
    }
