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
import com.marlon.portalusuario.databinding.ActivityCallForReverseChargeBinding

private const val RequestCode = 1000
private const val IdealPhoneNumberLength = 8
private const val ContaminatedPhoneNumberLength = 14
private const val CanBeOptimizedPhoneNumberLength = 10
private const val UnionIndex2 = 2
private const val UnionIndex4 = 4
private const val UnionIndex12 = 12

class CallForReverseChargeActivity : AppCompatActivity() {
    private var numberToCall = ""
    private val i = Intent()

    private val pickContactLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        if (uri != null) {
            processSelectedContact(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityCallForReverseChargeBinding.inflate(
            layoutInflater
        )
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

    private fun processSelectedContact(uri: Uri) {
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val phoneNumberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val phoneNumber = cursor.getString(phoneNumberColumn)
            launchError(phoneNumber)
            val union = extractDigits(phoneNumber)
            numberToCall = getNumberToCall(union)

            if (numberToCall.isNotEmpty()) {
                i.setAction(Intent.ACTION_CALL)
                val reverseChargePreferences = getSharedPreferences("share_99", MODE_PRIVATE)
                val key99 = reverseChargePreferences.getString("key99", "")
                i.setData(getCallUri(key99, numberToCall))
                startActivity(i)
            }
            cursor.close()
        }
        finish()
    }

    private fun extractDigits(phoneNumber: String): String {
        var union = ""
        for (element in phoneNumber) {
            if (Character.isDigit(element)) {
                union += element
            }
        }
        return union
    }

    private fun getNumberToCall(union: String): String {
        return when {
            union.length == IdealPhoneNumberLength && union[0] == '5' -> union
            union.length ==
                ContaminatedPhoneNumberLength && union.startsWith("99535") &&
                union.endsWith("99") -> union.substring(UnionIndex4, UnionIndex12)
            itCanBeImproved(union) -> union.substring(UnionIndex2, CanBeOptimizedPhoneNumberLength - 1)
            union.length < IdealPhoneNumberLength -> {
                launchError("Cuidado...\nFaltan caracteres o su número seleccionado no es un número de telefonia móvil")
                ""
            }
            else -> {
                launchError("Numero erroneo")
                ""
            }
        }
    }

    private fun itCanBeImproved(union: String) = union.length == CanBeOptimizedPhoneNumberLength &&
        (union.startsWith("53") || union.startsWith("99")) &&
        union[UnionIndex2] == '5'

    private fun getCallUri(key99: String?, numberToCall: String): Uri {
        return when (key99) {
            null, "", "null", "1" -> Uri.parse("tel:*99$numberToCall")
            "2" -> Uri.parse("tel:*+99$numberToCall")
            "3" -> Uri.parse("tel:+539953$numberToCall")
            else -> Uri.parse("tel:*99$numberToCall")
        }
    }

    private fun launchError(message: String) {
        Toast.makeText(this@CallForReverseChargeActivity, message, Toast.LENGTH_SHORT).show()
    }
}
