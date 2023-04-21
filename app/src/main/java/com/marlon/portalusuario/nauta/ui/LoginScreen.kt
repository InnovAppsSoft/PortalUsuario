package com.marlon.portalusuario.nauta.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.data.repository.Users
import com.marlon.portalusuario.nauta.ui.components.CardUserManager

@Composable
fun NautaScreen(viewModel: NautaViewModel) {
    val initialUser = User(
        0,
        "",
        "",
        NavigationType.INTERNATIONAL,
        0L,
        ", ",
        "",
        "",
        "",
        "",
        "",
        ""
    )
    val users: Users by viewModel.users.observeAsState(initial = listOf())
    val currentUser: User by viewModel.currentUser.observeAsState(
        initial = initialUser
    )

    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
        CardUserManager(
            users = users,
            selectedItem = currentUser,
            onItemSelected = { viewModel.onUserSelected(it) },
            onReloadUserInfo = { viewModel.showCaptchaDialog(true) },
            onAddUser = { viewModel.onUserSelected(initialUser) },
            onEditUser = { },
            onDeleteUser = { viewModel.deleteUser(it) },
            modifier = Modifier.padding(16.dp)
        )
        Column(modifier = Modifier.weight(1f)){
            if (currentUser == initialUser) {
                AddUserDashboard(viewModel = viewModel)
            } else {
                CurrentUserDashboard(viewModel = viewModel)
            }
        }
    }
}