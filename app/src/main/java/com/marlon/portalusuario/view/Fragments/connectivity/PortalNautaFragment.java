package com.marlon.portalusuario.view.Fragments.connectivity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;
import com.makeramen.roundedimageview.RoundedImageView;
import com.marlon.portalusuario.util.AutoCompleteAdapter;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.view.activities.PortalNautaActivity;
import com.marlon.portalusuario.net.Communicator;
import com.marlon.portalusuario.net.RunTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cu.marilasoft.selibrary.UserPortal;
import soup.neumorphism.NeumorphButton;

public class PortalNautaFragment extends Fragment {

    private SharedPreferences pref;
    String _time;
    String account;
    String block_date;
    String delete_date;
    String account_type;
    String service_type;
    String credit;
    String mail_account;

    AutoCompleteTextView actv_user_name;
    TextInputEditText et_password;
    TextInputEditText et_captcha_code;
    RoundedImageView iv_captcha_img;
    NeumorphButton bt_login_acept;
    Bitmap bitmap = null;
    ProgressDialog progressDialog;
    Intent intent;
    Button btnReload;
    Button btnLogin;

    Map<String, String> cookies;
    Map<Object, Object> status;

    UserPortal userPortal = new UserPortal();

    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_portalnauta, container, false);

        actv_user_name = view.findViewById(R.id.actv);
        et_password = view.findViewById(R.id.et_password);
        et_captcha_code = view.findViewById(R.id.et_captchaCode);
        iv_captcha_img = view.findViewById(R.id.iv_captcha);
        bt_login_acept = view.findViewById(R.id.bt_login);
        bt_login_acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btnReload = view.findViewById(R.id.bt_reload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reLoadCaptcha();
            }
        });
        progressDialog = new ProgressDialog(getContext());
        intent = new Intent(getContext(), PortalNautaActivity.class);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getContext(), android.R.layout.simple_expandable_list_item_1);
        //ArrayAdapter adapter1 = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1, dbHandler.selectAllNautaUsers());
        actv_user_name.setAdapter(adapter);

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        String user = pref.getString("nauta_user", "");
        String password = pref.getString("nauta_password", "");
        actv_user_name.setText(user);
        et_password.setText(password);

        preLoad();
        return view;
    }

    public void preLoad() {
        new RunTask(new Communicator() {
            @Override
            public void Communicate() {

            }

            @Override
            public void Communicate(UserPortal userPortal) {
                try {
                    userPortal.preLogin();
                    Map<String, String> cookies = userPortal.cookies();
                    userPortal.loadCAPTCHA(cookies);
                    cookies = cookies;
                    userPortal = userPortal;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void communicate() {
                if (userPortal.captchaImg() != null) {
                    bitmap = BitmapFactory.decodeStream(
                            new ByteArrayInputStream(userPortal.captchaImg())
                    );
                    iv_captcha_img.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getContext(),
                            "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();
                }
            }
        }, userPortal).execute();
    }

    public void reLoadCaptcha() {
        new RunTask(new Communicator() {
            @Override
            public void Communicate() {

            }

            @Override
            public void Communicate(UserPortal userPortal) {
                try {
                    if (cookies == null) {
                        userPortal.preLogin();
                        cookies = userPortal.cookies();
                        userPortal = userPortal;
                    }
                    userPortal.loadCAPTCHA(cookies);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void communicate() {
                if (userPortal.captchaImg() != null) {
                    bitmap = BitmapFactory.decodeStream(
                            new ByteArrayInputStream(userPortal.captchaImg())
                    );
                    iv_captcha_img.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getContext(),
                            "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();
                }
            }
        }, userPortal).execute();
    }

    public void login() {
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Conectando...");
        progressDialog.setCancelable(false);
        int validate = 0;
        if (actv_user_name.getText().toString().equals("")) {
            actv_user_name.setError("Introduzca un nombre de usuario");
            validate = 1;
        } else if (!actv_user_name.getText().toString().endsWith("@nauta.com.cu") &&
                !actv_user_name.getText().toString().endsWith("@nauta.co.cu")) {
            actv_user_name.setError("Introduzca un nombre de usuario válido");
            validate = 1;
        }
        if (et_password.getText().toString().equals("")) {
            et_password.setError("Introduzca una contraseña");
            validate = 1;
        }
        if (et_captcha_code.getText().toString().equals("")) {
            et_captcha_code.setError("Introduzca el cddigo captcha.");
            validate = 1;
        }
        if (validate != 1) {
            progressDialog.show();
            new RunTask(new Communicator() {
                @Override
                public void Communicate() {

                }

                @Override
                public void Communicate(UserPortal userPortal) {
                    try {
                        userPortal.login(actv_user_name.getText().toString(),
                                et_password.getText().toString(),
                                et_captcha_code.getText().toString(),
                                cookies);
                        status = userPortal.status();
                        if (userPortal.status().get("status").equals("success")) {
                            account = userPortal.userName();
                            _time = userPortal.time();
                            block_date = userPortal.blockDate();
                            delete_date = userPortal.delDate();
                            account_type = userPortal.accountType();
                            service_type = userPortal.serviceType();
                            credit = userPortal.credit();
                            mail_account = userPortal.mailAccount();
                            //
                            // guardar usuario
                            SharedPreferences.Editor prefsEditor = pref.edit();
                            prefsEditor.putString("nauta_user", actv_user_name.getText().toString());
                            prefsEditor.putString("nauta_password", et_password.getText().toString());
                            prefsEditor.apply();
                            //
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void communicate() {
                    String errors = "";
                    try {
                        progressDialog.dismiss();
                        if (status.get("status").equals("success")) {
                            intent.putExtra("userName",
                                    account);
                            intent.putExtra("password",
                                    et_password.getText().toString());
                            intent.putExtra("blockDate",
                                    block_date);
                            intent.putExtra("delDate",
                                    delete_date);
                            intent.putExtra("accountType",
                                    account_type);
                            intent.putExtra("serviceType",
                                    service_type);
                            intent.putExtra("credit",
                                    credit);
                            intent.putExtra("time",
                                    _time);
                            intent.putExtra("mailAccount",
                                    mail_account);
                            intent.putExtra("session",
                                    cookies.get("session"));
                            intent.putExtra("nauta_lang",
                                    cookies.get("nauta_lang"));
                            transition();
                        } else if (status.get("status").equals("error")) {
                            for (String error : (List<String>) status.get("msg")) {
                                errors += "\n" + error;
                                if (error.contains("Usuario")) {
                                    actv_user_name.setError("Puede que el " +
                                            "nombre de usuario sea incorrecto");
                                }
                                if (error.contains("contraseña")) {
                                    et_password.setError("Puede que la " +
                                            "contraseña sea incorrecta");
                                }
                                if (error.contains("Captcha")) {
                                    et_captcha_code.setError(error);
                                }
                            }
                            reLoadCaptcha();
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(getContext(), "Ha ocurrido un error :-(\n" + errors, Toast.LENGTH_LONG).show();
                    }
                }
            }, userPortal).execute();
        }
    }

    public void transition() {
        startActivity(intent);
    }
}
