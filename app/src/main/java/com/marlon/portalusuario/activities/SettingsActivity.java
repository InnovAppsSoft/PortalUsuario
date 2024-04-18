package com.marlon.portalusuario.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.trafficbubble.FloatingBubbleService;

import java.util.Objects;

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
        }

        @Override
        public void onResume() {
            super.onResume();
            // Registrar escucha
            Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
                    .registerOnSharedPreferenceChangeListener(this);
            //
            show_traffic_speed_bubble.setEnabled(true);
            //
            if (!Settings.canDrawOverlays(getContext())) {
                show_traffic_speed_bubble.setChecked(false);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            // Eliminar registro de la escucha
            Objects.requireNonNull(getPreferenceScreen().getSharedPreferences())
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (Objects.requireNonNull(key)) {
                case "keynoche" -> {
                    switch (sharedPreferences.getString("keynoche", "")) {
                        case "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        case "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        default -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                }
                case "show_traffic_speed_bubble" -> {
                    if (sharedPreferences.getBoolean("show_traffic_speed_bubble", false)) {
                        if (!Settings.canDrawOverlays(getContext())) {
                            Toast.makeText(getContext(), "Otorgue a Portal Usuario los permisos requeridos", Toast.LENGTH_SHORT).show();
                            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName())), 0);
                        }
                    } else {
                        requireContext().stopService(new Intent(getContext(), FloatingBubbleService.class));
                    }
                }
            }
        }
    }
}