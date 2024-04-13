package com.marlon.portalusuario.activities

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
import android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.marlon.portalusuario.databinding.ActivityCallForReverseChargeBinding
import io.github.suitetecsa.sdk.android.utils.extractShortNumber
import io.github.suitetecsa.sdk.android.utils.validateFormat

private const val REQUEST_CODE = 1000

class CallForReverseChargeActivity : AppCompatActivity() {

    private val pickContactLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.also { uri ->
                    processSelectedContact(uri)?.also { phoneNumber ->
                        makePhoneCall(phoneNumber, recoverPrefix())
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityCallForReverseChargeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initialize()
    }

    private fun initialize() {
        if (checkSelfPermission(this, CALL_PHONE) == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(CALL_PHONE), REQUEST_CODE)
        } else {
            pickContactLauncher.launch(createIntent())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            pickContactLauncher.launch(createIntent())
        }
    }

    private fun createIntent() = Intent(Intent.ACTION_PICK).apply {
        setDataAndType(Uri.parse("content://contacts"), CONTENT_TYPE)
    }

    private fun processSelectedContact(uri: Uri) =
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val phoneNumberColumn = cursor.getColumnIndex(NUMBER)
                getNumberToCall(cursor.getString(phoneNumberColumn))
            } else {
                null
            }
        }

    private fun recoverPrefix() =
        getSharedPreferences("share_99", MODE_PRIVATE).getString("key99", "")


    private fun makePhoneCall(numberToCall: String, callPrefix: String?) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = getCallUri(callPrefix, numberToCall)
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

    private fun getCallUri(key99: String?, numberToCall: String): Uri {
        return when (key99) {
            null, "", "null", "1" -> Uri.parse("tel:*99$numberToCall")
            "2" -> Uri.parse("tel:*+99$numberToCall")
            "3" -> Uri.parse("tel:+539953$numberToCall")
            else -> Uri.parse("tel:*99$numberToCall")
        }
    }
}
