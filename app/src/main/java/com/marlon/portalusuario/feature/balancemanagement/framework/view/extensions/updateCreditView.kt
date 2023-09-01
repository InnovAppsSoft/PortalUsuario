package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity

fun FragmentCuentasBinding.updateCreditView(balance: MainBalanceDomainEntity) {
    balance.credit?.let { credit ->
        llCredit.visibility = View.VISIBLE
        remainingCredit.text = root.context.getString(R.string.credit_placeholder, credit)
        simCardDueDate.text = balance.activeUntil
    }
}
