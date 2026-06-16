package com.marlon.portalusuario.feature.mobileservices.domain.usecases

import io.github.suitetecsa.sdk.android.model.SimCard

interface IUssdExecute {
    operator fun invoke(
        simCard: SimCard,
        ussdCode: String,
    )
}
