package com.marlon.portalusuario;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.marlon.portalusuario.net.Communicator;
import com.marlon.portalusuario.net.RunTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cu.marilasoft.selibrary.UserPortal;
import soup.neumorphism.NeumorphButton;

public class PortalUsuarioActivity extends AppCompatActivity {

    String _time;
    String account;
    String block_date;
    String delete_date;
    String account_type;
    String service_type;
    String credit;
    String mail_account;

    AutoCompleteTextView actv_user_name;
    EditText et_password;
    EditText et_captcha_code;
    ImageView iv_captcha_img;
    NeumorphButton bt_login_acept;
    Bitmap bitmap = null;
    ProgressDialog progressDialog;
    Intent intent;

    Map<String, String> cookies;
    Map<Object, Object> status;

    UserPortal userPortal = new UserPortal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portalusuario);

        this.actv_user_name = findViewById(R.id.actv);
        this.et_password = findViewById(R.id.et_password);
        this.et_captcha_code = findViewById(R.id.et_captchaCode);
        this.iv_captcha_img = findViewById(R.id.iv_captcha);
        this.bt_login_acept = findViewById(R.id.bt_login);

        this.progressDialog = new ProgressDialog(this);
        this.intent = new Intent(this, UserInfo.class);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_expandable_list_item_1);
        actv_user_name.setAdapter(adapter);

        preLoad();
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
                    PortalUsuarioActivity.this.cookies = cookies;
                    PortalUsuarioActivity.this.userPortal = userPortal;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void communicate() {
                if (userPortal.captchaImg() != null) {
                    PortalUsuarioActivity.this.bitmap = BitmapFactory.decodeStream(
                            new ByteArrayInputStream(userPortal.captchaImg())
                    );
                    PortalUsuarioActivity.this.iv_captcha_img.setImageBitmap(PortalUsuarioActivity.this.bitmap);
                } else {
                    Toast.makeText(PortalUsuarioActivity.this,
                            "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();
                }
            }
        }, userPortal).execute();
    }

    public void reLoadCaptcha(View v) {
        reLoadCaptcha();
    }

    public void reLoadCaptcha() {
        new RunTask(new Communicator() {
            @Override
            public void Communicate() {

            }

            @Override
            public void Communicate(UserPortal userPortal) {
                try {
                    if (PortalUsuarioActivity.this.cookies == null) {
                        userPortal.preLogin();
                        PortalUsuarioActivity.this.cookies = userPortal.cookies();
                        PortalUsuarioActivity.this.userPortal = userPortal;
                    }
                    userPortal.loadCAPTCHA(PortalUsuarioActivity.this.cookies);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void communicate() {
                if (userPortal.captchaImg() != null) {
                    PortalUsuarioActivity.this.bitmap = BitmapFactory.decodeStream(
                            new ByteArrayInputStream(userPortal.captchaImg())
                    );
                    PortalUsuarioActivity.this.iv_captcha_img.setImageBitmap(PortalUsuarioActivity.this.bitmap);
                } else {
                    Toast.makeText(PortalUsuarioActivity.this,
                            "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();
                }
            }
        }, userPortal).execute();
    }

    public void login(View v) {
        progressDialog.setIcon(R.mipmap.ic_launcher);
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
                        userPortal.login(PortalUsuarioActivity.this.actv_user_name.getText().toString(),
                                PortalUsuarioActivity.this.et_password.getText().toString(),
                                PortalUsuarioActivity.this.et_captcha_code.getText().toString(),
                                PortalUsuarioActivity.this.cookies);
                        PortalUsuarioActivity.this.status = userPortal.status();
                        if (userPortal.status().get("status").equals("success")) {
                            PortalUsuarioActivity.this.account = userPortal.userName();
                            PortalUsuarioActivity.this._time = userPortal.time();
                            PortalUsuarioActivity.this.block_date = userPortal.blockDate();
                            PortalUsuarioActivity.this.delete_date = userPortal.delDate();
                            PortalUsuarioActivity.this.account_type = userPortal.accountType();
                            PortalUsuarioActivity.this.service_type = userPortal.serviceType();
                            PortalUsuarioActivity.this.credit = userPortal.credit();
                            PortalUsuarioActivity.this.mail_account = userPortal.mailAccount();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        PortalUsuarioActivity.this.progressDialog.dismiss();
                    }
                }

                @Override
                public void communicate() {
                    PortalUsuarioActivity.this.progressDialog.dismiss();
                    if (PortalUsuarioActivity.this.status.get("status").equals("success")) {
                        PortalUsuarioActivity.this.intent.putExtra("userName",
                                PortalUsuarioActivity.this.account);
                        PortalUsuarioActivity.this.intent.putExtra("password",
                                PortalUsuarioActivity.this.et_password.getText().toString());
                        PortalUsuarioActivity.this.intent.putExtra("blockDate",
                                PortalUsuarioActivity.this.block_date);
                        PortalUsuarioActivity.this.intent.putExtra("delDate",
                                PortalUsuarioActivity.this.delete_date);
                        PortalUsuarioActivity.this.intent.putExtra("accountType",
                                PortalUsuarioActivity.this.account_type);
                        PortalUsuarioActivity.this.intent.putExtra("serviceType",
                                PortalUsuarioActivity.this.service_type);
                        PortalUsuarioActivity.this.intent.putExtra("credit",
                                PortalUsuarioActivity.this.credit);
                        PortalUsuarioActivity.this.intent.putExtra("time",
                                PortalUsuarioActivity.this._time);
                        PortalUsuarioActivity.this.intent.putExtra("mailAccount",
                                PortalUsuarioActivity.this.mail_account);
                        PortalUsuarioActivity.this.intent.putExtra("session",
                                PortalUsuarioActivity.this.cookies.get("session"));
                        PortalUsuarioActivity.this.intent.putExtra("nauta_lang",
                                PortalUsuarioActivity.this.cookies.get("nauta_lang"));
                        PortalUsuarioActivity.this.transition();
                    } else if (PortalUsuarioActivity.this.status.get("status").equals("error")) {
                        String errors = "";
                        for (String error : (List<String>) PortalUsuarioActivity.this.status.get("msg")) {
                            errors += "\n" + error;
                            if (error.contains("Usuario")) {
                                PortalUsuarioActivity.this.actv_user_name.setError("Puede que el " +
                                        "nombre de usuario sea incorrecto");
                            }
                            if (error.contains("contraseña")) {
                                PortalUsuarioActivity.this.et_password.setError("Puede que la " +
                                        "contraseña sea incorrecta");
                            }
                            if (error.contains("Captcha")) {
                                PortalUsuarioActivity.this.et_captcha_code.setError(error);
                            }
                        }
                        Toast.makeText(PortalUsuarioActivity.this, "No se pudo iniciar sesión" +
                                "por los siguientes errores:" + errors, Toast.LENGTH_LONG).show();
                        PortalUsuarioActivity.this.reLoadCaptcha();
                    }
                }
            }, userPortal).execute();
        }
    }

    public void transition() {
        startActivity(intent);
    }
}
