package com.marlon.portalusuario.feature.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.R
import kotlinx.coroutines.launch

data class ScreenItem(
    val title: String,
    val description: String,
    val screenImg: Int,
)

@Composable
fun IntroScreen(onGetStarted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val screens =
        listOf(
            ScreenItem("Seguro", "Garantizamos la seguridad de tus datos\nY un funcionamiento óptimo", R.drawable.img2),
            ScreenItem("Útil", "Portal Usuario gestiona y asiste\nSerá tu herramienta #1", R.drawable.img1),
            ScreenItem("Sencillo", "Interfaz amigable y agradable\nExperiencia de usuario única", R.drawable.img3),
        )
    val pagerState = rememberPagerState(pageCount = { screens.size })

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) { page ->
            ScreenContent(screenItem = screens[page])
        }

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(screens.size) { index ->
                Box(
                    modifier =
                        Modifier
                            .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                },
                            ),
                )
            }
        }

        if (pagerState.currentPage == screens.size - 1) {
            Button(
                onClick = onGetStarted,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                shape = RoundedCornerShape(24.dp),
            ) {
                Text(text = "Continuar")
            }
        } else {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                shape = RoundedCornerShape(24.dp),
            ) {
                Text(text = "Siguiente")
            }
        }
    }
}

@Composable
fun ScreenContent(screenItem: ScreenItem) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = screenItem.screenImg),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = screenItem.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = screenItem.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIntroScreen() {
    IntroScreen {}
}
