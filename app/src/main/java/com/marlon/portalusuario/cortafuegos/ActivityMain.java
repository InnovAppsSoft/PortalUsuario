package com.marlon.portalusuario.cortafuegos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.marlon.portalusuario.R;

import java.util.List;

public class ActivityMain extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "Firewall.Main";
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private NotificationManagerCompat notificationManagerCompat;
    private static final int NOTIFICATION_ID = 0;
    private static String CHANNEL_ID = "Servicio de Cortafuegos";

    private boolean running = false;
    private RuleAdapter adapter = null;
    private MenuItem searchItem = null;
    private ProgressDialog loadingBar;

    private androidx.appcompat.widget.SearchView searchView;

    private SwitchCompat swEnabled;
    private AppCompatImageView Wifi, Datos;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Toolbar toolbar;

    private static final int REQUEST_VPN = 1;

    String titulo = "Cortafuegos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Create");
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firewall_activity);

        running = true;

        this.setTitle(R.string.cortafuegos);

        Wifi = findViewById(R.id.lock_wifi_icon);
        Datos = findViewById(R.id.lock_data_icon);


        // On/off switch
        SwitchCompat swEnabled = (SwitchCompat) findViewById(R.id.swEnabled);
        swEnabled.setChecked(prefs.getBoolean("enabled", false));
        swEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "Switch on");
                    Intent prepare = VpnService.prepare(ActivityMain.this);
                    if (prepare == null) {
                        Log.e(TAG, "Prepare done");
                        onActivityResult(REQUEST_VPN, RESULT_OK, null);
                    } else {
                        Log.i(TAG, "Start intent=" + prepare);
                        try {
                            startActivityForResult(prepare, REQUEST_VPN);
                        } catch (Throwable ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                            onActivityResult(REQUEST_VPN, RESULT_CANCELED, null);
                            Toast.makeText(ActivityMain.this, ex.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    // MOSTRAR NOTIFIACION
                    createNotification(ActivityMain.this);
                    BlackHoleService.start(ActivityMain.this);
                } else {
                    // OCULTAR NOTIFIACION EN CASO DE QUE SE DETUVIERA EL SERVICIO
                    // STOPING SERVICE
                    Log.i(TAG, "Switch off");
                    prefs.edit().putBoolean("enabled", false).apply();
                    //deleteNotification();
                    BlackHoleService.stop(ActivityMain.this);
                }
            }
        });

        // Listen for preference changes
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Fill application list
        fillApplicationList();

        // Listen for connectivity updates
        IntentFilter ifConnectivity = new IntentFilter();
        ifConnectivity.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityChangedReceiver, ifConnectivity);

        // Listen for added/removed applications
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(packageChangedReceiver, intentFilter);

        Wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean("whitelist_wifi", !prefs.getBoolean("whitelist_wifi", true)).apply();
                fillApplicationList();
                BlackHoleService.reload("wifi", ActivityMain.this);

            }
        });

        Datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean("whitelist_other", !prefs.getBoolean("whitelist_other", true)).apply();
                fillApplicationList();
                BlackHoleService.reload("other", ActivityMain.this);

            }
        });

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroy");
        running = false;
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        unregisterReceiver(connectivityChangedReceiver);
        unregisterReceiver(packageChangedReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver connectivityChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received " + intent);
            Util.logExtras(TAG, intent);
            invalidateOptionsMenu();
        }
    };

    private BroadcastReceiver packageChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received " + intent);
            Util.logExtras(TAG, intent);
            fillApplicationList();
        }
    };

    @SuppressLint("StaticFieldLeak")
    private void fillApplicationList() {
        // Get recycler view
        final RecyclerView rvApplication = (RecyclerView) findViewById(R.id.rvApplication);
        rvApplication.setHasFixedSize(true);
        rvApplication.setLayoutManager(new LinearLayoutManager(this));

        // Swipe to refresh
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, tv, true);
        mSwipeRefreshLayout = findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.BLUE, Color.WHITE);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(tv.data);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Rule.clearCache(ActivityMain.this);
                BlackHoleService.reload("pull", ActivityMain.this);
                fillApplicationList();
            }
        });

        // Get/set application list
        new AsyncTask<Object, Object, List<Rule>>() {
            private boolean refreshing = true;

            @Override
            protected void onPreExecute() {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshing)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            }

            @Override
            protected List<Rule> doInBackground(Object... arg) {
                return Rule.getRules(ActivityMain.this);
            }

            @Override
            protected void onPostExecute(List<Rule> result) {
                if (running) {
                    if (searchItem != null)
                        MenuItemCompat.collapseActionView(searchItem);
                    adapter = new RuleAdapter(result, ActivityMain.this);
                    rvApplication.setAdapter(adapter);
                }
                if (mSwipeRefreshLayout != null) {
                    refreshing = false;
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String name) {
        Log.i(TAG, "Preference " + name + "=" + prefs.getAll().get(name));
        if ("enabled".equals(name)) {
            // Get enabled
            boolean enabled = prefs.getBoolean(name, false);

            // Check switch state
            SwitchCompat swEnabled = (SwitchCompat) findViewById(R.id.swEnabled);
            if (swEnabled.isChecked() != enabled)
                swEnabled.setChecked(enabled);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_firewall, menu);

        // Search
        searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar por título o paquete");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null)
                    adapter.getFilter().filter(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (searchView.getQuery() == "") {

                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        MenuItem network = menu.findItem(R.id.menu_network);
        network.setIcon(Util.isWifiActive(this) ? R.drawable.ic_network_wifi_white_24dp : R.drawable.ic_network_cell_white_24dp);

        MenuItem show_sys_apps = menu.findItem(R.id.menu_show_system_apps);
        show_sys_apps.setChecked(prefs.getBoolean("show_sys_apps", true));

        MenuItem wifi = menu.findItem(R.id.menu_whitelist_wifi);
        wifi.setChecked(prefs.getBoolean("whitelist_wifi", true));

        MenuItem other = menu.findItem(R.id.menu_whitelist_other);
        other.setChecked(prefs.getBoolean("whitelist_other", true));

        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int item1 = item.getItemId();
        if (item1 == R.id.menu_network) {
            Intent settings = new Intent(Util.isWifiActive(this)
                    ? Settings.ACTION_WIFI_SETTINGS : Settings.ACTION_WIRELESS_SETTINGS);
            if (settings.resolveActivity(getPackageManager()) != null)
                startActivity(settings);
            else
                Log.w(TAG, settings + " not available");
            return true;


        } else if( item1 == R.id.menu_show_system_apps ){
            prefs.edit().putBoolean("show_sys_apps", !prefs.getBoolean("show_sys_apps", true)).apply();
            fillApplicationList();
            return true;
        } else if( item1 == R.id.menu_refresh ){
            fillApplicationList();
            return true;

        } else if( item1 == R.id.menu_whitelist_wifi ){

            prefs.edit().putBoolean("whitelist_wifi", !prefs.getBoolean("whitelist_wifi", true)).apply();
            fillApplicationList();
            BlackHoleService.reload("wifi", this);
            return true;
        } else if( item1 == R.id.menu_whitelist_other ){
            prefs.edit().putBoolean("whitelist_other", !prefs.getBoolean("whitelist_other", true)).apply();
            fillApplicationList();
            BlackHoleService.reload("other", this);
            return true;
        } else if( item1 == R.id.menu_reset_wifi ){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_sure)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reset("wifi");
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        } else if( item1 == R.id.menu_reset_other ){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_sure)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reset("other");
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;

        } else if( item1 == R.id.menu_vpn_settings ){
            // Open VPN settings
            Intent vpn = new Intent("android.net.vpn.SETTINGS");
            if (vpn.resolveActivity(getPackageManager()) != null)
                startActivity(vpn);
            else
                Log.w(TAG, vpn + " not available");
            return true;

        }else{
            return super.onOptionsItemSelected(item);
        }

    }

    private void reset(String network) {
        SharedPreferences other = getSharedPreferences(network, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = other.edit();
        for (String key : other.getAll().keySet())
            edit.remove(key);
        edit.apply();
        fillApplicationList();
        BlackHoleService.reload(network, ActivityMain.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VPN) {
            // Update enabled state
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putBoolean("enabled", resultCode == RESULT_OK).apply();

            // Start service
            if (resultCode == RESULT_OK)
                BlackHoleService.start(this);
            createNotification(this);
        } else
            super.onActivityResult(requestCode, resultCode, data);


    }

    public void createNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // INTENT QUE LANZARA LA ACTIVITY
            // PUEDE MODIFICARSE PARA LANZAR CUALQUIER OTRA ACTIVIDAD
            // DEBE MODIFICARSE LA JERARQUIA DE ACTIVITIES EN EL MANIFIESTO
            Intent resultIntent = new Intent(context, ActivityMain.class);
            // CREAR TaskStackBuilder Y ANEXAR INTENT AL FINAL DE LA COLA DE LLAMADAS
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // OBTENER EL INTENT PENDIENTE A MOSTRAR
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            // SI ES VERSION ANDROID 8 EN ADELANTE

            // CREA CANAL DE NOTIFIACION
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("Firewall", "Portal Usuario VPN", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            // CREAR NOTIFIACION
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Firewall")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getApplicationInfo().loadLabel(context.getPackageManager()).toString())
                    .setContentText("Presione para abrir el cortafuegos")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .setContentIntent(resultPendingIntent);
            //We only need to call this for SDK 26+, since startForeground always has to be called after startForegroundService.
            //startForeground(NOTIFICATION_ID, builder.build());
            notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManagerCompat.notify(0, builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // SI CREATE ES FALSA, SE ELIMINA LA NOTIFICACION
            //stopForeground(NOTIFICATION_ID);
            if (notificationManagerCompat.areNotificationsEnabled()) {
                notificationManagerCompat.cancel(0);
            }
        }

    }

}
