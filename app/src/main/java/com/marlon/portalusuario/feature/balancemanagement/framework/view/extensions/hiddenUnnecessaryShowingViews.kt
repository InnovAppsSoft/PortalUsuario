package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import android.view.View
import com.marlon.portalusuario.databinding.FragmentCuentasBinding

fun FragmentCuentasBinding.hiddenUnnecessaryShowingViews() {
    if (cardData.visibility == View.GONE && cardVoice.visibility == View.GONE) {
        llDataVoice.visibility = View.GONE
    }

    if (cardDataLte.visibility == View.GONE && cardSms.visibility == View.GONE) {
        llSmsLte.visibility = View.GONE
    }

    if (cardDataDaily.visibility == View.GONE && cardDataLte.visibility == View.GONE) {
        llDataRemainingDays.visibility = View.GONE
    }
}