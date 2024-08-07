package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.databinding.ActivityDonacionBinding

private const val ALL_FIELDS_ARE_REQUIRED = "Todos los campos son obligatorios"
private const val REQUEST_CODE = 1000

class DonationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDonacionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.btn1.setOnClickListener {
            val phoneNumber = "54871663"
            val amount = binding.monto1.text.toString().trim { it <= ' ' }.ifBlank {
                Toast.makeText(this@DonationActivity, ALL_FIELDS_ARE_REQUIRED, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val password = binding.pass1.text.toString().trim { it <= ' ' }.ifBlank {
                Toast.makeText(this@DonationActivity, ALL_FIELDS_ARE_REQUIRED, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            launchCall(phoneNumber, amount, password)
        }

        binding.btn2.setOnClickListener {
            val phoneNumber = "58076608"
            val amount = binding.monto2.text.toString().trim { it <= ' ' }.ifBlank {
                Toast.makeText(this@DonationActivity, ALL_FIELDS_ARE_REQUIRED, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val password = binding.pass2.text.toString().trim { it <= ' ' }.ifBlank {
                Toast.makeText(this@DonationActivity, ALL_FIELDS_ARE_REQUIRED, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            launchCall(phoneNumber, amount, password)
        }

        binding.btn3.setOnClickListener {
            val phoneNumber = "54871663"
            val amount = binding.monto3.text.toString().trim { it <= ' ' }.ifBlank {
                Toast.makeText(this@DonationActivity, ALL_FIELDS_ARE_REQUIRED, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val password = binding.pass3.text.toString().trim { it <= ' ' }.ifBlank {
                Toast.makeText(this@DonationActivity, ALL_FIELDS_ARE_REQUIRED, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            launchCall(phoneNumber, amount, password)
        }
    }

    private fun launchCall(phoneNumber: String, amount: String, password: String) {
        val r = Intent()
            .setAction(Intent.ACTION_CALL)
            .setData(Uri.parse("tel:*234*1*$phoneNumber*$amount*$password%23"))
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CODE
            )
        } else {
            startActivity(r)
        }
    }
}
