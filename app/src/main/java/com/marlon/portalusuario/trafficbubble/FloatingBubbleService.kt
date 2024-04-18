package com.marlon.portalusuario.trafficbubble

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.BackBurbujaDeleteOverBinding
import com.marlon.portalusuario.databinding.FloatingBubbleBinding
import com.marlon.portalusuario.databinding.FloatingBubbleDeleteBinding
import com.marlon.portalusuario.trafficbubble.Util.calcDownSpeed
import com.marlon.portalusuario.trafficbubble.Util.calcUpSpeed
import com.marlon.portalusuario.trafficbubble.Util.humanReadableByteCount
import com.marlon.portalusuario.util.Util
import java.lang.ref.WeakReference


private const val VIBRATE_TIME = 100L
private const val GRAVITY_CENTER = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
private const val INITIAL_POSITION_X = 0 // Posición inicial X para la burbuja
private const val INITIAL_POSITION_Y = 0 // Posición inicial Y para la burbuja

class FloatingBubbleService : Service() {
    private lateinit var networkType: String

    // UI
    private lateinit var windowManager: WindowManager
    private lateinit var bubbleLayoutParams: WindowManager.LayoutParams
    private lateinit var deleteBubbleLayoutParams: WindowManager.LayoutParams
    private lateinit var bubbleBinding: FloatingBubbleBinding
    private lateinit var trashBinding: FloatingBubbleDeleteBinding
    private lateinit var trashOverBinding: BackBurbujaDeleteOverBinding

    private val bubbleState = BubbleState()

    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable
    private lateinit var weakReference: WeakReference<FloatingBubbleService>

    // VARS
    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0

    private var mLastTime: Long = 0
    var screenWidth: Int = 0
    var screenHeight: Int = 0

    private lateinit var sharedPreferences: SharedPreferences

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key) {
                "saldo" -> {
                    bubbleState.accountBalance = prefs.getString(key, "0.00 CUP")!!
                }

                "paquete" -> {
                    bubbleState.dataBalance = prefs.getString(key, "0 B")!!
                }
            }
            updateBubbleUI()
        }

    private fun updateBubbleUI() {
        bubbleBinding.bubbleTrafficUploadText.text = getString(
            R.string.upload_traffic_template,
            humanReadableByteCount(bubbleState.uploadSpeed)
        )
        bubbleBinding.bubbleTrafficDownloadText.text = getString(
            R.string.download_traffic_template,
            humanReadableByteCount(bubbleState.downloadSpeed)
        )
        bubbleBinding.textCuentasSaldo.text = bubbleState.accountBalance
        bubbleBinding.textCuentasDatos.text = bubbleState.dataBalance
    }

    private fun setupUpdateRunnable() {
        updateRunnable = Runnable {
            // Obtener una referencia fuerte al servicio; si ya no existe, no hacer nada
            val service = weakReference.get() ?: return@Runnable

            // Actualiza lógica aquí, por ejemplo, actualizar UI o verificar conexiones
            service.performUpdate()

            // Re-programar el mismo runnable para ejecutarse en el futuro
            1000L.also { handler.postDelayed(this@FloatingBubbleService.updateRunnable, it) }
        }
    }

    private fun performUpdate() {
        // Verificar conexión a Internet antes de continuar
        if (!Util.isConnected(applicationContext)) {
            stopSelf() // Detener el servicio de manera más apropiada
            return // Salir de la función si no hay conexión a internet
        }

        // Calcular el uso de datos desde la última actualización
        val currentTime = System.currentTimeMillis()
        val usedTime = currentTime - mLastTime
        val currentRxBytes = TrafficStats.getTotalRxBytes()
        val currentTxBytes = TrafficStats.getTotalTxBytes()
        val usedRxBytes = currentRxBytes - mLastRxBytes
        val usedTxBytes = currentTxBytes - mLastTxBytes

        // Actualizar las referencias para la próxima ejecución
        mLastRxBytes = currentRxBytes
        mLastTxBytes = currentTxBytes
        mLastTime = currentTime

        bubbleState.uploadSpeed = calcUpSpeed(usedTime, usedTxBytes)
        bubbleState.downloadSpeed = calcDownSpeed(usedTime, usedRxBytes)

        // Actualizar UI
        updateBubbleUI()
    }

    override fun onCreate() {
        super.onCreate()
        isStarted = true
        weakReference = WeakReference(this)
        //
        screenWidth = applicationContext.resources.displayMetrics.widthPixels
        screenHeight = applicationContext.resources.displayMetrics.heightPixels
        //
        windowManager =
            ContextCompat.getSystemService(
                applicationContext,
                WindowManager::class.java
            ) as WindowManager

        sharedPreferences = applicationContext.getSharedPreferences("cuentas", MODE_PRIVATE).apply {
            bubbleState.apply {
                accountBalance = getString("saldo", "0.00 CUP").toString()
                dataBalance = getString("paquete", "0 B").toString()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        //
        setUpLayouts()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        networkType = intent.getStringExtra("networkType").toString()
        // UTIL
        showFloatingWindow()
        return START_STICKY // - return START_NOT_STICKY;
    }

    private fun setUpLayouts() {
        // FLOATING BUBBLE LAYOUT PARAMS
        val type =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_PHONE
            } else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        bubbleLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        //
        bubbleLayoutParams.gravity = GRAVITY_CENTER

        // Configurar la posición inicial de la burbuja flotante en el centro de la pantalla
        bubbleLayoutParams.x = INITIAL_POSITION_X
        bubbleLayoutParams.y = INITIAL_POSITION_Y
        //
        // TRASH ICON LAYOUT PARAMS
        //
        deleteBubbleLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        deleteBubbleLayoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        20.also { deleteBubbleLayoutParams.y = it }
    }

    private fun showFloatingWindow() {
        // PERMISSION
        if (Settings.canDrawOverlays(this)) {
            // INFLATER
            val layoutInflater =
                ContextCompat.getSystemService(
                    applicationContext,
                    LayoutInflater::class.java
                ) as LayoutInflater

            // INFLATING BUBBLE LAYOUT
            bubbleBinding = FloatingBubbleBinding.inflate(layoutInflater)

            // TRASH ICON
            trashBinding = FloatingBubbleDeleteBinding.inflate(layoutInflater)
            trashOverBinding = BackBurbujaDeleteOverBinding.inflate(layoutInflater)

            //
            when (networkType) {
                "Mobile" -> bubbleBinding.ConnectionTypeImage.setImageResource(R.drawable.ic_round_signal_cellular)
                "WiFi" -> bubbleBinding.ConnectionTypeImage.setImageResource(R.drawable.ic_round_wifi_24)
                else -> bubbleBinding.ConnectionTypeImage.visibility = View.GONE
            }

            //
            bubbleBinding.root.setOnTouchListener(FloatingOnTouchListener())

            //
            windowManager.addView(trashBinding.root, deleteBubbleLayoutParams)
            windowManager.addView(trashOverBinding.root, deleteBubbleLayoutParams)
            windowManager.addView(bubbleBinding.root, bubbleLayoutParams)

            // OCULTANDO BURBUJA DE ELIMINACION
            trashBinding.root.visibility = View.GONE
            trashOverBinding.root.visibility = View.INVISIBLE

            setupUpdateRunnable()
            // Iniciar la primera ejecución del runnable
            handler.post(updateRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyFloatingWindow()
        isStarted = false
    }

    private fun destroyFloatingWindow() {
        // Detener todas las ejecuciones futuras del runnable para evitar fugas de memoria
        handler.removeCallbacks(updateRunnable)
        windowManager.removeView(bubbleBinding.root)
        windowManager.removeView(trashBinding.root)
        windowManager.removeView(trashOverBinding.root)
        isStarted = false
        stopSelf()
    }

    private inner class FloatingOnTouchListener : OnTouchListener {
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        private var initialX = 0
        private var initialY = 0
        private var erase = false


        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = bubbleLayoutParams.x
                    initialY = bubbleLayoutParams.y

                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    trashBinding.root.visibility = View.VISIBLE
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    //
                    bubbleLayoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    bubbleLayoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    // actualizar vista de la ventana flotante
                    windowManager.updateViewLayout(bubbleBinding.root, bubbleLayoutParams)
                    //
                    trashBinding.root.visibility = View.VISIBLE
                    //int LESS_SPACE = 100;
                    val topLimit = 6.let { screenHeight / it * 2 }
                    val leftLimit = 6.let { -1 * (screenWidth / it) }
                    val rightLimit = 6.let { screenWidth / it }
                    if (bubbleLayoutParams.y > topLimit &&
                        (bubbleLayoutParams.x in (leftLimit + 1)..<rightLimit)
                    ) {
                        trashBinding.root.visibility = View.GONE
                        trashOverBinding.root.visibility = View.VISIBLE
                        //
                        if (!erase) {
                            startVibrate()
                        }
                        erase = true
                    } else {
                        erase = false
                        trashOverBinding.root.visibility = View.GONE
                        trashBinding.root.visibility = View.VISIBLE
                    }
                    true
                }

                MotionEvent.ACTION_UP -> if (erase) {
                    onDestroy()
                    true
                } else {
                    trashBinding.root.visibility = View.GONE
                    true
                }

                else -> false
            }
        }

        private fun startVibrate() {
            val vibrator =
                ContextCompat.getSystemService(applicationContext, Vibrator::class.java) as Vibrator
            // Para Android API nivel 26 (Oreo) o superior, use VibrationEffect.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        VIBRATE_TIME,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                // Métodos obsoletos para versiones anteriores a Android Oreo.
                vibrator.vibrate(VIBRATE_TIME)
            }
        }
    }

    companion object {
        var isStarted: Boolean = false
    }
}
