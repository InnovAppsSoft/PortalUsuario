package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.databinding.ActivitySmsBinding
import com.marlon.portalusuario.util.Utils.hasPermissions

private const val REQUEST_CALL = 1

class SmsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.plan.setOnClickListener { ussdCall("*133*2*1%23") }
        binding.plan20.setOnClickListener { ussdCall("*133*2*2%23") }
        binding.plan35.setOnClickListener { ussdCall("*133*2*3%23") }
        binding.plan45.setOnClickListener { ussdCall("*133*2*4%23") }
    }

    private fun ussdCall(ussd: String) {
        if (hasPermissions(Manifest.permission.CALL_PHONE)) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
        } else {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussd")))
        }
    }
}
