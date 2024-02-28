package com.marlon.portalusuario.presentation.onboarding

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.marlon.portalusuario.R
import kotlinx.coroutines.launch

private const val Fraction = .5f
val permissions = mutableListOf(
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.CALL_PHONE,
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.CAMERA,
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
internal fun PermissionsScreen(
    onNavigateToMainScreen: () -> Unit
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)

    val pages = remember { mutableStateListOf<OnBoardingPage>() }
    LaunchedEffect(key1 = true) {
        pages.addAll(
            multiplePermissionsState.permissions.filter { it.status != PermissionStatus.Granted }.map {
                when (it.permission) {
                    Manifest.permission.READ_PHONE_STATE -> PhoneState
                    Manifest.permission.CALL_PHONE -> Call
                    Manifest.permission.READ_CONTACTS -> Contact
                    Manifest.permission.CAMERA -> Camera
                    else -> Finish
                }
            }
        )
        pages.add(Finish)
    }

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            userScrollEnabled = false
        ) { index ->
            PagerScreen(
                onBoardingPage = pages[index],
                onNavigateToNext = {
                    if (index != pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(index + 1) }
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .offset(y = -(16).dp)
                .fillMaxWidth(Fraction)
                .padding(8.dp)
                .align(Alignment.BottomCenter)
        ) {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = pagerState.currentPage == pages.size - 1
            ) {
                Button(
                    onClick = onNavigateToMainScreen,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = stringResource(id = R.string.finish))
                }
            }
        }
    }
}
