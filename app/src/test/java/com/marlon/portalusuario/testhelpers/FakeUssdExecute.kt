package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.feature.mobileservices.domain.usecases.IUssdExecute
import io.github.suitetecsa.sdk.android.model.SimCard

class FakeUssdExecute : IUssdExecute {
    private var lastSimCard: SimCard? = null
    private var lastCode: String? = null

    override fun invoke(
        simCard: SimCard,
        ussdCode: String,
    ) {
        lastSimCard = simCard
        lastCode = ussdCode
    }

    fun lastSimCard(): SimCard? = lastSimCard

    fun lastCode(): String? = lastCode
}
