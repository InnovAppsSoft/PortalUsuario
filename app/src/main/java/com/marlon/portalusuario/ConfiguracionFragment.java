package com.marlon.portalusuario;


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

import com.marlon.portalusuario.ui.PrivacyActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracionFragment extends Fragment {

    private static Context context;

    static Fragment newInstance(Context ctx) {
        context = ctx;
        return new ConfiguracionFragment();
    }

    private LinearLayout Info32,Politica,Donar,Acerca,Valorar,Canal,Feedback,Apariencia,Face,Cortafuegos,Une,PoliticaWeb,Widget,Noti;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

            //Definiendo Card
        Politica =  view.findViewById(R.id.politicade);
        Donar =  view.findViewById(R.id.donarportal);
        Acerca = view.findViewById(R.id.acercade);
        Valorar =  view.findViewById(R.id.Tocar);
        Canal =  view.findViewById(R.id.canaloficial);
        Feedback =  view.findViewById(R.id.Feedback);
        Apariencia = view.findViewById(R.id.apariencia);
        Face = view.findViewById(R.id.pgface);
        PoliticaWeb = view.findViewById(R.id.politicadeprivacidad);


        PoliticaWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                context.startActivity(new Intent(context, PrivacyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

        Apariencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                context.startActivity(new Intent(context, ApparenceActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendFeedback(context);
            }
        });

        Politica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Politica();
            }
        });

        Donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DonarActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Acerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                context.startActivity(new Intent(context, Creditos.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                            Uri.parse("play.google.com/store/apps/details?id=" + context.getPackageName())));

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
        final AlertDialog.Builder Dialogo = new AlertDialog.Builder(context);
        Dialogo.setIcon(R.drawable.ic_security);
        Dialogo.setTitle("Política de Privacidad");
        Dialogo.setMessage("○Portal Usuario: es una aplicación creada por Marlon de Jesus Milanes Rivero, para terminales android, que facilita el acceso a los diferentes servicios que brinda la Empresa de Telecomunicaciones ETECSA.\n" +
                "○Reconozco la importancia que existe en administrar y proteger correctamente la información de mis usuarios.Por este motivo cuento con política de privacidad, describe cómo los servicios prestados a la aplicación Portal Usuario gestionan y aseguran la información que obtienen .\n" +
                "○Existe un solo tipo de usuario en Portal Usuario:\n" +
                "Usuario: hace uso total de la aplicación.\n" +
                "○Los servicios solicitados mediante Portal Usuario responde de ETECSA o sea la aplicación solo los solicita por usted,no me hago responsable por demora o mal funcionamiento de los servicios de esta compañía. No me hago responsable del mal uso que le den a la aplicación.\n" +
                "○Solicitudes de permiso: \n" +
                "permiso CALL_PHONE: Se utiliza para generar los códigos ussd.\n" +
                "permiso INTERNET: Se utiliza para que usted acceda a los webview implementados en la aplicación.\n" +
                "permiso para leer sus contactos  READ_CONTACTS y pueda acceder mas rapidos a la hora de transferir su saldo.\n" +
                "permiso ACCESS_COARSE_LOCATION: Localización GPS detallada. Localización basada en satélites GPS. Al dar este permiso también estamos permitiendo la localización basada en telefonía móvil y Wi-Fi\n" +
                "○Cambios a esta política de privacidad Podemos actualizar nuestra Política de privacidad de vez en cuando. Le notificaremos cualquier cambio publicando la nueva Política de privacidad en esta página. \n" +
                "○Contacto:\n" +
                "○Pueden ponerse en contacto conmigo en : marlonrivero1999@gmail.com\n" +
                "○Última actualización : 25 de enero de 2021 ");
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


