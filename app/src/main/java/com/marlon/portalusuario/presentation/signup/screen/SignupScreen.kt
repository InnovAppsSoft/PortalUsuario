package com.marlon.portalusuario.presentation.signup.screen

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
import com.marlon.portalusuario.presentation.resetpassword.screen.ConfirmCodeScreen
import com.marlon.portalusuario.presentation.resetpassword.screen.SetNewPasswordScreen
import com.marlon.portalusuario.presentation.signup.SignupEvent
import com.marlon.portalusuario.presentation.signup.SignupViewModel
import com.marlon.portalusuario.ui.components.ErrorDialog
import kotlinx.coroutines.launch

private const val TotalPages = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = viewModel.userCreated.value) {
        if (viewModel.userCreated.value) {
            navController.popBackStack()
            navController.navigate("auth")
        }
    }

    val pagerState = rememberPagerState { TotalPages }
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, pageSize = PageSize.Fill, userScrollEnabled = false) { page ->
            when (page) {
                0 -> RegisterUserScreen(state = viewModel.state.value, viewModel::onEvent)
                1 -> ConfirmCodeScreen(
                    viewModel.state.value.confirmCode,
                    !viewModel.state.value.isLoading,
                    { viewModel.onEvent(SignupEvent.OnChangedConfirmCode(it)) },
                    { viewModel.onEvent(SignupEvent.OnConfirmCode) }
                )
                2 -> SetNewPasswordScreen(
                    viewModel.state.value.password,
                    viewModel.state.value.confirmPassword,
                    viewModel.state.value.isLoading,
                    viewModel.state.value.isPasswordVisible,
                    { viewModel.onEvent(SignupEvent.OnChangedPassword(it)) },
                    { viewModel.onEvent(SignupEvent.OnChangedConfirmPassword(it)) },
                    { viewModel.onEvent(SignupEvent.OnTogglePasswordVisibility) },
                    { viewModel.onEvent(SignupEvent.OnCreateUser) },
                )
            }
        }
    }

    viewModel.state.value.error?.let {
        ErrorDialog(errorText = it) { viewModel.onEvent(SignupEvent.OnErrorDismiss) }
    }

    LaunchedEffect(key1 = viewModel.currentStep.value) {
        if (viewModel.currentStep.value != 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(viewModel.currentStep.value)
            }
        }
    }
}
