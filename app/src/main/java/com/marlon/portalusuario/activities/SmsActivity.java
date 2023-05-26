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

public class SmsActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView plan,plan20,plan35,plan45;
    private ImageView Atras;

    AdView mAdViewsms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);


        mAdViewsms = findViewById(R.id.adViewsms);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9665109922019776/1173610479");
        mAdViewsms.loadAd(adRequest);
        mAdViewsms.setAdListener(new AdListener() {
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
        plan = findViewById(R.id.plan);
        plan20 = findViewById(R.id.plan20);
        plan35 = findViewById(R.id.plan35);
        plan45 = findViewById(R.id.plan45);
        Atras = findViewById(R.id.atras1);

        //Añadiendo Clic a los Card
        plan.setOnClickListener(this);
        plan20.setOnClickListener(this);
        plan35.setOnClickListener(this);
        plan45.setOnClickListener(this);


        Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsActivity.super.onBackPressed();
            }
        });

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {


        int id = v.getId();
        if (id == R.id.plan) {
            USSDcall("*133*2*1%23");
        } else if (id == R.id.plan20) {
            USSDcall("*133*2*2%23");
        } else if (id == R.id.plan35) {
            USSDcall("*133*2*3%23");
        } else if (id == R.id.plan45) {
            USSDcall("*133*2*4%23");
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