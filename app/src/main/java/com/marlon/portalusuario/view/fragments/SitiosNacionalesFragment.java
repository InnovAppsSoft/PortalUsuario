package com.marlon.portalusuario.view.Fragments;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marlon.portalusuario.R;

public class SitiosNacionalesFragment extends Fragment {

    private TextView saldo,bono,transferir,recargar,megas,adelantar,WE,MC,E,Correo,TuEnvio,iTecno,Ecu,EnZona,Wifi,Portal,Juve;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sitios_nacionales, container, false);

        final String pesquiza = "http://autopesquisa.sld.cu/";
        final String micubacel = "https://www.uci.cu/";
        final String etecsa = "http://www.etecsa.cu/";
        final String nauta = "https://www.portal.nauta.cu/user/login";
        final String paginas = "http://www.pamarillas.cu/";
        final String todus = "https://www.todus.cu";
        final String apklis = "https://www.apklis.cu/";
        final String picta = "https://www.picta.cu";
        final String cubadebate = "http://www.cubadebate.cu";
        final String lamochila = "https://mochila.cubava.cu/portada";
        final String correo = "https://webmail.nauta.cu/";
        final String tuenvio = "https://www.tuenvio.cu/";
        final String itecno = "https://itecnogeek.cubava.cu/";
        final String ecured = "https://www.ecured.cu/EcuRed:Enciclopedia_cubana";
        final String enzona = "https://www.enzona.net/";
        final String juventudt = "http://www.juventudtecnica.cu/";

        saldo = view.findViewById(R.id.saldo);
        bono = view.findViewById(R.id.bono);
        transferir = view.findViewById(R.id.transferir);
        recargar = view.findViewById(R.id.recargar);
        megas = view.findViewById(R.id.megas);
        adelantar = view.findViewById(R.id.adelantar);
        MC = view.findViewById(R.id.micubacel);
        E = view.findViewById(R.id.etecsa);
        Correo = view.findViewById(R.id.correo);
        TuEnvio = view.findViewById(R.id.tuenvio);
        Ecu = view.findViewById(R.id.ecured);
        EnZona = view.findViewById(R.id.enzona);
        iTecno = view.findViewById(R.id.tecno);
        Juve = view.findViewById(R.id.juventud);

        Juve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCustomJuventud(juventudt);
            }
        });

        iTecno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCustomEcu(itecno);
            }
        });


        Ecu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCustomEcu(ecured);
            }
        });

        EnZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCustomEnzona(enzona);
            }
        });


        MC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomMCubacel(micubacel);
            }
        });

        E.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomEtecsa(etecsa);
            }
        });

        saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomPaginas(paginas);
            }
        });


        bono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomTodus(todus);
            }
        });

        megas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomApklis(apklis);
            }
        });

        transferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomPicta(picta);
            }
        });

        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomCuba(cubadebate);
            }
        });

        adelantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomMochila(lamochila);
            }
        });

        Correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCustomCorreo(correo);
            }
        });

        TuEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCustomTuEnvio(tuenvio);
            }
        });

        return view;

    }

    void openCustomJuventud(String juventudt) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(juventudt));
    }



    void openCustomEcu(String ecured) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(ecured));
    }

    void openCustomEnzona(String enzona) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(enzona));
    }

    void openCustomTecno(String itecno) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(itecno));
    }

    void openCustomTuEnvio(String tuenvio) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(tuenvio));
    }

    void openCustomCorreo(String correo) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(correo));
    }

    private void openCustomMCubacel(String micubacel) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(micubacel));
    }

    private void openCustomEtecsa(String etecsa) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(etecsa));
    }

    private void openCustomPaginas(String paginas) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(paginas));
    }

    private void openCustomTodus(String todus) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(todus));
    }

    private void openCustomApklis(String apklis) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(apklis));
    }

    void openCustomPicta(String picta) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(picta));
    }

    private void openCustomCuba(String cubadebate) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(cubadebate));
    }

    void openCustomMochila(String lamochila) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(getContext(), Uri.parse(lamochila));
    }

}
