package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity
import cu.suitetecsa.sdk.ussd.uitls.toSizeString

fun FragmentCuentasBinding.updateDataMailView(balance: MainBalanceDomainEntity) {
    balance.mail.data?.let { dataMail ->
        cardDataMail.visibility = View.VISIBLE
        remainingMailData.text = dataMail.toSizeString()
        balance.mail.remainingDays?.let { days ->
            mailDataRemainingDays.text = root.context.getString(R.string.remaining_days_hint, days)
        }
    } ?: run {
        cardDataMail.visibility = View.GONE
    }
}
