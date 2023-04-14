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
import com.marlon.portalusuario.net.Communicator
import com.marlon.portalusuario.net.RunTask
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
    var amount: String? = null
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

        btTransfer?.setOnClickListener { transfer() }
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

            val builder = AlertDialog.Builder(this@PortalNautaActivity)
            builder.setTitle("Portal Usuario")
            builder.setPositiveButton("OK", null)

            RunTask(object : Communicator {
                private lateinit var status: Pair<Boolean, String?>

                override fun communicate() {
                    status = try {
                        client.toUpBalance(rechargeCode!!)
                        Pair(true, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Pair(false, e.message)
                    }
                }

                override fun postCommunicate() {
                    val (isOk, err) = status
                    progressDialog!!.dismiss()
                    if (isOk) {
                        builder.setMessage("Cuenta recargada!")
                        val success = builder.create()
                        success.setCancelable(false)
                        success.show()
                    } else {
                        builder.setMessage("No se pudo recargar la cuenta: ${err}")
                        val success = builder.create()
                        success.setCancelable(false)
                        success.show()
                    }
                }
            }).execute()
        }
    }

    fun transfer() {
        var validate = 0
        if (etAmount!!.text.toString().isEmpty()) {
            etAmount!!.error = "Introduzca un monto válido";
            validate = 1;
        }
        if (autoCompleteTextViewAccountToTransfer!!.text.toString() == "") {
            autoCompleteTextViewAccountToTransfer!!.error = "Introduzca un nombre de usuario";
            validate = 1;
        } else if (!autoCompleteTextViewAccountToTransfer!!.text.toString()
                .endsWith("@nauta.com.cu") &&
            !autoCompleteTextViewAccountToTransfer!!.text.toString().endsWith("@nauta.co.cu")
        ) {
            autoCompleteTextViewAccountToTransfer!!.error =
                "Introduzca un nombre de usuario válido";
            validate = 1;
        }
        if (validate != 1) {
            if (credit.equals("$0,00 CUP")) {
                val builder = AlertDialog.Builder(this);
                builder.setTitle("Portal Usuario").setMessage("Su saldo es insuficiente!");
                builder.setPositiveButton("OK", null);
                val success = builder.create();
                success.setCancelable(false);
                success.show();
            } else {
                progressDialog!!.setTitle("Portal Usuario");
                progressDialog!!.setMessage("Transfiriendo...")
                progressDialog!!.show()
                amount = etAmount!!.text.toString()
                accountToTransfer = autoCompleteTextViewAccountToTransfer!!.text.toString()

                RunTask(object : Communicator {
                    private lateinit var status: Pair<Boolean, String?>
                    val builder = AlertDialog.Builder(this@PortalNautaActivity)

                    override fun communicate() {
                        status = try {
                            client.transferBalance(amount!!.toFloat(), accountToTransfer!!)
                            Pair(true, null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Pair(false, e.message)
                        }
                    }

                    override fun postCommunicate() {
                        val (isOk, err) = status
                        progressDialog!!.dismiss()
                        if (isOk) {
                            builder.setTitle("Portal Usuario").setMessage("Transferencia realizada!");
                            val success = builder.create()
                            success.show()
                        } else {
                            builder.setTitle("Portal Usuario").setMessage(err)
                            val success = builder.create()
                            success.show()
                        }
                    }
                }).execute()
            }
        }
    }

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