package com.marlon.portalusuario.usodedatos.activities.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.marlon.portalusuario.R
import com.marlon.portalusuario.usodedatos.database.SharedPreferenceDB
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        title = getString(R.string.config_config)
        setupNotifications()

    }

    private fun setupNotifications() {
        var notificationEnabled = SharedPreferenceDB.isNotificationEnabled(this)
        var persistentNotification = SharedPreferenceDB.isPersistentNotification(this)
        settings_notification_enable_switch.isChecked = notificationEnabled
        settings_persistent_notification_switch.isChecked = persistentNotification

        settings_notification_enable_switch.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferenceDB.setNotification(this@SettingsActivity, isChecked)
        }

        settings_persistent_notification_switch.setOnCheckedChangeListener { _, isChecked ->
            SharedPreferenceDB.setPersistentNotification(this@SettingsActivity, isChecked)
        }
    }


}
