package com.marlon.portalusuario.view.activities;

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

import com.marlon.portalusuario.R;

import soup.neumorphism.NeumorphCardView;

public class VozActivity extends AppCompatActivity implements View.OnClickListener{

    private NeumorphCardView min5,min10,min15,min25,min40;
    private ImageView Atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voz);

        //Definiendo Card
        min5 = (NeumorphCardView) findViewById(R.id.min5);
        min10 = (NeumorphCardView) findViewById(R.id.min10);
        min15 = (NeumorphCardView) findViewById(R.id.min15);
        min25 = (NeumorphCardView) findViewById(R.id.min25);
        min40 = (NeumorphCardView) findViewById(R.id.min40);
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


        switch (v.getId()){
            case R.id.min5: USSDcall("*133*3*1%23");

                break;
            case R.id.min10 : USSDcall("*133*3*2%23");

                break;
            case R.id.min15 : USSDcall("*133*3*3%23");

                break;
            case R.id.min25 : USSDcall("*133*3*4%23");

                break;
            case R.id.min40 : USSDcall("*133*3*5%23");

                break;
            default:break;
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

