package com.marlon.portalusuario.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.DialogPlanAmigosBinding

private const val RequestCode = 1000

class PlanAmigosActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: DialogPlanAmigosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPlanAmigosBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        binding.activar.setOnClickListener(this)
        binding.adicionar.setOnClickListener(this)
        binding.consultar.setOnClickListener(this)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.activar -> { makeCall("*133*4*1%23") }
            R.id.adicionar -> { makeCall("*133*4*2%23") }
            R.id.consultar -> { makeCall("*133*4*3%23") }
        }
    }

    private fun makeCall(ussd: String) {
        val r = Intent()
        r.setAction(Intent.ACTION_CALL)
        r.setData(Uri.parse("tel:$ussd"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), RequestCode)
            } else {
                startActivity(r)
            }
        } else {
            startActivity(r)
        }
    }
}
