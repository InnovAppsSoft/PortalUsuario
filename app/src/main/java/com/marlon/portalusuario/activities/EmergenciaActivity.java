package com.marlon.portalusuario.activities;

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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.marlon.portalusuario.R;

public class EmergenciaActivity extends AppCompatActivity {

    private CardView Ambulancia,Policia,Bomberos,Antidrogas,Maritimo,Cubacel;

    private ImageView Atras;
    AdView mAdVieweme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);

        mAdVieweme = findViewById(R.id.adViewsemes);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9665109922019776/1173610479");
        mAdVieweme.loadAd(adRequest);
        mAdVieweme.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

        });

        Ambulancia = findViewById(R.id.ambulancia);
        Policia = findViewById(R.id.policia);
        Bomberos = findViewById(R.id.bomberos);
        Antidrogas= findViewById(R.id.antidrogas);
        Maritimo = findViewById(R.id.Mar);
        Cubacel = findViewById(R.id.cubacel);

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

