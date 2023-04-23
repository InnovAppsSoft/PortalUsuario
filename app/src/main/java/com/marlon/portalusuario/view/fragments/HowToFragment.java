package com.marlon.portalusuario.view.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marlon.portalusuario.R;

public class HowToFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_how_to, container, false);
        //
        return v;
    }

    public void openHiddenMenu() {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            if (Build.VERSION.SDK_INT >= 30) {
                intent.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
            } else {
                intent.setClassName("com.android.settings", "com.android.settings.RadioInfo");
            }
            startActivity(intent);
        } catch (Exception unused) {
            Toast.makeText(getContext(), "Su dispositivo no admite esta funcionalidad, lamentamos las molestias :(", Toast.LENGTH_SHORT).show();
        }
    }
}