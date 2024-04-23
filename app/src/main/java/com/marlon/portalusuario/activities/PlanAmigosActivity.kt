package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.databinding.DialogPlanAmigosBinding
import com.marlon.portalusuario.util.Utils.hasPermissions

private const val REQUEST_CALL = 1

class PlanAmigosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogPlanAmigosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activar.setOnClickListener { ussdCall("*133*4*1%23") }
        binding.adicionar.setOnClickListener { ussdCall("*133*4*2%23") }
        binding.consultar.setOnClickListener { ussdCall("*133*4*3%23") }
    }

    private fun ussdCall(ussd: String) {
        if (hasPermissions(Manifest.permission.CALL_PHONE)) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
        } else {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$ussd")))
        }
    }
}
