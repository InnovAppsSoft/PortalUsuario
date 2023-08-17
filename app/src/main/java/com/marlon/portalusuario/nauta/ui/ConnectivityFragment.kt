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
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme
import javax.inject.Inject

class ConnectivityFragment @Inject constructor() : Fragment() {
    private val nautaViewModel: NautaViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext()).apply {
            setContent {
                SuitEtecsaTheme { NautaScreen(viewModel = nautaViewModel) }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nautaViewModel.recoverSession()
    }

    @Preview(showSystemUi = true)
    @Composable
    fun NautaScreenPreview() {
        NautaScreen(viewModel = nautaViewModel)
    }
}