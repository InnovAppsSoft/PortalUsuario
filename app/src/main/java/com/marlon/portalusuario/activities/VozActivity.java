package com.marlon.portalusuario.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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

public class VozActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView min5,min10,min15,min25,min40;
    private ImageView Atras;

    AdView mAdViewvoz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voz);

        mAdViewvoz = findViewById(R.id.adViewvoz);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9665109922019776/1173610479");
        mAdViewvoz.loadAd(adRequest);
        mAdViewvoz.setAdListener(new AdListener() {
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

        //Definiendo Card
        min5 = findViewById(R.id.min5);
        min10 = findViewById(R.id.min10);
        min15 = findViewById(R.id.min15);
        min25 = findViewById(R.id.min25);
        min40 = findViewById(R.id.min40);
        Atras = findViewById(R.id.atras2);


        //Añadiendo Clic a los Card
        min5.setOnClickListener(this);
        min10.setOnClickListener(this);
        min15.setOnClickListener(this);
        min25.setOnClickListener(this);
        min40.setOnClickListener(this);

        Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VozActivity.super.onBackPressed();
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {


        int id = v.getId();
        if (id == R.id.min5) {
            USSDcall("*133*3*1%23");
        } else if (id == R.id.min10) {
            USSDcall("*133*3*2%23");
        } else if (id == R.id.min15) {
            USSDcall("*133*3*3%23");
        } else if (id == R.id.min25) {
            USSDcall("*133*3*4%23");
        } else if (id == R.id.min40) {
            USSDcall("*133*3*5%23");
        }

    }

    public void USSDcall(String ussd){

        Intent r = new Intent();
        r.setAction(Intent.ACTION_CALL); r.setData(Uri.parse("tel:"+ussd + ""));

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED ) {
                requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 1000);


            } else {

                startActivity(r);

            }

        } else {

            startActivity(r);

        }

    }

}

