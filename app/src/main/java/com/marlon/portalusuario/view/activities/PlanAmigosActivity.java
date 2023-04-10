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
import androidx.cardview.widget.CardView;

import com.marlon.portalusuario.R;

import soup.neumorphism.NeumorphCardView;

public class PlanAmigosActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView activar,adicionar,consultar;
    private ImageView Atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_plan_amigos);

        //Definiendo Card
        activar = findViewById(R.id.activar);
        adicionar = findViewById(R.id.adicionar);
        consultar = findViewById(R.id.consultar);

        //Añadiendo Clic a los Card
        activar.setOnClickListener(this);
        adicionar.setOnClickListener(this);
        consultar.setOnClickListener(this);

        Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlanAmigosActivity.super.onBackPressed();
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.activar: USSDcall("*133*4*1%23");

                break;
            case R.id.adicionar : USSDcall("*133*4*2%23");

                break;
            case R.id.consultar : USSDcall("*133*4*3%23");

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

