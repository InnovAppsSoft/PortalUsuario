package com.marlon.portalusuario.presentation.mobileservices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.marlon.portalusuario.presentation.mobileservices.screen.MobileServicesScreen
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MobileServicesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { PortalUsuarioTheme { MobileServicesScreen() } }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }
}
