package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.commons.fullUserName
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.data.repository.Users

@Composable
fun CardUserManager(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isFoundErrors: Boolean = false,
    users: Users,
    selectedItem: User,
    onItemSelected: (User) -> Unit,
    onReloadUserInfo: (User) -> Unit,
    onAddUser: () -> Unit,
    onEditUser: (User) -> Unit,
    onDeleteUser: (User) -> Unit
) {
    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = isFoundErrors) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { onReloadUserInfo(selectedItem) }, enabled = users.isNotEmpty()) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_autorenew_24),
                    contentDescription = null
                )
            }
            if (users.isNotEmpty()) {
                Spinner(
                    items = users,
                    selectedItem = selectedItem,
                    onItemSelected = onItemSelected,
                    selectedItemFactory = { modifier, item ->
                        Row(
                            modifier = modifier
                                .padding(8.dp)
                                .wrapContentSize()
                        ) {
                            Text(text = item.userName)
                        }
                    },
                    dropdownItemFactory = { item, _ ->
                        Text(text = item.fullUserName())
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onAddUser() }, enabled = users.isNotEmpty()) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_person_add_alt_24),
                    contentDescription = null
                )
            }
            IconButton(onClick = { onEditUser(selectedItem) }, enabled = users.isNotEmpty()) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_mode_edit_outline_24),
                    contentDescription = null
                )
            }
            IconButton(onClick = { onDeleteUser(selectedItem) }, enabled = users.isNotEmpty()) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_delete_forever_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CardUserManagerPreview() {
    val user = User(0, "", "", NavigationType.INTERNATIONAL, 0L, "", "", "", "", "", "", "")
    val users = listOf<User>()
    MaterialTheme {
        CardUserManager(
            users = users,
            selectedItem = user,
            onItemSelected = {},
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            onReloadUserInfo = {}, onAddUser = {}, onEditUser = {}, onDeleteUser = {}
        )
    }
}