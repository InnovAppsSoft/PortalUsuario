package com.marlon.portalusuario;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class DonarActivity extends AppCompatActivity {

    private Button Send;
    private EditText Edit1, Edit2;
    private ImageView Bandec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar);

        Send = findViewById(R.id.btnSend);
        Edit1 = findViewById(R.id.etMontoDon);
        Edit2 = findViewById(R.id.etPasword);
        Bandec = findViewById(R.id.imageSlider);

        Bandec.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {

                //convertir imagen a bitmap
                Bandec.buildDrawingCache();
                Bitmap bmap = Bandec.getDrawingCache();

                //guardar imagen
                Save savefile = new Save();
                savefile.SaveImage(DonarActivity.this, bmap);
                return false;
            }
        });


        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Monto = Edit1.getText().toString().trim();
                if (Monto.equals("")) {
                    Toast.makeText(DonarActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }
                String Clave = Edit2.getText().toString().trim();
                if (Clave.equals("")) {
                    Toast.makeText(DonarActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussdd = "*234*1*" + "54871663" + "*" + Clave + "*" + Monto + "%23";
                USSDcall(ussdd);

            }
        });

    }

    public void USSDcall(String ussd) {

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
