package com.marlon.portalusuario;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;

import trikita.log.Log;

public class DatosUsuario extends AppCompatActivity {

    public User my_user;
    TextView saldo, leftTime, estadoCuenta;
    public Intent main;
    public StringBuilder builder;
    public Button btnDisconnect;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuarios_datos);

        my_user = User.getUser();
        loadingBar = new ProgressDialog(this);
        saldo = findViewById(R.id.editSaldoCuenta);
        leftTime = findViewById(R.id.textLeftTime);
        estadoCuenta = findViewById(R.id.editEstadoCuenta);
        btnDisconnect = findViewById(R.id.buttonDisconnect);
        saldo.setText(my_user.getSaldoCuenta());
        estadoCuenta.setText(my_user.getEstadoCuenta());
        main = new Intent(this, WifiFragment.class);
        builder = new StringBuilder();
        sendLeftTime();
        Chronometer simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(String.format("%s:%s:%s", hh, mm, ss));
            }
        });
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.start();

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDisconnect();
            }
        });
    }

    public void saveUser(){
        Gson g = new Gson();
        String data = g.toJson(my_user);
        try {
            FileOutputStream fos = openFileOutput("dataNautaConnect.dat", Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(DatosUsuario.this, "Datos guardados", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(DatosUsuario.this, "Error guardando los datos !", Toast.LENGTH_SHORT).show();
        }
    }

    public void Disconnect(View v) {
        sendDisconnect();
    }

    public void leftTime(View v){
        sendLeftTime();
    }

    public void countDown(String time){
        long milisecondsLeft = 0;

        try {
            milisecondsLeft = Integer.parseInt(time.split(":")[0]) * 60 * 60 * 1000 + Integer.parseInt(time.split(":")[1]) * 60 * 1000 + Integer.parseInt(time.split(":")[2]) * 1000;
        }catch (Exception e){
            e.printStackTrace();
        }
        new CountDownTimer(milisecondsLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                int h   = (int)(millisUntilFinished /3600000);
                int m = (int)(millisUntilFinished - h*3600000)/60000;
                int s= (int)(millisUntilFinished - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                leftTime.setText(String.format("%s:%s:%s", hh, mm, ss));
            }

            public void onFinish() {
                leftTime.setText("00:00:00");
            }
        }.start();
    }

    public void sendLeftTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document leftTimeDocument = Jsoup.connect("https://secure.etecsa.net:8443//EtecsaQueryServlet")
                            .data("username", my_user.getUsername()).data("ATTRIBUTE_UUID", my_user.getATTRIBUTE_UUID())
                            .data("op", "getLeftTime")
                            .followRedirects(true).post();
                    my_user.setLeftTime(leftTimeDocument.select("body").text());

                    builder.append(my_user.getLeftTime());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        countDown(builder.toString());
                        saveUser();
                    }
                });
            }

        }).start();
    }

    public void sendDisconnect() {

        loadingBar.setTitle("Desconectando");
        loadingBar.setMessage("Por favor espere....");
        loadingBar.setIcon(R.mipmap.ic_launcher);
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                Document loggin = null;
                try {
                    loggin = Jsoup.connect("https://secure.etecsa.net:8443/LogoutServlet")
                            .data("username", my_user.getUsername()).data("ATTRIBUTE_UUID", my_user.getATTRIBUTE_UUID()).followRedirects(true).post();
                    System.out.println(loggin.body().toString().split("'")[1]);
                    startActivity(new Intent(DatosUsuario.this, Main3Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    loadingBar.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DatosUsuario.this, "Desconectado", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });
            }
        }).start();
    }

    //----SHOWING ALERT DIALOG FOR EXITING THE APP----
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(DatosUsuario.this);
        builder.setMessage("Estas a punto de desloguear tu cuenta y regresar al menú príncipal");
        builder.setTitle("Portal Usuario");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.setPositiveButton("Menú Príncipal",new DatosUsuario.MyListener());
        builder.setNegativeButton("Cancelar",null);
        builder.show();

    }

    public class MyListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {
            loadingBar.setTitle("Desconectando");
            loadingBar.setMessage("Por favor espere....");
            loadingBar.setIcon(R.mipmap.ic_launcher);
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            sendDisconnect();
            startActivity(new Intent(DatosUsuario.this, Main3Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            loadingBar.dismiss();
        }
    }

    @Override
    public void onResume() {
        Log.e("Data", "onResume of Ok");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("Data", "OnPause of Ok");
        super.onPause();
    }
}