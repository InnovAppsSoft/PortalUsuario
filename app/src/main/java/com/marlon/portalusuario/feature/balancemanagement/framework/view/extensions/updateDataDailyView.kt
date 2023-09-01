package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity
import cu.suitetecsa.sdk.ussd.uitls.toSizeString

fun FragmentCuentasBinding.updateDataDailyView(balance: MainBalanceDomainEntity) {
    balance.daily.data?.let { dataDaily ->
        cardDataDaily.visibility = View.VISIBLE
        remainingDailyData.text = dataDaily.toSizeString()
        balance.mail.remainingDays?.let { days ->
            dailyDataRemainingDays.text = root.context.getString(R.string.remaining_days_hint, days)
        }
    } ?: run {
        cardDataDaily.visibility = View.GONE
    }
}
