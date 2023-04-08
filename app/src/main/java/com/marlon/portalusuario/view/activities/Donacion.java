package com.marlon.portalusuario.view.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.marlon.portalusuario.R;

public class Donacion extends AppCompatActivity {

    int ResultCall = 1001;
    private EditText monto1,monto2,pass1,pass2;
    private Button btn1,btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donacion);

        monto1 = findViewById(R.id.monto1);
        monto2 = findViewById(R.id.monto2);
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Donar1 = monto1.getText().toString().trim();
                if (Donar1.equals("")) {
                    Toast.makeText(Donacion.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String Pass1 = pass1.getText().toString().trim();
                if (Pass1.equals("")) {
                    Toast.makeText(Donacion.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussdd = "*234*1*" + "54871663" + "*" + Donar1 + "*" + Pass1 + "%23";

                Intent r = new Intent();
                r.setAction(Intent.ACTION_CALL);
                r.setData(Uri.parse("tel:" + ussdd + ""));

                if (Build.VERSION.SDK_INT >= 23) {
                    if (Donacion.this.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                        Donacion.this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);

                    } else {

                        startActivity(r);

                    }

                } else {

                    startActivity(r);

                }

                return;
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Donar2 = monto2.getText().toString().trim();
                if (Donar2.equals("")) {
                    Toast.makeText(Donacion.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String Pass2 = pass2.getText().toString().trim();
                if (Pass2.equals("")) {
                    Toast.makeText(Donacion.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussdd = "*234*1*" + "58076608" + "*" + Donar2 + "*" + Pass2 + "%23";

                Intent r = new Intent();
                r.setAction(Intent.ACTION_CALL);
                r.setData(Uri.parse("tel:" + ussdd + ""));

                if (Build.VERSION.SDK_INT >= 23) {
                    if (Donacion.this.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                        Donacion.this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);

                    } else {

                        startActivity(r);

                    }

                } else {

                    startActivity(r);

                }

                return;

            }
        });

    }
}