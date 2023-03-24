package com.marlon.portalusuario.view.Fragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.datatransport.runtime.logging.Logging;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.logging.LogFileViewerActivity;
import com.marlon.portalusuario.view.activities.PrivacyActivity;


public class MoreFragment extends Fragment {

    private LinearLayout Beta,Bug,Politica, Valorar,Canal,Feedback,Face,PoliticaWeb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_more, container, false);

            //Definiendo Card
        Politica =  view.findViewById(R.id.politicade);
        Valorar =  view.findViewById(R.id.rating);
        Canal =  view.findViewById(R.id.canaloficial);
        Feedback =  view.findViewById(R.id.Feedback);
        Face = view.findViewById(R.id.pgface);
        PoliticaWeb = view.findViewById(R.id.politicadeprivacidad);
        Bug = view.findViewById(R.id.logconfi);
        Beta = view.findViewById(R.id.grupobeta);


        Beta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beta = ("https://t.me/portalusuarioBT");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(beta));
                startActivity(i);
            }
        });


        Bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), LogFileViewerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        PoliticaWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getContext().startActivity(new Intent(getContext(), PrivacyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        Face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String faceb = ("https://www.facebook.com/portalusuario");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(faceb));
                startActivity(i);
            }
        });


        Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendFeedback(getContext());
            }
        });

        Politica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Politica();
            }
        });

        Valorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + "com.marlon.portalusuario")));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("play.google.com/store/apps/details?id=" + getContext().getPackageName())));

                }
            }
        });

        Canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String face = ("https://t.me/portalusuario");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(face));
                startActivity(i);
            }
        });


        return view;

    }

    public void Politica() {
        final AlertDialog.Builder Dialogo = new AlertDialog.Builder(getContext());
        Dialogo.setIcon(R.drawable.ic_security);
        Dialogo.setTitle("Política de Privacidad");
        String txt = "";
        try {
            txt = getResources().getString(R.string.terminos);//Util.getTextFrom(getContext(), R.raw.terminos);
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.e("", "", ex);
        }
        Dialogo.setMessage(txt);
        Dialogo.setPositiveButton("ACEPTO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        Dialogo.setNeutralButton("NO ACEPTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }

        });

        Dialogo.create();
        Dialogo.show();
    }

    public static void sendFeedback(Context context) {
        String debugInfo = "\n\n\n---";
        debugInfo += "\nOS Version: " + System.getProperty("os.version") + " (" + Build.VERSION.INCREMENTAL + ")";
        debugInfo += "\nAndroid API: " + Build.VERSION.SDK_INT;
        debugInfo += "\nModel (Device): " + Build.MODEL + " ("+ Build.DEVICE + ")";
        debugInfo += "\nManufacturer: " + Build.MANUFACTURER;
        debugInfo += "\n---";

        Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", context.getString(R.string.feedback_email), null));

        intent.putExtra(Intent.EXTRA_EMAIL, context.getString(R.string.feedback_email));
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject));
        intent.putExtra(Intent.EXTRA_TEXT, debugInfo);

        context.startActivity(Intent.createChooser(intent, "Enviar Feedback usando..."));
    }

    public void onResume() {
        super.onResume();
    }

}


