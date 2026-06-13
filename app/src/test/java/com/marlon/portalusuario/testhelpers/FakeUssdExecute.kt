package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.feature.mobileservices.domain.usecases.IUssdExecute
import io.github.suitetecsa.sdk.android.model.SimCard

class FakeUssdExecute : IUssdExecute {
    private var _lastSimCard: SimCard? = null
    private var _lastCode: String? = null

    override fun invoke(simCard: SimCard, ussdCode: String) {
        _lastSimCard = simCard
        _lastCode = ussdCode
    }

    fun lastSimCard(): SimCard? = _lastSimCard
    fun lastCode(): String? = _lastCode
}
