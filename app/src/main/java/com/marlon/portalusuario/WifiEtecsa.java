package com.marlon.portalusuario;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphImageButton;
import trikita.log.Log;

public class WifiEtecsa extends AppCompatActivity {

    EditText  edit_password;
    TextView errorsTextView;
    public User my_user;
    public boolean user_passw_error;
    private ProgressDialog loadingBar;
    private NeumorphImageButton Avanzar;

    private AutoCompleteTextView actv_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_etecsa);
        Avanzar = findViewById(R.id.portalavanzar);
        NeumorphButton btn_connect = findViewById(R.id.buttonConnect);
        actv_user_name = findViewById(R.id.editUsername);
        edit_password = findViewById(R.id.editPassword);
        errorsTextView = findViewById(R.id.errorTextView);
        loadingBar = new ProgressDialog(this);

        user_passw_error = false;

        this.my_user = User.getUser();
        loadUser();
        if (my_user.getUsername()!="prp"){
            actv_user_name.setText(my_user.getUsername());
            edit_password.setText(my_user.getPassword());
        }else{
            actv_user_name.setText("");
            edit_password.setText("");
        }

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_expandable_list_item_1);
        actv_user_name.setAdapter(adapter);


        Avanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WifiEtecsa.this, PortalUsuarioActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendConnect();
            }
        });
    }

    public void loadUser(){
        String data = "";

        try{
            FileInputStream fis = openFileInput("dataNautaConnect.dat");
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            data = new String(buffer);
            if (data!=""){
                Gson gson = new Gson();
                User new_user = gson.fromJson(data, User.class);
                my_user.setUsername(new_user.getUsername());
                my_user.setPassword(new_user.getPassword());
                Toast.makeText(WifiEtecsa.this, "Usuario cargado", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(WifiEtecsa.this, "No se cargaron los usuarios", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendConnect()  {

        loadingBar.setTitle("Inciando Sesión");
        loadingBar.setMessage("Por favor espere....");
        loadingBar.setIcon(R.mipmap.ic_launcher);
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                final StringBuilder builder = new StringBuilder();

                try {
                    String str = "td";
                    String str2 = "";
                    @SuppressLint("SimpleDateFormat") String format = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
                    Connection.Response execute = Jsoup.connect("https://secure.etecsa.net:8443").method(Connection.Method.GET).execute();

                    my_user.setCSRFHW(execute.parse().select("input[name=CSRFHW]").first().val());
                    my_user.setUsername(actv_user_name.getText().toString());
                    my_user.setPassword(edit_password.getText().toString());

                    Document post = Jsoup.connect("https://secure.etecsa.net:8443/EtecsaQueryServlet").cookies(execute.cookies())
                            .data("wlanacname", str2).data("wlanmac", str2).data("firsturl", "notFound.jsp")
                            .data("ssid", str2).data("usertype", str2).data("gotopage", "/nauta_etecsa/LoginURL/mobile_login.jsp")
                            .data("successpage", "/nauta_etecsa/OnlineURL/mobile_index.jsp")
                            .data("loggerId", format).data("lang", "es_ES").data("username", my_user.getUsername())
                            .data("password", my_user.getPassword())
                            .data("CSRFHW", my_user.getCSRFHW()).followRedirects(true).post();
                    if(!post.select("script").last().toString().contains("alert(\"return null\");")){
                        my_user.setSaldoCuenta(post.select("table#sessioninfo > tbody > tr > td").get(3).text());
                        my_user.setEstadoCuenta(post.select("table#sessioninfo > tbody > tr > td").get(1).text());

                        Document loggin = Jsoup.connect("https://secure.etecsa.net:8443/LoginServlet")
                                .data("username", my_user.getUsername()).data("password", my_user.getPassword()).followRedirects(true).post();

                        my_user.setATTRIBUTE_UUID(loggin.select("script").first().toString().split("ATTRIBUTE_UUID=")[1].split("&")[0]);

                        System.out.println(my_user);
                        sendMessage(null);
                    }else{
                        user_passw_error = true;
                    }


                }catch (Exception e){
                    System.out.println(e.getMessage());
                    builder.append("error: ").append(e.getMessage()).append("\n");

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(builder.toString());
                        if (user_passw_error){
                            errorsTextView.setText(R.string.user_passw_error);
                            edit_password.setText("");
                            actv_user_name.setText("");
                            loadingBar.dismiss();
                        }
                    }
                });

            }
        }).start();

    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DatosUsuario.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        WifiEtecsa.super.onBackPressed();
    }

    @Override
    public void onResume() {
        Log.e("Wifi", "onResume of Wifi");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("Wifi", "OnPause of Wifi");
        super.onPause();
    }
}

