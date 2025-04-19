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
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.BackBurbujaDeleteOverBinding
import com.marlon.portalusuario.databinding.FloatingBubbleBinding
import com.marlon.portalusuario.databinding.FloatingBubbleDeleteBinding
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
    private lateinit var bubbleBinding: FloatingBubbleBinding
    private lateinit var trashBinding: FloatingBubbleDeleteBinding
    private lateinit var trashOverBinding: BackBurbujaDeleteOverBinding

    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable
    private lateinit var weakReference: WeakReference<FloatingBubbleService>

    var screenWidth: Int = 0
    var screenHeight: Int = 0

    private fun updateBubbleUI() {
        serviceScope.launch {
            viewModel.state.collect { bubbleState ->
                bubbleBinding.bubbleTrafficUploadText.text = getString(
                    R.string.upload_traffic_template,
                    humanReadableByteCount(bubbleState.uploadSpeed)
                )
                bubbleBinding.bubbleTrafficDownloadText.text = getString(
                    R.string.download_traffic_template,
                    humanReadableByteCount(bubbleState.downloadSpeed)
                )

                if (bubbleState.isShowingAccountBalance) {
                    bubbleBinding.textCuentasSaldo.visibility = View.VISIBLE
                    bubbleBinding.textCuentasSaldo.text = bubbleState.accountBalance
                } else {
                    bubbleBinding.textCuentasSaldo.visibility = View.GONE
                }

                if (bubbleState.isShowingDataBalance) {
                    bubbleBinding.textCuentasDatos.visibility = View.VISIBLE
                    bubbleBinding.textCuentasDatos.text = bubbleState.dataBalance
                } else {
                    bubbleBinding.textCuentasDatos.visibility = View.GONE
                }
            }
        }
    }

    private fun setupUpdateRunnable() {
        updateRunnable = Runnable {
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

    /**
     * Sets up the layout parameters for the bubble and delete bubble views.
     *
     * This function initializes two `WindowManager.LayoutParams` objects:
     * - `bubbleLayoutParams`: Defines the layout parameters for the main floating bubble.
     * - `deleteBubbleLayoutParams`: Defines the layout parameters for the delete zone bubble.
     *
     * It configures these parameters as follows:
     *
     * **Common Settings:**
     * - `type`: Set to `WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY` to ensure the bubbles
     *   float above other applications.
     * - `flags`: Set to `WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE` to prevent the bubbles
     *   from taking input focus. This ensures that clicks can pass through the bubbles to the
     *   underlying app.
     * - `format`: Set to `PixelFormat.TRANSLUCENT` to allow for transparency in the bubble views.
     * - `width` and `height`: Set to `WindowManager.LayoutParams.WRAP_CONTENT` so the bubbles
     *    will resize depending on their content.
     *
     * **Bubble Layout Parameters (`bubbleLayoutParams`) Specific Settings:**
     * - `gravity`: Set to `Gravity.CENTER` to position the bubble in the center of the screen.
     * - `x`: Set to `INITIAL_POSITION_X` to define the initial horizontal position.
     * - `y`: Set to `INITIAL_POSITION_Y` to define the initial vertical position.
     *
     * **Delete Bubble Layout Parameters (`deleteBubbleLayoutParams`) Specific Settings:**
     * - `gravity`: Set to `Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL` to position the delete zone bubble
     *   at the bottom center of the screen.
     * - `y`: Set to `20` to position the delete bubble 20 pixels from the bottom of the screen. This offset helps it
     * to be slightly above the bottom edge.
     *
     * **Note:**
     * - `INITIAL_POSITION_X` and `INITIAL_POSITION_Y` are assumed to be pre-defined constants that specify the initial
     *   coordinates for the main floating bubble.
     * - The values for `x` and `y` are in pixels.
     */
    private fun setUpLayouts() {
        val type =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        bubbleLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        bubbleLayoutParams.gravity = GRAVITY_CENTER


        bubbleLayoutParams.x = INITIAL_POSITION_X
        bubbleLayoutParams.y = INITIAL_POSITION_Y

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
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    VIBRATE_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }

    companion object {
        var isStarted: Boolean = false
    }
}
