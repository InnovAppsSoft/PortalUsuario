package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme
import com.marlon.portalusuario.nauta.data.network.Users
import com.marlon.portalusuario.nauta.domain.model.UserModel

@Composable
fun CardUserManager(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isFoundErrors: Boolean = false,
    isButtonsEnable: Boolean = true,
    users: Users,
    selectedItem: UserModel,
    onItemSelected: (UserModel) -> Unit,
    onReloadUserInfo: (UserModel) -> Unit,
    onAddUser: () -> Unit,
    onEditUser: (UserModel) -> Unit,
    onDeleteUser: (UserModel) -> Unit
) {
    val isEnabled = users.isNotEmpty() && isButtonsEnable
    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = isFoundErrors) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { onReloadUserInfo(selectedItem) }, enabled = isEnabled) {
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
                            Text(text = item.username.split("@")[0])
                        }
                    },
                    dropdownItemFactory = { item, _ ->
                        Text(text = item.username)
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onAddUser() }, enabled = isEnabled) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_person_add_alt_24),
                    contentDescription = null
                )
            }
            IconButton(onClick = { onEditUser(selectedItem) }, enabled = isEnabled) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_mode_edit_outline_24),
                    contentDescription = null
                )
            }
            IconButton(onClick = { onDeleteUser(selectedItem) }, enabled = isEnabled) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_delete_forever_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardUserManagerPreview() {
    val users = listOf<UserModel>()
    SuitEtecsaTheme {
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

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardUserManagerPreviewDark() {
    val users = listOf<UserModel>()
    SuitEtecsaTheme {
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