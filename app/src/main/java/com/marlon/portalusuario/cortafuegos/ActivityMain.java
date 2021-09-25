package com.marlon.portalusuario.cortafuegos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlon.portalusuario.R;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;


public class ActivityMain extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "Firewall.Main";

    private boolean running = false;
    private RuleAdapter adapter = null;
    private MenuItem searchItem = null;

    private static final int REQUEST_VPN = 1;
    private ProgressDialog loadingBar;
    String titulo = "Cortafuegos";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Create");

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setTitle(R.string.Cortafuegos);
        loadingBar = new ProgressDialog(this);

        running = true;

        // Action bar
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.actionbar, null);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(view);

        // On/off switch
        SwitchCompat swEnabled = (SwitchCompat) view.findViewById(R.id.swEnabled);
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
                            prefs.edit().putBoolean("enabled", false).apply();
                            Toast.makeText(ActivityMain.this, ex.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Log.i(TAG, "Switch off");
                    prefs.edit().putBoolean("enabled", false).apply();
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


    private void fillApplicationList() {
        // Get recycler view
        final RecyclerView rvApplication = (RecyclerView) findViewById(R.id.rvApplication);
        rvApplication.setHasFixedSize(true);
        rvApplication.setLayoutManager(new LinearLayoutManager(this));
        loadingBar.setMessage("Cargando aplicaciones");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        new ListAsyncTask(rvApplication).execute();
    }



    public void onSharedPreferenceChanged(@NonNull SharedPreferences prefs, @NonNull String name) {
        Log.i(TAG, "Preference " + name + "=" + prefs.getAll().get(name));
        if ("enabled".equals(name)) {
            // Get enabled
            boolean enabled = prefs.getBoolean(name, false);

            // Check switch state
            SwitchCompat swEnabled = (SwitchCompat) getSupportActionBar().getCustomView().findViewById(R.id.swEnabled);
            if (swEnabled.isChecked() != enabled)
                swEnabled.setChecked(enabled);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Search
        searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null)
                    adapter.getFilter().filter(query);
                return true;
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
                if (adapter != null)
                    adapter.getFilter().filter(null);
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

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_network:
                Intent settings = new Intent(Util.isWifiActive(this)
                        ? Settings.ACTION_WIFI_SETTINGS : Settings.ACTION_WIRELESS_SETTINGS);
                if (settings.resolveActivity(getPackageManager()) != null)
                    startActivity(settings);
                else
                    Log.w(TAG, settings + " not available");
                return true;

            case R.id.menu_refresh:
                fillApplicationList();
                return true;

            case R.id.menu_whitelist_wifi:
                prefs.edit().putBoolean("whitelist_wifi", !prefs.getBoolean("whitelist_wifi", true)).apply();
                fillApplicationList();
                BlackHoleService.reload("wifi", this);
                return true;

            case R.id.menu_whitelist_other:
                prefs.edit().putBoolean("whitelist_other", !prefs.getBoolean("whitelist_other", true)).apply();
                fillApplicationList();
                BlackHoleService.reload("other", this);
                return true;

            case R.id.menu_reset_wifi:
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

            case R.id.menu_reset_other:
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

            case R.id.menu_vpn_settings:
                // Open VPN settings
                Intent vpn = new Intent("android.net.vpn.SETTINGS");
                if (vpn.resolveActivity(getPackageManager()) != null)
                    startActivity(vpn);
                else
                    Log.w(TAG, vpn + " not available");
                return true;

            default:
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
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private class ListAsyncTask extends AsyncTask<Object, Object, List<Rule>> {
        private final RecyclerView rvApplication;

        public ListAsyncTask(RecyclerView rvApplication) {
            this.rvApplication = rvApplication;
        }

        @Override
        protected List<Rule> doInBackground(Object... arg) {
            return Rule.getRules(ActivityMain.this);
        }

        @Override
        protected void onPostExecute(List<Rule> result) {
            if (running) {
                if (searchItem != null)
                    searchItem.collapseActionView();
                adapter = new RuleAdapter(result, ActivityMain.this);
                rvApplication.setAdapter(adapter);
                loadingBar.dismiss();

            }
        }
    }
}
