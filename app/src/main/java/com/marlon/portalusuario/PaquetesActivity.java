package com.marlon.portalusuario;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import soup.neumorphism.NeumorphCardView;

public class PaquetesActivity extends AppCompatActivity {

    private NeumorphCardView Pb, Pm, Pe ,bolsac,bolsad,tarifa,P1,P2,P16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paquetes);

        Pb = findViewById(R.id.planbasico);
        Pm = findViewById(R.id.planmedio);
        Pe = findViewById(R.id.planextra);

        P1 = findViewById(R.id.plan1gb);
        P2 = findViewById(R.id.plan2gb);
        P16 = findViewById(R.id.plan16);


        bolsac = findViewById(R.id.bolsac);
        bolsad = findViewById(R.id.bolsad);
        tarifa = findViewById(R.id.tarifa);



        Pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*5*1%23");
            }
        });

        Pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*5*2%23");
            }
        });

        Pe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*5*3%23");
            }
        });


        /////////////////////////////////////////////


        P1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*1*4*1%23");
            }
        });

        P2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*1*4*2%23");
            }
        });

        P16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*1*4*3%23");
            }
        });


        ///////////////////////////////////////////////////////

        bolsac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*1*2%23");
            }
        });

        bolsad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*1*3%23");
            }
        });

        tarifa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                USSDcall("*133*1*1%23");
            }
        });

        return;
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