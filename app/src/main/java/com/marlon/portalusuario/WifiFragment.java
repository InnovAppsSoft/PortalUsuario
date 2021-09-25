package com.marlon.portalusuario;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class WifiFragment extends Fragment {

    private static Context context;

    EditText edit_password;
    TextView errorsTextView,textweb;
    public User my_user;
    public boolean user_passw_error;

    private AutoCompleteTextView actv_user_name;
    private ProgressDialog loadingBar;
    private NeumorphImageButton Avanzar;

    static Fragment newInstance(Context ctx) {
        context = ctx;
        return new WifiFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wifi, container, false);

        Avanzar = view.findViewById(R.id.portalavanzar);
        NeumorphButton btn_connect = view.findViewById(R.id.buttonConnect);
        actv_user_name = view.findViewById(R.id.editUsername);
        edit_password = view.findViewById(R.id.editPassword);
        errorsTextView = view.findViewById(R.id.errorTextView);
        loadingBar = new ProgressDialog(context);
        textweb = view.findViewById(R.id.web);

        user_passw_error = false;

        my_user = User.getUser();


        AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, android.R.layout.simple_expandable_list_item_1);
        actv_user_name.setAdapter(adapter);


        loadUser();

        if (my_user.getUsername()!="prp"){
            actv_user_name.setText(my_user.getUsername());
            edit_password.setText(my_user.getPassword());
        }else{
            actv_user_name.setText("");
            edit_password.setText("");
        }


        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConnect();
            }
        });

        textweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PaginasActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        Avanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PortalUsuarioActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        return view;
    }

    public void loadUser(){
        String data = "";

        try{
            FileInputStream fis = context.openFileInput("dataNautaConnect.dat");
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
                Toast.makeText(WifiFragment.context, "Usuario cargado", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendConnect()  {
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
                getActivity().runOnUiThread(new Runnable() {
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
        Intent intent = new Intent(WifiFragment.context, DatosUsuario.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
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
