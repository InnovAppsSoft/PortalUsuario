package com.marlon.portalusuario.feature.profile.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentProfileBinding
import com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions.observeIn
import java.io.IOException

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by activityViewModels()

    private lateinit var binding: FragmentProfileBinding

    /**
     * This method is called when the activity is created.
     * It sets up the layout and initializes the views.
     * It also handles the click events for selecting a profile picture,
     * saving the profile information, and deleting the profile.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.profileAddButton.setOnClickListener { launchImageSelectionActivity() }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                )
            }
        }

        viewModel.profileImageLiveData.observe(viewLifecycleOwner) { profileImage ->
            // If the image is successfully loaded, set it as the bitmap for the profileImage ImageView
            profileImage?.let { binding.profileImage.setImageBitmap(it) }
            // If the image cannot be loaded, set the default image resource
                    ?: binding.profileImage.setImageResource(R.drawable.portal)
        }

        viewModel.preferences.observeIn(viewLifecycleOwner) { preferences ->
            preferences?.let {
                binding.profileNameEditText.setText(it.name)
                binding.phoneNumberEditText.setText(it.phoneNumber)
                binding.nautaMailEditText.setText(it.mail)
                binding.profileCollapsinfTollbarLayout.title = it.name
            }
        }

        viewModel.successMessageLiveData.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.profileSaveButton, message, Snackbar.LENGTH_LONG).show()
        }

        binding.profileSaveButton.setOnClickListener {
            // Save profile information
            val name = binding.profileNameEditText.text.toString()
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            val email = binding.nautaMailEditText.text.toString()
            viewModel.saveProfileInformation(name, phoneNumber, email)
        }

        // Set onClickListener for the delete button
        binding.profileDeleteButton.setOnClickListener {
            // Delete the profile image
            viewModel.removeProfileImageAndSetDefaultResource()
            // Set the default image resource
            binding.profileImage.setImageResource(R.drawable.portal)
        }

        // Load the profile image
        viewModel.loadProfileImage()

        return view
    }

    /**
     * Selects a profile image.
     */
    private fun launchImageSelectionActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Request permission
                if (ContextCompat.checkSelfPermission(
                                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                    )
                } else {
                    // Permission granted, continue
                    imageSelectionContract.launch("image/*")
                }
            } else {
                // SDK version < VERSION_CODES.M, no permission required, continue
                imageSelectionContract.launch("image/*")
            }
        } else {
            // SDK version 32, no permission required, continue
            imageSelectionContract.launch("image/*")
        }
    }

    private var imageSelectionContract = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
    ) { result ->

        /**
         * Handle the result of an activity that returns a Uri.
         *
         * @param result The Uri returned by the activity.
         */
        // Check if the Uri is not null
        try {
            // Check if the device SDK version is less than 29
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // Get the bitmap from the Uri using the getContentResolver method
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, result)
                // Save the bitmap as "IMG.png" in the "PortalUsuario" directory
                viewModel.saveImage(bitmap)
                // Set the bitmap as the image resource of the profileImage ImageView
                binding.profileImage.setImageBitmap(bitmap)
            } else {
                // Create an ImageDecoder.Source from the Uri using the getContentResolver method
                val source = result?.let { ImageDecoder.createSource(requireContext().contentResolver, it) }
                // Decode the bitmap from the source
                val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                // Save the bitmap as "IMG.png" in the "PortalUsuario" directory
                bitmap?.let { viewModel.saveImage(it) }
                // Set the bitmap as the image resource of the profileImage ImageView
                binding.profileImage.setImageBitmap(bitmap)
            }
        } catch (e: IOException) {
            // Handle the IOException
            e.printStackTrace()
        }
    }

    private companion object {
        const val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 20
        const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 22
    }
}
