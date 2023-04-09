package com.marlon.portalusuario.view.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.marlon.portalusuario.PortalUsuarioApplication.Companion.client
import com.marlon.portalusuario.R
import com.marlon.portalusuario.util.AutoCompleteAdapter

class PortalNautaActivity : AppCompatActivity() {
    var userName: String? = null
    var password: String? = null
    private var remainingTime: String? = null
    private var blockingDate: String? = null
    private var dateOfElimination: String? = null
    var accountType: String? = null
    private var serviceType: String? = null
    var credit: String? = null
    var mailAccount: String? = null
    var rechargeCode: String? = null
    var mont: String? = null
    var accountToTransfer: String? = null
    private var tvTime: TextView? = null
    private var tvAccount: TextView? = null
    var tvBlockingDate: TextView? = null
    var tvDateOfElimination: TextView? = null
    var tvAccountType: TextView? = null
    var tvServiceType: TextView? = null
    var tvCredit: TextView? = null
    var tvMailAccount: TextView? = null
    var progressDialog: ProgressDialog? = null
    var etRechargeCode: EditText? = null
    var etAmount: EditText? = null
    private var autoCompleteTextViewAccountToTransfer: AutoCompleteTextView? = null
    var btRecharge: AppCompatButton? = null
    var btTransfer: AppCompatButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal_nauta)
        tvTime = findViewById(R.id.tv_info_time)
        tvAccount = findViewById(R.id.tv_info_user)
        tvBlockingDate = findViewById(R.id.tv_block_date)
        tvDateOfElimination = findViewById(R.id.tv_delete_date)
        tvAccountType = findViewById(R.id.tv_account_type)
        tvServiceType = findViewById(R.id.tv_service_type)
        tvCredit = findViewById(R.id.tv_account_credit)
        tvMailAccount = findViewById(R.id.tv_mail_account)

        val data = intent.extras
        userName = data!!.getString("userName")
        password = data.getString("password")
        blockingDate = data.getString("blockDate")
        dateOfElimination = data.getString("delDate")
        accountType = data.getString("accountType")
        serviceType = data.getString("serviceType")
        credit = data.getString("credit")
        remainingTime = data.getString("time")
        mailAccount = data.getString("mailAccount")

        tvTime?.text = remainingTime
        tvAccount?.text = userName
        tvBlockingDate?.text = blockingDate
        tvDateOfElimination?.text = dateOfElimination
        tvAccountType?.text = accountType
        tvServiceType?.text = serviceType
        tvCredit?.text = credit
        tvMailAccount?.text = mailAccount
        etRechargeCode = findViewById(R.id.et_recharge_code)
        btRecharge = findViewById(R.id.bt_recharge)
        btTransfer = findViewById(R.id.bt_transfer)
        etAmount = findViewById(R.id.et_mont)
        autoCompleteTextViewAccountToTransfer = findViewById(R.id.actv_accountToTransfer)
        progressDialog = ProgressDialog(this)
        val adapter = AutoCompleteAdapter(
            this,
            android.R.layout.simple_expandable_list_item_1
        )
        autoCompleteTextViewAccountToTransfer?.setAdapter(adapter)
        btRecharge?.setOnClickListener { recharge() }

//        bt_transfer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                transfer(v);
//            }
//        });
    }

    fun recharge() {
        if (etRechargeCode!!.text.toString().length != 12 &&
            etRechargeCode!!.text.toString().length != 16
        ) {
            etRechargeCode!!.error = "El código de recarga debe ser de 12 o 16 dígitos."
        } else {
            progressDialog!!.setTitle("Portal Usuario")
            progressDialog!!.setIcon(R.mipmap.ic_launcher)
            progressDialog!!.setMessage("Recargando...")
            progressDialog!!.show()
            rechargeCode = etRechargeCode!!.text.toString()
            Thread {
                val builder = AlertDialog.Builder(this@PortalNautaActivity)
                builder.setTitle("Portal Usuario")
                builder.setPositiveButton("OK", null)
                try {
                    client.toUpBalance(rechargeCode!!)
                    progressDialog!!.dismiss()
                    builder.setMessage("Cuenta recargada!")
                    val success = builder.create()
                    success.setCancelable(false)
                    success.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    progressDialog!!.dismiss()
                    builder.setMessage("No se pudo recargar la cuenta: ${e.message}")
                    val success = builder.create()
                    success.setCancelable(false)
                    success.show()
                }
            }.start()
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