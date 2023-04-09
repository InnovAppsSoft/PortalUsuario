package com.marlon.portalusuario.view.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.marlon.portalusuario.PortalUsuarioApplication
import com.marlon.portalusuario.R
import com.marlon.portalusuario.net.Communicator
import com.marlon.portalusuario.net.RunTask
import com.marlon.portalusuario.util.AutoCompleteAdapter
import cu.suitetecsa.sdk.nauta.domain.service.NautaClient

class PortalNautaActivity : AppCompatActivity() {
    var userName: String? = null
    var password: String? = null
    var _time: String? = null
    var block_date: String? = null
    var delete_date: String? = null
    var account_type: String? = null
    var service_type: String? = null
    var credit: String? = null
    var mail_account: String? = null
    var recharge_code: String? = null
    var mont: String? = null
    var accountToTransfer: String? = null
    var tv_time: TextView? = null
    var tv_account: TextView? = null
    var tv_block_date: TextView? = null
    var tv_delete_date: TextView? = null
    var tv_account_type: TextView? = null
    var tv_service_type: TextView? = null
    var tv_credit: TextView? = null
    var tv_mail_account: TextView? = null
    var progressDialog: ProgressDialog? = null
    var et_recharge_code: EditText? = null
    var et_mont: EditText? = null
    var actv_accountToTransfer: AutoCompleteTextView? = null
    var bt_recharge: AppCompatButton? = null
    var bt_transfer: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal_nauta)
        tv_time = findViewById(R.id.tv_info_time)
        tv_account = findViewById(R.id.tv_info_user)
        tv_block_date = findViewById(R.id.tv_block_date)
        tv_delete_date = findViewById(R.id.tv_delete_date)
        tv_account_type = findViewById(R.id.tv_account_type)
        tv_service_type = findViewById(R.id.tv_service_type)
        tv_credit = findViewById(R.id.tv_account_credit)
        tv_mail_account = findViewById(R.id.tv_mail_account)

        val datos = intent.extras
        userName = datos!!.getString("userName")
        password = datos.getString("password")
        block_date = datos.getString("blockDate")
        delete_date = datos.getString("delDate")
        account_type = datos.getString("accountType")
        service_type = datos.getString("serviceType")
        credit = datos.getString("credit")
        _time = datos.getString("time")
        mail_account = datos.getString("mailAccount")

        tv_time?.text = _time
        tv_account?.text = userName
        tv_block_date?.text = block_date
        tv_delete_date?.text = delete_date
        tv_account_type?.text = account_type
        tv_service_type?.text = service_type
        tv_credit?.text = credit
        tv_mail_account?.text = mail_account
        et_recharge_code = findViewById(R.id.et_recharge_code)
        bt_recharge = findViewById(R.id.bt_recharge)
        bt_transfer = findViewById(R.id.bt_transfer)
        et_mont = findViewById(R.id.et_mont)
        actv_accountToTransfer = findViewById(R.id.actv_accountToTransfer)
        progressDialog = ProgressDialog(this)
        val adapter = AutoCompleteAdapter(
            this,
            android.R.layout.simple_expandable_list_item_1
        )
        actv_accountToTransfer?.setAdapter(adapter)
        bt_recharge?.setOnClickListener { recharge() }

//        bt_transfer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                transfer(v);
//            }
//        });
    }

    fun recharge() {
        if (et_recharge_code!!.text.toString().length != 12 &&
            et_recharge_code!!.text.toString().length != 16
        ) {
            et_recharge_code!!.error = "El código de recarga debe ser de 12 o 16 dígitos."
        } else {
            progressDialog!!.setTitle("Portal Usuario")
            progressDialog!!.setIcon(R.mipmap.ic_launcher)
            progressDialog!!.setMessage("Recargando...")
            progressDialog!!.show()
            recharge_code = et_recharge_code!!.text.toString()
            RunTask(object : Communicator {
                override fun Communicate() {}
                override fun Communicate(client: NautaClient) {
                    client.toUpBalance(recharge_code!!)
                }

                override fun communicate() {
                    progressDialog!!.dismiss()
                    val builder = AlertDialog.Builder(this@PortalNautaActivity)
                    builder.setTitle("Portal Usuario").setMessage("Cuenta recargada!")
                    builder.setPositiveButton("OK", null)
                    val success = builder.create()
                    success.setCancelable(false)
                    success.show()
                }
            }, PortalUsuarioApplication.client).execute()
        }
    }

    //    public void transfer(View v) {
    //        int validate = 0;
    //        if (et_mont.getText().toString().length() == 0) {
    //            et_mont.setError("Introduzca un monto válido");
    //            validate = 1;
    //        }
    //        if (actv_accountToTransfer.getText().toString().equals("")) {
    //            actv_accountToTransfer.setError("Introduzca un nombre de usuario");
    //            validate = 1;
    //        } else if (!actv_accountToTransfer.getText().toString().endsWith("@nauta.com.cu") &&
    //                !actv_accountToTransfer.getText().toString().endsWith("@nauta.co.cu")) {
    //            actv_accountToTransfer.setError("Introduzca un nombre de usuario válido");
    //            validate = 1;
    //        }
    //        if (validate != 1) {
    //            if (credit.equals("$0,00 CUP")) {
    //                AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //                builder.setTitle("Portal Usuario").setMessage("Su saldo es insuficiente!");
    //                builder.setPositiveButton("OK", null);
    //                AlertDialog success = builder.create();
    //                success.setCancelable(false);
    //                success.show();
    //            } else {
    //                progressDialog.setTitle("Portal Usuario");
    //                progressDialog.setMessage("Transfiriendo...");
    //                progressDialog.show();
    //                mont = et_mont.getText().toString();
    //                accountToTransfer = actv_accountToTransfer.getText().toString();
    //                new RunTask(new Communicator() {
    //                    @Override
    //                    public void Communicate() {
    //
    //                    }
    //
    //                    @Override
    //                    public void Communicate(NautaClient client) {
    //                        try {
    //                            client.transferBalance(mont,
    //                                    password,
    //                                    accountToTransfer,
    //                                    cookies);
    //                        } catch (IOException e) {
    //                            e.printStackTrace();
    //                        }
    //                    }
    //
    //                    @Override
    //                    public void communicate() {
    //                        progressDialog.dismiss();
    //                        AlertDialog.Builder builder = new AlertDialog.Builder(PortalNautaActivity.this);
    //                        if (userPortal.status().get("status").equals("error")) {
    //                            List<String> errors = (List<String>) userPortal.status().get("msg");
    //                            String errors_ = "Se encontraron los siguientes errores al intentar" +
    //                                    " transferir el saldo:";
    //                            for (String error : errors) {
    //                                errors_ = errors_ + "\n" + error;
    //                            }
    //                            builder.setTitle("Portal Usuario").setMessage(errors_);
    //                            AlertDialog success = builder.create();
    //                            success.show();
    //                        } else {
    //                            builder.setTitle("Portal Usuario").setMessage("Transferencia realizada!");
    //                            AlertDialog success = builder.create();
    //                            success.show();
    //                        }
    //                    }
    //                }, userPortal).execute();
    //            }
    //        }
    //    }
    //----SHOWING ALERT DIALOG FOR EXITING THE APP----
    override fun onBackPressed() {
        super.onBackPressed()
        //        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(this);
//        builder.setMessage("Estas a punto de desloguear la cuenta y regresar al menú príncipal");
//        builder.setTitle("Portal Usuario");
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setCancelable(false);
//        builder.setPositiveButton("Menú Príncipal",new MyListener());
//        builder.setNegativeButton("Cancelar",null);
//        builder.show();
    } //    public class MyListener implements DialogInterface.OnClickListener{
    //
    //        @Override
    //        public void onClick(DialogInterface dialog, int which) {
    //            startActivity(new Intent(PortalNautaActivity.this, Main3Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    //        }
    //    }
}