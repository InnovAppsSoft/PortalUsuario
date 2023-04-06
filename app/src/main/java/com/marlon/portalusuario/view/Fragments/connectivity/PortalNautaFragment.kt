package com.marlon.portalusuario.view.Fragments.connectivity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import com.makeramen.roundedimageview.RoundedImageView
import com.marlon.portalusuario.R
import com.marlon.portalusuario.net.Communicator
import com.marlon.portalusuario.net.RunTask
import com.marlon.portalusuario.util.AutoCompleteAdapter
import com.marlon.portalusuario.view.activities.PortalNautaActivity
import cu.marilasoft.selibrary.UserPortal
import soup.neumorphism.NeumorphButton
import java.io.ByteArrayInputStream
import java.io.IOException

class PortalNautaFragment : Fragment() {
    private var pref: SharedPreferences? = null
    var _time: String? = null
    var account: String? = null
    var blockDate: String? = null
    var deleteDate: String? = null
    var accountType: String? = null
    var serviceType: String? = null
    var credit: String? = null
    var mailAccount: String? = null
    var autoCompleteTextViewUserName: AutoCompleteTextView? = null
    var etPassword: TextInputEditText? = null
    var etCaptchaCode: TextInputEditText? = null
    var ivCaptchaImg: RoundedImageView? = null
    var btLoginAccept: NeumorphButton? = null
    var bitmap: Bitmap? = null
    var progressDialog: ProgressDialog? = null
    var intent: Intent? = null
    var btnReload: Button? = null
    var btnLogin: Button? = null
    var cookies: Map<String, String>? = null
    var status: Map<Any, Any>? = null
    var userPortal = UserPortal()
    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_portalnauta, container, false)
        autoCompleteTextViewUserName = view.findViewById(R.id.actv)
        etPassword = view.findViewById(R.id.et_password)
        etCaptchaCode = view.findViewById(R.id.et_captchaCode)
        ivCaptchaImg = view.findViewById(R.id.iv_captcha)
        btLoginAccept = view.findViewById(R.id.bt_login)
        btLoginAccept?.setOnClickListener { login() }
        btnReload = view.findViewById(R.id.bt_reload)
        btnReload?.setOnClickListener { reLoadCaptcha() }
        progressDialog = ProgressDialog(context)
        intent = Intent(context, PortalNautaActivity::class.java)
        val adapter = AutoCompleteAdapter(requireContext(), android.R.layout.simple_expandable_list_item_1)
        //ArrayAdapter adapter1 = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1, dbHandler.selectAllNautaUsers());
        autoCompleteTextViewUserName?.setAdapter(adapter)
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val user = pref?.getString("nauta_user", "")
        val password = pref?.getString("nauta_password", "")
        autoCompleteTextViewUserName?.setText(user)
        etPassword?.setText(password)
        preLoad()
        return view
    }

    private fun preLoad() {
        RunTask(object : Communicator {
            override fun Communicate() {}
            override fun Communicate(userPortal: UserPortal) {
                var userPortal = userPortal
                try {
                    UserPortal.preLogin()
                    var cookies = userPortal.cookies()
                    userPortal.loadCAPTCHA(cookies)
                    cookies = cookies
                    userPortal = userPortal
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun communicate() {
                if (userPortal.captchaImg() != null) {
                    bitmap = BitmapFactory.decodeStream(
                        ByteArrayInputStream(userPortal.captchaImg())
                    )
                    ivCaptchaImg!!.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(
                        context,
                        "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, userPortal).execute()
    }

    fun reLoadCaptcha() {
        RunTask(object : Communicator {
            override fun Communicate() {}
            override fun Communicate(userPortal: UserPortal) {
                var userPortal = userPortal
                try {
                    if (cookies == null) {
                        UserPortal.preLogin()
                        cookies = userPortal.cookies()
                        userPortal = userPortal
                    }
                    userPortal.loadCAPTCHA(cookies)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun communicate() {
                if (userPortal.captchaImg() != null) {
                    bitmap = BitmapFactory.decodeStream(
                        ByteArrayInputStream(userPortal.captchaImg())
                    )
                    ivCaptchaImg!!.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(
                        context,
                        "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }, userPortal).execute()
    }

    private fun login() {
        progressDialog!!.setIcon(R.mipmap.ic_launcher)
        progressDialog!!.setMessage("Conectando...")
        progressDialog!!.setCancelable(false)
        var validate = 0
        if (autoCompleteTextViewUserName!!.text.toString() == "") {
            autoCompleteTextViewUserName!!.error = "Introduzca un nombre de usuario"
            validate = 1
        } else if (!autoCompleteTextViewUserName!!.text.toString().endsWith("@nauta.com.cu") &&
            !autoCompleteTextViewUserName!!.text.toString().endsWith("@nauta.co.cu")
        ) {
            autoCompleteTextViewUserName!!.error = "Introduzca un nombre de usuario válido"
            validate = 1
        }
        if (etPassword!!.text.toString() == "") {
            etPassword!!.error = "Introduzca una contraseña"
            validate = 1
        }
        if (etCaptchaCode!!.text.toString() == "") {
            etCaptchaCode!!.error = "Introduzca el cddigo captcha."
            validate = 1
        }
        if (validate != 1) {
            progressDialog!!.show()
            RunTask(object : Communicator {
                override fun Communicate() {}
                override fun Communicate(userPortal: UserPortal) {
                    try {
                        userPortal.login(
                            autoCompleteTextViewUserName!!.text.toString(),
                            etPassword!!.text.toString(),
                            etCaptchaCode!!.text.toString(),
                            cookies
                        )
                        status = userPortal.status()
                        if (userPortal.status()["status"] == "success") {
                            account = userPortal.userName()
                            _time = userPortal.time()
                            blockDate = userPortal.blockDate()
                            deleteDate = userPortal.delDate()
                            accountType = userPortal.accountType()
                            serviceType = userPortal.serviceType()
                            credit = userPortal.credit()
                            mailAccount = userPortal.mailAccount()
                            //
                            // guardar usuario
                            val prefsEditor = pref!!.edit()
                            prefsEditor.putString("nauta_user", autoCompleteTextViewUserName!!.text.toString())
                            prefsEditor.putString("nauta_password", etPassword!!.text.toString())
                            prefsEditor.apply()
                            //
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        progressDialog!!.dismiss()
                    }
                }

                override fun communicate() {
                    var errors = ""
                    try {
                        progressDialog!!.dismiss()
                        if (status!!["status"] == "success") {
                            intent!!.putExtra(
                                "userName",
                                account
                            )
                            intent!!.putExtra(
                                "password",
                                etPassword!!.text.toString()
                            )
                            intent!!.putExtra(
                                "blockDate",
                                blockDate
                            )
                            intent!!.putExtra(
                                "delDate",
                                deleteDate
                            )
                            intent!!.putExtra(
                                "accountType",
                                accountType
                            )
                            intent!!.putExtra(
                                "serviceType",
                                serviceType
                            )
                            intent!!.putExtra(
                                "credit",
                                credit
                            )
                            intent!!.putExtra(
                                "time",
                                _time
                            )
                            intent!!.putExtra(
                                "mailAccount",
                                mailAccount
                            )
                            intent!!.putExtra(
                                "session",
                                cookies!!["session"]
                            )
                            intent!!.putExtra(
                                "nauta_lang",
                                cookies!!["nauta_lang"]
                            )
                            transition()
                        } else if (status!!["status"] == "error") {
                            for (error in (status!!["msg"] as List<String>?)!!) {
                                errors += """
                                    
                                    $error
                                    """.trimIndent()
                                if (error.contains("Usuario")) {
                                    autoCompleteTextViewUserName!!.error = "Puede que el " +
                                            "nombre de usuario sea incorrecto"
                                }
                                if (error.contains("contraseña")) {
                                    etPassword!!.error = "Puede que la " +
                                            "contraseña sea incorrecta"
                                }
                                if (error.contains("Captcha")) {
                                    etCaptchaCode!!.error = error
                                }
                            }
                            reLoadCaptcha()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        Toast.makeText(
                            context,
                            "Ha ocurrido un error :-(\n$errors",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }, userPortal).execute()
        }
    }

    fun transition() {
        startActivity(intent)
    }
}