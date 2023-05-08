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

import com.marlon.portalusuario.R;

public class SmsActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView plan,plan20,plan35,plan45;
    private ImageView Atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //Definiendo Card
        plan = (CardView) findViewById(R.id.plan);
        plan20 = (CardView) findViewById(R.id.plan20);
        plan35 = (CardView) findViewById(R.id.plan35);
        plan45 = (CardView) findViewById(R.id.plan45);
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