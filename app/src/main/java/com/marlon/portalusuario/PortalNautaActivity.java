package com.marlon.portalusuario;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.marlon.portalusuario.net.Communicator;
import com.marlon.portalusuario.net.RunTask;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cu.marilasoft.selibrary.UserPortal;
public class PortalNautaActivity extends AppCompatActivity {

    String userName;
    String password;

    String _time;
    String block_date;
    String delete_date;
    String account_type;
    String service_type;
    String credit;
    String mail_account;
    String recharge_code;

    String mont;
    String accountToTransfer;

    Map<String, String> cookies = new HashMap<>();

    TextView tv_time;
    TextView tv_account;
    TextView tv_block_date;
    TextView tv_delete_date;
    TextView tv_account_type;
    TextView tv_service_type;
    TextView tv_credit;
    TextView tv_mail_account;

    ProgressDialog progressDialog;

    EditText et_recharge_code;
    EditText et_mont;
    AutoCompleteTextView actv_accountToTransfer;
    Button bt_recharge;

    UserPortal userPortal = new UserPortal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_nauta);

        tv_time = findViewById(R.id.tv_info_time);
        tv_account = findViewById(R.id.tv_info_user);
        tv_block_date = findViewById(R.id.tv_block_date);
        tv_delete_date = findViewById(R.id.tv_delete_date);
        tv_account_type = findViewById(R.id.tv_account_type);
        tv_service_type = findViewById(R.id.tv_service_type);
        tv_credit = findViewById(R.id.tv_account_credit);
        tv_mail_account = findViewById(R.id.tv_mail_account);

        Bundle datos = getIntent().getExtras();
        userName = datos.getString("userName");
        password = datos.getString("password");
        block_date = datos.getString("blockDate");
        delete_date = datos.getString("delDate");
        account_type = datos.getString("accountType");
        service_type = datos.getString("serviceType");
        credit = datos.getString("credit");
        _time = datos.getString("time");
        mail_account = datos.getString("mailAccount");

        cookies.put("session", datos.getString("session"));
        cookies.put("nauta_lang", datos.getString("nauta_lang"));

        tv_time.setText(_time);
        tv_account.setText(userName);
        tv_block_date.setText(block_date);
        tv_delete_date.setText(delete_date);
        tv_account_type.setText(account_type);
        tv_service_type.setText(service_type);
        tv_credit.setText(credit);
        tv_mail_account.setText(mail_account);

        et_recharge_code = findViewById(R.id.et_recharge_code);
        bt_recharge = findViewById(R.id.bt_recharge);
        et_mont = findViewById(R.id.et_mont);
        actv_accountToTransfer = findViewById(R.id.actv_accountToTransfer);

        progressDialog = new ProgressDialog(this);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1);
        actv_accountToTransfer.setAdapter(adapter);
    }

    public void recharge(View v) {
        if (et_recharge_code.getText().toString().length() != 12 &&
                et_recharge_code.getText().toString().length() != 16) {
            et_recharge_code.setError("El código de recarga debe ser de 12 o 16 dígitos.");
        } else {
            progressDialog.setTitle("Portal Usuario");
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setMessage("Recargando...");
            progressDialog.show();
            recharge_code = et_recharge_code.getText().toString();
            new RunTask(new Communicator() {
                @Override
                public void Communicate() {

                }

                @Override
                public void Communicate(UserPortal userPortal) {
                    try {
                        userPortal.recharge(recharge_code,
                                cookies);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void communicate() {
                    progressDialog.dismiss();
                    if (userPortal.status().get("status").equals("error")) {
                        List<String> errors = (List<String>) userPortal.status().get("msg");
                        et_recharge_code.setError(errors.get(0));
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(PortalNautaActivity.this);
                        builder.setTitle("Portal Usuario").setMessage("Cuenta recargada!");
                        builder.setPositiveButton("OK", null);
                        AlertDialog success = builder.create();
                        success.setCancelable(false);
                        success.show();
                    }
                }
            }, userPortal).execute();
        }
    }

    public void transfer(View v) {
        int validate = 0;
        if (et_mont.getText().toString().length() == 0) {
            et_mont.setError("Introduzca un monto válido");
            validate = 1;
        }
        if (actv_accountToTransfer.getText().toString().equals("")) {
            actv_accountToTransfer.setError("Introduzca un nombre de usuario");
            validate = 1;
        } else if (!actv_accountToTransfer.getText().toString().endsWith("@nauta.com.cu") &&
                !actv_accountToTransfer.getText().toString().endsWith("@nauta.co.cu")) {
            actv_accountToTransfer.setError("Introduzca un nombre de usuario válido");
            validate = 1;
        }
        if (validate != 1) {
            if (credit.equals("$0,00 CUP")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Portal Usuario").setMessage("Su saldo es insuficiente!");
                builder.setPositiveButton("OK", null);
                AlertDialog success = builder.create();
                success.setCancelable(false);
                success.show();
            } else {
                progressDialog.setTitle("Portal Usuario");
                progressDialog.setMessage("Transfiriendo...");
                progressDialog.show();
                mont = et_mont.getText().toString();
                accountToTransfer = actv_accountToTransfer.getText().toString();
                new RunTask(new Communicator() {
                    @Override
                    public void Communicate() {

                    }

                    @Override
                    public void Communicate(UserPortal userPortal) {
                        try {
                            userPortal.transfer(mont,
                                    password,
                                    accountToTransfer,
                                    cookies);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void communicate() {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PortalNautaActivity.this);
                        if (userPortal.status().get("status").equals("error")) {
                            List<String> errors = (List<String>) userPortal.status().get("msg");
                            String errors_ = "Se encontraron los siguientes errores al intentar" +
                                    " transferir el saldo:";
                            for (String error : errors) {
                                errors_ = errors_ + "\n" + error;
                            }
                            builder.setTitle("Portal Usuario").setMessage(errors_);
                            AlertDialog success = builder.create();
                            success.show();
                        }else{
                            builder.setTitle("Portal Usuario").setMessage("Transferencia realizada!");
                            AlertDialog success = builder.create();
                            success.show();
                        }
                    }
                }, userPortal).execute();
            }
        }
    }

    //----SHOWING ALERT DIALOG FOR EXITING THE APP----
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(this);
//        builder.setMessage("Estas a punto de desloguear la cuenta y regresar al menú príncipal");
//        builder.setTitle("Portal Usuario");
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setCancelable(false);
//        builder.setPositiveButton("Menú Príncipal",new MyListener());
//        builder.setNegativeButton("Cancelar",null);
//        builder.show();
    }

//    public class MyListener implements DialogInterface.OnClickListener{
//
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            startActivity(new Intent(PortalNautaActivity.this, Main3Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        }
//    }
}

