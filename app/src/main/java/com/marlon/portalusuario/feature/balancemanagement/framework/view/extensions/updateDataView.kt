package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity
import cu.suitetecsa.sdk.ussd.uitls.toSizeString

fun FragmentCuentasBinding.updateDataView(balance: MainBalanceDomainEntity) {
    balance.data.data?.let { data ->
        llDataVoice.visibility = View.VISIBLE
        cardData.visibility = View.VISIBLE
        remainingData.text = data.toSizeString()
        balance.data.remainingDays?.let { days ->
            llDataRemainingDays.visibility = View.VISIBLE
            dataRemainingDays.text = root.context.getString(R.string.remaining_days_hint, days)
        }
    } ?: run {
        cardData.visibility = View.GONE
    }
}
