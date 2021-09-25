package com.marlon.portalusuario.usodedatos.database

import android.content.Context
import com.marlon.portalusuario.usodedatos.util.ApplicationConstants

class SharedPreferenceDB{
    companion object{

        fun isNotificationEnabled(context : Context) : Boolean {
            var sharedPreferences = context.getSharedPreferences(ApplicationConstants.defaultSharedPreferences,Context.MODE_PRIVATE)
            var notifications = sharedPreferences.getBoolean(ApplicationConstants.notificationEnabled,true)

            return notifications
        }

        fun setNotification(context: Context, nightMode: Boolean){
            var sharedPreferenceDB = context.getSharedPreferences(ApplicationConstants.defaultSharedPreferences,Context.MODE_PRIVATE)
            var editor =  sharedPreferenceDB.edit()

            editor.putBoolean(ApplicationConstants.notificationEnabled, nightMode)
            editor.apply()
            editor.apply()
        }

        fun isPersistentNotification(context : Context) : Boolean {
            var sharedPreferences = context.getSharedPreferences(ApplicationConstants.defaultSharedPreferences,Context.MODE_PRIVATE)
            var notifications = sharedPreferences.getBoolean(ApplicationConstants.persistentNotification,true)

            return notifications
        }

        fun setPersistentNotification(context: Context, nightMode: Boolean){
            var sharedPreferenceDB = context.getSharedPreferences(ApplicationConstants.defaultSharedPreferences,Context.MODE_PRIVATE)
            var editor =  sharedPreferenceDB.edit()

            editor.putBoolean(ApplicationConstants.persistentNotification, nightMode)
            editor.apply()
            editor.apply()
        }
    }
}