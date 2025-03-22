package com.marlon.portalusuario.intro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.marlon.portalusuario.Permisos.PermissionActivity
import com.marlon.portalusuario.R
import com.marlon.portalusuario.activities.AuthActivity
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScreenItem(
    val title: String,
    val description: String,
    val screenImg: Int
)

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {
    @Inject
    lateinit var appPreferencesManager: AppPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manageBatteryConsumption()

        setContent {
            IntroScreen {
                savePrefsData()
                startActivity(Intent(this, PermissionActivity::class.java))
                finish()
            }
        }
    }

    private fun manageBatteryConsumption() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            startActivity(
                Intent().apply {
                    action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = android.net.Uri.parse("package:$packageName")
                }
            )
        }
    }

    private fun savePrefsData() {
        lifecycleScope.launch {
            appPreferencesManager.updateIsIntroOpened(true)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val screens = listOf(
        ScreenItem("Seguro", "Garantizamos la seguridad de tus datos\nY un funcionamiento óptimo", R.drawable.img2),
        ScreenItem("Útil", "Portal Usuario gestiona y asiste\nSerá tu herramienta #1", R.drawable.img1),
        ScreenItem("Sencillo", "Interfaz amigable y agradable\nExperiencia de usuario única", R.drawable.img3)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            count = screens.size,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            ScreenContent(screenItem = screens[page])
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(16.dp)
        )

        if (pagerState.currentPage == screens.size - 1) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Siguiente")
            }
        }
    }
}

@Composable
fun ScreenContent(screenItem: ScreenItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = screenItem.screenImg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = screenItem.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = screenItem.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIntroScreen() {
    IntroScreen {}
}
