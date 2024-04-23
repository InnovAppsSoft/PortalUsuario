package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.marlon.portalusuario.R
import com.marlon.portalusuario.util.Utils.hasPermissions
import moe.feng.common.stepperview.IStepperAdapter
import moe.feng.common.stepperview.VerticalStepperItemView
import moe.feng.common.stepperview.VerticalStepperView

private const val CALL_PHONE = 0
private const val CAMERA = 1
private const val CONTACTS = 2
private const val LOCATION = 3
private const val OVERLAY = 4
private const val SUCCESS = 5
private const val RESULT_CALL: Int = 1001

class PermissionActivity : AppCompatActivity(), IStepperAdapter {
    private lateinit var mVerticalStepperView: VerticalStepperView

    private lateinit var overlayPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        overlayPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            val idx = mVerticalStepperView.currentStep
            if (result.resultCode == RESULT_OK) {
                if (!Settings.canDrawOverlays(this)) {
                    mVerticalStepperView.setErrorText(
                        idx,
                        "Permiso no concedido. Puede concederlo más adelante"
                    )
                    return@registerForActivityResult
                }
            }
            mVerticalStepperView.setErrorText(idx, null)
            mVerticalStepperView.nextStep()
        }

        //
        mVerticalStepperView = findViewById(R.id.vertical_stepper_view)
        mVerticalStepperView.setStepperAdapter(this)
    }

    override fun getTitle(index: Int): CharSequence {
        return when (index) {
            CALL_PHONE -> "Permiso para realizar llamadas"
            CAMERA -> "Permiso para usar la cámara"
            CONTACTS -> "Permiso para leer contactos"
            LOCATION -> "Permiso para obtener ubicación"
            OVERLAY -> "Permiso de superposición"
            SUCCESS -> "¡Permisos concedidos!"
            else -> ""
        }
    }

    override fun getSummary(index: Int): CharSequence? {
        return null
    }

    override fun size(): Int {
        return 6
    }

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
                CALL_PHONE -> R.string.content_step_0
                CAMERA -> R.string.content_step_1
                CONTACTS -> R.string.content_step_2
                LOCATION -> R.string.content_step_3
                OVERLAY -> R.string.content_step_4
                else -> R.string.content_step_7
            }
        )
        val nextButton = inflateView.findViewById<Button>(R.id.button_next)
        setUpNextButton(nextButton, index)
        val prevButton = inflateView.findViewById<Button>(R.id.button_prev)
        prevButton.visibility = if (index == OVERLAY) View.VISIBLE else View.GONE
        inflateView.findViewById<View>(R.id.button_prev)
            .setOnClickListener { mVerticalStepperView.nextStep() }
        return inflateView
    }

    private fun setUpNextButton(nextButton: Button, index: Int) {
        nextButton.text = if (index == size() - 1) "Comenzar" else "Conceder"
        nextButton.setOnClickListener {
            when (index) {
                CALL_PHONE -> ActivityCompat.requestPermissions(
                    this@PermissionActivity, arrayOf(Manifest.permission.CALL_PHONE), RESULT_CALL
                )

                CAMERA -> ActivityCompat.requestPermissions(
                    this@PermissionActivity, arrayOf(Manifest.permission.CAMERA), RESULT_CALL
                )

                CONTACTS -> ActivityCompat.requestPermissions(
                    this@PermissionActivity, arrayOf(Manifest.permission.READ_CONTACTS), RESULT_CALL
                )

                LOCATION -> ActivityCompat.requestPermissions(
                    this@PermissionActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    RESULT_CALL
                )

                OVERLAY -> overlayPermissionLauncher
                    .launch(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )

                else -> {
                    startActivity(Intent(this@PermissionActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onShow(index: Int) {
        // Do nothing
    }

    override fun onHide(index: Int) {
        // Do nothing
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RESULT_CALL) {
            when(val idx = mVerticalStepperView.currentStep) {
                CALL_PHONE ->
                    if (hasPermissions(Manifest.permission.CALL_PHONE)) {
                    mVerticalStepperView.setErrorText(idx, getString(R.string.permission_required_message))
                }
                CAMERA ->
                    if (hasPermissions(Manifest.permission.CAMERA)) {
                        mVerticalStepperView.setErrorText(idx, getString(R.string.permission_required_message))
                }
                CONTACTS ->
                    if (hasPermissions(Manifest.permission.READ_CONTACTS)) {
                        mVerticalStepperView.setErrorText(idx, getString(R.string.permission_required_message))
                }
                LOCATION ->
                    if (hasPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE
                    )) {
                        mVerticalStepperView.setErrorText(idx, getString(R.string.permission_required_message))
                    }
                else -> {
                    mVerticalStepperView.setErrorText(idx, null)
                    mVerticalStepperView.nextStep()
                }
            }
        }
    }
}