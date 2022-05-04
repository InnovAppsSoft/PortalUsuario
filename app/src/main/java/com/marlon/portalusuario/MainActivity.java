package com.marlon.portalusuario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.marlon.portalusuario.Fragments.BottomSheetDialog;
import com.marlon.portalusuario.Fragments.MoreFragment;
import com.marlon.portalusuario.Fragments.PaquetesFragment;
import com.marlon.portalusuario.Fragments.ServiciosFragment;
import com.marlon.portalusuario.Fragments.WebFragment;
import com.marlon.portalusuario.ui.connectivity.ConnectivityFragment;
import com.marlon.portalusuario.PUNotifications.PUNotification;
import com.marlon.portalusuario.ViewModel.PunViewModel;
import com.marlon.portalusuario.apklis.ApklisUtil;
import com.marlon.portalusuario.biometric.BiometricCallback;
import com.marlon.portalusuario.biometric.BiometricManager;
import com.marlon.portalusuario.floating_window.BootReceiver;
import com.marlon.portalusuario.floating_window.FloatingBubbleService;
import com.marlon.portalusuario.logging.JCLogging;
import com.marlon.portalusuario.senal.AppConfiguracionTool;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements BiometricCallback, BottomSheetDialog.BottomSheetListener {

    private static Context context;
    private TextView details;
    private TextView log;

    // UI ELEMENTOS
    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private Button download_apklis;
    private Button download_ps;
    private Button remind_me_later;

    private BottomSheetDialog bottomSheetDialog;

    // VARS
    private String APP_NAME;
    private final int WAITING_TIME = 300;
    private boolean update_info_already_showed = false;
    // SETTINGS
    SharedPreferences settings;

    // LOGGING
    private JCLogging Logging;

    // APKLIS
    private ApklisUtil apklis;

    private static PunViewModel punViewModel;

    BiometricManager mBiometricManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    int ResultCall = 1001;

    private static final String SELECTED_MENU = "selected_menu";
    BottomNavigationView navView;

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public static void insertNotification(PUNotification pun) {
        punViewModel.insertPUN(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        punViewModel = new ViewModelProvider(this).get(PunViewModel.class);

        APP_NAME = getPackageName();
        // VALORES POR DEFECTO EN LAS PREFERENCIAS
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        // check Donation's Time
        int donateMainOpen = AppConfiguracionTool.getDonateMainOpen(this);
        if (donateMainOpen == 50 || donateMainOpen == 100 || donateMainOpen == 150) {
            makeDonation();
        }
        if (donateMainOpen != -1 && donateMainOpen <= 200) {
            AppConfiguracionTool.donateAddOneOpen(this);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }

        context = this;
        bottomSheetDialog = new BottomSheetDialog();
        navView = findViewById(R.id.bottomNavigation);
        navView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.servicios) {
                fragment = new ServiciosFragment();
            } else if (item.getItemId() == R.id.paquete) {
                fragment = new PaquetesFragment();
            } else if (item.getItemId() == R.id.wifi) {
                fragment = new ConnectivityFragment();
            } else if (item.getItemId() == R.id.more) {
                bottomSheetDialog.show(getSupportFragmentManager(), "Menu");
            }
            if (fragment != null) {
                setFragment(fragment);
                return true;
            }

            return false;
        });

        if (savedInstanceState != null) {
            savedInstanceState.getInt(SELECTED_MENU);
        } else {
            navView.setSelectedItemId(R.id.servicios);
        }

        details = findViewById(R.id.details);
        log = findViewById(R.id.log);
        download_apklis = findViewById(R.id.download_apklis);
        TextView details = findViewById(R.id.details);
        TextView log = findViewById(R.id.log);
        download_apklis = findViewById(R.id.download_apklis);
        remind_me_later = findViewById(R.id.remind_me_later);

        //
        Logging = new JCLogging(this);
        download_apklis = findViewById(R.id.download_apklis);
        download_ps = findViewById(R.id.download_ps);
        remind_me_later = findViewById(R.id.remind_me_later);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        // FLOATING BUBBLE TRAFFIC SPEED
        BootReceiver bootReceiver = new BootReceiver();
        JCLogging.message("Registering networkStateReceiver", null);
        LocalBroadcastManager.getInstance(this).registerReceiver(bootReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (settings.getBoolean("show_traffic_speed_bubble", false)) {
            startFloatingBubbleService();
        }

        // FINGERPRINT
        if (settings.getBoolean("show_fingerprint", false)) {
            startFingerprint();
        }

        Button updateBtn = findViewById(R.id.updateBtn);
        mBottomSheetLayout = findViewById(R.id.bottom_sheet_update);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    updateBtn.setVisibility(View.GONE);
                } else {
                    updateBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        // DESCARGAR DE APKLIS
        download_apklis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "https://www.apklis.cu/application/" + APP_NAME;
                Logging.message("Opening Apklis URL::url=" + URL, null);
                //Toast.makeText(this, URL, Toast.LENGTH_LONG);
                Uri url = Uri.parse(URL);
                Intent openUrl = new Intent(Intent.ACTION_VIEW, url);
                startActivity(openUrl);
            }
        });
        // DESCARGAR DE GOOGLE PLAY
        download_ps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "https://play.google.com/store/apps/details?id=" + APP_NAME;
                Logging.message("Opening PlayStore URL::url=" + URL, null);
                //Toast.makeText(this, URL, Toast.LENGTH_LONG);
                Uri url = Uri.parse(URL);
                Intent openUrl = new Intent(Intent.ACTION_VIEW, url);
                startActivity(openUrl);
            }
        });
        // RECORDAR LUEGO
        remind_me_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_info_already_showed = false;
                startService(apklis, WAITING_TIME);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        // CHECK FOR UPDATES
        BroadcastReceiver apklis_update;
        if (settings.getBoolean("start_checking_for_updates", true)) {
            //
            // BROADCAST
            apklis_update = new BroadcastReceiver() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        boolean updateExist = intent.getBooleanExtra("update_exist", false);
                        Log.e("IS UPDATE", String.valueOf(updateExist));
                        if (updateExist) {
                            String version_name = intent.getStringExtra("version_name"); /* Respuesta Del Método startLookingForUpdates() Valor De La Versión Name De La App
                                                                                           Si Existe Una Actualización */
                            String new_version_size = intent.getStringExtra("new_version_size");
                            String changelog = intent.getStringExtra("changelog");
                            Logging.message("On receive Update info", null);
                            String version_size = "?? MB";
                            if (new_version_size != null) {
                                version_size = new_version_size;
                            }
                            // DETALLES de la NUEVA VERSION
                            if (changelog != null) {
                                log.setText(changelog);
                            } else {
                                log.setText("• Nada nuevo, todo igual :-)");
                            }
                            // NOMBRE DE LA VERSION Y TAMANNO
                            if (version_name != null && !version_name.isEmpty()) {
                                String v = "Versión " + version_name + " • " + version_size;
                                details.setText(v);
                            }
                            Log.e("Showing info", "True");
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            Logging.message("Update data received succesfully::version_name=" + version_name + "::version_size=" + version_size, null);
                            return;
                        }
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Logging.error(null, null, ex);
                    }
                }
            };
            /* Registro De Recibidores Para Manejar Existencia De Actualización U Obtención De Info Respectivamente */
            LocalBroadcastManager.getInstance(this).registerReceiver(apklis_update, new IntentFilter("apklis_update"));
            LocalBroadcastManager.getInstance(this).registerReceiver(apklis_update, new IntentFilter("apklis_app_info"));
            apklis = new ApklisUtil(this, APP_NAME);
            startService(apklis, 0);
        }
        //
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startService(ApklisUtil apklis, int latency) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Logging.message("Starting 'checking for updates' service::enabled=" + String.valueOf(settings.getBoolean("start_checking_for_updates", true)) + "::latency=" + latency + "::update_info_already_showed=" + update_info_already_showed, null);
        if (!update_info_already_showed) {
            apklis.startLookingForUpdates(latency);
            update_info_already_showed = true;
        }
    }

    public void showMessage(Context c, String _s) {
        Toast.makeText(c, _s, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFingerprint() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_fingerprint = settings.getBoolean("show_fingerprint", false);
        if (show_fingerprint) {
            mBiometricManager = new BiometricManager.BiometricBuilder(MainActivity.this)
                    .setTitle(getString(R.string.biometric_title))
                    .setSubtitle(getString(R.string.biometric_subtitle))
                    .setDescription(getString(R.string.biometric_description))
                    .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                    .build();

            //start authentication
            mBiometricManager.authenticate(MainActivity.this);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, PermissionActivity.class));
        }

    }

    static final int PICK_CONTACT_REQUEST = 1;
    String ca0, ca1, ca2, ca3, ca4, ca5, ca6, ca7, ca8, ca9, ca10, ca11, ca12, ca13, ca14, ca15;
    String as0, as1, as2, as3, as4, as12, as13;
    String qwe = "";
    String union = "";
    String error = "Numero erroneo";
    int cuantos_caracteres;
    String error2 = "            Cuidado ..\n Faltan caracteres o su número seleccionado no es un número de telefonia móvil  ";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String nombre = cursor.getString(columnaNombre);
                    String numero = cursor.getString(columnaNumero);

                    union = "";
                    if (numero.length() > 0) {
                        ca0 = "" + numero.charAt(0) + "";
                        qwe = ca0;
                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                            union += qwe;
                        }

                        if (numero.length() > 1) {
                            ca1 = "" + numero.charAt(1) + "";
                            qwe = ca1;
                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                union += qwe;
                            }
                            if (numero.length() > 2) {
                                ca2 = "" + numero.charAt(2) + "";
                                qwe = ca2;
                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                    union += qwe;
                                }
                                if (numero.length() > 3) {
                                    ca3 = "" + numero.charAt(3) + "";
                                    qwe = ca3;
                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                        union += qwe;
                                    }
                                    if (numero.length() > 4) {
                                        ca4 = "" + numero.charAt(4) + "";
                                        qwe = ca4;
                                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                            union += qwe;
                                        }
                                        if (numero.length() > 5) {
                                            ca5 = "" + numero.charAt(5) + "";
                                            qwe = ca5;
                                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                union += qwe;
                                            }
                                            if (numero.length() > 6) {
                                                ca6 = "" + numero.charAt(6) + "";
                                                qwe = ca6;
                                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                    union += qwe;
                                                }
                                                if (numero.length() > 7) {
                                                    ca7 = "" + numero.charAt(7) + "";
                                                    qwe = ca7;
                                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                        union += qwe;
                                                    }
                                                    if (numero.length() > 8) {
                                                        ca8 = "" + numero.charAt(8) + "";
                                                        qwe = ca8;
                                                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                            union += qwe;
                                                        }
                                                        if (numero.length() > 9) {
                                                            ca9 = "" + numero.charAt(9) + "";
                                                            qwe = ca9;
                                                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                union += qwe;
                                                            }
                                                            if (numero.length() > 10) {
                                                                ca10 = "" + numero.charAt(10) + "";
                                                                qwe = ca10;
                                                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                    union += qwe;
                                                                }
                                                                if (numero.length() > 11) {
                                                                    ca11 = "" + numero.charAt(11) + "";
                                                                    qwe = ca11;
                                                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                        union += qwe;
                                                                    }
                                                                    if (numero.length() > 12) {
                                                                        ca12 = "" + numero.charAt(12) + "";
                                                                        qwe = ca12;
                                                                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                            union += qwe;
                                                                        }
                                                                        if (numero.length() > 13) {
                                                                            ca13 = "" + numero.charAt(13) + "";
                                                                            qwe = ca13;
                                                                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                                union += qwe;
                                                                            }
                                                                            if (numero.length() > 14) {
                                                                                ca14 = "" + numero.charAt(14) + "";
                                                                                qwe = ca14;
                                                                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                                    union += qwe;
                                                                                }
                                                                                if (numero.length() > 15) {
                                                                                    ca15 = "" + numero.charAt(15) + "";
                                                                                    qwe = ca15;
                                                                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                                        union += qwe;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                    if (union.length() == 8) {
                        String ewq = "" + union.charAt(0) + "";
                        if (ewq.equals("5")) {
                            ServiciosFragment.phoneNumber.setText(union);
                        } else {
                            showMessage(this, error);
                        }

                    } else {

                        if (union.length() < 15) {
                            cuantos_caracteres = union.length();
                            if (cuantos_caracteres == 14) {
                                as0 = "" + union.charAt(0) + "";
                                as1 = "" + union.charAt(1) + "";
                                as2 = "" + union.charAt(2) + "";
                                as3 = "" + union.charAt(3) + "";
                                as4 = "" + union.charAt(4) + "";
                                as12 = "" + union.charAt(12) + "";
                                as13 = "" + union.charAt(13) + "";
                                if (as0.equals("9") && as1.equals("9") && as2.equals("5") && as3.equals("3") && as4.equals("5") && as12.equals("9") && as13.equals("9")) {
                                    String nuu = "" + union.charAt(4) + union.charAt(5) + union.charAt(6) + union.charAt(7) + union.charAt(8) + union.charAt(9) + union.charAt(10) + union.charAt(11) + "";
                                    ServiciosFragment.phoneNumber.setText(nuu);
                                } else {
                                    showMessage(this, error);
                                }
                            } else {
                                cuantos_caracteres = union.length();
                                if (cuantos_caracteres == 10) {
                                    as0 = "" + union.charAt(0) + "";
                                    as1 = "" + union.charAt(1) + "";
                                    as2 = "" + union.charAt(2) + "";
                                    if ((as0.equals("5") && as1.equals("3")) || (as0.equals("9") && as1.equals("9"))) {
                                        if (as2.equals("5")) {
                                            String nuu = "" + union.charAt(2) + union.charAt(3) + union.charAt(4) + union.charAt(5) + union.charAt(6) + union.charAt(7) + union.charAt(8) + union.charAt(9) + "";
                                            ServiciosFragment.phoneNumber.setText(nuu);
                                        } else {
                                            showMessage(this, error);
                                        }
                                    } else {
                                        showMessage(this, error);
                                    }


                                } else {

                                    if (cuantos_caracteres < 8) {
                                        ServiciosFragment.phoneNumber.setText(union);
                                        showMessage(this, error2);
                                    } else {
                                        showMessage(this, error);
                                    }
                                }

                            }

                        } else {

                            showMessage(this, error);


                        }

                    }


                    //textnombre.setText(nombre);
                    // editnumero.setText(numero);
                }
            }

        }
        // FLOATING BUBBLE SERVICE
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
            } else {
                startService(new Intent(this, FloatingBubbleService.class));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ResultCall) {
            //PermisoLlamada();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_MENU, navView.getSelectedItemId());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFloatingBubbleService() {
        if (FloatingBubbleService.isStarted) {
            return;
        }
        if (Settings.canDrawOverlays(this)) {
            Log.i("CALLING ON MA", "STARTING SERVICE");
            stopService(new Intent(getApplicationContext(), FloatingBubbleService.class));
            startService(new Intent(getApplicationContext(), FloatingBubbleService.class));
        }
    }

    public static void showConnectedTime(boolean status) {
        FloatingBubbleService.showConnectedTime(status);
    }

    public static void setConnectedTime(String time) {
        FloatingBubbleService.setConnectedTime(time);
    }

    @Override
    public void onPause() {
        if (settings.getBoolean("show_traffic_speed_bubble", false)) {
            //unregisterReceiver(networkStateReceiver);
        }
        super.onPause();
    }

    @Override
    public void onSdkVersionNotSupported() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationNotSupported() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_hardware_not_supported), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_fingerprint_not_available), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_permission_not_granted), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAuthenticationFailed() {

    }

    @Override
    public void onAuthenticationCancelled() {
        mBiometricManager.cancelAuthentication();
        finish();

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onAuthenticationSuccessful() {
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        mBiometricManager.cancelAuthentication();
        finish();

    }

    @Override
    public void onButtonClicked(String text) {
        if (text.equals("tools")) {
            setFragment(new WebFragment());
        } else if (text.equals("settings")) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (text.equals("donate")) {
            new DonationDialog(MainActivity.this);
        } else if (text.equals("more")) {
            setFragment(new MoreFragment());
        } else if (text.equals("about")) {
            startActivity(new Intent(this, AboutActivity.class));//.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        bottomSheetDialog.dismiss();
    }

    public class DonationDialog {
        public DonationDialog(final Context context

        ) {
            final Dialog donationDialog = new Dialog(context);

            donationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            donationDialog.setCancelable(true);
            donationDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            donationDialog.setContentView(R.layout.alert_dialog);

            final EditText mount = donationDialog.findViewById(R.id.dialog_donation_mount_et);
            final EditText key = donationDialog.findViewById(R.id.dialog_donation_key_et);
            final LinearLayout bpaLayout = donationDialog.findViewById(R.id.bpa_layout);
            final TextView bpaTransfer = donationDialog.findViewById(R.id.bpa_transfer);
            bpaTransfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bpaTransfer.setVisibility(View.GONE);
                    bpaLayout.setVisibility(View.VISIBLE);
                }
            });
            final ImageView imageView = donationDialog.findViewById(R.id.close);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bpaLayout.setVisibility(View.GONE);
                    bpaTransfer.setVisibility(View.VISIBLE);
                }
            });


            ImageView send = donationDialog.findViewById(R.id.dialog_send_donation_btn);

            send.setOnClickListener(v -> {

                String donationMount = mount.getText().toString().trim();
                if (donationMount.equals("")) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String donationKey = key.getText().toString().trim();
                if (donationKey.equals("")) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussd = "*234*1*" + "54655909" + "*" + donationKey + "*" + donationMount + "%23";

                Intent r = new Intent();
                r.setAction(Intent.ACTION_CALL);
                r.setData(Uri.parse("tel:" + ussd + ""));

                if (Build.VERSION.SDK_INT >= 23) {
                    if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                    } else {
                        startActivity(r);
                    }
                } else {

                    startActivity(r);

                }
                donationDialog.dismiss();
            });

            donationDialog.show();
        }

    }

    private void makeDonation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.main_dialog_donate_title)
                .setMessage(R.string.main_action_about_donar_text)
                .setPositiveButton(getResources().getText(R.string.main_action_about_donar_button), new donateButtonListener())
                .setNegativeButton(getResources().getText(R.string.cancelar), new cancelButtonListener());
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.show();
    }

    public class cancelButtonListener implements DialogInterface.OnClickListener {
        cancelButtonListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    public class donateButtonListener implements DialogInterface.OnClickListener {
        donateButtonListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            new DonationDialog(MainActivity.this);
            dialogInterface.dismiss();
        }
    }

    public static void openLink(String link){
        try {
            //JCLogging.message("Opening PROMO URL::url=" + link, null);
            Uri url = Uri.parse(link);
            Intent openUrl = new Intent(Intent.ACTION_VIEW, url);
            context.startActivity(openUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}






