package cu.suitetecsa.nautanav.commons.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cu.suitetecsa.nauta_nav.R

private val Dosis = FontFamily(
    Font(R.font.displaybold),
    Font(R.font.displaybold),
    Font(R.font.displaybold, FontWeight.W500),
    Font(R.font.displaybold, FontWeight.W600)
)

val SuitEtecsaTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W600,
        fontSize = 30.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Dosis,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Dosis,
        fontSize = 14.sp
    )
)