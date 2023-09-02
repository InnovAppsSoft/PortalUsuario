package com.marlon.portalusuario.feature.profile.presentation

import android.graphics.Bitmap
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.feature.profile.util.ImageSaver
import com.marlon.portalusuario.feature.profile.domain.data.repository.ProfilePreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
        private val repository: ProfilePreferencesRepository,
        private val imageSaver: ImageSaver
) : ViewModel() {
    val preferences = repository.getProfilePreferences().stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
    )

    private val _successMessageLiveData = MutableLiveData<String>()
    val successMessageLiveData: LiveData<String> = _successMessageLiveData

    private val _profileImageLiveData = MutableLiveData<Bitmap?>()
    val profileImageLiveData: LiveData<Bitmap?> = _profileImageLiveData

    fun saveProfileInformation(name: String, phoneNumber: String, email: String) {
        // Guardar la información del perfil utilizando los métodos correspondientes
        updateProfileName(name.trim())
        updateProfilePhoneNumber(phoneNumber.trim())
        updateProfileEmail(email.trim())

        // Mostrar mensaje de éxito en la vista
        _successMessageLiveData.value = "Guardado con éxito"
    }

    private fun updateProfileName(name: String) = viewModelScope.launch { repository.updateName(name) }
    private fun updateProfileEmail(email: String) = viewModelScope.launch { repository.updateEmail(email) }
    private fun updateProfilePhoneNumber(phoneNumber: String) = viewModelScope.launch {
        repository.updatePhone(phoneNumber)
    }

    /**
     * Loads the profile image from the specified directory and sets it to the profileImage ImageView.
     * If the image cannot be loaded, the default image resource is set.
     */
    fun loadProfileImage() {
        // Create a new instance of the ImageSaver class
        _profileImageLiveData.value = imageSaver
                .setExternal(Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                .setFileName("IMG.png")
                .setDirectoryName("PortalUsuario")
                .load()
    }

    /**
     * Deletes the profile image file and sets the default image resource.
     */
    fun removeProfileImageAndSetDefaultResource() {
        // Delete the profile image file using ImageSaver utility
        imageSaver.setExternal(Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                .setFileName("IMG.png")
                .setDirectoryName("PortalUsuario")
                .deleteFile()
    }

    /**
     * Saves the given bitmap image to the specified directory with the provided file name.
     *
     * @param bitmap    The bitmap image to be saved.
     */
    fun saveImage(bitmap: Bitmap){
        imageSaver.setExternal(Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                .setFileName("IMG.png")
                .setDirectoryName("PortalUsuario")
                .save(bitmap)
    }
}
