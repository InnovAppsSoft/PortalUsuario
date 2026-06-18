package com.marlon.portalusuario.trafficbubble

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.R
import com.marlon.portalusuario.trafficbubble.Util.humanReadableByteCount
import com.marlon.portalusuario.util.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

private const val VIBRATE_TIME = 100L
private const val GRAVITY_CENTER = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
private const val INITIAL_POSITION_X = 0
private const val INITIAL_POSITION_Y = 0

private const val TAG = "FloatingBubbleService"

@AndroidEntryPoint
class FloatingBubbleService : Service() {
    @Inject
    lateinit var viewModel: FloatingBubbleViewModel

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var networkType: String

    // UI
    private lateinit var windowManager: WindowManager
    private lateinit var bubbleLayoutParams: WindowManager.LayoutParams
    private lateinit var deleteBubbleLayoutParams: WindowManager.LayoutParams
    private lateinit var bubbleRoot: View
    private lateinit var trashRoot: View
    private lateinit var trashOverRoot: View
    private lateinit var bubbleTrafficUploadText: TextView
    private lateinit var bubbleTrafficDownloadText: TextView
    private lateinit var timeLayout: View
    private lateinit var textCuentasSaldo: TextView
    private lateinit var dataLayout: View
    private lateinit var textCuentasDatos: TextView
    private lateinit var connectionTypeImage: ImageView

    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable
    private lateinit var weakReference: WeakReference<FloatingBubbleService>

    var screenWidth: Int = 0
    var screenHeight: Int = 0

    private fun updateBubbleUI() {
        serviceScope.launch {
            viewModel.state.collect { bubbleState ->
                bubbleTrafficUploadText.text =
                    getString(
                        R.string.upload_traffic_template,
                        humanReadableByteCount(bubbleState.uploadSpeed),
                    )
                bubbleTrafficDownloadText.text =
                    getString(
                        R.string.download_traffic_template,
                        humanReadableByteCount(bubbleState.downloadSpeed),
                    )

                if (bubbleState.isShowingAccountBalance) {
                    timeLayout.visibility = View.VISIBLE
                } else {
                    timeLayout.visibility = View.GONE
                }
                textCuentasSaldo.text = bubbleState.accountBalance

                if (bubbleState.isShowingDataBalance) {
                    dataLayout.visibility = View.VISIBLE
                } else {
                    dataLayout.visibility = View.GONE
                }
                textCuentasDatos.text = bubbleState.dataBalance
            }
        }
    }

    private fun setupUpdateRunnable() {
        updateRunnable =
            Runnable {
                val service = weakReference.get() ?: return@Runnable

                service.performUpdate()

                1000L.also { handler.postDelayed(this@FloatingBubbleService.updateRunnable, it) }
            }
    }

    private fun performUpdate() {
        if (!Util.isConnected(applicationContext)) {
            Log.w("NetworkMonitor", "No internet connection. Stopping update.")
            stopSelf()
            return
        }

        viewModel.onEvent(FloatingBubbleEvent.OnCalculateDataUsage)

        // Actualizar UI
        updateBubbleUI()
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate: Service is creating...")

        isStarted = true
        weakReference = WeakReference(this)
        //
        screenWidth = applicationContext.resources.displayMetrics.widthPixels
        screenHeight = applicationContext.resources.displayMetrics.heightPixels
        //
        windowManager =
            ContextCompat.getSystemService(
                applicationContext,
                WindowManager::class.java,
            ) as WindowManager

        //
        setUpLayouts()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int,
    ): Int {
        super.onStartCommand(intent, flags, startId)
        networkType = intent.getStringExtra("networkType").toString()
        // UTIL
        showFloatingWindow()
        return START_STICKY // - return START_NOT_STICKY;
    }

    private fun setUpLayouts() {
        val type =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        bubbleLayoutParams =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT,
            )

        bubbleLayoutParams.gravity = GRAVITY_CENTER

        bubbleLayoutParams.x = INITIAL_POSITION_X
        bubbleLayoutParams.y = INITIAL_POSITION_Y

        deleteBubbleLayoutParams =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT,
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
                    LayoutInflater::class.java,
                ) as LayoutInflater

            // INFLATING BUBBLE LAYOUT
            bubbleRoot = layoutInflater.inflate(R.layout.floating_bubble, null)
            trashRoot = layoutInflater.inflate(R.layout.floating_bubble_delete, null)
            trashOverRoot = layoutInflater.inflate(R.layout.back_burbuja_delete_over, null)

            bubbleTrafficUploadText = bubbleRoot.findViewById(R.id.bubble_traffic_upload_text)
            bubbleTrafficDownloadText = bubbleRoot.findViewById(R.id.bubble_traffic_download_text)
            timeLayout = bubbleRoot.findViewById(R.id.time_layout)
            textCuentasSaldo = bubbleRoot.findViewById(R.id.text_cuentas_saldo)
            dataLayout = bubbleRoot.findViewById(R.id.data_layout)
            textCuentasDatos = bubbleRoot.findViewById(R.id.text_cuentas_datos)
            connectionTypeImage = bubbleRoot.findViewById(R.id.ConnectionTypeImage)

            //
            when (networkType) {
                "Mobile" -> connectionTypeImage.setImageResource(R.drawable.ic_round_signal_cellular)
                "WiFi" -> connectionTypeImage.setImageResource(R.drawable.ic_round_wifi_24)
                else -> connectionTypeImage.visibility = View.GONE
            }

            //
            bubbleRoot.setOnTouchListener(FloatingOnTouchListener())

            //
            windowManager.addView(trashRoot, deleteBubbleLayoutParams)
            windowManager.addView(trashOverRoot, deleteBubbleLayoutParams)
            windowManager.addView(bubbleRoot, bubbleLayoutParams)

            // OCULTANDO BURBUJA DE ELIMINACION
            trashRoot.visibility = View.GONE
            trashOverRoot.visibility = View.INVISIBLE

            setupUpdateRunnable()
            // Iniciar la primera ejecución del runnable
            handler.post(updateRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service is destroying...")
        destroyFloatingWindow()
    }

    private fun destroyFloatingWindow() {
        handler.removeCallbacks(updateRunnable)

        try {
            removeViewSafely(bubbleRoot, "bubble")
            removeViewSafely(trashRoot, "trash")
            removeViewSafely(trashOverRoot, "trashOver")
        } finally {
            isStarted = false
            stopSelf()
        }
    }

    private fun removeViewSafely(
        view: View,
        viewName: String,
    ) {
        try {
            windowManager.removeView(view)
        } catch (e: IllegalArgumentException) {
            Log.w("destroyFloatingWindow", "$viewName view not attached", e)
        }
    }

    private inner class FloatingOnTouchListener : OnTouchListener {
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        private var initialX = 0
        private var initialY = 0
        private var erase = false

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(
            v: View,
            event: MotionEvent,
        ): Boolean =
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = bubbleLayoutParams.x
                    initialY = bubbleLayoutParams.y

                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    trashRoot.visibility = View.VISIBLE
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    //
                    bubbleLayoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                    bubbleLayoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                    // actualizar vista de la ventana flotante
                    windowManager.updateViewLayout(bubbleRoot, bubbleLayoutParams)
                    //
                    trashRoot.visibility = View.VISIBLE
                    // int LESS_SPACE = 100;
                    val topLimit = 6.let { screenHeight / it * 2 }
                    val leftLimit = 6.let { -1 * (screenWidth / it) }
                    val rightLimit = 6.let { screenWidth / it }
                    if (bubbleLayoutParams.y > topLimit &&
                        (bubbleLayoutParams.x in (leftLimit + 1)..<rightLimit)
                    ) {
                        trashRoot.visibility = View.GONE
                        trashOverRoot.visibility = View.VISIBLE
                        //
                        if (!erase) {
                            startVibrate()
                        }
                        erase = true
                    } else {
                        erase = false
                        trashOverRoot.visibility = View.GONE
                        trashRoot.visibility = View.VISIBLE
                    }
                    true
                }

                MotionEvent.ACTION_UP ->
                    if (erase) {
                        onDestroy()
                        true
                    } else {
                        trashRoot.visibility = View.GONE
                        true
                    }

                else -> false
            }

        private fun startVibrate() {
            val vibrator =
                ContextCompat.getSystemService(applicationContext, Vibrator::class.java) as Vibrator
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    VIBRATE_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE,
                ),
            )
        }
    }

    companion object {
        var isStarted: Boolean = false
    }
}
