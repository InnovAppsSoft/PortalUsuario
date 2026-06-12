package com.marlon.portalusuario.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.marlon.portalusuario.servicios.ServiciosScreen
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiciosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent { PortalUsuarioTheme { Surface { ServiciosScreen() } } }
        }
}
