package com.marlon.portalusuario.perfil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PerfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MaterialTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                ) { padding ->
                    Surface(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        PerfilScreen(snackbarHostState = snackbarHostState)
                    }
                }
            }
        }
    }
}
