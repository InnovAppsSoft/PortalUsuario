package com.marlon.portalusuario.une;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.marlon.portalusuario.Creditos;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.electricidad.tarifaElect;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UneActivity extends AppCompatActivity {


    private int lecturaActual = 0;
    private int lecturaAnterior = 0;
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


    private double CalcularImporteAPagar(int i) {
        if (i > 5000) {
            return (((double) (i - 5000)) * 20.0d) + 57911.0d;
        }
        if (i >= 4201 && i <= 5000) {
            return (((double) (i - 4200)) * 15.0d) + 45911.0d;
        }
        if (i >= 3401 && i <= 4200) {
            return (((double) (i - 3400)) * 13.95d) + 34751.0d;
        }
        if (i >= 2601 && i <= 3400) {
            return (((double) (i - 2600)) * 12.9d) + 24431.0d;
        }
        if (i >= 1801 && i <= 2600) {
            return (((double) (i - 1800)) * 11.8d) + 14991.0d;
        }
        if (i >= 1001 && i <= 1800) {
            return (((double) (i - 1000)) * 10.8d) + 6351.0d;
        }
        if (i >= 701 && i <= 1000) {
            return (((double) (i - 700)) * 9.85d) + 3396.0d;
        }
        if (i >= 601 && i <= 700) {
            return (((double) (i - 600)) * 9.45d) + 2451.0d;
        }
        if (i >= 501 && i <= 600) {
            return (((double) (i - 500)) * 9.2d) + 1531.0d;
        }
        if (i >= 451 && i <= 500) {
            return (((double) (i - 450)) * 7.0d) + 1181.0d;
        }
        if (i >= 401 && i <= 450) {
            return (((double) (i - 400)) * 6.0d) + 881.0d;
        }
        if (i >= 351 && i <= 400) {
            return (((double) (i - 350)) * 5.0d) + 631.0d;
        }
        if (i >= 301 && i <= 350) {
            return (((double) (i - 300)) * 4.0d) + 431.0d;
        }
        if (i >= 251 && i <= 300) {
            return (((double) (i - ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION)) * 3.0d) + 281.0d;
        }
        if (i >= 201 && i <= 250) {
            return (((double) (i - ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION)) * 2.46d) + 158.0d;
        }
        if (i >= 151 && i <= 200) {
            return (((double) (i - 150)) * 1.43d) + 86.5d;
        }
        if (i >= 101 && i <= 150) {
            return (((double) (i - 100)) * 1.07d) + 33.0d;
        }
        if (i < 0 || i > 100) {
            return 0.0d;
        }
        return ((double) i) * 0.33d;
    }

    private ImageView Virar;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_une);

        this.e = new tarifaElect();
        this.lecturaAnterior2 = (EditText) findViewById(R.id.lecturaAnteriorTf);
        this.lecturaActual2 = (EditText) findViewById(R.id.lecturaActualTf);
        this.consumo = (TextView) findViewById(R.id.consumoTV);
        this.aPagar = (TextView) findViewById(R.id.aPagarTV);
        Borrar = (Button) findViewById(R.id.LimpiarBt);
        Calcular = (Button) findViewById(R.id.CalcularBt);
        Virar = (ImageView)findViewById(R.id.virarhaciatras);

        this.b = new ArrayList<>();
        this.consumoTotal = 0.0d;
        this.cadena = "";



        Virar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UneActivity.this.finish();
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

    }

    public void Calcular(View view) {
        String msg = "";
        if (!TextUtils.isEmpty(this.lecturaAnterior2.getText().toString())) {
            this.lectInicial = Double.parseDouble(this.lecturaAnterior2.getText().toString());
        } else {
            msg = String.valueOf(msg) + "Lectura Anterior: Valor invalido \n";
        }
        if (!TextUtils.isEmpty(this.lecturaActual2.getText().toString())) {
            this.lectFinal = Double.parseDouble(this.lecturaActual2.getText().toString());
        } else {
            msg = String.valueOf(msg) + "Lectura Actual: Valor invalido \n";
        }
        if (msg == "") {
            if (this.lectFinal < 0.0d) {
                msg = String.valueOf(msg) + "Lectura Actual: Valor invalido \n";
            } else if (this.lectInicial < 0.0d) {
                msg = String.valueOf(msg) + "Lectua Anterior: Valor invalido \n";
            } else if (this.lectFinal < this.lectInicial) {
                msg = String.valueOf(msg) + "La lectura Anterior no puede ser mayor a la Actual \n";
            } else if (this.lectFinal == this.lectInicial) {
                msg = String.valueOf(msg) + "Lectura anterior y Lectura Actual no deben ser iguales \n";
            }
        }
        if (msg == "") {
            incializarVariables();
            this.consumoElectrico = this.lectFinal - this.lectInicial;
            cc(0);
            for (int j = this.e.size(); j > 0; j--) {
            }

            this.cadena = String.valueOf(this.cadena) + "Consumo total: " + String.valueOf(redondearDouble(this.consumoElectrico)) + " Kwh \n";
            this.cadena = String.valueOf(this.cadena) + "Usted debe pagar: " + String.valueOf(redondearDouble(this.consumoTotal)) + " Pesos \n";
            Toast.makeText(getApplicationContext(), this.cadena, Toast.LENGTH_SHORT).show();
            this.consumo.setText("Consumo total: " + String.valueOf(redondearDouble(this.consumoElectrico)) + " Kwh");
            this.aPagar.setText("Usted debe pagar: " + String.valueOf(redondearDouble(this.consumoTotal)) + " Pesos");
            return;
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void Limpiar(View view) {
        this.lecturaAnterior2.setText("");
        this.lecturaAnterior2.requestFocus();
        this.lecturaActual2.setText("");
        this.consumo.setText(R.string.consumoTV);
        this.aPagar.setText(R.string.aPagarTV);

    }

    public void incializarVariables() {
        this.b = new ArrayList<>();
        this.consumoTotal = 0.0d;
        this.cadena = "";
    }

    public double redondearDouble(double numero) {
        return Double.parseDouble(new DecimalFormat("###.##").format(numero));
    }

    public void cc(int i) {
        if (this.consumoElectrico > this.e.rangosConsumo.get(i).getFinRango() || this.consumoElectrico <= this.e.rangosConsumo.get(i).getInicioRango()) {
            this.b.add(Double.valueOf(this.e.rangosConsumo.get(i).getFinRango() - this.e.rangosConsumo.get(i).getInicioRango()));
            this.consumoTotal += (this.e.rangosConsumo.get(i).getFinRango() - this.e.rangosConsumo.get(i).getInicioRango()) * this.e.rangosConsumo.get(i).getPrecioRango();
            cc(i + 1);
            return;
        }
        this.b.add(Double.valueOf(this.consumoElectrico - this.e.rangosConsumo.get(i).getInicioRango()));
        this.consumoTotal += (this.consumoElectrico - this.e.rangosConsumo.get(i).getInicioRango()) * this.e.rangosConsumo.get(i).getPrecioRango();
    }

}