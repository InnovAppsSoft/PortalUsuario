@file:Suppress("TopLevelPropertyNaming")

package com.marlon.portalusuario.feature.profile

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

private const val API_29 = 29

data class PerfilUiState(
    val name: String = "",
    val number: String = "",
    val nauta: String = "",
    val userName: String = "Usuario",
    val profileImage: Bitmap? = null,
    val showMenu: Boolean = false,
)

@HiltViewModel
@Suppress("TooManyFunctions")
class PerfilViewModel
    @Inject
    constructor(
        application: Application,
    ) : AndroidViewModel(application) {
        private val prefs =
            application.getSharedPreferences("profile", Context.MODE_PRIVATE)
        private val imageSaver =
            ImageSaver(application)
                .setFileName("IMG.png")
                .setDirectoryName("PortalUsuario")

        private val _uiState = MutableStateFlow(PerfilUiState())
        val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

        init {
            loadProfile()
            loadImage()
        }

        private fun loadProfile() {
            val name = prefs.getString("nombre", "") ?: ""
            val number = prefs.getString("numero", "") ?: ""
            val nauta = prefs.getString("nauta", "") ?: ""
            val userName = prefs.getString("nombre", "Usuario") ?: "Usuario"
            _uiState.update {
                it.copy(
                    name = name,
                    number = number,
                    nauta = nauta,
                    userName = userName,
                )
            }
        }

        private fun loadImage() {
            viewModelScope.launch(Dispatchers.IO) {
                val bitmap =
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        imageSaver.setExternal(true).load()
                    } else {
                        imageSaver.load()
                    }
                _uiState.update { it.copy(profileImage = bitmap) }
            }
        }

        fun updateName(name: String) {
            _uiState.update { it.copy(name = name, userName = name) }
        }

        fun updateNumber(number: String) {
            _uiState.update { it.copy(number = number) }
        }

        fun updateNauta(nauta: String) {
            _uiState.update { it.copy(nauta = nauta) }
        }

        fun saveProfile() {
            viewModelScope.launch {
                val state = _uiState.value
                prefs
                    .edit {
                        putString("nombre", state.name)
                            .putString("numero", state.number)
                            .putString("nauta", state.nauta)
                    }
            }
        }

        fun onImageSelected(uri: Uri?) {
            if (uri == null) return
            viewModelScope.launch(Dispatchers.IO) {
                val bitmap = getBitmapFromUri(uri)
                if (bitmap != null) {
                    saveImageToStorage(bitmap)
                    _uiState.update { it.copy(profileImage = bitmap) }
                }
            }
        }

        fun deleteImage() {
            viewModelScope.launch(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    imageSaver.deleteFile()
                } else {
                    imageSaver.setExternal(true).deleteFile()
                }
                _uiState.update { it.copy(profileImage = null) }
            }
        }

        fun toggleMenu() {
            _uiState.update { it.copy(showMenu = !it.showMenu) }
        }

        private fun saveImageToStorage(bitmap: Bitmap) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                imageSaver.save(bitmap)
            } else {
                imageSaver.setExternal(true).save(bitmap)
            }
        }

        private fun getBitmapFromUri(uri: Uri): Bitmap? =
            try {
                val context = getApplication<Application>()
                if (Build.VERSION.SDK_INT < API_29) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
            } catch (e: IOException) {
                android.util.Log.e("PerfilViewModel", "Error loading bitmap from URI", e)
                null
            }
    }
