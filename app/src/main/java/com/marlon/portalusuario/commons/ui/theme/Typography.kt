package com.marlon.portalusuario.commons.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.R

private val Dosis = FontFamily(
    Font(R.font.dosis),
    Font(R.font.dosislight),
    Font(R.font.displaybold, FontWeight.W500),
    Font(R.font.dosisbold, FontWeight.W600)
)

val SuitEtecsaTypography = Typography(
    h4 = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W600,
        fontSize = 30.sp
    ),
    h5 = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = Dosis,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    )
)