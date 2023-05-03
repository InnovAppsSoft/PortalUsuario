package com.marlon.portalusuario.cortafuegos;

import static com.felipecsl.gifimageview.library.GifHeaderParser.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rule implements Comparable<Rule> {

    private static Map<PackageInfo, Boolean> cacheEnabled = new HashMap();
    private static Map<String, Boolean> cacheInternet = new HashMap();
    private static Map<PackageInfo, String> cacheLabel = new HashMap();
    private static List<PackageInfo> cachePackageInfo;
    private static Map<String, Boolean> cacheSystem = new HashMap();

    public PackageInfo info;
    public String name;
    public boolean system;
    public boolean disabled;
    public boolean wifi_blocked;
    public boolean other_blocked;
    public boolean changed;
    public boolean visible;

    private Rule(PackageInfo info, boolean wifi_blocked, boolean other_blocked, boolean changed, Context context) {
        PackageManager pm = context.getPackageManager();
        this.info = info;
        this.name = info.applicationInfo.loadLabel(pm).toString();
        this.system = ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);


        int setting = pm.getApplicationEnabledSetting(info.packageName);
        if (setting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
            this.disabled = !info.applicationInfo.enabled;
        else
            this.disabled = (setting != PackageManager.COMPONENT_ENABLED_STATE_ENABLED);

        this.wifi_blocked = wifi_blocked;
        this.other_blocked = other_blocked;
        this.changed = changed;
        this.visible = true;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static List<Rule> getRules(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences wifi = context.getSharedPreferences("wifi", Context.MODE_PRIVATE);
        SharedPreferences other = context.getSharedPreferences("other", Context.MODE_PRIVATE);

        boolean show_sys_apps = prefs.getBoolean("show_sys_apps", true);
        boolean wlWifi = prefs.getBoolean("whitelist_wifi", true);
        boolean wlOther = prefs.getBoolean("whitelist_other", true);

        List<Rule> listRules = new ArrayList<>();
        for (PackageInfo info : context.getPackageManager().getInstalledPackages(0)) {
            boolean blWifi = wifi.getBoolean(info.packageName, wlWifi);
            boolean blOther = other.getBoolean(info.packageName, wlOther);
            boolean changed = (blWifi != wlWifi || blOther != wlOther);
            Rule rule = new Rule(info, blWifi, blOther, changed, context);
            if (!show_sys_apps) {
                if (rule.system) {
                    continue;
                }
            }
                listRules.add(rule);
        }

        Collections.sort(listRules);

        return listRules;
    }

    public Drawable getIcon(Context context) {
        return info.applicationInfo.loadIcon(context.getPackageManager());
    }

    public static void clearCache(Context context) {
        Log.i(TAG, "Clearing cache");
        synchronized (context.getApplicationContext()) {
            cachePackageInfo = null;
            cacheLabel.clear();
            cacheSystem.clear();
            cacheInternet.clear();
            cacheEnabled.clear();
        }
      
    }

    @Override
    public int compareTo(Rule other) {
        if (changed == other.changed) {
            int i = name.compareToIgnoreCase(other.name);
            return (i == 0 ? info.packageName.compareTo(other.info.packageName) : i);
        }
        return (changed ? -1 : 1);
    }
}
