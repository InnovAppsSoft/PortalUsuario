package com.marlon.portalusuario.view.fragments.connectivity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Chronometer.OnChronometerTickListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import com.marlon.portalusuario.PortalUsuarioApplication.Companion.client
import com.marlon.portalusuario.PortalUsuarioApplication.Companion.sessionPref
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ViewModel.UserViewModel
import com.marlon.portalusuario.logging.JCLogging
import com.marlon.portalusuario.model.User
import com.marlon.portalusuario.net.Communicator
import com.marlon.portalusuario.net.RunTask
import com.marlon.portalusuario.util.Util
import com.marlon.portalusuario.view.activities.PortalNautaActivity
import cu.suitetecsa.sdk.nauta.domain.service.NautaClient
import trikita.log.Log
import java.io.ByteArrayInputStream
import java.io.IOException

class WifiEtecsaFragment : Fragment() {
    private val nationalAccount = 1
    private val nationalAccountText = "Cuenta Nacional"
    private val internationalAccount = 0
    private val internationalAccountText = "Cuenta Internacional"
    private var context: Context? = null
    private var pref: SharedPreferences? = null
    private var errorsTextView: TextView? = null
    private var selectUserSpinner: Spinner? = null
    private var addUserBtn: ImageView? = null
    private var editUserBtn: ImageView? = null
    private var delUserBtn: ImageView? = null
    private var connectionLimiter: CheckBox? = null
    private var limiterType: Spinner? = null
    private var limiterTime: EditText? = null
    private var logOutBtn: Button? = null
    private var connectBtn: Button? = null
    private var userInfoBtn: Button? = null
    var errorLayout: LinearLayout? = null
    private var sessionInfoLayout: ConstraintLayout? = null


    //private AutoCompleteTextView usernameEditText;
    private lateinit var usernameEditText: EditText
    private lateinit var captchaEditText: EditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var accountTypeSpinner: Spinner
    private var noUsers: TextView? = null
    private lateinit var info: TextView
    private lateinit var reloadCaptcha: Button
    private lateinit var captchaImg: ImageView
    private var loadingBar: ProgressDialog? = null
    private var userViewModel: UserViewModel? = null
    private var userList: List<User> = ArrayList()
    private var currentUserInt = 0
    private var accountTypes: MutableList<String>? = null
    private var logger: JCLogging? = null
    var cookies: Map<String, String>? = null
    var status: Map<Any, Any>? = null
    private val errors = StringBuilder()
    var saldo: TextView? = null
    var leftTime: TextView? = null
    var accountState: TextView? = null
    var btnDisconnect: Button? = null
    private var firstTime = true
    private var initialH = 0
    private var initialM = 0
    private var initialS = 0
    private val maxTime = 0
    private val timeType: String? = null
    private var simpleChronometer: Chronometer? = null
    var remainingTime: String? = null
    var account: String? = null
    var blockDate: String? = null
    var deleteDate: String? = null
    var accountType1: String? = null
    var serviceType: String? = null
    var credit: String? = null
    var mailAccount: String? = null
    var transitionIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = JCLogging(getContext())
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Account types list
        accountTypes = mutableListOf()
        accountTypes!!.add(internationalAccountText)
        accountTypes!!.add(nationalAccountText)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = getContext()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wifi_etecsa, container, false)

        //
        initViewModel()
        initUi(view)

        //
        return view
    }

    private fun initViewModel() {
        // ViewModel
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel!!.allUsers.observe(viewLifecycleOwner) { user: List<User> ->
            if (user.isNotEmpty()) {
                isUserListEmpty(false)
                userList = user
                val spinnerAdapter = UsersSpinnerAdapter()
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                selectUserSpinner!!.adapter = spinnerAdapter
            } else {
                isUserListEmpty(true)
            }
        }
    }

    private fun initUi(view: View) {
        //
        logOutBtn = view.findViewById(R.id.logout_button)
        logOutBtn?.setOnClickListener { sendDisconnect() }
        connectBtn = view.findViewById(R.id.connect_button)
        connectBtn?.setOnClickListener { sendConnect(view) }
        //
        sessionInfoLayout = view.findViewById(R.id.sessionInfoLayout)
        //
        userInfoBtn = view.findViewById(R.id.user_info_button)
        userInfoBtn?.setOnClickListener {
            FastInfoUserDialog(
                context,
                currentUser()
            )
        }
        //
        selectUserSpinner = view.findViewById(R.id.select_user_spinner)
        selectUserSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                currentUserInt = i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        //
        addUserBtn = view.findViewById(R.id.add_user_btn)
        addUserBtn?.setOnClickListener {
            EditUserDialog(
                context,
                null
            )
        }
        editUserBtn = view.findViewById(R.id.edit_user_btn)
        editUserBtn?.setOnClickListener {
            EditUserDialog(
                context,
                currentUser()
            )
        }
        delUserBtn = view.findViewById(R.id.del_user_btn)
        delUserBtn?.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("¿Desea eliminar este usuario?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    userViewModel!!.deleteUser(
                        currentUser()
                    )
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
        connectionLimiter = view.findViewById(R.id.connection_limiter)
        connectionLimiter?.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            val limiterParamsLayout = view.findViewById<LinearLayout>(
                R.id.limiter_params_layout
            )
            if (b) {
                limiterParamsLayout.visibility = View.VISIBLE
            } else {
                limiterParamsLayout.visibility = View.GONE
            }
        }

        // Limiter type
        limiterType = view.findViewById(R.id.limiter_type)

        // Account types list
        val limiterTypes = mutableListOf<String>()
        limiterTypes.add("horas")
        limiterTypes.add("minutos")
        limiterTypes.add("segundos")
        val limiterTypeSpinnerAdapter: ArrayAdapter<String>? =
            getContext()?.let {
                ArrayAdapter<String>(
                    it,
                    android.R.layout.simple_list_item_1,
                    limiterTypes
                )
            }
        limiterType?.adapter = limiterTypeSpinnerAdapter

        //
        limiterTime = view.findViewById(R.id.limiter_time)
        noUsers = view.findViewById(R.id.no_users)
        errorsTextView = view.findViewById(R.id.error_tv)
        errorLayout = view.findViewById(R.id.error_layout)
        errorLayout?.setOnClickListener(View.OnClickListener {
            if (errors.isNotEmpty()) {
                val errorDialog = AlertDialog.Builder(context)
                errorDialog.setCancelable(true)
                    .setTitle("Errores")
                    .setIcon(R.drawable.error)
                    .setMessage(errors.toString())
                errorDialog.show()
            }
        })

        loadingBar = ProgressDialog(getContext())
        saldo = view.findViewById(R.id.editSaldoCuenta)
        leftTime = view.findViewById(R.id.textLeftTime)
        accountState = view.findViewById(R.id.editEstadoCuenta)
        btnDisconnect = view.findViewById(R.id.buttonDisconnect)
    }

    private fun initChronometer(view: View) {
        simpleChronometer = view.findViewById(R.id.simpleChronometer) // initiate a chronometer
        simpleChronometer?.onChronometerTickListener =
            OnChronometerTickListener { cArg: Chronometer ->
                val time = SystemClock.elapsedRealtime() - cArg.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                val hh = if (h < 10) "0$h" else h.toString() + ""
                val mm = if (m < 10) "0$m" else m.toString() + ""
                val ss = if (s < 10) "0$s" else s.toString() + ""
                val formattedTime = String.format("%s:%s:%s", hh, mm, ss)
                if (firstTime) {
                    initialH = h
                    initialM = m
                    initialS = s
                    firstTime = false
                }
                //            if (prefs.getBoolean("show_traffic_speed_bubble", false)) {
                //                MainActivity.setConnectedTime(formatted_time);
                //            }
                cArg.text = formattedTime
                //
                try {
                    if (maxTime > 0) {
                        if (timeType == "horas") {
                            if (h - initialH == maxTime) {
                                sendDisconnect()
                                return@OnChronometerTickListener
                            }
                        } else if (timeType == "minutos") {
                            if (m - initialM == maxTime) {
                                sendDisconnect()
                                return@OnChronometerTickListener
                            }
                        } else if (timeType == "segundos") {
                            if (s - initialS == maxTime) {
                                sendDisconnect()
                                return@OnChronometerTickListener
                            }
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    JCLogging.error(null, null, ex)
                }
            }
        simpleChronometer?.base = SystemClock.elapsedRealtime()
        simpleChronometer?.start()
    }

    private fun setUserDataOnDashboard() {
        saldo!!.text = currentUser().accountCredit
        accountState!!.text = currentUser().accountState
    }

    private fun sendConnect(view: View) {
        if (Util.isConnected(context)) {
            loadingBar!!.setIcon(R.mipmap.ic_launcher)
            loadingBar!!.setMessage("Conectando...")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            loadingBar!!.setMessage("Iniciando sesión...")
            val username = currentUser().fullUsername
            val pwd = currentUser().password

            RunTask(object : Communicator {
                private lateinit var status: Pair<Boolean, String?>

                override fun communicate() {
                    status = try {
                        // login to captive portal
                        client.setCredentials(username, pwd)
                        client.connect()

                        // Saving data session
                        sessionPref.saveSession(client.dataSession)

                        Pair(true, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Pair(false, e.message)
                    }
                }

                override fun postCommunicate() {
                    val (isOk, err) = status
                    loadingBar!!.dismiss()

                    if (isOk) {
                        setUserDataOnDashboard()
                        initChronometer(view)
                        sendLeftTime()
                        connectBtn!!.visibility = View.GONE
                        logOutBtn!!.visibility = View.VISIBLE
                        sessionInfoLayout!!.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                    }
                }
            }).execute()
        } else {
            Toast.makeText(context, "No está conectado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun countDown() {
        val time = client.remainingTime
        JCLogging.message("Initial time value", time)
        var millisecondsLeft: Long = 0
        //
        try {
            millisecondsLeft = (time.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].toInt() * 60 * 60 * 1000 + time.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].toInt() * 60 * 1000 + time.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[2].toInt() * 1000).toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            JCLogging.error(null, null, null)
            JCLogging.error(null, null, e)
        }
        object : CountDownTimer(millisecondsLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val h = (millisUntilFinished / 3600000).toInt()
                val m = (millisUntilFinished - h * 3600000).toInt() / 60000
                val s = (millisUntilFinished - h * 3600000 - m * 60000).toInt() / 1000
                val hh = if (h < 10) "0$h" else h.toString() + ""
                val mm = if (m < 10) "0$m" else m.toString() + ""
                val ss = if (s < 10) "0$s" else s.toString() + ""
                leftTime!!.text = String.format("%s:%s:%s", hh, mm, ss)
            }

            override fun onFinish() {
                leftTime!!.text = "00:00:00"
            }
        }.start()
    }

    private fun sendLeftTime() {
        Thread { countDown() }.start()
    }

    //
    private fun sendDisconnect() {
        loadingBar!!.setTitle("Desconectando")
        loadingBar!!.setMessage("Por favor espere....")
        loadingBar!!.setIcon(R.mipmap.ic_launcher)
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()

        RunTask(object : Communicator {
            private lateinit var status: Pair<Boolean, String?>

            override fun communicate() {
                status = try {
                    client.disconnect()
                    Pair(true, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Pair(false, e.message)
                }
            }

            override fun postCommunicate() {
                val (isOk, err) = status
                loadingBar!!.dismiss()

                if (isOk) {
                    // update UI
                    connectBtn!!.visibility = View.VISIBLE
                    logOutBtn!!.visibility = View.GONE
                    sessionInfoLayout!!.visibility = View.GONE
                    // toast
                    Toast.makeText(activity, "Desconectado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, err, Toast.LENGTH_SHORT).show()
                }
            }
        }).execute()
    }

    inner class UsersSpinnerAdapter : ArrayAdapter<User?>(
        context!!.applicationContext, R.layout.user_item_layout, userList
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.user_item_layout, parent, false)
            }
            val user = userList[position]
            val username = convertView!!.findViewById<TextView>(R.id.username)
            val accountType = convertView.findViewById<TextView>(R.id.account_type)
            username.text = user.username
            accountType.text = accountTypes!![user.accountNavegationType]
            return convertView
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
                .setTitle("Editar usuario")
                .setIcon(R.drawable.round_person_24)
            usernameEditText = inflate.findViewById(R.id.username_et)
            passwordEditText = inflate.findViewById(R.id.password_et)
            accountTypeSpinner = inflate.findViewById(R.id.account_type_spinner)
            info = inflate.findViewById(R.id.info)
            info.setOnClickListener {
                Toast.makeText(
                    context,
                    "El nombre de usuario no puede contener el nombre de dominio @nauta.com.cu\nEjemplo: Si su cuenta es juan@nauta.com.cu, solo debe poner juan",
                    Toast.LENGTH_LONG
                ).show()
            }

//            AutoCompleteAdapter adapter = new AutoCompleteAdapter(getContext(), android.R.layout.simple_expandable_list_item_1);
//            usernameEditText.setAdapter(adapter);
            val accountTypeSpinnerAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, accountTypes!!
            )
            accountTypeSpinner.adapter = accountTypeSpinnerAdapter
            if (user != null) {
                usernameEditText.setText(user.username)
                passwordEditText.setText(user.password)
                accountTypeSpinner.setSelection(user.accountNavegationType)
            }
            editUserDialog.setPositiveButton("Guardar") { _: DialogInterface?, _: Int ->
                val username = usernameEditText.text.toString()
                val pwd = passwordEditText.text.toString()
                if (!username.matches(Regex("[A-Za-z\\d_.@]+"))) {
                    Toast.makeText(
                        context,
                        "El nombre de usuario no puede contener símbolos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                if (user == null) {
                    userViewModel!!.insertUser(
                        User(
                            username,
                            pwd,
                            "",
                            "",
                            "",
                            accountTypeSpinner.selectedItemPosition,
                            "",
                            "",
                            "",
                            0,
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                    )
                } else {
                    user.username = username
                    user.password = pwd
                    user.accountNavegationType = accountTypeSpinner.selectedItemPosition
                    userViewModel!!.updateUser(user)
                }
            }
            editUserDialog.setNegativeButton("Cancelar", null)
            editUserDialog.show()
        }
    }

    inner class FastInfoUserDialog(context: Context?, user: User) {
        init {
            var info = ""
            val lastUpdate = pref!!.getString("last_portal_nauta_update", "")
            info += """
                Última actualización: ${if (lastUpdate!!.isNotEmpty()) lastUpdate else "-"}
                
                """.trimIndent()
            info += """
                Usuario: ${user.fullUsername}
                
                """.trimIndent()
            info += """
                Cuenta de correo asociada: ${user.emailAccount.ifEmpty { "-" }}
                
                """.trimIndent()
            info += """
                Tipo de cuenta: ${if (user.accountNavegationType == internationalAccount) internationalAccountText else nationalAccountText}
                
                """.trimIndent()
            info += """
                Crédito: ${if (user.accountCredit !== "") user.accountCredit else "-"}
                
                """.trimIndent()
            info += """
                Tiempo restante: ${if (user.leftTime !== "") user.leftTime else "-"}
                
                """.trimIndent()
            info += """
                Última conexión: ${
                if (user.lastConnectionDateTime != 0L) Util.date2String(
                    Util.long2Date(
                        user.lastConnectionDateTime
                    )
                ) else "-"
            }
                
                """.trimIndent()
            val fastInfoUserDialog = AlertDialog.Builder(context)
            fastInfoUserDialog.setCancelable(true)
                .setMessage(info)
                .setTitle("Información de usuario")
                .setIcon(R.drawable.info)
                .setPositiveButton("Portal Nauta") { _, _ ->
                    CaptchaUserDialog(
                        context,
                        user
                    )
                }
                .setNegativeButton("Atrás", null)
            fastInfoUserDialog.show()
        }
    }

    inner class CaptchaUserDialog(context: Context?, user: User?) {
        private val progressLayout: LinearLayout
        private val errorLayout: LinearLayout
        private val captchaParamsLayout: LinearLayout

        init {
            val inflate = layoutInflater.inflate(R.layout.captcha_dialog, null, false)
            val editUserDialog = AlertDialog.Builder(context)
            editUserDialog.setCancelable(true)
                .setView(inflate)
                .setTitle("Escriba el código Captcha")
                .setIcon(R.drawable.ic_security)
                .setPositiveButton("Conectar") { _: DialogInterface?, _: Int -> login() }
                .setNegativeButton("Cancelar", null)
            loadCaptcha()
            //
            progressLayout = inflate.findViewById(R.id.progress_layout)
            errorLayout = inflate.findViewById(R.id.error_layout)
            captchaParamsLayout = inflate.findViewById(R.id.captcha_params_layout)
            captchaEditText = inflate.findViewById(R.id.captcha_et)
            reloadCaptcha = inflate.findViewById(R.id.bt_reload)
            reloadCaptcha.setOnClickListener { loadCaptcha() }
            captchaImg = inflate.findViewById(R.id.iv_captcha)
            editUserDialog.setPositiveButton("Conectar") { _: DialogInterface?, _: Int -> login() }
            editUserDialog.show()
        }

        private fun setCaptchaLoaded(b: Boolean) {
            if (b) {
                progressLayout.visibility = View.GONE
                captchaImg.visibility = View.VISIBLE
                captchaParamsLayout.visibility = View.VISIBLE
            } else {
                progressLayout.visibility = View.VISIBLE
                captchaImg.visibility = View.GONE
                captchaParamsLayout.visibility = View.GONE
            }
        }

        private fun setError(b: Boolean) {
            if (b) {
                progressLayout.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
            } else {
                progressLayout.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
            }
        }

        private fun loadCaptcha() {
            RunTask(object : Communicator {
                private lateinit var status: Pair<Boolean, String?>
                private lateinit var captchaImage: ByteArray

                override fun communicate() {
                    setCaptchaLoaded(false)
                    status = try {
                        captchaImage = client.captchaImage
                        Pair(true, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Pair(false, e.message)
                    }
                }

                override fun postCommunicate() {
                    val (isOk, err) = status

                    if (isOk) {
                        val bitmap = BitmapFactory.decodeStream(
                            ByteArrayInputStream(captchaImage)
                        )
                        setCaptchaLoaded(true)
                        captchaImg.setImageBitmap(bitmap)
                    } else {
                        Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show()
                    }
                }
            }).execute()
        }

        fun preLoad() {
            loadCaptcha()
        }

        private fun login(): Boolean {
            loadingBar!!.setIcon(R.drawable.ic_wifi)
            loadingBar!!.setMessage("Conectando, por favor, espere...")
            loadingBar!!.setCancelable(false)
            loadingBar!!.setCanceledOnTouchOutside(false)
            val validate = 0
            if (captchaEditText.text.toString() == "") {
//                captchaEditText.setError("Introduzca el código captcha.");
                Toast.makeText(context, "Introduzca el código captcha", Toast.LENGTH_SHORT).show()
                return false
            }
            if (validate != 1) {
                if (Util.isConnected(context)) {
                    loadingBar!!.show()

                    RunTask(object : Communicator {
                        private lateinit var status: Pair<Boolean, String?>

                        val current = currentUser()
                        val username = current.fullUsername
                        val password = current.password

                        override fun communicate() {
                            status = try {
                                client.setCredentials(username, password)
                                val nautaUser = client.login(captchaEditText.text.toString())
                                account = nautaUser.userName
                                remainingTime = nautaUser.time
                                blockDate = nautaUser.blockingDate
                                deleteDate = nautaUser.dateOfElimination
                                accountType1 = nautaUser.accountType
                                serviceType = nautaUser.serviceType
                                credit = nautaUser.credit
                                mailAccount = nautaUser.mailAccount

                                Pair(true, null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Pair(false, e.message)
                            }
                        }

                        override fun postCommunicate() {
                            val (isOk, err) = status
                            loadingBar!!.dismiss()

                            if (isOk) {
                                transitionIntent =
                                    Intent(context, PortalNautaActivity::class.java)
                                transitionIntent!!.putExtra(
                                    "userName",
                                    account
                                )
                                transitionIntent!!.putExtra(
                                    "password",
                                    currentUser().password
                                )
                                transitionIntent!!.putExtra(
                                    "blockDate",
                                    blockDate
                                )
                                transitionIntent!!.putExtra(
                                    "delDate",
                                    deleteDate
                                )
                                transitionIntent!!.putExtra(
                                    "accountType",
                                    accountType1
                                )
                                transitionIntent!!.putExtra(
                                    "serviceType",
                                    serviceType
                                )
                                transitionIntent!!.putExtra(
                                    "credit",
                                    credit
                                )
                                transitionIntent!!.putExtra(
                                    "time",
                                    remainingTime
                                )
                                transitionIntent!!.putExtra(
                                    "mailAccount",
                                    mailAccount
                                )
                                //
                                val user = currentUser()
                                user.blockDate = blockDate
                                user.delDate = deleteDate
                                user.accountType = accountType1
                                user.serviceType = serviceType
                                user.accountCredit = credit
                                user.leftTime = remainingTime
                                user.emailAccount = mailAccount
                                userViewModel!!.updateUser(user)
                                //
                                val prefEditor = pref!!.edit()
                                prefEditor.putString(
                                    "last_portal_nauta_update", Util.date2String(
                                        Util.currentDate()
                                    )
                                )
                                prefEditor.apply()
                                //
                                startActivity(transitionIntent)
                            } else {
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }).execute()
                } else {
                    Toast.makeText(context, "No está conectado", Toast.LENGTH_SHORT).show()
                }
            }
            return true
        }
    }

    fun setErrorMessage(message: String?, r: Int) {
        errorLayout!!.visibility = View.VISIBLE
        errorsTextView!!.text = message

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
            noUsers!!.visibility = View.VISIBLE
            selectUserSpinner!!.visibility = View.GONE
            editUserBtn!!.visibility = View.GONE
            delUserBtn!!.visibility = View.GONE
            userInfoBtn!!.visibility = View.GONE
            connectBtn!!.visibility = View.GONE
            connectionLimiter!!.isChecked = false
            connectionLimiter!!.visibility = View.GONE
            setErrorMessage(
                "Agrega al menos un usuario para usar esta funcionalidad",
                R.raw.userlogin
            )
        } else {
            noUsers!!.visibility = View.GONE
            selectUserSpinner!!.visibility = View.VISIBLE
            editUserBtn!!.visibility = View.VISIBLE
            delUserBtn!!.visibility = View.VISIBLE
            userInfoBtn!!.visibility = View.VISIBLE
            connectBtn!!.visibility = View.VISIBLE
            connectionLimiter!!.visibility = View.VISIBLE
            errorLayout!!.visibility = View.GONE
        }
    }

    private fun currentUser(): User {
        return userList[currentUserInt]
    }
}
