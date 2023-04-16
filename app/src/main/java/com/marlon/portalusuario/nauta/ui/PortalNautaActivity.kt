package com.marlon.portalusuario.nauta.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.ActivityPortalNautaBinding
import com.marlon.portalusuario.net.Communicator
import com.marlon.portalusuario.net.RunTask
import com.marlon.portalusuario.util.AutoCompleteAdapter

class PortalNautaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPortalNautaBinding
    private val nautaViewModel: NautaViewModel by viewModels()

    var userName: String? = null
    var password: String? = null
    private var remainingTime: String? = null
    private var blockingDate: String? = null
    private var dateOfElimination: String? = null
    var accountType: String? = null
    private var serviceType: String? = null
    private var credit: String? = null
    var mailAccount: String? = null
    var progressDialog: ProgressDialog? = null
    private lateinit var errMessage: String
    private lateinit var successMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPortalNautaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val data = intent.extras
        val userId = data!!.getString("userId")
        nautaViewModel.getUser(userId!!.toInt())

        nautaViewModel.currentUser.observe(this) {
            userName = it.userName
            password = it.password
            remainingTime = it.time
            blockingDate = it.blockingDate
            dateOfElimination = it.dateOfElimination
            accountType = it.accountType
            serviceType = it.serviceType
            credit = it.credit
            mailAccount = it.mailAccount
        }

        // Finish the activity by pressing the back button
        onBackPressedDispatcher.addCallback(this) {
            nautaViewModel.isLogged.postValue(false)
            finish()
        }

        // Observe the state of action
        nautaViewModel.status.observe(this) {
            if (progressDialog!!.isShowing) progressDialog!!.dismiss()
            val (isOk, err) = it
            val builder = AlertDialog.Builder(this@PortalNautaActivity)
            builder.setTitle(resources.getString(R.string.app_name))
            builder.setPositiveButton("OK", null)
            if (isOk) {
                builder.setMessage(successMessage)
                val success = builder.create()
                success.setCancelable(false)
                success.show()
            } else {
                builder.setMessage("$errMessage: $err")
                val error = builder.create()
                error.setCancelable(false)
                error.show()
            }
        }

        binding.tvInfoTime.text = remainingTime
        binding.tvInfoUser.text = userName
        binding.tvBlockDate.text = blockingDate
        binding.tvDeleteDate.text = dateOfElimination
        binding.tvAccountType.text = accountType
        binding.tvServiceType.text = serviceType
        binding.tvAccountCredit.text = credit
        binding.tvMailAccount.text = mailAccount

        progressDialog = ProgressDialog(this)
        val adapter = AutoCompleteAdapter(
            this,
            android.R.layout.simple_expandable_list_item_1
        )
        binding.autoCompleteTextViewAccountToTransfer.setAdapter(adapter)
        binding.btRecharge.setOnClickListener { toUp() }

        binding.btTransfer.setOnClickListener { transfer() }
    }

    private fun toUp() {
        if (binding.etRechargeCode.text.toString().length != 12 &&
            binding.etRechargeCode.text.toString().length != 16
        ) {
            binding.etRechargeCode.error = resources.getString(R.string.error_bad_recharge_code)
        } else {
            progressDialog!!.setTitle(resources.getString(R.string.app_name))
            progressDialog!!.setIcon(R.mipmap.ic_launcher)
            progressDialog!!.setMessage(resources.getString(R.string.recharging))
            progressDialog!!.show()
            val rechargeCode = binding.etRechargeCode.text.toString()

            val builder = AlertDialog.Builder(this@PortalNautaActivity)
            builder.setTitle(resources.getString(R.string.app_name))
            builder.setPositiveButton("OK", null)

            successMessage = resources.getString(R.string.success_recharge)
            errMessage = resources.getString(R.string.not_recharged)

            nautaViewModel.toUp(rechargeCode)
        }
    }

    private fun transfer() {
        var validate = 0
        if (binding.etAmount.text.toString().isEmpty()) {
            binding.etAmount.error = resources.getString(R.string.input_a_valid_amount);
            validate = 1;
        }
        if (binding.autoCompleteTextViewAccountToTransfer.text.toString() == "") {
            binding.autoCompleteTextViewAccountToTransfer.error = resources.getString(R.string.input_a_destination_account)
            validate = 1;
        } else if (!binding.autoCompleteTextViewAccountToTransfer.text.toString().endsWith("@nauta.com.cu") &&
            !binding.autoCompleteTextViewAccountToTransfer.text.toString().endsWith("@nauta.co.cu")) {
            binding.autoCompleteTextViewAccountToTransfer.error = resources.getString(R.string.input_a_valid_username);
            validate = 1;
        }
        if (validate != 1) {
            if (credit.equals("$0,00 CUP")) {
                val builder = AlertDialog.Builder(this);
                builder.setTitle(resources.getString(R.string.app_name)).setMessage(resources.getString(R.string.insufficient_balance));
                builder.setPositiveButton("OK", null);
                val success = builder.create();
                success.setCancelable(false);
                success.show();
            } else {
                progressDialog!!.setTitle(resources.getString(R.string.app_name))
                progressDialog!!.setMessage(resources.getString(R.string.transfering))
                progressDialog!!.show();
                val amount = binding.etAmount.text.toString()
                val accountToTransfer = binding.autoCompleteTextViewAccountToTransfer.text.toString()
                successMessage = resources.getString(R.string.success_transfer)
                errMessage = resources.getString(R.string.fail_transfer)

                nautaViewModel.transfer(amount.toFloat(), accountToTransfer)
            }
        }
    }
}
