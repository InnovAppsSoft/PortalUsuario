@file:Suppress("DEPRECATION")

package com.marlon.portalusuario.nauta.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Chronometer.OnChronometerTickListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.marlon.portalusuario.Pref
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.commons.fullUserName
import com.marlon.portalusuario.databinding.FragmentWifiEtecsaBinding
import com.marlon.portalusuario.logging.JCLogging
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.data.repository.Users
import com.marlon.portalusuario.util.Util
import dagger.hilt.android.AndroidEntryPoint
import trikita.log.Log
import java.io.ByteArrayInputStream
import javax.inject.Inject

@AndroidEntryPoint
class WifiEtecsaFragment @Inject constructor(
    private val pref: Pref
) : Fragment() {
    private val nautaViewModel: NautaViewModel by activityViewModels()

    private var _binding: FragmentWifiEtecsaBinding? = null
    private val binding get() = _binding!!

    private lateinit var context: Context

    private lateinit var currentUser: User

    val accountTypes = mutableListOf<String>()
    val navigationTypes = listOf(
        NavigationType.INTERNATIONAL,
        NavigationType.NATIONAL
    )

    //private AutoCompleteTextView usernameEditText;
    private lateinit var usernameEditText: EditText
    private lateinit var captchaEditText: EditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var accountTypeSpinner: Spinner
    private lateinit var info: TextView
    private lateinit var reloadCaptcha: Button
    private lateinit var captchaImg: ImageView
    private var loadingBar: ProgressDialog? = null
    private var userList: Users = ArrayList()
    private var logger: JCLogging? = null
    var status: Map<Any, Any>? = null
    private val errors = StringBuilder()
    private var firstTime = true
    private var initialH = 0
    private var initialM = 0
    private var initialS = 0
//    private val timeType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = JCLogging(getContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWifiEtecsaBinding.inflate(inflater, container, false)
        context = requireContext()

        // Initialize UI
        initUi()

        accountTypes[0] = resources.getString(R.string.international_navigation)
        accountTypes[1] = resources.getString(R.string.national_navigation)

        // Initialize viewModel
        nautaViewModel.onCreate()

        // Observe the viewModel variables
        nautaViewModel.leftTime.observe(viewLifecycleOwner) {
            binding.textLeftTime.text = it
        }
        nautaViewModel.status.observe(viewLifecycleOwner) {
            if (loadingBar!!.isShowing) loadingBar!!.dismiss()
            val (isOk, err) = it
            if (!isOk) Toast.makeText(context, err, Toast.LENGTH_LONG).show()
        }
        nautaViewModel.users.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                isUserListEmpty(false)
                userList = it
                val spinnerAdapter = UsersSpinnerAdapter()
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.selectUserSpinner.adapter = spinnerAdapter
            } else {
                isUserListEmpty(true)
            }
        }
        nautaViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (loadingBar!!.isShowing) loadingBar!!.dismiss()
            if (it) {
                setUserDataOnDashboard()
                initChronometer()
                sendLeftTime()
                binding.connectButton.visibility = View.GONE
                binding.logoutButton.visibility = View.VISIBLE
                binding.sessionInfoLayout.visibility = View.VISIBLE
            } else {
                binding.connectButton.visibility = View.VISIBLE
                binding.logoutButton.visibility = View.GONE
                binding.sessionInfoLayout.visibility = View.GONE
            }
        }
        nautaViewModel.currentUser.observe(viewLifecycleOwner) {
            currentUser = it
        }
        nautaViewModel.isLogged.observe(viewLifecycleOwner) {
            if (it) {
                val transitionIntent = Intent(context, PortalNautaActivity::class.java)
                transitionIntent.putExtra("userId", currentUser.id)
                pref.saveLastUserUpdate(Util.date2String(Util.currentDate()))
                startActivity(transitionIntent)
            }
        }

        return binding.root
    }

    private fun initUi() {
        //
        binding.logoutButton.setOnClickListener { sendDisconnect() }
        binding.connectButton.setOnClickListener { sendConnect() }
        //
        binding.userInfoButton.setOnClickListener { FastInfoUserDialog(context, currentUser) }
        //
        binding.selectUserSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    nautaViewModel.onUserSelected { i }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        //
        binding.addUserBtn.setOnClickListener { EditUserDialog(context, null) }
        binding.editUserBtn.setOnClickListener { EditUserDialog(context, currentUser) }
        binding.delUserBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(resources.getString(R.string.ask_user_dalete))
                .setPositiveButton(R.string.yes) { _, _ ->
                    nautaViewModel.deleteUser(currentUser)
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
        binding.connectionLimiter.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                binding.limiterParamsLayout.visibility = View.VISIBLE
            } else {
                binding.limiterParamsLayout.visibility = View.GONE
            }
        }

        // Account types list
        val limiterTypes = mutableListOf<String>()
        limiterTypes.add(resources.getString(R.string.hours))
        limiterTypes.add(resources.getString(R.string.minutos).lowercase())
        limiterTypes.add(resources.getString(R.string.seconds))
        val limiterTypeSpinnerAdapter: ArrayAdapter<String>? =
            getContext()?.let {
                ArrayAdapter<String>(
                    it,
                    android.R.layout.simple_list_item_1,
                    limiterTypes
                )
            }
        binding.limiterType.adapter = limiterTypeSpinnerAdapter

        //
        binding.errorLayout.setOnClickListener {
            if (errors.isNotEmpty()) {
                val errorDialog = AlertDialog.Builder(context)
                errorDialog.setCancelable(true)
                    .setTitle(resources.getString(R.string.errors))
                    .setIcon(R.drawable.error)
                    .setMessage(errors.toString())
                errorDialog.show()
            }
        }

        loadingBar = ProgressDialog(getContext())
    }

    private fun initChronometer() {
        binding.simpleChronometer.onChronometerTickListener =
            OnChronometerTickListener { cArg: Chronometer ->
                val time = SystemClock.elapsedRealtime() - cArg.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                val formattedTime = String.format("%02d:%02d:%02d", h, m, s)
                if (firstTime) {
                    initialH = h
                    initialM = m
                    initialS = s
                    firstTime = false
                }
                cArg.text = formattedTime
//                try {
//                    if (maxTime > 0) {
//                        if (timeType == resources.getString(R.string.hours)) {
//                            if (h - initialH == maxTime) {
//                                sendDisconnect()
//                                return@OnChronometerTickListener
//                            }
//                        } else if (timeType == resources.getString(R.string.minutos).lowercase()) {
//                            if (m - initialM == maxTime) {
//                                sendDisconnect()
//                                return@OnChronometerTickListener
//                            }
//                        } else if (timeType == resources.getString(R.string.seconds)) {
//                            if (s - initialS == maxTime) {
//                                sendDisconnect()
//                                return@OnChronometerTickListener
//                            }
//                        }
//                    }
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                    JCLogging.error(null, null, ex)
//                }
            }
        binding.simpleChronometer.base = SystemClock.elapsedRealtime()
        binding.simpleChronometer.start()
    }

    private fun setUserDataOnDashboard() {
        binding.editSaldoCuenta.text = currentUser.credit
    }

    private fun sendConnect() {
        if (Util.isConnected(context)) {
            loadingBar!!.setIcon(R.mipmap.ic_launcher)
            loadingBar!!.setMessage(resources.getString(R.string.connecting))
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            loadingBar!!.setMessage(resources.getString(R.string.logging))
            val username = currentUser.fullUserName()
            val password = currentUser.password

            nautaViewModel.connect(username, password)
        } else {
            Toast.makeText(context, resources.getString(R.string.you_not_are_connected), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendLeftTime() {
        nautaViewModel.countDown()
    }

    //
    private fun sendDisconnect() {
        loadingBar!!.setTitle(resources.getString(R.string.disconnecting))
        loadingBar!!.setMessage(resources.getString(R.string.wait_please))
        loadingBar!!.setIcon(R.mipmap.ic_launcher)
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()

        nautaViewModel.disconnect()
    }

    inner class UsersSpinnerAdapter : ArrayAdapter<User>(
        context.applicationContext, R.layout.user_item_layout, userList
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view =
                convertView ?: layoutInflater.inflate(R.layout.user_item_layout, parent, false)
            val user = userList[position]
            val username = view.findViewById<TextView>(R.id.username)
            val accountType = view.findViewById<TextView>(R.id.account_type)
            username.text = user.userName
            accountType.text = accountTypes[navigationTypes.indexOf(user.accountNavigationType)]
            return view
        }
    }

    /* DIALOGS */
    inner class EditUserDialog(context: Context?, user: User?) {
        init {
            val inflate = layoutInflater.inflate(
                R.layout.nauta_accounts_edit_dialog,
                null as ViewGroup?,
                false
            )
            val editUserDialog = AlertDialog.Builder(context)
            editUserDialog.setCancelable(true)
                .setView(inflate)
                .setTitle(resources.getString(R.string.edit_user))
                .setIcon(R.drawable.round_person_24)
            usernameEditText = inflate.findViewById(R.id.username_et)
            passwordEditText = inflate.findViewById(R.id.password_et)
            accountTypeSpinner = inflate.findViewById(R.id.account_type_spinner)
            info = inflate.findViewById(R.id.info)
            info.setOnClickListener {
                Toast.makeText(
                    context,
                    resources.getString(R.string.no_suffix_permited),
                    Toast.LENGTH_LONG
                ).show()
            }

            val accountTypeSpinnerAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, accountTypes
            )
            accountTypeSpinner.adapter = accountTypeSpinnerAdapter
            if (user != null) {
                usernameEditText.setText(user.userName)
                passwordEditText.setText(user.password)
                accountTypeSpinner.setSelection(navigationTypes.indexOf(user.accountNavigationType))
            }
            editUserDialog.setPositiveButton(resources.getString(R.string.save_)) { _: DialogInterface?, _: Int ->
                val username = usernameEditText.text.toString()
                val pwd = passwordEditText.text.toString()
                if (!username.matches(Regex("[A-Za-z\\d_.]+"))) {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.no_suffix_permited),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                if (user == null) {
                    nautaViewModel.addUser(
                        User(
                            userName = username,
                            password = pwd,
                            accountNavigationType = navigationTypes[accountTypeSpinner.selectedItemPosition],
                            lastConnection = 0,
                            blockingDate = "",
                            dateOfElimination = "",
                            accountType = "",
                            serviceType = "",
                            credit = "",
                            time = "",
                            mailAccount = ""
                        )
                    )
                } else {
                    user.userName = username
                    user.password = pwd
                    user.accountNavigationType =
                        navigationTypes[accountTypeSpinner.selectedItemPosition]
                    nautaViewModel.updateUser(user)
                }
            }
            editUserDialog.setNegativeButton(resources.getString(R.string.cancel), null)
            editUserDialog.show()
        }
    }

    inner class FastInfoUserDialog(context: Context?, user: User) {
        init {
            val info = """
                ${resources.getString(R.string.last_update)}: ${
                pref.getLastUserUpdate().ifEmpty { "-" }
            }
                ${resources.getString(R.string.user)}: ${user.fullUserName()}
                ${resources.getString(R.string.associated_email_account)}: ${user.mailAccount.ifEmpty { "-" }}
                ${resources.getString(R.string.accountType)}: ${
                accountTypes[navigationTypes.indexOf(
                    user.accountNavigationType
                )]
            }
                ${resources.getString(R.string.credit)}: ${user.credit.ifEmpty { "-" }}
                ${resources.getString(R.string.remaining_time)}: ${user.time.ifEmpty { "-" }}
                ${resources.getString(R.string.last_connection)}: ${
                if (user.lastConnection != 0L) Util.date2String(Util.long2Date(user.lastConnection)) else "-"
            }
                """.trimIndent()
            val fastInfoUserDialog = AlertDialog.Builder(context)
            fastInfoUserDialog.setCancelable(true)
                .setMessage(info)
                .setTitle(resources.getString(R.string.user_info))
                .setIcon(R.drawable.info)
                .setPositiveButton(resources.getString(R.string.portal_nauta)) { _, _ ->
                    CaptchaUserDialog(
                        context
                    )
                }
                .setNegativeButton(resources.getString(R.string.atr_s), null)
            fastInfoUserDialog.show()
        }
    }

    inner class CaptchaUserDialog(context: Context?) {
        private val progressLayout: LinearLayout
        private val errorLayout: LinearLayout
        private val captchaParamsLayout: LinearLayout

        init {
            val inflate = layoutInflater.inflate(R.layout.captcha_dialog, null, false)
            val editUserDialog = AlertDialog.Builder(context)
            editUserDialog.setCancelable(true)
                .setView(inflate)
                .setTitle(resources.getString(R.string.write_captcha_code))
                .setIcon(R.drawable.ic_security)
                .setPositiveButton(resources.getString(R.string.connect)) { _: DialogInterface?, _: Int -> login() }
                .setNegativeButton(resources.getString(R.string.cancel), null)

            progressLayout = inflate.findViewById(R.id.progress_layout)
            errorLayout = inflate.findViewById(R.id.error_layout)
            captchaParamsLayout = inflate.findViewById(R.id.captcha_params_layout)
            captchaEditText = inflate.findViewById(R.id.captcha_et)
            reloadCaptcha = inflate.findViewById(R.id.bt_reload)
            reloadCaptcha.setOnClickListener { nautaViewModel.getCaptcha() }
            captchaImg = inflate.findViewById(R.id.iv_captcha)
            editUserDialog.setPositiveButton(resources.getString(R.string.connect)) { _: DialogInterface?, _: Int -> login() }

            nautaViewModel.captchaImage.observe(viewLifecycleOwner) {
                val bitmap = BitmapFactory.decodeStream(
                    ByteArrayInputStream(it)
                )
                captchaImg.setImageBitmap(bitmap)
            }
            nautaViewModel.isCaptchaLoaded.observe(viewLifecycleOwner) {
                if (it) {
                    progressLayout.visibility = View.GONE
                    captchaImg.visibility = View.VISIBLE
                    captchaParamsLayout.visibility = View.VISIBLE
                } else {
                    progressLayout.visibility = View.VISIBLE
                    captchaImg.visibility = View.GONE
                    captchaParamsLayout.visibility = View.GONE
                }
            }
            nautaViewModel.getCaptcha()

            editUserDialog.show()
        }

        private fun login() {
            loadingBar!!.setIcon(R.drawable.ic_wifi)
            loadingBar!!.setMessage(resources.getString(R.string.connecting_please_wait))
            loadingBar!!.setCancelable(false)
            loadingBar!!.setCanceledOnTouchOutside(false)
            if (captchaEditText.text.toString() == "") {
                Toast.makeText(context, resources.getString(R.string.input_catpcha_code), Toast.LENGTH_SHORT).show()
            }
            if (Util.isConnected(context)) {
                loadingBar!!.show()
                val current = currentUser
                val username = current.fullUserName()
                val password = current.password

                nautaViewModel.login(username, password, captchaEditText.text.toString())
            } else {
                Toast.makeText(context, resources.getString(R.string.you_not_are_connected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setErrorMessage(message: String?) {
        binding.errorLayout.visibility = View.VISIBLE
        binding.errorTv.text = message

    }

    override fun onResume() {
        Log.e("Wifi", "onResume of Wifi")
        super.onResume()
    }

    override fun onPause() {
        Log.e("Wifi", "OnPause of Wifi")
        super.onPause()
    }

    private fun isUserListEmpty(b: Boolean) {
        if (b) {
            binding.noUsers.visibility = View.VISIBLE
            binding.selectUserSpinner.visibility = View.GONE
            binding.editUserBtn.visibility = View.GONE
            binding.delUserBtn.visibility = View.GONE
            binding.userInfoButton.visibility = View.GONE
            binding.connectButton.visibility = View.GONE
            binding.connectionLimiter.isChecked = false
            binding.connectionLimiter.visibility = View.GONE
            setErrorMessage(
                resources.getString(R.string.err_message_add_user)
            )
        } else {
            binding.noUsers.visibility = View.GONE
            binding.selectUserSpinner.visibility = View.VISIBLE
            binding.editUserBtn.visibility = View.VISIBLE
            binding.delUserBtn.visibility = View.VISIBLE
            binding.userInfoButton.visibility = View.VISIBLE
            binding.connectButton.visibility = View.VISIBLE
            binding.connectionLimiter.visibility = View.VISIBLE
            binding.errorLayout.visibility = View.GONE
        }
    }
}
