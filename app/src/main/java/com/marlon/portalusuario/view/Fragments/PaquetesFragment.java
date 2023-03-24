package com.marlon.portalusuario.view.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marlon.portalusuario.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaquetesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaquetesFragment extends Fragment {

    private static Context context;

    static Fragment newInstance(Context ctx) {
        context = ctx;
        return new PaquetesFragment();

    }

    private AppCompatButton Pb, Pm, Pe ,bolsac,tarifa,bolsad,P1,P2,P16;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_paquetes, container, false);

        Pb = view.findViewById(R.id.planbasico);
        Pm = view.findViewById(R.id.planmedio);
        Pe = view.findViewById(R.id.planextra);

        P1 = view.findViewById(R.id.plan1gb);
        P2 = view.findViewById(R.id.plan2gb);
        P16 = view.findViewById(R.id.plan16);


        bolsac = view.findViewById(R.id.bolsac);
        bolsad = view.findViewById(R.id.bolsad);
        tarifa = view.findViewById(R.id.tarifa);



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



        return view;
    }

    public void USSDcall (String ussd){

        Intent r = new Intent();
        r.setAction(Intent.ACTION_CALL);
        r.setData(Uri.parse("tel:" + ussd + ""));

        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);

            } else {

                startActivity(r);

            }

        } else {

            startActivity(r);

        }

    }

}