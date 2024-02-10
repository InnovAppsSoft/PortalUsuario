package com.marlon.portalusuario.presentation.onboarding

import android.Manifest
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.marlon.portalusuario.R

internal sealed class OnBoardingPage(
    @RawRes val animation: Int,
    val permission: String,
    @StringRes val title: Int,
    val description: String,
)

internal object Location : OnBoardingPage(
    R.raw.location,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    R.string.location,
    "Se utiliza para leer el estado del teléfeno y asi obtener las tarjetas SIM insertadas en el mismo"
)

internal object FineLocation : OnBoardingPage(
    R.raw.location,
    Manifest.permission.ACCESS_FINE_LOCATION,
    R.string.fine_location,
    "Se utiliza para leer el estado del teléfeno y asi obtener las tarjetas SIM insertadas en el mismo"
)

internal object PhoneState : OnBoardingPage(
    R.raw.call,
    Manifest.permission.READ_PHONE_STATE,
    R.string.read_phone_state,
    "Se utiliza para la gestión de los códigos USSD y llamadas"
)

internal object Call : OnBoardingPage(
    R.raw.call,
    Manifest.permission.CALL_PHONE,
    R.string.call_phone,
    "Se utiliza para la gestión de los códigos USSD y llamadas"
)

internal object Contact : OnBoardingPage(
    R.raw.contact,
    "",
    R.string.access_contacts,
    "Necesitamos el permiso de ubicacion para muchas cosas blablabla blablabla blablabla " +
        "blablabla blablabla blablabla blablabla blablabla blablabla blablabla blablabla " +
        "blablabla blablabla blablabla blablabla blablabla blablabla blablabla"
)

internal object Camera : OnBoardingPage(
    R.raw.camera,
    "",
    R.string.access_camera,
    "Necesitamos el permiso de ubicacion para muchas cosas blablabla blablabla " +
        "blablabla blablabla blablabla blablabla blablabla blablabla blablabla blablabla " +
        "blablabla blablabla blablabla blablabla blablabla blablabla blablabla blablabla"
)

internal object Finish : OnBoardingPage(
    R.raw.finish,
    "",
    R.string.well_done,
    "Eso es todo , ya puedes disfrutar de Portal Usuario"
)
