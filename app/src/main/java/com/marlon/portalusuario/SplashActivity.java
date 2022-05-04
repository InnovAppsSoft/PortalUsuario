package com.marlon.portalusuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.makeramen.roundedimageview.RoundedImageView;

public class SplashActivity extends AppCompatActivity {

    private RoundedImageView logo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // APP THEME
        if (settings.getString("keynoche", "").equals("claro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (settings.getString("keynoche", "").equals("oscuro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
//        Animation rotate = new RotateAnimation(0, 360);
//        rotate.setInterpolator(new DecelerateInterpolator()); //add this
//        rotate.setDuration(1000);
//        logo = findViewById(R.id.logo);
//        logo.setAnimation(rotate);
//        logo.animate();
        //rotate.startNow();

    new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

        }.start();
    }

}
