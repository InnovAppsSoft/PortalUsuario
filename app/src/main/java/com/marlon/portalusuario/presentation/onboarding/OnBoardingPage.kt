package com.marlon.portalusuario.presentation.onboarding

import android.Manifest
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.marlon.portalusuario.R

internal sealed class OnBoardingPage(
    @RawRes val animation: Int,
    val permission: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
)

internal data object PhoneState : OnBoardingPage(
    R.raw.call,
    Manifest.permission.READ_PHONE_STATE,
    R.string.read_phone_state,
    R.string.message_for_read_phone_state
)

internal data object Call : OnBoardingPage(
    R.raw.call,
    Manifest.permission.CALL_PHONE,
    R.string.call_phone,
    R.string.message_for_call_phone
)

internal data object Contact : OnBoardingPage(
    R.raw.contact,
    Manifest.permission.READ_CONTACTS,
    R.string.access_contacts,
    R.string.message_for_read_contacts
)

internal data object Camera : OnBoardingPage(
    R.raw.camera,
    Manifest.permission.CAMERA,
    R.string.access_camera,
    R.string.message_for_camera
)

internal data object Finish : OnBoardingPage(
    R.raw.finish,
    "",
    R.string.well_done,
    R.string.message_well_done
)
