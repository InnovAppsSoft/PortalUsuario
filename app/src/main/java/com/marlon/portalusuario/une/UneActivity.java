package com.marlon.portalusuario.une;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.ViewModel.UneViewModel;
import com.marlon.portalusuario.errores_log.JCLogging;
import com.marlon.portalusuario.util.Util;

import java.util.ArrayList;
import java.util.List;

public class UneActivity extends AppCompatActivity {

    private final int lecturaActual = 0;
    private final int lecturaAnterior = 0;
    private SharedPreferences preferences;
    private TextView tVLecturaActualUne;
    private TextView tVLecturaAnteriorUne;

    //////NUEVO////
    private TextView aPagar;
    private ArrayList<Double> b;
    private String cadena;
    private TextView consumo;
    private double consumoElectrico;
    private double consumoTotal;
    private tarifaElect e;
    private double lectFinal;
    private double lectInicial;
    private EditText lecturaActual2;
    private EditText lecturaAnterior2;
    private Button Borrar,Calcular;
    private JCLogging Logging;
    private ImageView Virar;
    private RecyclerView recyclerView;
    private List<Une> uneRegisters;
    public TextView totalConsumption;
    public TextView totalToPay;
    
    private UneViewModel uneViewModel;

    AdView mAdViewune;


    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_une);

        e = new tarifaElect();
        Logging = new JCLogging(UneActivity.this);
        lecturaAnterior2 = findViewById(R.id.lecturaAnteriorTf);
        lecturaActual2 = findViewById(R.id.lecturaActualTf);
        consumo = findViewById(R.id.consumoTV);
        aPagar = findViewById(R.id.aPagarTV);
        Borrar = findViewById(R.id.LimpiarBt);
        Calcular = findViewById(R.id.CalcularBt);
        Virar = findViewById(R.id.virarhaciatras);
        totalConsumption = findViewById(R.id.total_consumption);
        totalToPay = findViewById(R.id.total_to_pay);

        consumoTotal = 0.0;
        consumoElectrico = 0;
        cadena = "";

        // ADS //
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdViewune = findViewById(R.id.adViewune);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9665109922019776/5591970278");
        mAdViewune.loadAd(adRequest);
        mAdViewune.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

        });

        BottomSheetDialogUne bottomSheetDialog = new BottomSheetDialogUne();

        ImageView resumenBtn = findViewById(R.id.resumen);
        resumenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show(getSupportFragmentManager(), "Menu");
            }
        });

        Virar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Limpiar(v);
            }
        });

        Calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calcular(v);
            }
        });
        recyclerView = findViewById(R.id.recycler_une);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //
        // VIEW MODEL
        uneViewModel = new ViewModelProvider(this).get(UneViewModel.class);
        uneViewModel.getAllUnes().observe(this, une -> {
            setAdapter(une);
        });
    }

    private void setAdapter(List<Une> uneRegisters) {
        UneAdapter uneAdapter = new UneAdapter(uneRegisters, this);
        recyclerView.setAdapter(uneAdapter);
    }

    @SuppressLint("ShowToast")
    public void Calcular(View view) {
//        try {
        String msg = "";
        if (!TextUtils.isEmpty(lecturaAnterior2.getText().toString())) {
            lectInicial = Double.parseDouble(lecturaAnterior2.getText().toString());
        } else {
            msg = msg + "Lectura Anterior: Valor inválido \n";
        }
        if (!TextUtils.isEmpty(lecturaActual2.getText().toString())) {
            lectFinal = Double.parseDouble(lecturaActual2.getText().toString());
        } else {
            msg = msg + "Lectura Actual: Valor inválido \n";
        }
        if (msg == "") {
            if (lectFinal < 0.0d) {
                msg = msg + "Lectura Actual: Valor inválido \n";
            } else if (lectInicial < 0.0d) {
                msg = msg + "Lectua Anterior: Valor inválido \n";
            } else if (lectFinal < lectInicial) {
                msg = msg + "La lectura Anterior no puede ser mayor a la Actual \n";
            } else if (lectFinal == lectInicial) {
                msg = msg + "Lectura anterior y Lectura Actual no deben ser iguales \n";
            }
        }
        if (msg == "") {
            incializarVariables();
            consumoElectrico = lectFinal - lectInicial;
            cc(0);
            cadena = cadena + "Consumo total: " + Util.roundDouble(consumoElectrico) + " Kwh \n";
            cadena = cadena + "Usted debe pagar: " + Util.roundDouble(consumoTotal) + " Pesos \n";
            consumoElectrico = Util.roundDouble(consumoElectrico);
            double totalAPagar = Util.roundDouble(consumoTotal);
            consumo.setText("Consumo total: " + consumoElectrico + " Kwh");
            aPagar.setText("Usted debe pagar: " + totalAPagar + " Pesos");
            //
            uneViewModel.insertUne(new Une(Util.currentDate2Long(), lectInicial, lectFinal, consumoElectrico, totalAPagar));
        }
            //Toast.makeText(UneActivity.this, msg, Toast.LENGTH_SHORT).show();
//        }catch (Exception ex){
//            Logging.error(null, null, ex);
//            Toast.makeText(this, "Ha ocurrido un error. Revise el registro para detalles", Toast.LENGTH_LONG);
//        }
    }

    public void Limpiar(View view) {
        lecturaAnterior2.setText("");
        lecturaAnterior2.requestFocus();
        lecturaActual2.setText("");
        consumo.setText(R.string.consumoTV);
        aPagar.setText(R.string.aPagarTV);

    }

    public void incializarVariables() {
        //b = new ArrayList<>();
        consumoTotal = 0.0d;
        cadena = "";
    }

    

    @SuppressLint("ShowToast")
    public void cc(int i) {
        try{
            if (consumoElectrico > e.rangosConsumo.get(i).getFinRango() || consumoElectrico <= e.rangosConsumo.get(i).getInicioRango()) {
                //b.add(Double.valueOf(e.rangosConsumo.get(i).getFinRango() - e.rangosConsumo.get(i).getInicioRango()));
                consumoTotal += (e.rangosConsumo.get(i).getFinRango() - e.rangosConsumo.get(i).getInicioRango()) * e.rangosConsumo.get(i).getPrecioRango();
                cc(i + 1);
                return;
            }
            //b.add(Double.valueOf(consumoElectrico - e.rangosConsumo.get(i).getInicioRango()));
            consumoTotal += (consumoElectrico - e.rangosConsumo.get(i).getInicioRango()) * e.rangosConsumo.get(i).getPrecioRango();
        }catch (Exception ex){
            JCLogging.error(null, null, ex);
            Toast.makeText(this, "Ha ocurrido un error. Revise el registro para detalles", Toast.LENGTH_LONG);
        }
    }

}