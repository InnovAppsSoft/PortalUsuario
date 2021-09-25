package com.marlon.portalusuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PaginasActivity extends AppCompatActivity {

    private TextView saldo,bono,transferir,recargar,megas,adelantar,WE,MC,E,Correo,TuEnvio,iTecno,Ecu,EnZona,Wifi,Portal,Juve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paginas);

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

        saldo = findViewById(R.id.saldo);
        bono = findViewById(R.id.bono);
        transferir = findViewById(R.id.transferir);
        recargar = findViewById(R.id.recargar);
        megas = findViewById(R.id.megas);
        adelantar = findViewById(R.id.adelantar);
        MC = findViewById(R.id.micubacel);
        E = findViewById(R.id.etecsa);
        Correo = findViewById(R.id.correo);
        TuEnvio = findViewById(R.id.tuenvio);
        Ecu = findViewById(R.id.ecured);
        EnZona = findViewById(R.id.enzona);
        iTecno = findViewById(R.id.tecno);
        Juve = findViewById(R.id.juventud);

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

        return;

    }

    void openCustomJuventud(String juventudt) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(juventudt));
    }



    void openCustomEcu(String ecured) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(ecured));
    }

    void openCustomEnzona(String enzona) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(enzona));
    }

    void openCustomTecno(String itecno) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(itecno));
    }

    void openCustomTuEnvio(String tuenvio) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(tuenvio));
    }

    void openCustomCorreo(String correo) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(correo));
    }

    private void openCustomMCubacel(String micubacel) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(micubacel));
    }

    private void openCustomEtecsa(String etecsa) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(etecsa));
    }

    private void openCustomPaginas(String paginas) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(paginas));
    }

    private void openCustomTodus(String todus) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(todus));
    }

    private void openCustomApklis(String apklis) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(apklis));
    }

    void openCustomPicta(String picta) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(picta));
    }

    private void openCustomCuba(String cubadebate) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(cubadebate));
    }

    void openCustomMochila(String lamochila) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(lamochila));
    }

}
