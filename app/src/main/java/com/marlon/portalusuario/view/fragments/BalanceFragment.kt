package com.marlon.portalusuario.view.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.UssdResponseCallback
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.FragmentCuentasBinding
import com.marlon.portalusuario.perfil.ImageSaver
import com.marlon.portalusuario.perfil.PerfilActivity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Objects

class BalanceFragment : Fragment() {
    private lateinit var binding: FragmentCuentasBinding

    private var manager: TelephonyManager? = null
    private var manager2: TelephonyManager? = null
    private var managerMain: TelephonyManager? = null

    var spBalance: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    // preference dualSim
    private var simCardSelected: String? = "1"
    private var spSim: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCuentasBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Show an alert dialog to the user that the feature is not supported
            showFeatureUnsupportedAlert()
        }

        // SharedPreferences para guardar datos de cuentas
        spBalance = requireActivity().getSharedPreferences("cuentas", Context.MODE_PRIVATE)
        editor = spBalance?.edit()

        // Preferences DualSIM
        spSim = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        simCardSelected = spSim?.getString(getString(R.string.sim_key), "1")

        // ESCOGER SIM0-SIM1
        binding.checkSimDual.isChecked = simCardSelected == "0"
        binding.checkSimDual.setOnCheckedChangeListener { _, isChecked ->
            simCardSelected = if (isChecked) {
                "0"
            } else {
                "1"
            }
            spSim?.edit()?.putString("sim_key", simCardSelected)?.apply()
        }
        return binding.root
    }

    private fun showFeatureUnsupportedAlert() {
        val featureUnsupportedDialog = AlertDialog.Builder(requireActivity())
        featureUnsupportedDialog.setTitle("Advertencia")
        featureUnsupportedDialog.setMessage(R.string.advertencia)
        featureUnsupportedDialog.setPositiveButton("Ok", null)
        featureUnsupportedDialog.create().show()
    }

    override fun onViewCreated(arg0: View, arg1: Bundle?) {
        super.onViewCreated(arg0, arg1)

        // Mostrar saludo con el nombre del usuario
        val spPerfil = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)
        val name = spPerfil.getString("nombre", "").toString()
        val numero = spPerfil.getString("numero", "").toString()
        val nauta = spPerfil.getString("nauta", "").toString()
        if (name.isEmpty()) {
            binding.textname.text = "Usuario"
        } else {
            binding.textname.text = name
        }
        if (numero.isEmpty()) {
            binding.textPhoneNumber.text = "Numero"
        } else {
            binding.textPhoneNumber.text = numero
        }
        if (nauta.isEmpty()) {
            binding.textEmail.text = "Nauta"
        } else {
            binding.textEmail.text = nauta
        }
        // saludo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentTime = LocalDateTime.now()
            if (currentTime.hour < 12) {
                binding.imgGreetings.setImageResource(R.drawable.baseline_light_mode_24)
                binding.textSaludoPerfil.text = getString(R.string.title_good_morning)
            } else if (currentTime.hour in 12..17) {
                binding.imgGreetings.setImageResource(R.drawable.outline_brightness_4_24)
                binding.textSaludoPerfil.text = getString(R.string.title_good_afternoon)
            } else {
                binding.imgGreetings.setImageResource(R.drawable.outline_dark_mode_24)
                binding.textSaludoPerfil.text = getString(R.string.title_good_night)
            }
        } else {
            binding.textSaludoPerfil.text = "Hola"
        }
        binding.editar.setOnClickListener { startActivity(Intent(context, PerfilActivity::class.java)) }

        // Perfil en el header
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val load = ImageSaver(context)
                .setFileName("IMG.png")
                .setDirectoryName("PortalUsuario")
                .load()
            if (load == null) {
                binding.imgDrawerPerfil.setImageResource(R.drawable.portal)
            } else {
                binding.imgDrawerPerfil.setImageBitmap(load)
            }
        } else {
            val load = ImageSaver(context)
                .setExternal(true)
                .setFileName("IMG.png")
                .setDirectoryName("PortalUsuario")
                .load()
            if (load == null) {
                binding.imgDrawerPerfil.setImageResource(R.drawable.portal)
            } else {
                binding.imgDrawerPerfil.setImageBitmap(load)
            }
        }

        // Preferences DualSIM
        spSim = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        simCardSelected = spSim?.getString(getString(R.string.sim_key), "0")


        // Ver bonos promocionales
        binding.buttonCuentasBono.setOnClickListener {
            val promo = spBalance!!.getString("bonos", null)
            val alertdialogo = AlertDialog.Builder(requireActivity())
            alertdialogo.setMessage(promo)
            alertdialogo.setPositiveButton("Ok", null)
            alertdialogo.create().show()
        }

        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener {
            // Progress dialog
            val progressDialog = ProgressDialog(activity)
            progressDialog.max = 100
            progressDialog.setTitle("Actualizando USSD")
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.setCancelable(false)
            progressDialog.show()

            // UPDATE
            Handler(Looper.getMainLooper())
                .postDelayed(
                    {
                        if (simCardSelected == "0") {
                            for (s in simSlotName) {
                                getBalance(0)
                            }
                        } else if (simCardSelected == "1") {
                            for (s in simSlotName) {
                                getBalance(1)
                            }
                        }
                        progressDialog.setTitle(
                            "Actualizando saldo"
                        )
                        progressDialog.progress = 20

                        //// MINUTOS
                        Handler(Looper.getMainLooper())
                            .postDelayed(
                                {
                                    if (simCardSelected == "0") {
                                        for (s in simSlotName) {
                                            getMinutes(
                                                0
                                            )
                                        }
                                    } else if (simCardSelected ==
                                        "1"
                                    ) {
                                        for (s in simSlotName) {
                                            getMinutes(
                                                1
                                            )
                                        }
                                    }
                                    progressDialog.setTitle(
                                        "Actualizando minutos"
                                    )
                                    progressDialog.progress = 40

                                    // MENSAJES
                                    Handler(
                                        Looper
                                            .getMainLooper()
                                    )
                                        .postDelayed(
                                            {
                                                if (simCardSelected
                                                    ==
                                                    "0"
                                                ) {
                                                    for (s in simSlotName) {
                                                        getRemainingSMS(
                                                            0
                                                        )
                                                    }
                                                } else if (simCardSelected
                                                    ==
                                                    "1"
                                                ) {
                                                    for (s in simSlotName) {
                                                        getRemainingSMS(
                                                            1
                                                        )
                                                    }
                                                }
                                                progressDialog.setTitle(
                                                    "Actualizando mensajes"
                                                )
                                                progressDialog.progress = 60
                                                // BONOS
                                                Handler(
                                                    Looper
                                                        .getMainLooper()
                                                )
                                                    .postDelayed(
                                                        {
                                                            if (simCardSelected
                                                                ==
                                                                "0"
                                                            ) {
                                                                for (s in simSlotName) {
                                                                    getRemainingBonuses(
                                                                        0
                                                                    )
                                                                }
                                                            } else if (simCardSelected
                                                                ==
                                                                "1"
                                                            ) {
                                                                for (s in simSlotName) {
                                                                    getRemainingBonuses(
                                                                        1
                                                                    )
                                                                }
                                                            }
                                                            progressDialog.setTitle(
                                                                "Actualizando bonos"
                                                            )
                                                            progressDialog.progress = 80

                                                            /// DATOS
                                                            Handler(
                                                                Looper
                                                                    .getMainLooper()
                                                            )
                                                                .postDelayed(
                                                                    {
                                                                        if (simCardSelected
                                                                            ==
                                                                            "0"
                                                                        ) {
                                                                            for (s in simSlotName) {
                                                                                getRemainingData(
                                                                                    0
                                                                                )
                                                                            }
                                                                        } else if (simCardSelected
                                                                            ==
                                                                            "1"
                                                                        ) {
                                                                            for (s in simSlotName) {
                                                                                getRemainingData(
                                                                                    1
                                                                                )
                                                                            }
                                                                        }
                                                                        progressDialog.setTitle(
                                                                            "Actualizando datos"
                                                                        )
                                                                        progressDialog.progress = 100
                                                                        binding.swipeRefresh.isRefreshing = false
                                                                        // hora
                                                                        val hora = SimpleDateFormat(
                                                                            "h:mm a"
                                                                        )
                                                                        val time = hora
                                                                            .format(
                                                                                Date()
                                                                            )
                                                                        editor
                                                                            ?.putString(
                                                                                "hora",
                                                                                time
                                                                                    .toString()
                                                                            )
                                                                        editor
                                                                            ?.commit()
                                                                        binding.textHoraUpdate.text = "Actualizado: $time"
                                                                        Handler(
                                                                            Looper
                                                                                .getMainLooper()
                                                                        )
                                                                            .postDelayed(
                                                                                { progressDialog.dismiss() },
                                                                                8000
                                                                            )
                                                                    },
                                                                    6000
                                                                )
                                                        },
                                                        6000
                                                    )
                                            },
                                            5000
                                        )
                                },
                                4000
                            )
                    },
                    1000
                )
        }
    }

    private fun getBalance(sim: Int) {
        val ussdCode = "*222#"
        if (ussdCode.equals("", ignoreCase = true)) return
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager!!.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain!!.sendUssdRequest(
                ussdCode,
                object : UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        super.onReceiveUssdResponse(telephonyManager, request, response)
                        val messageSaldo = response.toString()
                            .replace("Saldo:", "")
                            .replaceFirst("CUP(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        val sb1 = StringBuilder()
                        sb1.append(messageSaldo)
                        sb1.append(" CUP")
                        binding.textCuentasSaldo.text = sb1
                        editor!!.putString("saldo", sb1.toString())
                        editor!!.commit()

                        // String mostrar fecha de vencimiento de saldo principal
                        val messageVence = response.toString()
                            .replaceFirst("(.*)vence".toRegex(), "")
                            .replace("-", "/")
                            .replace(".", "")
                            .trim { it <= ' ' }
                        val sb2 = StringBuilder()
                        sb2.append("Expira: ")
                        sb2.append(messageVence)
                        binding.textCuentasVenceSim.text = sb2
                        editor!!.putString("vence_saldo", sb2.toString())
                        editor!!.commit()
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        super.onReceiveUssdResponseFailed(
                            telephonyManager, request, failureCode
                        )
                        Log.e(
                            "TAG",
                            "onReceiveUssdResponseFailed: $failureCode$request"
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }

    private fun getMinutes(sim: Int) {
        val ussdCode = "*222*869#"
        if (ussdCode.equals("", ignoreCase = true)) return
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager!!.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain!!.sendUssdRequest(
                ussdCode,
                object : UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        super.onReceiveUssdResponse(telephonyManager, request, response)
                        val minutos = response.toString()
                            .replace(
                                "Usted debe adquirir un plan de minutos. Para una nueva compra marque *133#",
                                "00.00.00"
                            )
                            .replace("Usted dispone de", "")
                            .replaceFirst("MIN(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        binding.textCuentasMinutos.text = minutos
                        editor!!.putString("minutos", minutos)
                        editor!!.commit()
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        super.onReceiveUssdResponseFailed(
                            telephonyManager, request, failureCode
                        )
                        Log.e(
                            "TAG",
                            "onReceiveUssdResponseFailed: $failureCode$request"
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }

    private fun getRemainingSMS(sim: Int) {
        val ussdCode = "*222*767#"
        if (ussdCode.equals("", ignoreCase = true)) return
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager!!.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain!!.sendUssdRequest(
                ussdCode,
                object : UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        super.onReceiveUssdResponse(telephonyManager, request, response)
                        // cantidad de mensajes
                        val messageSms = response.toString()
                            .replace(
                                "Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#",
                                "0"
                            )
                            .replace(
                                "Usted no dispone de SMS. Para una nueva compra marque *133#",
                                "0"
                            )
                            .replace("Usted dispone de", "")
                            .replace("no activos.", "")
                            .replaceFirst("SMS(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        binding.textCuentasMensajes.text = messageSms
                        editor!!.putString("sms", messageSms)
                        editor!!.commit()

                        // Fecha vencimiento sms y voz
                        val messageVenceSms = response.toString()
                            .replace(
                                "Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#",
                                "0 días"
                            )
                            .replace(
                                "Usted no dispone de SMS. Para una nueva compra marque *133#",
                                "0 días"
                            )
                            .replaceFirst("(.*)por".toRegex(), "")
                            .replace("dias", "días")
                            .trim { it <= ' ' }
                        editor!!.putString(
                            "vence_sms",
                            messageVenceSms
                                .replaceFirst("(.*)no activos.".toRegex(), "0 días")
                        )
                        editor!!.commit()
                        binding.textCuentasVenceMinSms.text = messageVenceSms

                        // no usp
                        spBalance!!.getString("vence_sms", messageVenceSms)
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        super.onReceiveUssdResponseFailed(
                            telephonyManager, request, failureCode
                        )
                        Log.e(
                            "TAG",
                            "onReceiveUssdResponseFailed: $failureCode$request"
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }

    private fun getRemainingData(sim: Int) {
        val ussdCode = "*222*328#"
        if (ussdCode.equals("", ignoreCase = true)) return
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager =
                requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager!!.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain!!.sendUssdRequest(
                ussdCode,
                object : UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        super.onReceiveUssdResponse(telephonyManager, request, response)

                        // bolsa diaria
                        val diaria = response.toString()
                            .replace(
                                "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                "0 MB"
                            )
                            .replace(
                                "Tarifa: No activa. Paquetes: No dispone de MB.",
                                "0 MB"
                            )
                            .replace("Tarifa: No activa. Diaria:", "")
                            .replace(
                                "Tarifa: No activa. Diaria: No dispone de MB.",
                                "0 MB"
                            )
                            .replace("Tarifa: No activa.", "0 MB")
                            .replaceFirst("Mensajeria:(.*)".toRegex(), "")
                            .replaceFirst("Paquetes:(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        editor!!.putString(
                            "diaria",
                            diaria
                                .replace("no activos.", "")
                                .replaceFirst("validos(.*)".toRegex(), "")
                                .replace("No dispone de MB.", "0 MB")
                        )
                        editor!!.commit()
                        val bolsaDiaria = spBalance!!.getString("diaria", diaria)
                        binding.textCuentasDiaria.text = bolsaDiaria

                        // vencimiento de bolsa diaria
                        val messageVenceDiaria = response.toString()
                            .replace(
                                "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                "Debe adquirir un paquete"
                            )
                            .replace(
                                "Tarifa: No activa. Paquetes: No dispone de MB.",
                                "0 horas"
                            )
                            .replace("Tarifa: No activa. Diaria:", "")
                            .replace("Tarifa: No activa.", "0 horas")
                            .replaceFirst("Mensajeria:(.*)".toRegex(), "")
                            .replaceFirst("Paquetes:(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        editor!!.putString(
                            "vence_diaria",
                            messageVenceDiaria
                                .replaceFirst("(.*)no activos.".toRegex(), "sin consumir")
                                .replaceFirst("(.*)validos".toRegex(), "")
                                .replace("No dispone de MB.", "0 horas")
                                .replace("horas.", "horas")
                        )
                        editor!!.commit()
                        val venceDiaria = spBalance!!.getString(
                            "vence_diaria", messageVenceDiaria
                        )
                        binding.textCuentasVenceDiaria.text = venceDiaria

                        // bolsa de mensajeria
                        val messageMensajeria = response.toString()
                            .replace(
                                "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                "0 MB"
                            )
                            .replace(
                                "Tarifa: No activa. Paquetes: No dispone de MB.",
                                "0 MB"
                            )
                            .replace(
                                "Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:",
                                ""
                            )
                            .replaceFirst("(.*)no activos. Mensajeria:".toRegex(), "")
                            .replaceFirst("(.*)horas. Mensajeria:".toRegex(), "")
                            .replace(
                                "Tarifa: No activa. Mensajeria: No dispone de MB.",
                                "0 MB"
                            )
                            .replace("Tarifa: No activa. Mensajeria:", "")
                            .replace("Tarifa: No activa.", "0 MB")
                            .replaceFirst("Diaria:(.*)".toRegex(), "")
                            .replaceFirst("Paquetes:(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        editor!!.putString(
                            "mensajeria",
                            messageMensajeria
                                .replace("vencen hoy.", "")
                                .replaceFirst("validos(.*)".toRegex(), "")
                        )
                        editor!!.commit()
                        val bolsaMsg = spBalance!!.getString(
                            "mensajeria", messageMensajeria
                        )
                        binding.textCuentasMensajeria.text = bolsaMsg

                        // vence mensajeria
                        val messageVenceMensajeria = response.toString()
                            .replace(
                                "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                "0 días"
                            )
                            .replace(
                                "Tarifa: No activa. Paquetes: No dispone de MB.",
                                "0 días"
                            )
                            .replace(
                                "Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:",
                                ""
                            )
                            .replaceFirst("(.*)no activos. Mensajeria:".toRegex(), "")
                            .replaceFirst("(.*)horas. Mensajeria:".toRegex(), "")
                            .replace(
                                "Tarifa: No activa. Mensajeria: No dispone de MB.",
                                "0 días"
                            )
                            .replace("Tarifa: No activa. Mensajeria:", "")
                            .replace("Tarifa: No activa.", "0 días")
                            .replaceFirst("Diaria:(.*)".toRegex(), "")
                            .replaceFirst("Paquetes:(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        editor!!.putString(
                            "vence_mensajeria",
                            messageVenceMensajeria
                                .replaceFirst("(.*)validos".toRegex(), "")
                                .replaceFirst("(.*)vencen hoy.".toRegex(), "vence hoy")
                                .replace("dias.", "días")
                        )
                        editor!!.commit()
                        val venceMensajeria = spBalance!!.getString("vence_mensajeria", "0 días")
                        binding.textCuentasVenceMensajeria.text = venceMensajeria

                        // paquetes
                        val message_paquete = response.toString()
                            .replace(
                                "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                "0 MB"
                            )
                            .replace(
                                "Tarifa: No activa. Diaria: No dispone de MB.",
                                "0 MB"
                            )
                            .replaceFirst("(.*)Paquetes:".toRegex(), "")
                            .replaceFirst("\\+(.*)".toRegex(), "")
                            .replaceFirst("Tarifa: No activa. Diaria:(.*)".toRegex(), "0 MB")
                            .replaceFirst(
                                "Tarifa: No activa. Mensajeria:(.*)".toRegex(), "0 MB"
                            )
                            .trim { it <= ' ' }
                        editor!!.putString(
                            "paquete",
                            message_paquete
                                .replaceFirst("(.*)LTE(.*)".toRegex(), "0 MB")
                                .replaceFirst("validos(.*)".toRegex(), "")
                        )
                        editor!!.commit()
                        val paquete = spBalance!!.getString("paquete", message_paquete)
                        binding.textCuentasDatos.text = paquete

                        // message paquete LTE
                        val messageLte = response.toString()
                            .replace(
                                "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                "0 MB"
                            )
                            .replace(
                                "Tarifa: No activa. Diaria: No dispone de MB.",
                                "0 MB"
                            )
                            .replaceFirst("(.*)Paquetes:".toRegex(), "")
                            .replaceFirst("(.*)\\+".toRegex(), "")
                            .replaceFirst("Tarifa: No activa. Diaria:(.*)".toRegex(), "0 MB")
                            .replaceFirst(
                                "Tarifa: No activa. Mensajeria:(.*)".toRegex(), "0 MB"
                            )
                            .trim { it <= ' ' }
                        val lte = messageLte
                            .replaceFirst("LTE(.*)".toRegex(), "")
                            .replaceFirst("(.*)validos(.*)".toRegex(), "0 MB")
                            .replace("no activos.", "")
                        editor!!.putString("lte", lte)
                        editor!!.commit()
                        binding.textCuentasDatosLte.text = lte

                        // vencimiento de datos
                        val vencimientoDatos = response.toString()
                            .replace(
                                "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                "0 días"
                            )
                            .replace(
                                "Tarifa: No activa. Diaria: No dispone de MB.",
                                "0 MB"
                            )
                            .replaceFirst("(.*)Paquetes:".toRegex(), "")
                            .replaceFirst("Tarifa: No activa. Diaria:(.*)".toRegex(), "0 MB")
                            .replaceFirst(
                                "Tarifa: No activa. Mensajeria:(.*)".toRegex(), "0 MB"
                            )
                            .trim { it <= ' ' }
                        val vence = vencimientoDatos
                            .replaceFirst("(.*)validos".toRegex(), "")
                            .replace("dias.", "días")
                            .replace("no activos", "0 días")
                            .replaceFirst("(.*)LTE".toRegex(), "")
                        editor!!.putString("vence_datos", vence)
                        editor!!.commit()
                        binding.textCuentasVenceDatos.text = vence
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        super.onReceiveUssdResponseFailed(
                            telephonyManager, request, failureCode
                        )
                        Log.e(
                            "TAG",
                            "onReceiveUssdResponseFailed: $failureCode$request"
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }

    private fun getRemainingBonuses(sim: Int) {
        val ussdCode = "*222*266#"
        if (ussdCode.equals("", ignoreCase = true)) return
        if (ContextCompat.checkSelfPermission(
                Objects.requireNonNull(requireActivity()),
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager =
                requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager!!.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain!!.sendUssdRequest(
                ussdCode,
                object : UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        super.onReceiveUssdResponse(telephonyManager, request, response)
                        val datosNacionales = response.toString()
                            .replace("Usted no dispone de bonos activos.", "0 MB")
                            .replaceFirst("(.*)Datos.cu ".toRegex(), "")
                            .replaceFirst("vence(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        editor!!.putString("datos_nacionales", datosNacionales)
                        editor!!.commit()
                        binding.textCuentasDatosNacionales.text = datosNacionales

                        // Bonos en promoción
                        val bonos = response.toString()
                            .replace("Usted no dispone de bonos activos.", "")
                            .replaceFirst("Datos.cu(.*)".toRegex(), "")
                            .trim { it <= ' ' }
                        if (!TextUtils.isEmpty(bonos)) {
                            binding.buttonCuentasBono.visibility = View.VISIBLE
                        } else {
                            binding.buttonCuentasBono.visibility = View.GONE
                        }
                        editor!!.putString("bonos", bonos)
                        editor!!.commit()
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        super.onReceiveUssdResponseFailed(
                            telephonyManager, request, failureCode
                        )
                        Log.e(
                            "TAG",
                            "onReceiveUssdResponseFailed: $failureCode$request"
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (view == null) {
            return
        }
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK
        }
        val spProfile = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)

        binding.textname.text =
            spProfile.getString("nombre", "")?.takeIf { it.isNotEmpty() } ?: getString(R.string.user_string)
        binding.textPhoneNumber.text =
            spProfile.getString("numero", "")?.takeIf { it.isNotEmpty() } ?: getString(R.string.phone_number_string)
        binding.textEmail.text =
            spProfile.getString("nauta", "")?.takeIf { it.isNotEmpty() } ?: getString(R.string.mail_nauta_string)

        // saldo movil
        binding.textCuentasSaldo.text = spBalance!!.getString("saldo", "0.00 CUP")

        // vence saldo movil
        binding.textCuentasVenceSim.text = spBalance!!.getString("vence_saldo", "Expira: 00/00/00")

        // minutos
        binding.textCuentasMinutos.text = spBalance!!.getString("minutos", "00:00:00")

        // mensajes
        binding.textCuentasMensajes.text = spBalance!!.getString("sms", "0")

        // vence sms y voz
        binding.textCuentasVenceMinSms.text = spBalance!!.getString("vence_sms", "0 días")

        // bonos en promo
        val promo = spBalance?.getString("bonos", null)
        binding.buttonCuentasBono.visibility = if (!promo.isNullOrEmpty()) View.VISIBLE else View.GONE

        // datos nacionales
        binding.textCuentasDatosNacionales.text = spBalance!!.getString("datos_nacionales", "0 MB")

        // paquetes
        binding.textCuentasDatos.text = spBalance!!.getString("paquete", "0 MB")

        // lte
        binding.textCuentasDatosLte.text = spBalance!!.getString("lte", "0 MB")

        // vence datos
        binding.textCuentasVenceDatos.text = spBalance!!.getString("vence_datos", "0 días")

        // mensajeria y vencimiento
        binding.textCuentasMensajeria.text = spBalance!!.getString("mensajeria", "0 MB")
        binding.textCuentasVenceMensajeria.text = spBalance!!.getString("vence_mensajeria", "0 días")

        // diaria y vencimiento
        binding.textCuentasDiaria.text = spBalance!!.getString("diaria", "0 MB")
        binding.textCuentasVenceDiaria.text = spBalance!!.getString("vence_diaria", "0 horas")

        // update hora
        binding.textHoraUpdate.text = "Actualizado: ${spBalance!!.getString("hora", "00:00")}"
    }

    companion object {
        val simSlotName = arrayOf(
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
        )
    }
}