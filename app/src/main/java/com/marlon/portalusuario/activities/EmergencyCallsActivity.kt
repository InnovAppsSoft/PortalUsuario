package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.databinding.ActivityEmergencyCallsBinding

private const val REQUEST_CODE = 1000

class EmergencyCallsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmergencyCallsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEmergencyCallsBinding.inflate(
            layoutInflater
        )
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        binding.goBack.setOnClickListener { super@EmergencyCallsActivity.onBackPressed() }
        binding.ambulance.setOnClickListener { launchCall("104") }
        binding.police.setOnClickListener { launchCall("106") }
        binding.firefighters.setOnClickListener { launchCall("105") }
        binding.antiDrugs.setOnClickListener { launchCall("103") }
        binding.maritimeRescue.setOnClickListener { launchCall("107") }
        binding.cubacel.setOnClickListener { launchCall("52642266") }
    }

    private fun launchCall(ussd: String) {
        val r = Intent()
            .setAction(Intent.ACTION_CALL)
            .setData(Uri.parse("tel:$ussd"))
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CODE
            )
        } else {
            startActivity(r)
        }
    }
}
