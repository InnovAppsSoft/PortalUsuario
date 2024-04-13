package com.marlon.portalusuario.activities

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.marlon.portalusuario.databinding.ActivityPrivateCallBinding
import io.github.suitetecsa.sdk.android.utils.extractShortNumber
import io.github.suitetecsa.sdk.android.utils.validateFormat

private const val REQUEST_CODE = 1000

class PrivateCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateCallBinding

    private val pickContactLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.also { uri ->
                    processSelectedContact(uri)?.also { phoneNumber ->
                        makePhoneCall(phoneNumber)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPrivateCallBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initialize()
    }

    private fun initialize() {
        if (checkSelfPermission(this, CALL_PHONE) == PERMISSION_DENIED) {
            requestPermissions(this, arrayOf(CALL_PHONE),
                REQUEST_CODE
            )
        } else {
            pickContactLauncher.launch(null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            pickContactLauncher.launch(null)
        }
    }
    private fun processSelectedContact(uri: Uri) =
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val phoneNumberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                getNumberToCall(cursor.getString(phoneNumberColumn))
            } else null
        }

    private fun makePhoneCall(numberToCall: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("#31#$numberToCall")
        }
        startActivity(intent)
        finish()
    }

    private fun getNumberToCall(phoneNumber: String) = validateFormat(phoneNumber)?.let {
        extractShortNumber(it)
    } ?: run {
        Toast.makeText(
            this,
            "No es un número de telefonia móvil de ETECSA",
            Toast.LENGTH_LONG
        ).show()
        null
    }
}
