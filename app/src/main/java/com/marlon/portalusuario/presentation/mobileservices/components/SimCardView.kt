package com.marlon.portalusuario.presentation.mobileservices.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cu.suitetecsa.nautanav.ui.components.PrettyCard
import io.github.suitetecsa.sdk.android.model.SimCard

@SuppressLint("MissingPermission", "HardwareIds")
@Composable
fun SimCardView(simCard: SimCard) {
    PrettyCard {
        Column {
            Text(text = "ID: ${simCard.telephony.subscriberId}")
            Text(text = "displayName: ${simCard.displayName}")
            Text(text = "phoneNumber: ${simCard.phoneNumber}")
            Text(text = "SIM Slot: ${simCard.slotIndex}")
            Text(text = "subscriptionId: ${simCard.subscriptionId}")
        }
    }
}
