@file:Suppress("TopLevelPropertyNaming")

package com.marlon.portalusuario.feature.profile

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.LocalPhone
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.marlon.portalusuario.R
import kotlinx.coroutines.launch

private val PROFILE_IMAGE_SIZE = 100.dp
private val FIELD_HORIZONTAL_PADDING = 32.dp
private val BUTTON_COLOR = Color(0xFF1976D2)
private val DELETE_ICON_COLOR = Color(0xFFD32F2F)
private val BACKGROUND_COLOR = Color(0xFFF5F5F5)
private const val SDK_32 = 32

@Composable
@Suppress("LongMethod")
fun PerfilScreen(
    viewModel: PerfilViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val pickPhoto =
        rememberPickPhotoLauncher(
            onImageSelected = { viewModel.onImageSelected(it) },
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(BACKGROUND_COLOR),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        ProfileImageSection(
            profileImage = uiState.profileImage,
            onAddPhoto = { pickPhoto() },
            onDeletePhoto = { viewModel.deleteImage() },
            onToggleMenu = { viewModel.toggleMenu() },
            showMenu = uiState.showMenu,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.userName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(24.dp))

        PerfilFormFields(
            name = uiState.name,
            number = uiState.number,
            nauta = uiState.nauta,
            onNameChange = { viewModel.updateName(it) },
            onNumberChange = { viewModel.updateNumber(it) },
            onNautaChange = { viewModel.updateNauta(it) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.saveProfile()
                scope.launch { snackbarHostState.showSnackbar("Guardado con éxito") }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FIELD_HORIZONTAL_PADDING)
                    .height(48.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = BUTTON_COLOR,
                ),
        ) {
            Text(
                text = "Guardar",
                color = Color.White,
                fontSize = 13.sp,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun rememberPickPhotoLauncher(onImageSelected: (Uri?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onImageSelected(uri)
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                imagePickerLauncher.launch("image/*")
            }
        }

    return {
        if (Build.VERSION.SDK_INT < SDK_32) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    imagePickerLauncher.launch("image/*")
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        } else {
            imagePickerLauncher.launch("image/*")
        }
    }
}

@Composable
private fun PerfilFormFields(
    name: String,
    number: String,
    nauta: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onNautaChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Nombre") },
        leadingIcon = {
            Icon(Icons.Outlined.Person, contentDescription = "Nombre")
        },
        singleLine = true,
        maxLines = 1,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = FIELD_HORIZONTAL_PADDING),
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = number,
        onValueChange = onNumberChange,
        label = { Text("Número") },
        leadingIcon = {
            Icon(Icons.Outlined.LocalPhone, contentDescription = "Número")
        },
        singleLine = true,
        maxLines = 1,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = FIELD_HORIZONTAL_PADDING),
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = nauta,
        onValueChange = onNautaChange,
        label = { Text("Correo nauta") },
        leadingIcon = {
            Icon(Icons.Outlined.MarkEmailUnread, contentDescription = "Correo nauta")
        },
        singleLine = true,
        maxLines = 1,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = FIELD_HORIZONTAL_PADDING),
    )
}

@Composable
@Suppress("LongMethod")
private fun ProfileImageSection(
    profileImage: android.graphics.Bitmap?,
    onAddPhoto: () -> Unit,
    onDeletePhoto: () -> Unit,
    onToggleMenu: () -> Unit,
    showMenu: Boolean,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            if (profileImage != null) {
                Image(
                    bitmap = profileImage.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    modifier =
                        Modifier
                            .size(PROFILE_IMAGE_SIZE)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.portal),
                    contentDescription = "Foto de perfil por defecto",
                    modifier =
                        Modifier
                            .size(PROFILE_IMAGE_SIZE)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = onToggleMenu) {
                Icon(
                    Icons.Outlined.AddPhotoAlternate,
                    contentDescription = "Añadir foto",
                    tint = BUTTON_COLOR,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = onDeletePhoto) {
                Icon(
                    Icons.Outlined.DeleteForever,
                    contentDescription = "Eliminar foto",
                    tint = DELETE_ICON_COLOR,
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { onToggleMenu() },
        ) {
            DropdownMenuItem(
                text = { Text("Seleccionar imagen") },
                onClick = {
                    onToggleMenu()
                    onAddPhoto()
                },
            )
        }
    }
}
