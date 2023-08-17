package com.marlon.portalusuario.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.burbuja_trafico.FloatingBubbleService;
import com.marlon.portalusuario.view.fragments.BalanceNotification;

public class SettingsActivity extends AppCompatActivity{

    public SettingsActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // permiso de notificación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no tienes el permiso, solicítalo al usuario
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                        this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, 0);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat  implements SharedPreferences.OnSharedPreferenceChangeListener{
        private SwitchPreferenceCompat show_traffic_speed_bubble;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            // INIT
            show_traffic_speed_bubble = findPreference("show_traffic_speed_bubble");

            // notification
            SwitchPreferenceCompat notification = findPreference("notifi");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification.setEnabled(true);
            } else {
                notification.setEnabled(false);
                notification.setSummary(
                        getActivity().getString(R.string.summary_notification_not_comp));
            }
            notification.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        boolean isChecked = (Boolean) newValue;
                        if (isChecked) {
                            getActivity()
                                    .sendBroadcast(
                                            new Intent(getActivity(), BalanceNotification.class));
                        } else {
                            NotificationManagerCompat notificationManager =
                                    NotificationManagerCompat.from(getActivity());
                            notificationManager.cancel(1);
                        }
                        return true;
                    });


    }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onResume() {
            super.onResume();
            // Registrar escucha
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
            //
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                show_traffic_speed_bubble.setEnabled(false);
            }else{
                show_traffic_speed_bubble.setEnabled(true);
            }
            //
            if (!Settings.canDrawOverlays(getContext())) {
                show_traffic_speed_bubble.setChecked(false);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            // Eliminar registro de la escucha
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("keynoche")){
                if (sharedPreferences.getString("keynoche", "").equals("claro")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else if (sharedPreferences.getString("keynoche", "").equals("oscuro")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            }else if (key.equals("show_traffic_speed_bubble")){
                if (sharedPreferences.getBoolean("show_traffic_speed_bubble", false)) {
                    if (!Settings.canDrawOverlays(getContext())) {
                        Toast.makeText(getContext(), "Otorgue a Portal Usuario los permisos requeridos", Toast.LENGTH_SHORT).show();
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName())), 0);
                    }
                }else{
                    getContext().stopService(new Intent(getContext(), FloatingBubbleService.class));
                }
            }else if(key.equals("show_etecsa_promo_carousel")){
                boolean b = sharedPreferences.getBoolean("show_etecsa_promo_carousel", true);
                MainActivity.setCarouselVisibility(b);
            }
        }
    }
}