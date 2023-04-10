package com.marlon.portalusuario.view.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.marlon.cz.mroczis.netmonster.core.factory.NetMonsterFactory;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellCdma;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellGsm;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellLte;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellNr;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellTdscdma;
import com.marlon.cz.mroczis.netmonster.core.model.cell.CellWcdma;
import com.marlon.cz.mroczis.netmonster.core.model.cell.ICell;
import com.marlon.cz.mroczis.netmonster.core.model.cell.ICellProcessor;
import com.marlon.portalusuario.PUNotifications.PUNotificationsActivity;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.Utils;
import com.marlon.portalusuario.blueChat.ActivityChat;
import com.marlon.portalusuario.etecsa_scraping.Promo;
import com.marlon.portalusuario.etecsa_scraping.PromoSliderAdapter;
import com.marlon.portalusuario.firewall.ActivityMain;
import com.marlon.portalusuario.logging.LogFileViewerActivity;
import com.marlon.portalusuario.onboard.IntroActivity;
import com.marlon.portalusuario.une.UneActivity;
import com.marlon.portalusuario.util.Connectivity;
import com.marlon.portalusuario.util.SSLHelper;
import com.marlon.portalusuario.util.Util;
import com.marlon.portalusuario.view.Fragments.BottomSheetDialog;
import com.marlon.portalusuario.view.Fragments.HowToFragment;
import com.marlon.portalusuario.view.Fragments.PaquetesFragment;
import com.marlon.portalusuario.view.Fragments.ServiciosFragment;
import com.marlon.portalusuario.view.Fragments.connectivity.ConnectivityFragment;
import com.marlon.portalusuario.PUNotifications.PUNotification;
import com.marlon.portalusuario.ViewModel.PunViewModel;
import com.marlon.portalusuario.util.apklis.ApklisUtil;
import com.marlon.portalusuario.biometric.BiometricCallback;
import com.marlon.portalusuario.biometric.BiometricManager;
import com.marlon.portalusuario.floating_window.BootReceiver;
import com.marlon.portalusuario.floating_window.FloatingBubbleService;
import com.marlon.portalusuario.logging.JCLogging;
import com.marlon.portalusuario.senal.AppConfiguracionTool;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

import kotlin.Unit;
import mobi.gspd.segmentedbarview.Segment;
import mobi.gspd.segmentedbarview.SegmentedBarView;
import mobi.gspd.segmentedbarview.SegmentedBarViewSideStyle;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageButton;
import soup.neumorphism.NeumorphImageView;

public class MainActivity extends AppCompatActivity implements BiometricCallback {

    private static Context context;
    private TextView details, titleTextView;
    private TextView log;

    // UI ELEMENTOS
    private LinearLayout mBottomSheetLayout;
    private static LinearLayout titleLayout;
    private BottomSheetBehavior sheetBehavior;
    private Button download_apklis;
    private Button download_ps;
    private Button remind_me_later;

    private BottomSheetDialog bottomSheetDialog;

    // PROMO ETECSA CAROUSEL
    private static ConstraintLayout carouselLayout;
    private static SliderView sliderView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private TextView try_again;
    public List<Promo> promoCache;

    private FrameLayout notificationBtn;
    private ImageView menu;
    private TextView cartBadge;

    private DrawerLayout drawer;
    public static NavigationView navigationView;

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

    public void setFragment(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        titleTextView.setText(title);
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
        // drawer Layout
        drawer = findViewById(R.id.drawer_layout);
        // drawer Nav View
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                switch (item.getItemId()) {
                    case R.id.services:
                        setFragment(new ServiciosFragment(), "Servicios");
                        break;
                    case R.id.plans:
                        setFragment(new PaquetesFragment(), "Planes");
                        break;
                    case R.id.connectivity:
                        setFragment(new ConnectivityFragment(), "Conectividad");
                        break;
                    case R.id.firewall:
                        i = new Intent(MainActivity.this, ActivityMain.class);
                        startActivity(i);
                        break;
                    case R.id.networkChange:
                        new SetLTEModeDialog(context);
                        break;
                    case R.id.une:
                        i = new Intent(MainActivity.this, UneActivity.class);
                        startActivity(i);
                        break;
                    case R.id.sim_info:
                        new SimInfoDialog(context, MainActivity.this);
                        break;
                    case R.id.tutos:
                        setFragment(new HowToFragment(), "Información útil");
                        break;
                    case R.id.errors_register:
                        startActivity(new Intent(MainActivity.this, LogFileViewerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                    case R.id.feedback:
                        String debugInfo = "\n\n\n---";
                        debugInfo += "\nOS Version: " + System.getProperty("os.version") + " (" + Build.VERSION.INCREMENTAL + ")";
                        debugInfo += "\nAndroid API: " + Build.VERSION.SDK_INT;
                        debugInfo += "\nModel (Device): " + Build.MODEL + " (" + Build.DEVICE + ")";
                        debugInfo += "\nManufacturer: " + Build.MANUFACTURER;
                        debugInfo += "\n---";

                        Intent intent = new Intent(Intent.ACTION_SENDTO,
                                Uri.fromParts("mailto", context.getString(R.string.feedback_email), null));

                        intent.putExtra(Intent.EXTRA_EMAIL, context.getString(R.string.feedback_email));
                        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject));
                        intent.putExtra(Intent.EXTRA_TEXT, debugInfo);

                        startActivity(Intent.createChooser(intent, "Enviar Feedback usando..."));
                        break;
                    case R.id.telegram_channel:
                        String telgramUrl = ("https://t.me/portalusuario");
                        Intent telegramLauch = new Intent(Intent.ACTION_VIEW);
                        telegramLauch.setData(Uri.parse(telgramUrl));
                        startActivity(telegramLauch);
                        break;
                    case R.id.facebook:
                        String facebookUrl = ("https://www.facebook.com/portalusuario");
                        Intent facebookLaunch = new Intent(Intent.ACTION_VIEW);
                        facebookLaunch.setData(Uri.parse(facebookUrl));
                        startActivity(facebookLaunch);
                        break;
                    case R.id.betatesters:
                        String betaUrl = ("https://t.me/portalusuarioBT");
                        Intent betaLaunch = new Intent(Intent.ACTION_VIEW);
                        betaLaunch.setData(Uri.parse(betaUrl));
                        startActivity(betaLaunch);
                        break;
                    // CONFIGURACION
                    case R.id.settings:
                        i = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(i);
                        break;
                    // CONFIGURACION
                    case R.id.about:
                        i = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(i);
                        break;
                    // CONFIGURACION
                    case R.id.donate:
                        i = new Intent(MainActivity.this,Donacion.class);
                        startActivity(i);
                        break;
                    // CONFIGURACION
                    case R.id.bluechat:
                        i = new Intent(MainActivity.this, ActivityChat.class);
                        startActivity(i);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        titleLayout = findViewById(R.id.titleLayout);
        titleTextView = findViewById(R.id.puTV);
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

            // etecsa carousel
            carouselLayout = findViewById(R.id.carouselLayout);
            sliderView = findViewById(R.id.imageSlider);
            progressBar = findViewById(R.id.progressBar);
            errorLayout = findViewById(R.id.errorLayoutBanner);
            try_again = findViewById(R.id.try_again);
            try_again.setPaintFlags(try_again.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            try_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPromo();
                }
            });
            loadPromo();
            //
            // check if there are unseen notifications
            cartBadge = findViewById(R.id.cart_badge);
            setupBadge();
            notificationBtn = findViewById(R.id.notificationBtn);
            notificationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent i = new Intent(MainActivity.this, PUNotificationsActivity.class);
                    startActivity(i);
                    Toast.makeText(MainActivity.this, "Espera la nueva funcionalidad en próximas versiones 😉", Toast.LENGTH_SHORT).show();
                }
            });
        }
        //
        setFragment(new ServiciosFragment(), "Servicios");
    }

    private void setupBadge() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int count = sharedPreferences.getInt("notifications_count", 0);
        Log.e("UNSEE NOTIFICATIONS", String.valueOf(count));
        if (count == 0) {
            if (cartBadge.getVisibility() != View.GONE) {
                cartBadge.setVisibility(View.GONE);
            }
        } else {
            if (cartBadge.getVisibility() != View.VISIBLE) {
                cartBadge.setVisibility(View.VISIBLE);
            }
        }
        cartBadge.setText(String.valueOf(count));
    }

    public void loadPromo() {
        // hiding promos card view
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean show_etecsa_promo_carousel = settings.getBoolean("show_etecsa_promo_carousel", true);
        if (show_etecsa_promo_carousel) {
            // mostrar progress && ocultar carrusel
            sliderView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            // mostrar error
            errorLayout.setVisibility(View.INVISIBLE);
            //
            if (promoCache != null && !promoCache.isEmpty()) {
                updatePromoSlider(promoCache);
                return;
            }
            //
            if (Util.isConnected(MainActivity.this)) {
                // llamada al metodo de scraping
                try {
                    new scrapingPromo().execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // ocultar progress && carrusel
                sliderView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                // mostrar error
                errorLayout.setVisibility(View.VISIBLE);
            }
        }
        setCarouselVisibility(show_etecsa_promo_carousel);
    }

    // scrapear Promo de Etecsa
    public class scrapingPromo extends AsyncTask<Void, Void, List<Promo>> {
        private boolean success;

        // cuando se termine de ejecutar la accion doInBackground
        public void onPostExecute(List<Promo> promos) {
            super.onPostExecute(promos);
            // adapter para cada item
            if (success) {
                try {
                    // ocultar error
                    errorLayout.setVisibility(View.INVISIBLE);
                    // mostrar card view
                    sliderView.setVisibility(View.VISIBLE);
                    //
                    updatePromoSlider(promos);
                    // ocultar progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // ocultar progress bar
                progressBar.setVisibility(View.INVISIBLE);
                // ocultar card view
                sliderView.setVisibility(View.INVISIBLE);
                // mostrar error
                errorLayout.setVisibility(View.VISIBLE);
            }
        }

        public List<Promo> doInBackground(Void... voidArr) {
            List<Promo> promos = new ArrayList<Promo>();
            try {
                Connection.Response response = SSLHelper.getConnection("https://www.etecsa.cu").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30").timeout(30000).ignoreContentType(true).method(Connection.Method.GET).followRedirects(true).execute();
                if (response.statusCode() == 200) {
                    Document parsed = response.parse();
                    // CAROUSEL
                    Elements carousel = parsed.select("div.carousel-inner").select("div.carousel-item");
                    for (int i = 0; i < carousel.size(); i++) {
                        Element items = carousel.get(i);
                        Element mipromoContent = items.selectFirst("div.carousel-item");
                        String link = items.selectFirst("div.mipromocion-contenido").select("a").attr("href");
                        String toText = mipromoContent.toString();
                        int idx1 = toText.indexOf("<div style=\"background: url(\'");
                        int idx2 = toText.indexOf("\');");
                        String divStyle = toText.substring(idx1, idx2);
                        divStyle = divStyle.replace("<div style=\"background: url(\'", "");
                        //
                        Element imageSvg = items.selectFirst("div.mipromocion-contenido").selectFirst("img");
                        String svg = imageSvg.attr("src");
                        //
                        promos.add(new Promo(svg, divStyle, link));
                    }
                    success = true;
                } else {
                    success = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return promos;
        }
    }

    public void updatePromoSlider(List<Promo> list) {
        if (!list.isEmpty()) {
            PromoSliderAdapter adapter = new PromoSliderAdapter(this, (ArrayList<Promo>) list);
            sliderView.setSliderAdapter(adapter);
            // setting up el slider view
            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINSCALINGTRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(4);
            sliderView.startAutoCycle();
        } else {
            carouselLayout.setVisibility(View.GONE);
        }
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
    String errorMessage = "Numero erroneo";
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
                            showMessage(this, errorMessage);
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
                                    showMessage(this, errorMessage);
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
                                            showMessage(this, errorMessage);
                                        }
                                    } else {
                                        showMessage(this, errorMessage);
                                    }


                                } else {

                                    if (cuantos_caracteres < 8) {
                                        ServiciosFragment.phoneNumber.setText(union);
                                        showMessage(this, error2);
                                    } else {
                                        showMessage(this, errorMessage);
                                    }
                                }

                            }

                        } else {

                            showMessage(this, errorMessage);


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
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    public static void openLink(String link) {
        try {
            //JCLogging.message("Opening PROMO URL::url=" + link, null);
            Uri url = Uri.parse(link);
            Intent openUrl = new Intent(Intent.ACTION_VIEW, url);
            context.startActivity(openUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCarouselVisibility(boolean b) {
        if (b) {
            carouselLayout.setVisibility(View.VISIBLE);
        } else {
            carouselLayout.setVisibility(View.GONE);
        }
    }

    public class SimInfoDialog {
        private final ServiceState myServiceState = new ServiceState();
        private PhoneStateListener listener;
        PhoneStateListener listener2 = null;
        private TextView networkClass;
        private TelephonyManager telephonyManager;
        private TextView tvSignal;
        private com.marlon.portalusuario.Utils Utils;
        private SegmentedBarView bv;
        private Connectivity Conn;
        private Activity activity;

        public SimInfoDialog(final Context context, final Activity activity) {
            final Dialog simDialog = new Dialog(context);
            this.activity = activity;

            simDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            simDialog.setCancelable(true);
            simDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            simDialog.setContentView(R.layout.dialog_info_sim);

            bv = simDialog.findViewById(R.id.bar_view);
            tvSignal = simDialog.findViewById(R.id.tvSignal);
            networkClass = simDialog.findViewById(R.id.tvTipoRedM);
            Utils = new Utils(getApplicationContext());
            Conn = new Connectivity();
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            inicializarSegmentoFuerzaSenal();
            obtenerTipoSenal();
            reinicializarSegmentoSenal();

            simDialog.show();
        }

        private void reinicializarSegmentoSenal() {
            ArrayList arrayList = new ArrayList();
            if (this.networkClass.getText().toString().contains("4G")) {
                arrayList.add(new Segment(-100.0f, -95.0f, "Mala", Color.parseColor("#ffd50000")));
                arrayList.add(new Segment(-90.0f, -85.0f, "", Color.parseColor("#ffd50000")));
                arrayList.add(new Segment(-80.0f, -75.0f, "Buena", Color.parseColor("#ffffd600")));
                arrayList.add(new Segment(-70.0f, -65.0f, "", Color.parseColor("#8CC63E")));
                arrayList.add(new Segment(-60.0f, -0.0f, "Perfecta", Color.parseColor("#8CC63E")));
            } else if (this.networkClass.getText().toString().contains("3G")) {
                arrayList.add(new Segment(-100.0f, -95.0f, "Mala", Color.parseColor("#ffd50000")));
                arrayList.add(new Segment(-90.0f, -85.0f, "", Color.parseColor("#ffd50000")));
                arrayList.add(new Segment(-80.0f, -75.0f, "Buena", Color.parseColor("#ffffd600")));
                arrayList.add(new Segment(-70.0f, -65.0f, "", Color.parseColor("#8CC63E")));
                arrayList.add(new Segment(-60.0f, -0.0f, "Perfecta", Color.parseColor("#8CC63E")));
            } else {
                arrayList.add(new Segment(-100.0f, -95.0f, "Mala", Color.parseColor("#ffd50000")));
                arrayList.add(new Segment(-90.0f, -85.0f, "", Color.parseColor("#ffd50000")));
                arrayList.add(new Segment(-80.0f, -75.0f, "Buena", Color.parseColor("#ffffd600")));
                arrayList.add(new Segment(-70.0f, -65.0f, "", Color.parseColor("#8CC63E")));
                arrayList.add(new Segment(-60.0f, -0.0f, "Perfecta", Color.parseColor("#8CC63E")));

            }

            this.bv.setSegments(arrayList);
            this.bv.setUnit("dBm");
            this.bv.setShowDescriptionText(true);
            this.bv.setSideStyle(SegmentedBarViewSideStyle.ROUNDED);
            this.bv.setDescriptionTextColor(R.color.colorDes);
        }

        private void inicializarSegmentoFuerzaSenal() {
            @SuppressLint("WrongConstant") final TelephonyManager telephonyManager2 = (TelephonyManager) getSystemService("phone");
            if (Build.VERSION.SDK_INT >= 28 && !this.Utils.PermissionUELocationGPS(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "Debe habilitar el permiso de ubicación", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(activity, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 1);

            }
            if (Build.VERSION.SDK_INT >= 28 && this.Utils.PermissionUELocationGPS(getApplicationContext()) && !this.Utils.openLocation(getApplicationContext())) {

            }
            this.listener = new PhoneStateListener() {

                @SuppressLint("WrongConstant")
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    List<CellInfo> list;
                    String valueOf = null;
                    super.onSignalStrengthsChanged(signalStrength);
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sb2 = new StringBuilder();
                    if (!Utils.PermissionUELocation(context)) {
                        list = null;
                    } else if (Build.VERSION.SDK_INT < 23 || context.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                        list = telephonyManager2.getAllCellInfo();
                    } else {
                        return;
                    }
                    if (Build.VERSION.SDK_INT < 23) {
                        try {
                            new getSignal(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                        } catch (RejectedExecutionException e) {
                            e.printStackTrace();
                        }
                    } else if (context.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 && context.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0 && context.checkSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                        try {
                            new getSignal(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                        } catch (RejectedExecutionException e2) {
                            e2.printStackTrace();
                        }
                    } else {
                    }
                    String str = "0";
                    if (list != null) {
                        android.util.Log.d("cellInfos", list.toString());
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) instanceof CellInfoWcdma) {
                                android.util.Log.d("cellInfos.get(i)", list.get(i).toString());
                                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) list.get(i);
                                CellSignalStrengthWcdma cellSignalStrength = cellInfoWcdma.getCellSignalStrength();
                                android.util.Log.d("cellInfoWcdma", String.valueOf(cellSignalStrength.getDbm()));
                                valueOf = String.valueOf(cellSignalStrength.getDbm());
                                if (list.get(i).isRegistered()) {
                                    sb.append("\nCelda: ");
                                    sb.append(cellInfoWcdma.getCellIdentity().getCid());
                                    sb.append(" Red: 3G ");
                                    sb.append(" dBm: ");
                                    sb.append(valueOf);
                                } else {
                                    sb2.append("\nCelda: ");
                                    sb2.append(cellInfoWcdma.getCellIdentity().getCid());
                                    sb2.append(" Red: 3G ");
                                    sb2.append(" dBm: ");
                                    sb2.append(valueOf);
                                }
                            } else if (list.get(i) instanceof CellInfoGsm) {
                                android.util.Log.d("cellInfos.get(i)", list.get(i).toString());
                                CellInfoGsm cellInfoGsm = (CellInfoGsm) list.get(i);
                                CellSignalStrengthGsm cellSignalStrength2 = cellInfoGsm.getCellSignalStrength();
                                android.util.Log.d("cellInfoGsm", String.valueOf(cellSignalStrength2.getDbm()));
                                valueOf = String.valueOf(cellSignalStrength2.getDbm());
                                if (list.get(i).isRegistered()) {
                                    sb.append("\nCelda: ");
                                    sb.append(cellInfoGsm.getCellIdentity().getCid());
                                    sb.append(" Red: 2G ");
                                    sb.append(" dBm: ");
                                    sb.append(valueOf);
                                } else {
                                    sb2.append("\nCelda: ");
                                    sb2.append(cellInfoGsm.getCellIdentity().getCid());
                                    sb2.append(" Red: 2G ");
                                    sb2.append(" dBm: ");
                                    sb2.append(valueOf);
                                }
                            } else if (list.get(i) instanceof CellInfoLte) {
                                android.util.Log.d("cellInfos.get(i)", list.get(i).toString());
                                CellInfoLte cellInfoLte = (CellInfoLte) list.get(i);
                                CellSignalStrengthLte cellSignalStrength3 = cellInfoLte.getCellSignalStrength();
                                android.util.Log.d("cellInfoLte", String.valueOf(cellSignalStrength3.getDbm()));
                                valueOf = String.valueOf(cellSignalStrength3.getDbm());
                                if (list.get(i).isRegistered()) {
                                    sb.append("\nCelda: ");
                                    sb.append(cellInfoLte.getCellIdentity().getCi());
                                    sb.append(" Red: 4G ");
                                    sb.append(" dBm: ");
                                    sb.append(valueOf);
                                } else {
                                    sb2.append("\nCelda: ");
                                    sb2.append(cellInfoLte.getCellIdentity().getCi());
                                    sb2.append(" Red: 4G ");
                                    sb2.append(" dBm: ");
                                    sb2.append(valueOf);
                                }
                            }
                            str = valueOf;
                        }
                        android.util.Log.d("RadioN:", sb.toString());
                        try {
                            if (sb.toString().equals("")) {

                            } else {

                                Log.d("RadioN2:", sb2.toString());
                            }
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                    android.util.Log.v("Signal Strenght:", str);
                }
            };
        }


        public ServiceState getServiceState() {
            return this.myServiceState;
        }

        @SuppressLint("WrongConstant")
        private void obtenerTipoSenal() {
            networkClass.setText(this.Conn.getNetworkClass(context));
            if (this.Utils.PermissionUELocation(getApplicationContext())) {
                try {
                    if (Build.VERSION.SDK_INT < 23 || getApplicationContext().checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 || getApplicationContext().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                        this.telephonyManager.listen(this.listener, 256);
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

            }
        }

        public class getSignal extends AsyncTask<Void, Void, Void> {
            String banda = "";
            Context context;
            Float rssi = Float.valueOf(0.0f);

            public getSignal(Context context2) {
                this.context = context2;
            }

            public Void doInBackground(Void... voidArr) {
                @SuppressLint("MissingPermission") List<ICell> cells = NetMonsterFactory.INSTANCE.get(this.context).getCells();
                ICellProcessor<Unit> r0 = new ICellProcessor<Unit>() {

                    @Override
                    public Unit processCdma(CellCdma cellCdma) {
                        try {
                            String num = ((Integer) Objects.requireNonNull(cellCdma.getBand().getNumber())).toString();
                            String name = cellCdma.getBand().getName();
                            banda = "B" + num + " - " + name;
                            rssi = Float.valueOf((float) cellCdma.getSignal().getCdmaRssi().intValue());
                            return null;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public Unit processGsm(CellGsm cellGsm) {
                        try {
                            String num = ((Integer) Objects.requireNonNull(cellGsm.getBand().getNumber())).toString();
                            String name = cellGsm.getBand().getName();
                            if (num.equals("900")) {
                                num = "8";
                            }
                            banda = "B" + num + " - " + name;
                            rssi = Float.valueOf((float) cellGsm.getSignal().getRssi().intValue());
                            return null;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public Unit processLte(CellLte cellLte) {
                        try {
                            String num = ((Integer) Objects.requireNonNull(cellLte.getBand().getNumber())).toString();
                            String name = cellLte.getBand().getName();
                            banda = "B" + num + " - " + name;
                            rssi = Float.valueOf((float) cellLte.getSignal().getRssi().intValue());
                            return null;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public Unit processNr(CellNr cellNr) {
                        try {
                            String num = ((Integer) Objects.requireNonNull(cellNr.getBand().getNumber())).toString();
                            String name = cellNr.getBand().getName();
                            banda = "B" + num + " - " + name;
                            return null;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public Unit processTdscdma(CellTdscdma cellTdscdma) {
                        try {
                            String num = ((Integer) Objects.requireNonNull(cellTdscdma.getBand().getNumber())).toString();
                            String name = cellTdscdma.getBand().getName();
                            banda = "B" + num + " - " + name;
                            rssi = Float.valueOf((float) cellTdscdma.getSignal().getRssi().intValue());
                            return null;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override // com.marlon.cz.mroczis.netmonster.core.model.cell.ICellProcessor
                    public Unit processWcdma(CellWcdma cellWcdma) {
                        try {
                            String num = ((Integer) Objects.requireNonNull(cellWcdma.getBand().getNumber())).toString();
                            String name = cellWcdma.getBand().getName();
                            banda = "B" + num + "-" + name;
                            rssi = Float.valueOf((float) cellWcdma.getSignal().getRscp().intValue());
                            return null;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
                if (cells.isEmpty()) {
                    return null;
                }
                cells.get(0).let(r0);
                return null;
            }

            public void onPostExecute(Void r4) {
                tvSignal.setText(banda + " MHz");
                bv.setValue(rssi);
                super.onPostExecute((Void) r4);
            }
        }

    }

    public class SetLTEModeDialog {
        private Button set4GBtn;

        public SetLTEModeDialog(final Context context) {
            final Dialog simDialog = new Dialog(context);

            simDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            simDialog.setCancelable(true);
            simDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            simDialog.setContentView(R.layout.dialog_set_only_lte);

            set4GBtn = simDialog.findViewById(R.id.set_4g);


            set4GBtn.setOnClickListener(v -> openHiddenMenu());

            simDialog.show();
        }

        public void openHiddenMenu() {
            try {
                Intent intent = new Intent("android.intent.action.MAIN");
                if (Build.VERSION.SDK_INT >= 30) {
                    intent.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
                } else {
                    intent.setClassName("com.android.settings", "com.android.settings.RadioInfo");
                }
                startActivity(intent);
            } catch (Exception unused) {
                Toast.makeText(context, "Su dispositivo no admite esta funcionalidad, lamentamos las molestias :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}






