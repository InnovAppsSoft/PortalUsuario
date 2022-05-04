package com.marlon.portalusuario.ui.connectivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.marlon.portalusuario.MainActivity;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.User;
import com.marlon.portalusuario.logging.JCLogging;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;

import trikita.log.Log;

public class WifiAccountDetailsActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    public User my_user;
    TextView saldo, leftTime, estadoCuenta;
    public StringBuilder builder;
    public Button btnDisconnect;
    private ProgressDialog loadingBar;

    private JCLogging logger;

    private int maxTime;
    private String timeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifietecsa_account);

        Intent initialIntent = getIntent();
        maxTime = 0;
        timeType = "";
        if (initialIntent != null){
            if (initialIntent.hasExtra("max_time")){
                maxTime = initialIntent.getIntExtra("max_time", 0);
            }
            if (initialIntent.hasExtra("time_type")){
                timeType = initialIntent.getStringExtra("time_type");
            }
        }
        //
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        logger = new JCLogging(this);
        //
        my_user = User.getUser();
        loadingBar = new ProgressDialog(this);
        saldo = findViewById(R.id.editSaldoCuenta);
        leftTime = findViewById(R.id.textLeftTime);
        estadoCuenta = findViewById(R.id.editEstadoCuenta);
        btnDisconnect = findViewById(R.id.buttonDisconnect);
        saldo.setText(my_user.getAccountCredit());
        estadoCuenta.setText(my_user.getAccountState());
        builder = new StringBuilder();
        sendLeftTime();
        //
//        if (prefs.getBoolean("show_traffic_speed_bubble", false)){
//            MainActivity.showConnectedTime(true);
//        }

        Chronometer simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.setOnChronometerTickListener(cArg -> {
            long time = SystemClock.elapsedRealtime() - cArg.getBase();
            int h   = (int)(time /3600000);
            int m = (int)(time - h*3600000)/60000;
            int s= (int)(time - h*3600000- m*60000)/1000 ;
            String hh = h < 10 ? "0"+h: h+"";
            String mm = m < 10 ? "0"+m: m+"";
            String ss = s < 10 ? "0"+s: s+"";
            String formated_time = String.format("%s:%s:%s", hh, mm, ss);
//            if (prefs.getBoolean("show_traffic_speed_bubble", false)) {
//                MainActivity.setConnectedTime(formated_time);
//            }
            cArg.setText(formated_time);
            //
            try {
                if (maxTime > 0) {
                    if (timeType.equals("horas")) {
                        if (h == maxTime) {
                            sendDisconnect();
                            return;
                        }
                    } else if (timeType.equals("minutos")) {
                        if (m == maxTime) {
                            sendDisconnect();
                            return;
                        }
                    } else if (timeType.equals("segundos")) {
                        if (s == maxTime) {
                            sendDisconnect();
                            return;
                        }
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
                logger.error(null, null, ex);
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

    public void Disconnect(View v) {
        sendDisconnect();
    }

    public void countDown(String time){
        long milisecondsLeft = 0;
        //
        try {
            milisecondsLeft = Integer.parseInt(time.split(":")[0]) * 60 * 60 * 1000 + Integer.parseInt(time.split(":")[1]) * 60 * 1000 + Integer.parseInt(time.split(":")[2]) * 1000;
        }catch (Exception e){
            e.printStackTrace();
            logger.error(null, null, null);
            logger.error(null, null, e);
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
                    logger.error(null, null, e);
                    //
                    sendDisconnect();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        countDown(builder.toString());
                    }
                });
            }
        }).start();
    }
    //
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
                    //startActivity(main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(null, null, e);
                }
                // cleaning preferences
//                SharedPreferences.Editor prefEditor = prefs.edit();
//                prefEditor.putBoolean("connected", false);
//                prefEditor.apply();
//                if (prefs.getBoolean("show_traffic_speed_bubble", false)) {
//                    // hiding floating windows time
//                    MainActivity.showConnectedTime(false);
//                    MainActivity.setConnectedTime("");
//                }
                // hiding loading bar
                loadingBar.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WifiAccountDetailsActivity.this, "Desconectado", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        }).start();
    }

    //----SHOWING ALERT DIALOG FOR EXITING THE APP----
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que deseas salir?");
        builder.setTitle("Portal Usuario");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.setPositiveButton("Menú Príncipal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendDisconnect();
            }
        });
        builder.setNegativeButton("Cancelar",null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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