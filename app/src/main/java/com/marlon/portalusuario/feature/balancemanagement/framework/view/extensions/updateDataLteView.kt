package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity
import cu.suitetecsa.sdk.ussd.uitls.toSizeString

fun FragmentCuentasBinding.updateDataLteView(balance: MainBalanceDomainEntity) {
    balance.data.dataLte?.let { dataLte ->
        llSmsLte.visibility = View.VISIBLE
        cardDataLte.visibility = View.VISIBLE
        remainingLteData.text = dataLte.toSizeString()
        balance.data.remainingDays?.let { days ->
            llDataRemainingDays.visibility = View.VISIBLE
            dataRemainingDays.text = root.context.getString(R.string.remaining_days_hint, days)
        }
    } ?: run {
        cardDataLte.visibility = View.GONE
    }
}
