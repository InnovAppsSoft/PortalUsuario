package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.ActivityPermissionBinding
import moe.feng.common.stepperview.IStepperAdapter
import moe.feng.common.stepperview.VerticalStepperItemView

private const val ResultCall = 1001

class PermissionActivity : AppCompatActivity(), IStepperAdapter {
    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.verticalStepperView.stepperAdapter = this
    }

    override fun getTitle(index: Int): CharSequence {
        return when (index) {
            0 -> "Permiso para realizar llamadas"
            1 -> "Permiso para usar la cámara"
            2 -> "Permiso para leer contactos"
            3 -> "Permiso para obtener ubicación"
            4 -> "Permiso de superposición"
            5 -> "¡Permisos concedidos!"
            else -> ""
        }
    }

    override fun getSummary(index: Int): CharSequence? {
        return null
    }

    override fun size(): Int {
        return 6
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateCustomView(
        index: Int,
        context: Context,
        parent: VerticalStepperItemView
    ): View {
        val inflateView = LayoutInflater.from(context)
            .inflate(R.layout.vertical_stepper_sample_item, parent, false)
        val contentView = inflateView.findViewById<TextView>(R.id.item_content)
        contentView.setText(
            when (index) {
                0 -> R.string.content_step_0
                1 -> R.string.content_step_1
                2 -> R.string.content_step_2
                3 -> R.string.content_step_3
                4 -> R.string.content_step_4
                else -> R.string.content_step_7
            }
        )
        val nextButton = inflateView.findViewById<Button>(R.id.button_next)
        nextButton.text = if (index == size() - 1) "Comenzar" else "Conceder"
        nextButton.setOnClickListener {
            when (index) {
                0 -> ActivityCompat.requestPermissions(
                    this@PermissionActivity,
                    arrayOf(
                        Manifest.permission.CALL_PHONE
                    ),
                    ResultCall
                )

                1 -> ActivityCompat.requestPermissions(
                    this@PermissionActivity,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    ResultCall
                )

                2 -> ActivityCompat.requestPermissions(
                    this@PermissionActivity,
                    arrayOf(
                        Manifest.permission.READ_CONTACTS
                    ),
                    ResultCall
                )

                3 -> ActivityCompat.requestPermissions(
                    this@PermissionActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    ResultCall
                )

                4 -> startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse(
                            "package:$packageName"
                        )
                    ),
                    REQUEST_OVERLAY
                )

                else -> {
                    startActivity(Intent(this@PermissionActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
        val prevButton = inflateView.findViewById<Button>(R.id.button_prev)
        prevButton.visibility = if (index == 4) View.VISIBLE else View.GONE
        inflateView.findViewById<View>(R.id.button_prev)
            .setOnClickListener { binding.verticalStepperView.nextStep() }
        return inflateView
    }

    override fun onShow(index: Int) {}
    override fun onHide(index: Int) {}

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ResultCall) {
            val idx = binding.verticalStepperView.currentStep
            if (idx == 0) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    binding.verticalStepperView.setErrorText(
                        idx,
                        "Sin este permiso la aplicación no puede funcionar"
                    )
                    return
                }
            } else if (idx == 1) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    binding.verticalStepperView.setErrorText(
                        idx,
                        "Sin este permiso la aplicación no puede funcionar"
                    )
                    return
                }
            } else if (idx == 2) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_CONTACTS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    binding.verticalStepperView.setErrorText(
                        idx,
                        "Sin este permiso la aplicación no puede funcionar"
                    )
                    return
                }
            } else if (idx == 3) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    binding.verticalStepperView.setErrorText(
                        idx,
                        "Sin este permiso la aplicación no puede funcionar"
                    )
                    return
                }
            } else if (idx >= 4) {
                return
            }
            binding.verticalStepperView.setErrorText(idx, null)
            binding.verticalStepperView.nextStep()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val idx = binding.verticalStepperView.currentStep
        if (requestCode == REQUEST_OVERLAY) {
            if (!Settings.canDrawOverlays(this)) {
                binding.verticalStepperView.setErrorText(
                    idx,
                    "Permiso no concedido. Puede concederlo más adelante"
                )
                return
            }
        }
        binding.verticalStepperView.setErrorText(idx, null)
        binding.verticalStepperView.nextStep()
    }

    companion object {
        private const val REQUEST_OVERLAY = 0
    }
}
