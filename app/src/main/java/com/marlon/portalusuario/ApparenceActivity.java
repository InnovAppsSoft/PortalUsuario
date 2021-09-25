package com.marlon.portalusuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class ApparenceActivity extends AppCompatActivity {

    ImageView Virar;

    private SharedPreferences nochesalva;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apparence);

        nochesalva = getSharedPreferences("nochesalva", MODE_PRIVATE);

        String nc = nochesalva.getString("keynoche", "");
        RadioButton switchNightMode = findViewById(R.id.claro);
        RadioButton switchNightMode1 = findViewById(R.id.oscuro);
        if (nc.equals("")) {
            switchNightMode.setChecked(true);

        } else {

            switchNightMode1.setChecked(true);
        }
        switchNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                nochesalva.edit().putString("keynoche", "").apply();

            }
        });

        switchNightMode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    nochesalva.edit().putString("keynoche", "si").apply();


            }
        });


        Virar = (ImageView) findViewById(R.id.atras7);

        Virar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApparenceActivity.super.onBackPressed();
            }

        });
    }


    @Override
    public void onBackPressed() {
        ApparenceActivity.super.onBackPressed();
    }
}

