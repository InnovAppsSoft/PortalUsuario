package com.marlon.portalusuario.Inicio

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.R
import com.marlon.portalusuario.activities.MainActivity

private const val MIN_TIME = 1000L

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        // APP THEME
        when (settings.getString("keynoche", "")) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        setContent {
            PortalUsuarioTheme {
                SplashScreen {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.portal),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.portal_usuario),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = stringResource(id = R.string.sencillo_r_pido_y_cubano),
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        LaunchedEffect(Unit) {
            object : CountDownTimer(MIN_TIME, MIN_TIME) {
                override fun onTick(l: Long) {
                    // do nothing
                }

                override fun onFinish() {
                    onTimeout()
                }
            }.start()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PortalUsuarioTheme {
        SplashScreen()
    }
}
