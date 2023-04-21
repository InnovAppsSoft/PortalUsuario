package com.marlon.portalusuario.nauta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ConnectivityFragment : Fragment() {
    private val nautaViewModel: NautaViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NautaScreen(viewModel = nautaViewModel)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nautaViewModel.onCreate()
    }

    @Preview(showSystemUi = true)
    @Composable
    fun NautaScreenPreview() {
        NautaScreen(viewModel = nautaViewModel)
    }
}