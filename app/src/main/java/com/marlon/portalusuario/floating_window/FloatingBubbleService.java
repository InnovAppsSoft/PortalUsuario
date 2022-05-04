package com.marlon.portalusuario.floating_window;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.Utils;
import com.marlon.portalusuario.logging.JCLogging;

import java.util.Calendar;

import javax.annotation.Nullable;

public class FloatingBubbleService extends Service {
    // UTIL
    private JCLogging logging;
    private Utils Utils;
    // UI
    private WindowManager windowManager;
    private WindowManager.LayoutParams bubbleLayoutParams;
    private WindowManager.LayoutParams deleteBubbleLayoutParams;
    private View FloatingBubbleInflated;
    private View trashFloatingBubble;
    private View trashOverFloatingBubble;
    private RelativeLayout BubbleLayout;
    private ImageView TimeImage;
    private TextView Text;
    private ImageView ConnectionTypeImage;
    private TextView UploadText;
    private TextView DownloadText;
    // TIME
    private static LinearLayout timeLayout;
    private static TextView bubbleTimeText;
    // INTENT
    private Intent intent;
    // VARS
    private long mLastRxBytes = 0;
    private long mLastTxBytes = 0;
    private long mLastTime = 0;
    public static boolean isStarted = false;
    public int screenWidth;
    public int screenHeight;

    @SuppressLint("RtlHardcoded")
    @Override
    public void onCreate() {
        super.onCreate();
        //
        isStarted = true;
        //
        screenWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        //
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //
        setUpLayouts();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        this.intent = intent;
        logging = new JCLogging(getApplicationContext());
        showFloatingWindow();
        return Service.START_STICKY;// - return START_NOT_STICKY;
    }

    private void setUpLayouts(){
        // FLOATING BUBBLE LAYOUT PARAMS
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            bubbleLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            bubbleLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        //
        bubbleLayoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        //bubbleLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        // RIGHT-TOP POSITION
        bubbleLayoutParams.x =  0;//screenWidth / 2;
        bubbleLayoutParams.y = 0;//-1*(screenHeight / 2);
        //
        // TRASH ICON LAYOUT PARAMS
        //
        deleteBubbleLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            deleteBubbleLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            deleteBubbleLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        deleteBubbleLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        deleteBubbleLayoutParams.y = 20;
        
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {
        // PERMISSION
        if (Settings.canDrawOverlays(this)) {
            try {
                // INFLATER
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // INFLATING BUBBLE LAYOUT
                FloatingBubbleInflated = layoutInflater.inflate(R.layout.floating_bubble, (ViewGroup) null);
                // TRASH ICON
                trashFloatingBubble = layoutInflater.inflate(R.layout.floating_bubble_delete, (ViewGroup) null);
                trashOverFloatingBubble = layoutInflater.inflate(R.layout.back_burbuja_delete_over, (ViewGroup) null);
                //
                BubbleLayout = FloatingBubbleInflated.findViewById(R.id.bubble_back_layout);
                ConnectionTypeImage = FloatingBubbleInflated.findViewById(R.id.ConnectionTypeImage);
                //
                if (com.marlon.portalusuario.util.Util.isConnectedByMobileData(getApplicationContext())){
                    ConnectionTypeImage.setImageResource(R.drawable.ic_round_signal_cellular);
                }else if (com.marlon.portalusuario.util.Util.isConnectedByWifi(getApplicationContext())) {
                    ConnectionTypeImage.setImageResource(R.drawable.ic_round_wifi_24);
                }else{
                    ConnectionTypeImage.setVisibility(View.GONE);
                }
                //
                UploadText = FloatingBubbleInflated.findViewById(R.id.bubble_traffic_upload_text);
                DownloadText = FloatingBubbleInflated.findViewById(R.id.bubble_traffic_download_text);
                //
                timeLayout = FloatingBubbleInflated.findViewById(R.id.time_layout);
                bubbleTimeText = FloatingBubbleInflated.findViewById(R.id.bubble_time_text);
                //
                FloatingBubbleInflated.setOnTouchListener(new FloatingOnTouchListener());
                //
                addView();
                // OCULTANDO BURBUJA DE ELIMINACION
                trashFloatingBubble.setVisibility(View.GONE);
                trashOverFloatingBubble.setVisibility(View.INVISIBLE);
            } catch (Exception unused2) {
                unused2.printStackTrace();
                Log.w("Servicio_Burbuja", "Error en la burbuja");
            }

            handler.postDelayed(runnable, 0);
        }
    }

    public static void showConnectedTime(boolean status){
        if (status) {
            timeLayout.setVisibility(View.VISIBLE);
        }else{
            timeLayout.setVisibility(View.GONE);
        }
    }

    public static void setConnectedTime(String time){
        bubbleTimeText.setText(time);
    }

    public void addView(){
        try {
            windowManager.addView(trashFloatingBubble, deleteBubbleLayoutParams);
            windowManager.addView(trashOverFloatingBubble, deleteBubbleLayoutParams);
            windowManager.addView(FloatingBubbleInflated, bubbleLayoutParams);
        }catch (Exception ex){
            Log.e("Floating Bubble Service", "An error was ocurred");
            //ex.printStackTrace();
            //logging.error("Adding view to Window Manager", null, ex);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFloatingWindow();
    }

    public void destroyFloatingWindow(){
        try {
            handler.removeCallbacks(runnable);
            windowManager.removeView(FloatingBubbleInflated);
            windowManager.removeView(trashFloatingBubble);
            windowManager.removeView(trashOverFloatingBubble);
//            windowManager = null;
//            FloatingBubbleInflated = null;
//            trashFloatingBubble = null;
//            trashOverFloatingBubble = null;
            isStarted = false;
            //stopService(intent);
        }catch (Exception ex){
            Log.e("Floating Bubble Service", "An error was ocurred", ex);
            ex.printStackTrace();
            logging.error("Destroying Everything", null, ex);
        }
    }

    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        public void run() {
//            if (!isStarted){
//                onDestroy();
//            }
            if (!com.marlon.portalusuario.util.Util.isConnected(getApplicationContext())){
                onDestroy();
            }else{
                //if (!isStarted){
                    addView();
                //}
            }
            long currentRxBytes = TrafficStats.getTotalRxBytes();
            long currentTxBytes = TrafficStats.getTotalTxBytes();
            long usedRxBytes = currentRxBytes - mLastRxBytes;
            long usedTxBytes = currentTxBytes - mLastTxBytes;
            long currentTime = System.currentTimeMillis();
            long usedTime = currentTime - mLastTime;

            mLastRxBytes = currentRxBytes;
            mLastTxBytes = currentTxBytes;
            mLastTime = currentTime;
            try {
                long up = Util.calcUpSpeed(usedTime, usedRxBytes, usedTxBytes);
                long down = Util.calcDownSpeed(usedTime, usedRxBytes, usedTxBytes);
                //long total = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
                UploadText.setText("↑ " + Util.humanReadableByteCount(up, false));
                DownloadText.setText("↓ " + Util.humanReadableByteCount(down, false));
                handler.postDelayed(runnable, 1000);
            }catch (Exception ex){
                Log.e("Floating Bubble Service", "An error was ocurred", ex);
                ex.printStackTrace();
                logging.error("Calculating Traffic Speed", null, ex);
            }
        }
    };

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int lastAction;
        private float initialTouchX;
        private float initialTouchY;
        private static final int MAX_CLICK_DURATION = 700;
        private long startClickTime;
        private int initialX;
        private int initialY;
        private boolean erase = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        initialX = bubbleLayoutParams.x;
                        initialY = bubbleLayoutParams.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = event.getAction();
                        trashFloatingBubble.setVisibility(View.VISIBLE);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //
                        bubbleLayoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        bubbleLayoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        // actualizar vista de la ventana flotante
                        windowManager.updateViewLayout(FloatingBubbleInflated, bubbleLayoutParams);
                        //
                        lastAction = event.getAction();
                        //
                        trashFloatingBubble.setVisibility(View.VISIBLE);
                        //int LESS_SPACE = 100;
                        int TOP_LIMIT = screenHeight / 6 * 2;
                        int LEFT_LIMIT = -1 * ((screenWidth / 6));
                        int RIGHT_LIMIT = (screenWidth / 6);
                        if (bubbleLayoutParams.y > TOP_LIMIT && (bubbleLayoutParams.x > LEFT_LIMIT && bubbleLayoutParams.x < RIGHT_LIMIT)) {
                            trashFloatingBubble.setVisibility(View.GONE);
                            trashOverFloatingBubble.setVisibility(View.VISIBLE);
                            //
                            if (!erase) {
                                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
                            }
                            erase = true;
                        } else {
                            erase = false;
                            trashOverFloatingBubble.setVisibility(View.GONE);
                            trashFloatingBubble.setVisibility(View.VISIBLE);
                        }
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        return true;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {

                        }
                        if (erase) {
                            onDestroy();
                        } else {
                            trashFloatingBubble.setVisibility(View.GONE);
                            lastAction = event.getAction();
                            return true;
                        }
                }
            }catch (Exception ex){
                Log.e("Floating Bubble Service", "An error was ocurred", ex);
                ex.printStackTrace();
                logging.error("Moving Floating Bubble", null, ex);
            }
            return false;
        }
    };
}
