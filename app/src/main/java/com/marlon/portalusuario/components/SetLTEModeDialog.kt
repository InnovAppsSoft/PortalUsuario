package com.marlon.portalusuario.components

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.DialogSetOnlyLteBinding
import com.vanniktech.ui.Color
import com.vanniktech.ui.ColorDrawable

class SetLTEModeDialog(private val activity: Activity) {
    private lateinit var binding: DialogSetOnlyLteBinding
    private lateinit var simDialog: Dialog

    init {
        setupDialog()
    }

    private fun setupDialog() {
        simDialog = Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding = DialogSetOnlyLteBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.set4g.setOnClickListener { openHiddenMenu() }
        }
        simDialog.show()
    }

    private fun openHiddenMenu() {
        try {
            val intent = Intent("android.intent.action.MAIN").apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setClassName("com.android.phone", "com.android.phone.settings.RadioInfo")
                } else {
                    setClassName("com.android.settings", "com.android.settings.RadioInfo")
                }
            }
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                activity.getString(R.string.error_message_unsupported_feature),
                Toast.LENGTH_SHORT
            ).show()
            Log.e("SetLTEModeDialog", "Failed to open hidden menu", e)
        } catch (e: SecurityException) {
            Toast.makeText(
                activity,
                activity.getString(R.string.error_message_security_issue),
                Toast.LENGTH_SHORT
            ).show()
            Log.e("SetLTEModeDialog", "Security issue when trying to open hidden menu", e)
        }
    }
}