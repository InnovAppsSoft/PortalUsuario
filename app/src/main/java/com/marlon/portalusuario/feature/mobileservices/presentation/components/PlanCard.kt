package com.marlon.portalusuario.feature.mobileservices.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.core.components.ArcProgressbar
import com.marlon.portalusuario.core.components.PrettyCard
import com.marlon.portalusuario.core.theme.BrightOrange
import com.marlon.portalusuario.core.theme.VibrantGreen
import com.marlon.portalusuario.core.theme.VividRed

@Composable
fun PlanCard(
    modifier: Modifier = Modifier,
    planTitle: String = "Data",
    dataCount: String = "3.50 GB + 4.50 GB",
    remainingDays: Int? = null,
    isDailyData: Boolean = false,
) {
    val color = progressColor(remainingDays, 30)

    PrettyCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = planTitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.padding(horizontal = 68.dp))
            ArcProgressbar(
                canvasSize = 100.dp,
                indicatorValue = remainingDays ?: 0,
                maxIndicatorValue = 30,
                smallTextFontSize = 12.sp,
                bigTextFontSize = 14.sp,
                bigTextSuffix = if (!isDailyData) "D" else "H",
                backgroundIndicatorStrokeWidth = 30f,
                foregroundIndicatorStrokeWidth = 30f,
                foregroundIndicatorColor = color,
            )
            Text(text = dataCount)
        }
    }
}

private fun progressColor(remainingDays: Int?, maxDays: Int): Color {
    val ratio = if (remainingDays != null && maxDays > 0) remainingDays.toFloat() / maxDays else 1f
    return when {
        ratio > 0.5f -> VibrantGreen
        ratio > 0.16f -> BrightOrange
        else -> VividRed
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanCardPreview() {
    PlanCard(modifier = Modifier.padding(16.dp))
}
