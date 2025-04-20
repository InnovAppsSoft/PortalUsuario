package com.marlon.portalusuario.presentation.splash.screen

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marlon.portalusuario.R
import com.marlon.portalusuario.activities.MainActivity
import com.marlon.portalusuario.intro.IntroActivity
import com.marlon.portalusuario.presentation.splash.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MinTime = 1000L

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val preference by viewModel.pref.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
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

        LaunchedEffect(preference) {
            scope.launch {
                delay(MinTime)
                preference?.also {
                    if (!it.isIntroOpened) {
                        context.startActivity(Intent(context, IntroActivity::class.java))
                        (context as Activity).finish()
                    } else {
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as Activity).finish()
                    }
                }
            }
        }
    }
}
