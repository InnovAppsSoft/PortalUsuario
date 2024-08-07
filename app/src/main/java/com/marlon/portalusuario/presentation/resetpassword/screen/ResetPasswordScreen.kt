package com.marlon.portalusuario.presentation.resetpassword.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.marlon.portalusuario.presentation.resetpassword.ResetPasswordEvent
import com.marlon.portalusuario.presentation.resetpassword.ResetPasswordViewModel
import com.marlon.portalusuario.ui.components.ErrorDialog
import kotlinx.coroutines.launch

private const val TotalPages = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = viewModel.passwordRestored.value) {
        if (viewModel.passwordRestored.value) {
            navController.popBackStack()
            navController.navigate("auth")
        }
    }

    val pagerState = rememberPagerState { TotalPages }
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, pageSize = PageSize.Fill, userScrollEnabled = false) { position ->
            when (position) {
                0 -> ValidateUserScreen(viewModel.state.value, viewModel::onEvent)
                1 -> ConfirmCodeScreen(
                    viewModel.state.value.confirmCode,
                    !viewModel.state.value.isLoading,
                    { viewModel.onEvent(ResetPasswordEvent.OnChangeConfirmCode(it)) },
                    { viewModel.onEvent(ResetPasswordEvent.OnValidateConfirmCode) }
                )
                2 -> SetNewPasswordScreen(
                    viewModel.state.value.newPassword,
                    viewModel.state.value.confirmPassword,
                    viewModel.state.value.isLoading,
                    viewModel.state.value.isPasswordVisible,
                    { viewModel.onEvent(ResetPasswordEvent.OnChangeNewPassword(it)) },
                    { viewModel.onEvent(ResetPasswordEvent.OnChangeConfirmPassword(it)) },
                    { viewModel.onEvent(ResetPasswordEvent.OnTogglePasswordVisibility) },
                    { viewModel.onEvent(ResetPasswordEvent.OnResetPassword) },
                )
            }
        }
    }

    viewModel.state.value.error?.let {
        ErrorDialog(errorText = it) { viewModel.onEvent(ResetPasswordEvent.OnErrorDismiss) }
    }

    LaunchedEffect(key1 = viewModel.currentStep.value) {
        if (viewModel.currentStep.value != 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(viewModel.currentStep.value)
            }
        }
    }
}
