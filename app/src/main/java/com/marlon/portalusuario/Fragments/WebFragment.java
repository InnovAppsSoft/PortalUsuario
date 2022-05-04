package com.marlon.portalusuario.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.marlon.portalusuario.SetLTEModeActivity;
import com.marlon.portalusuario.InfoSimActivity;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.firewall.ActivityMain;
import com.marlon.portalusuario.une.UneActivity;

public class WebFragment extends Fragment {
    private LinearLayout Linea1, Linea2, Linea3, Linea4, Linea5, Linea6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_herramientas, container, false);


        Linea1 = view.findViewById(R.id.cotafuegos);
        Linea2 = view.findViewById(R.id.une);
        Linea4 = view.findViewById(R.id.info12);
        Linea5 = view.findViewById(R.id.forzar);


        Linea1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), ActivityMain.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Linea2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), UneActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        Linea4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), InfoSimActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Linea5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), SetLTEModeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        return view;

    }
}
