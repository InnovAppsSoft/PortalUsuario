package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.databinding.ActivityPrivateCallBinding

private const val RequestCode = 1000
private const val IdealPhoneNumberLength = 8
private const val ContaminatedPhoneNumberLength = 14
private const val CanBeOptimizedPhoneNumberLength = 10
private const val UnionIndex2 = 2
private const val UnionIndex4 = 4
private const val UnionIndex12 = 12

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
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), RequestCode)
        } else {
            pickContactLauncher.launch(null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCode) {
            pickContactLauncher.launch(null)
        }
    }
    private fun processSelectedContact(uri: Uri): String? {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val phoneNumberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                return getNumberToCall(cursor.getString(phoneNumberColumn))
            }
        }
        return null
    }

    private fun makePhoneCall(numberToCall: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("#31#$numberToCall")
        }
        startActivity(intent)
        finish()
    }

    private fun getNumberToCall(phoneNumber: String): String? {
        val regex = Regex("[^0-9]")
        val phoneNumberToProcess = phoneNumber.replace(regex, "")
        return when {
            phoneNumberToProcess.length == IdealPhoneNumberLength &&
                phoneNumberToProcess[0] == '5' -> phoneNumberToProcess
            phoneNumberToProcess.length ==
                ContaminatedPhoneNumberLength && phoneNumberToProcess.startsWith("99535") &&
                phoneNumberToProcess.endsWith("99") -> phoneNumberToProcess.substring(UnionIndex4, UnionIndex12)
            itCanBeImproved(
                phoneNumberToProcess
            ) -> phoneNumberToProcess.substring(UnionIndex2, CanBeOptimizedPhoneNumberLength)
            phoneNumberToProcess.length < IdealPhoneNumberLength -> {
                Toast.makeText(
                    this,
                    "Cuidado...\nFaltan caracteres o su número seleccionado no es un número de telefonia móvil",
                    Toast.LENGTH_SHORT
                ).show()
                null
            }
            else -> {
                Toast.makeText(this, "Numero erroneo", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    private fun itCanBeImproved(union: String) = union.length == CanBeOptimizedPhoneNumberLength &&
        (union.startsWith("53") || union.startsWith("99")) &&
        union[UnionIndex2] == '5'
}
