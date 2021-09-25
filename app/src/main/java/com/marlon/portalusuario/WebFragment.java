package com.marlon.portalusuario;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.marlon.portalusuario.cortafuegos.ActivityMain;
import com.marlon.portalusuario.datastats.WidgetActivity;
import com.marlon.portalusuario.une.UneActivity;
import com.marlon.portalusuario.usodedatos.activities.summary.SummaryActivity;

public class WebFragment extends Fragment {

    private static Context context;

    static Fragment newInstance(Context ctx) {
        context = ctx;
        return new WebFragment();
    }

    private LinearLayout Linea1, Linea2, Linea3, Linea4, Linea5, Linea6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_web, container, false);


        Linea1 = view.findViewById(R.id.cotafuegos);
        Linea2 = view.findViewById(R.id.une);
        Linea3 = view.findViewById(R.id.medidor);
        Linea4 = view.findViewById(R.id.info12);
        Linea5 = view.findViewById(R.id.forzar);
        Linea6 = view.findViewById(R.id.usodatos);


        Linea1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActivityMain.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Linea2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UneActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Linea3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, WidgetActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Linea4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, InfoSim.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Linea5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActivityCell.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Linea6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startActivity(new Intent(context, SummaryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(context, "Esta opción no esta disponible para su dispositivo", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return view;

    }
}
