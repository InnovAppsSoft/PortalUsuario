package com.marlon.portalusuario.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.marlon.portalusuario.R;

import soup.neumorphism.NeumorphCardView;

public class EmergenciaActivity extends AppCompatActivity {

    private CardView Ambulancia,Policia,Bomberos,Antidrogas,Maritimo,Cubacel;

    private ImageView Atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);


        Ambulancia = (CardView) findViewById(R.id.ambulancia);
        Policia = (CardView) findViewById(R.id.policia);
        Bomberos = (CardView) findViewById(R.id.bomberos);
        Antidrogas= (CardView) findViewById(R.id.antidrogas);
        Maritimo = (CardView) findViewById(R.id.Mar);
        Cubacel = (CardView) findViewById(R.id.cubacel);

        Atras = findViewById(R.id.atras4);

        Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmergenciaActivity.super.onBackPressed();
            }
        });

        Ambulancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("104");
            }
        });

        Policia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("106");
            }
        });

        Bomberos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("105");
            }
        });

        Antidrogas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("103");
            }
        });

        Maritimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("107");
            }
        });

        Cubacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("52642266");
            }
        });

    }

    public void USSDcall (String ussd){

        Intent r = new Intent();
        r.setAction(Intent.ACTION_CALL);
        r.setData(Uri.parse("tel:" + ussd + ""));

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);

            } else {

                startActivity(r);

            }

        } else {

            startActivity(r);

        }

    }
}

