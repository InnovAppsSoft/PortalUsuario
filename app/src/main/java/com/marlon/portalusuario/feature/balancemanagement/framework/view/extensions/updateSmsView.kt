package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity

fun FragmentCuentasBinding.updateSmsView(balance: MainBalanceDomainEntity) {
    balance.sms.mainSms?.let { sms ->
        llSmsLte.visibility = View.VISIBLE
        cardSms.visibility = View.VISIBLE
        messagesCountText.text = root.context.getString(R.string.sms_count_hint, sms)
        balance.sms.remainingDays?.let { days ->
            llSmsLte.visibility = View.VISIBLE
            smsAndMinRemainingDays.text = root.context.getString(R.string.remaining_days_hint, days)
        }
    } ?: run {
        cardSms.visibility = View.GONE
    }
}
