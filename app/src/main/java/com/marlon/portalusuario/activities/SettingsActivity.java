package com.marlon.portalusuario.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.trafficbubble.FloatingBubbleService;

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

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onResume() {
            super.onResume();
            // Registrar escucha
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
            //
            show_traffic_speed_bubble.setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
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