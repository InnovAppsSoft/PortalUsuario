package com.marlon.portalusuario.view.Fragments.connectivity

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.R
import com.marlon.portalusuario.logging.JCLogging
import com.marlon.portalusuario.model.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import trikita.log.Log
import java.io.IOException

class WifiAccountDetailsActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null
    var myUser: User? = null
    var saldo: TextView? = null
    var leftTime: TextView? = null
    var estadoCuenta: TextView? = null
    var builder: StringBuilder? = null
    var btnDisconnect: Button? = null
    private var loadingBar: ProgressDialog? = null
    private val firstTime = true
    private val initialH = 0
    private val initialM = 0
    private val initialS = 0
    private var logger: JCLogging? = null
    private var maxTime = 0
    private var timeType: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifietecsa_account)
        val initialIntent = intent
        maxTime = 0
        timeType = ""
        myUser = User.getUser()
        if (initialIntent != null) {
            if (initialIntent.hasExtra("max_time")) {
                maxTime = initialIntent.extras!!.getInt("max_time", 0)
            }
            if (initialIntent.hasExtra("time_type")) {
                timeType = initialIntent.extras!!.getString("time_type")
            }
            myUser = initialIntent.extras!!.getSerializable("user") as User?
        }
        //
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        logger = JCLogging(this)
        //
        loadingBar = ProgressDialog(this)
        saldo = findViewById(R.id.editSaldoCuenta)
        leftTime = findViewById(R.id.textLeftTime)
        estadoCuenta = findViewById(R.id.editEstadoCuenta)
        btnDisconnect = findViewById(R.id.buttonDisconnect)
        saldo?.text = myUser!!.accountCredit
        estadoCuenta?.text = myUser!!.accountState
        builder = StringBuilder()
        sendLeftTime()
        //
//        if (prefs.getBoolean("show_traffic_speed_bubble", false)){
//            MainActivity.showConnectedTime(true);
//        }
        btnDisconnect?.setOnClickListener { sendDisconnect() }
    }

    fun Disconnect(v: View?) {
        sendDisconnect()
    }

    private fun countDown(time: String) {
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
        Thread {
            try {
                val leftTimeDocument =
                    Jsoup.connect("https://secure.etecsa.net:8443//EtecsaQueryServlet")
                        .data("username", myUser!!.username)
                        .data("ATTRIBUTE_UUID", myUser!!.attributE_UUID)
                        .data("op", "getLeftTime")
                        .followRedirects(true).post()
                myUser!!.leftTime = leftTimeDocument.select("body").text()
                builder!!.append(myUser!!.leftTime)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            runOnUiThread { countDown(builder.toString()) }
        }.start()
    }

    //
    private fun sendDisconnect() {
        loadingBar!!.setTitle("Desconectando")
        loadingBar!!.setMessage("Por favor espere....")
        loadingBar!!.setIcon(R.mipmap.ic_launcher)
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()
        Thread {
            var loggin: Document? = null
            try {
                loggin = Jsoup.connect("https://secure.etecsa.net:8443/LogoutServlet")
                    .data("username", myUser!!.username)
                    .data("ATTRIBUTE_UUID", myUser!!.attributE_UUID).followRedirects(true).post()
                //startActivity(main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } catch (e: IOException) {
                e.printStackTrace()
                JCLogging.error(null, null, e)
            }
            // cleaning preferences
//                SharedPreferences.Editor prefEditor = prefs.edit();
//                prefEditor.putBoolean("connected", false);
//                prefEditor.apply();
//                if (prefs.getBoolean("show_traffic_speed_bubble", false)) {
//                    // hiding floating windows time
//                    MainActivity.showConnectedTime(false);
//                    MainActivity.setConnectedTime("");
//                }
            // hiding loading bar
            loadingBar!!.dismiss()
            runOnUiThread {
                Toast.makeText(
                    this@WifiAccountDetailsActivity,
                    "Desconectado",
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        }.start()
    }

    //----SHOWING ALERT DIALOG FOR EXITING THE APP----
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Seguro que deseas salir?")
        builder.setTitle("Portal Usuario")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setCancelable(false)
        builder.setPositiveButton("Salir") { dialogInterface, i -> sendDisconnect() }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    public override fun onResume() {
        Log.e("Data", "onResume of Ok")
        super.onResume()
    }

    public override fun onPause() {
        Log.e("Data", "OnPause of Ok")
        super.onPause()
    }
}