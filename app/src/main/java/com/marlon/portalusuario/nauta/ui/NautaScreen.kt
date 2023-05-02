package com.marlon.portalusuario.nauta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.nauta.core.INITIAL_USER
import com.marlon.portalusuario.nauta.data.network.Users
import com.marlon.portalusuario.nauta.domain.model.UserModel
import com.marlon.portalusuario.nauta.ui.components.CardUserManager

@Composable
fun NautaScreen(viewModel: NautaViewModel) {
    val users: Users by viewModel.users.observeAsState(initial = listOf())
    val currentUser: UserModel by viewModel.currentUser.observeAsState(
        initial = INITIAL_USER
    )
    val isUpdatingUserInformation: Boolean by viewModel.isUpdatingUserInfo.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val isLoggedIn: Boolean by viewModel.isLoggedIn.observeAsState(initial = false)
    val userConnected: String by viewModel.userConnected.observeAsState(initial = "")

    Column(modifier = Modifier
        .background(color = MaterialTheme.colors.background)
        .height(intrinsicSize = IntrinsicSize.Min)
        .verticalScroll(state = rememberScrollState())) {
        CardUserManager(
            users = users,
            selectedItem = currentUser,
            isLoading = isUpdatingUserInformation,
            isButtonsEnable = !isLoading && (!isLoggedIn || currentUser.username != userConnected),
            onItemSelected = { viewModel.onUserSelected(it) },
            onReloadUserInfo = { viewModel.updateUser() },
            onAddUser = { viewModel.onUserSelected(INITIAL_USER) },
            onEditUser = { },
            onDeleteUser = { viewModel.deleteUser() },
            modifier = Modifier.padding(16.dp)
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)){
            if (currentUser == INITIAL_USER) {
                AddUserDashboard(viewModel = viewModel)
            } else {
                CurrentUserDashboard(viewModel = viewModel)
            }
        }
    }
}