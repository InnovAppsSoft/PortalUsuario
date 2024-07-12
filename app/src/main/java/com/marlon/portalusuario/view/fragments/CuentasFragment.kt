package com.marlon.portalusuario.view.fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.marlon.portalusuario.R
import java.text.SimpleDateFormat
import java.util.*

class CuentasFragment : Fragment() {
    private lateinit var refrescar: SwipeRefreshLayout
    private lateinit var saldoText: TextView
    private lateinit var expiraText: TextView
    private lateinit var minutosText: TextView
    private lateinit var mensajesText: TextView
    private lateinit var venceMinutos: TextView
    private lateinit var datosText: TextView
    private lateinit var datosLte: TextView
    private lateinit var datosNacionales: TextView
    private lateinit var venceDatos: TextView
    private lateinit var bolsaSms: TextView
    private lateinit var venceBolsaSms: TextView
    private lateinit var bolsaDiaria: TextView
    private lateinit var venceBolsaDiaria: TextView
    private lateinit var actualizar: TextView
    private lateinit var escogerSim: CheckBox
    private lateinit var promoBonos: TextView
    private lateinit var spCuentas: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var spSim: SharedPreferences
    private lateinit var manager: TelephonyManager
    private lateinit var manager2: TelephonyManager
    private lateinit var managerMain: TelephonyManager
    private var sim = "0"

    companion object {
        val simSlotName = listOf(
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_cuentas, container, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            AlertDialog.Builder(requireActivity())
                .setTitle("Advertencia")
                .setMessage(R.string.advertencia)
                .setPositiveButton("Ok", null)
                .create()
                .show()
        }
        initializeUI(v)
        setupSimSelector()
        setupRefresh()
        return v
    }

    private fun initializeUI(v: View) {
        refrescar = v.findViewById(R.id.swipeRefresh)
        saldoText = v.findViewById(R.id.text_cuentas_saldo)
        expiraText = v.findViewById(R.id.text_cuentas_vence_sim)
        minutosText = v.findViewById(R.id.text_cuentas_minutos)
        mensajesText = v.findViewById(R.id.text_cuentas_mensajes)
        venceMinutos = v.findViewById(R.id.text_cuentas_vence_minutos)
        datosText = v.findViewById(R.id.text_cuentas_datos)
        datosLte = v.findViewById(R.id.text_cuentas_datos_lte)
        datosNacionales = v.findViewById(R.id.text_cuentas_datos_nacionales)
        venceDatos = v.findViewById(R.id.text_cuentas_vence_datos)
        bolsaSms = v.findViewById(R.id.text_cuentas_mensajeria)
        venceBolsaSms = v.findViewById(R.id.text_cuentas_vence_mensajeria)
        bolsaDiaria = v.findViewById(R.id.text_cuentas_diaria)
        venceBolsaDiaria = v.findViewById(R.id.text_cuentas_vence_diaria)
        actualizar = v.findViewById(R.id.text_hora_update)
        escogerSim = v.findViewById(R.id.check_sim_dual)
        promoBonos = v.findViewById(R.id.button_cuentas_bono)

        spCuentas = requireActivity().getSharedPreferences("cuentas", Context.MODE_PRIVATE)
        editor = spCuentas.edit()

        spSim = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!
        sim = spSim.getString(getString(R.string.sim_key), "0").orEmpty()

    }

    private fun setupSimSelector() {
        escogerSim.isChecked = sim == "1"
        escogerSim.setOnCheckedChangeListener { _, isChecked ->
            sim = if (isChecked) "1" else "0"
            spSim.edit().putString("sim_key", sim).apply()
        }
    }

    private fun setupRefresh() {
        refrescar.setOnRefreshListener {
            val progressDialog = ProgressDialog(activity).apply {
                setMax(100)
                setTitle("Actualizando USSD")
                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                setCancelable(false)
                show()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                val ussdCode = "*222#"
                val simSlot = if (sim == "0") 0 else 1
                simSlotName.forEach { consultaSaldo(ussdCode, simSlot) }

                progressDialog.setTitle("Actualizando saldo")
                progressDialog.progress = 20

                Handler(Looper.getMainLooper()).postDelayed({
                    val minutosCode = "*222*869#"
                    simSlotName.forEach { consultaMinutos(minutosCode, simSlot) }

                    progressDialog.setTitle("Actualizando minutos")
                    progressDialog.progress = 40

                    Handler(Looper.getMainLooper()).postDelayed({
                        val mensajesCode = "*222*767#"
                        simSlotName.forEach { consultaMensajes(mensajesCode, simSlot) }

                        progressDialog.setTitle("Actualizando mensajes")
                        progressDialog.progress = 60

                        Handler(Looper.getMainLooper()).postDelayed({
                            val bonosCode = "*222*266#"
                            simSlotName.forEach { consultaBonos(bonosCode, simSlot) }

                            progressDialog.setTitle("Actualizando bonos")
                            progressDialog.progress = 80

                            Handler(Looper.getMainLooper()).postDelayed({
                                val datosCode = "*222*328#"
                                simSlotName.forEach { consultaDatos(datosCode, simSlot) }

                                progressDialog.setTitle("Actualizando datos")
                                progressDialog.progress = 100

                                refrescar.isRefreshing = false
                                val hora = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                                editor.putString("hora", hora).apply()
                                actualizar.text = "Actualizado: $hora"

                                Handler(Looper.getMainLooper()).postDelayed({
                                    progressDialog.dismiss()
                                }, 8000)
                            }, 6000)
                        }, 6000)
                    }, 5000)
                }, 4000)
            }, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    private fun updateProfile() {
        saldoText.text = spCuentas.getString("saldo", "0.00 CUP")
        expiraText.text = spCuentas.getString("vence_saldo", "00/00/00")
        minutosText.text = spCuentas.getString("minutos", "00:00:00")
        mensajesText.text = spCuentas.getString("sms", "0")
        venceMinutos.text = spCuentas.getString("vence_sms", "0 días")
        promoBonos.visibility = if (TextUtils.isEmpty(spCuentas.getString("bonos", null))) View.GONE else View.VISIBLE
        datosNacionales.text = spCuentas.getString("datos_nacionales", "0 MB")
        datosText.text = spCuentas.getString("paquete", "0 MB")
        datosLte.text = spCuentas.getString("lte", "0 MB")
        venceDatos.text = spCuentas.getString("vence_datos", "0 días")
        bolsaSms.text = spCuentas.getString("mensajeria", "0 MB")
        venceBolsaSms.text = spCuentas.getString("vence_mensajeria", "0 días")
        bolsaDiaria.text = spCuentas.getString("diaria", "0 MB")
        venceBolsaDiaria.text = spCuentas.getString("vence_diaria", "0 horas")
        actualizar.text = "Actualizado: ${spCuentas.getString("hora", "00:00")}"
    }

    private fun consultaSaldo(ussdCode: String, sim: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain.sendUssdRequest(ussdCode, object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(telephonyManager: TelephonyManager, request: String, response: CharSequence) {
                    val messageSaldo = response.toString().replace("Saldo:", "").replaceFirst("CUP(.*)".toRegex(), "").trim()
                    saldoText.text = "$messageSaldo CUP"
                    editor.putString("saldo", "$messageSaldo CUP").apply()

                    val messageVence = response.toString().replaceFirst("(.*)vence".toRegex(), "").replace("-", "/").replace(".", "").trim()
                    expiraText.text = messageVence
                    editor.putString("vence_saldo", messageVence).apply()
                }

                override fun onReceiveUssdResponseFailed(telephonyManager: TelephonyManager, request: String, failureCode: Int) {
                    Log.e("TAG", "onReceiveUssdResponseFailed: $failureCode$request")
                }
            }, Handler(Looper.getMainLooper()))
        }
    }

    private fun consultaMinutos(ussdCode: String, sim: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain.sendUssdRequest(ussdCode, object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(telephonyManager: TelephonyManager, request: String, response: CharSequence) {
                    val minutos = response.toString()
                        .replace("Usted debe adquirir un plan de minutos. Para una nueva compra marque *133#".toRegex(), "00.00.00")
                        .replace("Usted dispone de", "")
                        .replaceFirst("MIN(.*)".toRegex(), "")
                        .trim()
                    minutosText.text = minutos
                    editor.putString("minutos", minutos).apply()
                }

                override fun onReceiveUssdResponseFailed(telephonyManager: TelephonyManager, request: String, failureCode: Int) {
                    Log.e("TAG", "onReceiveUssdResponseFailed: $failureCode$request")
                }
            }, Handler(Looper.getMainLooper()))
        }
    }

    private fun consultaMensajes(ussdCode: String, sim: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain.sendUssdRequest(ussdCode, object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(telephonyManager: TelephonyManager, request: String, response: CharSequence) {
                    val messageSms = response.toString()
                        .replace("Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#".toRegex(), "0")
                        .replace("Usted no dispone de SMS. Para una nueva compra marque *133#".toRegex(), "0")
                        .replace("Usted dispone de", "")
                        .replace("no activos.", "")
                        .replaceFirst("SMS(.*)".toRegex(), "")
                        .trim()
                    mensajesText.text = messageSms
                    editor.putString("sms", messageSms).apply()

                    val messageVenceSms = response.toString()
                        .replace("Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#".toRegex(), "0 días")
                        .replace("Usted no dispone de SMS. Para una nueva compra marque *133#".toRegex(), "0 días")
                        .replaceFirst("(.*)por".toRegex(), "")
                        .replace("dias", "días")
                        .trim()
                    editor.putString("vence_sms", messageVenceSms.replaceFirst("(.*)no activos.".toRegex(), "0 días")).apply()
                    venceMinutos.text = messageVenceSms

                    spCuentas.getString("vence_sms", messageVenceSms)?.let { venceSmsMin ->
                        venceMinutos.text = venceSmsMin
                    }
                }

                override fun onReceiveUssdResponseFailed(telephonyManager: TelephonyManager, request: String, failureCode: Int) {
                    Log.e("TAG", "onReceiveUssdResponseFailed: $failureCode$request")
                }
            }, Handler(Looper.getMainLooper()))
        }
    }

    private fun consultaDatos(ussdCode: String, sim: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain.sendUssdRequest(ussdCode, object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(telephonyManager: TelephonyManager, request: String, response: CharSequence) {
                    val diaria = response.toString()
                        .replace("Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#".toRegex(), "0 MB")
                        .replace("Tarifa: No activa. Paquetes: No dispone de MB.".toRegex(), "0 MB")
                        .replace("Tarifa: No activa. Diaria:", "")
                        .replace("Tarifa: No activa. Diaria: No dispone de MB.".toRegex(), "0 MB")
                        .replace("Tarifa: No activa.", "0 MB")
                        .replaceFirst("Mensajeria:(.*)".toRegex(), "")
                        .replaceFirst("Paquetes:(.*)".toRegex(), "")
                        .trim()
                    editor.putString("diaria", diaria.replace("no activos.", "").replaceFirst("validos(.*)".toRegex(), "").replace("No dispone de MB.", "0 MB")).apply()
                    bolsaDiaria.text = spCuentas.getString("diaria", diaria)

                    val messageVenceDiaria = response.toString()
                        .replace("Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#".toRegex(), "Debe adquirir un paquete")
                        .replace("Tarifa: No activa. Paquetes: No dispone de MB.".toRegex(), "0 horas")
                        .replace("Tarifa: No activa. Diaria:", "")
                        .replace("Tarifa: No activa.", "0 horas")
                        .replaceFirst("Mensajeria:(.*)".toRegex(), "")
                        .replaceFirst("Paquetes:(.*)".toRegex(), "")
                        .trim()
                    editor.putString("vence_diaria", messageVenceDiaria.replaceFirst("(.*)no activos.".toRegex(), "sin consumir").replaceFirst("(.*)validos".toRegex(), "").replace("No dispone de MB.", "0 horas").replace("horas.", "horas")).apply()
                    venceBolsaDiaria.text = spCuentas.getString("vence_diaria", messageVenceDiaria)

                    val messageMensajeria = response.toString()
                        .replace("Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#".toRegex(), "0 MB")
                        .replace("Tarifa: No activa. Paquetes: No dispone de MB.".toRegex(), "0 MB")
                        .replace("Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:", "")
                        .replaceFirst("(.*)no activos. Mensajeria:".toRegex(), "")
                        .replaceFirst("(.*)horas. Mensajeria:".toRegex(), "")
                        .replace("Tarifa: No activa. Mensajeria: No dispone de MB.".toRegex(), "0 MB")
                        .replace("Tarifa: No activa. Mensajeria:", "")
                        .replace("Tarifa: No activa.", "0 MB")
                        .replaceFirst("Diaria:(.*)".toRegex(), "")
                        .replaceFirst("Paquetes:(.*)".toRegex(), "")
                        .trim()
                    editor.putString("mensajeria", messageMensajeria.replace("vencen hoy.", "").replaceFirst("validos(.*)".toRegex(), "")).apply()
                    bolsaSms.text = spCuentas.getString("mensajeria", messageMensajeria)

                    val messageVenceMensajeria = response.toString()
                        .replace("Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#".toRegex(), "0 días")
                        .replace("Tarifa: No activa. Paquetes: No dispone de MB.".toRegex(), "0 días")
                        .replace("Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:", "")
                        .replaceFirst("(.*)no activos. Mensajeria:".toRegex(), "")
                        .replaceFirst("(.*)horas. Mensajeria:".toRegex(), "")
                        .replace("Tarifa: No activa. Mensajeria: No dispone de MB.".toRegex(), "0 días")
                        .replace("Tarifa: No activa. Mensajeria:", "")
                        .replace("Tarifa: No activa.", "0 días")
                        .replaceFirst("Diaria:(.*)".toRegex(), "")
                        .replaceFirst("Paquetes:(.*)".toRegex(), "")
                        .trim()
                    editor.putString("vence_mensajeria", messageVenceMensajeria.replaceFirst("(.*)validos".toRegex(), "").replaceFirst("(.*)vencen hoy.".toRegex(), "vence hoy").replace("dias.", "días")).apply()
                    venceBolsaSms.text = spCuentas.getString("vence_mensajeria", "0 días")

                    val messagePaquete = response.toString()
                        .replace("Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#".toRegex(), "0 MB")
                        .replace("Tarifa: No activa. Diaria: No dispone de MB.".toRegex(), "0 MB")
                        .replaceFirst("(.*)Paquetes:".toRegex(), "")
                        .replaceFirst("\\+(.*)".toRegex(), "")
                        .replaceFirst("Tarifa: No activa. Diaria:(.*)".toRegex(), "0 MB")
                        .replaceFirst("Tarifa: No activa. Mensajeria:(.*)".toRegex(), "0 MB")
                        .trim()
                    editor.putString("paquete", messagePaquete.replaceFirst("(.*)LTE(.*)".toRegex(), "0 MB").replaceFirst("validos(.*)".toRegex(), "")).apply()
                    datosText.text = spCuentas.getString("paquete", messagePaquete)

                    val messageLte = response.toString()
                        .replace("Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#".toRegex(), "0 MB")
                        .replace("Tarifa: No activa. Diaria: No dispone de MB.".toRegex(), "0 MB")
                        .replaceFirst("(.*)Paquetes:".toRegex(), "")
                        .replaceFirst("(.*)\\+".toRegex(), "")
                        .replaceFirst("Tarifa: No activa. Diaria:(.*)".toRegex(), "0 MB")
                        .replaceFirst("Tarifa: No activa. Mensajeria:(.*)".toRegex(), "0 MB")
                        .trim()
                    val lte = messageLte.replaceFirst("LTE(.*)".toRegex(), "").replaceFirst("(.*)validos(.*)".toRegex(), "0 MB").replace("no activos.", "")
                    editor.putString("lte", lte).apply()
                    datosLte.text = spCuentas.getString("lte", lte)

                    val vencimientoDatos = response.toString()
                        .replace("Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#".toRegex(), "0 días")
                        .replace("Tarifa: No activa. Diaria: No dispone de MB.".toRegex(), "0 MB")
                        .replaceFirst("(.*)Paquetes:".toRegex(), "")
                        .replaceFirst("Tarifa: No activa. Diaria:(.*)".toRegex(), "0 MB")
                        .replaceFirst("Tarifa: No activa. Mensajeria:(.*)".toRegex(), "0 MB")
                        .trim()
                    val vence = vencimientoDatos.replaceFirst("(.*)validos".toRegex(), "").replace("dias.", "días").replace("no activos", "0 días").replaceFirst("(.*)LTE".toRegex(), "")
                    editor.putString("vence_datos", vence).apply()
                    venceDatos.text = spCuentas.getString("vence_datos", vence)
                }

                override fun onReceiveUssdResponseFailed(telephonyManager: TelephonyManager, request: String, failureCode: Int) {
                    Log.e("TAG", "onReceiveUssdResponseFailed: $failureCode$request")
                }
            }, Handler(Looper.getMainLooper()))
        }
    }

    private fun consultaBonos(ussdCode: String, sim: Int) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 234)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            manager2 = manager.createForSubscriptionId(2)
            managerMain = if (sim == 0) manager else manager2
            managerMain.sendUssdRequest(ussdCode, object : TelephonyManager.UssdResponseCallback() {
                override fun onReceiveUssdResponse(telephonyManager: TelephonyManager, request: String, response: CharSequence) {
                    val datosNacionales = response.toString()
                        .replace("Usted no dispone de bonos activos.".toRegex(), "0 MB")
                        .replaceFirst("Datos.cu ".toRegex(), "")
                        .replaceFirst("vence(.*)".toRegex(), "")
                        .trim()
                    editor.putString("datos_nacionales", datosNacionales).apply()
                    this@CuentasFragment.datosNacionales.text = datosNacionales

                    val bonos = response.toString()
                        .replace("Usted no dispone de bonos activos.".toRegex(), "")
                        .replaceFirst("Datos.cu(.*)".toRegex(), "")
                        .trim()
                    promoBonos.visibility = if (!TextUtils.isEmpty(bonos)) View.VISIBLE else View.GONE
                    editor.putString("bonos", bonos).apply()
                }

                override fun onReceiveUssdResponseFailed(telephonyManager: TelephonyManager, request: String, failureCode: Int) {
                    Log.e("TAG", "onReceiveUssdResponseFailed: $failureCode$request")
                }
            }, Handler(Looper.getMainLooper()))
        }
    }
}


