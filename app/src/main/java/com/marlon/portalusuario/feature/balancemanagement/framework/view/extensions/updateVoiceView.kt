package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity
import cu.suitetecsa.sdk.ussd.uitls.toTimeString

fun FragmentCuentasBinding.updateVoiceView(balance: MainBalanceDomainEntity) {
    balance.voice.mainVoice?.let { voice ->
        llDataVoice.visibility = View.VISIBLE
        cardVoice.visibility = View.VISIBLE
        remainingMins.text = voice.toTimeString()
        balance.voice.remainingDays?.let { days ->
            smsAndMinRemainingDays.text = root.context.getString(R.string.remaining_days_hint, days)
        }
    } ?: run {
        cardVoice.visibility = View.GONE
    }
}
