package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.databinding.ActivityVozBinding

private const val REQUEST_CALL = 1

class VozActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityVozBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.min5.setOnClickListener { ussdCall("*133*3*1%23") }
        binding.min10.setOnClickListener { ussdCall("*133*3*2%23") }
        binding.min15.setOnClickListener { ussdCall("*133*3*3%23") }
        binding.min25.setOnClickListener { ussdCall("*133*3*4%23") }
        binding.min40.setOnClickListener { ussdCall("*133*3*5%23") }

        binding.atras2.setOnClickListener { super@VozActivity.onBackPressed() }
    }

    private fun ussdCall(ussd: String) {
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
        } else {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussd")))
        }
    }
}
