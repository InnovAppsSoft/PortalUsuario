package com.marlon.portalusuario.util.apklis;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
//import android.support.v4.app.NotificationCompat;          // API 29+: import androidx.core.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;   // API 29+: import androidx.core.app.NotificationManagerCompat;
//import android.support.v4.content.LocalBroadcastManager;   // API 29+: import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.logging.JCLogging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ApklisUpdatesJobService extends JobService {

    private Handler ApklisUpdateServiceHandler;
    private JCLogging Logging;

    @Override
    public void onCreate() {
        super.onCreate();

        // Util
        Logging = new JCLogging(ApklisUpdatesJobService.this);

        try {
            ApklisUpdateServiceHandler = new Handler(Looper.getMainLooper()) {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        boolean update_exist = msg.getData().getBoolean("update_exist", false);
                        String version_name = msg.getData().getString("version_name", "");
                        String new_version_size = msg.getData().getString("new_version_size", "");
                        String changelog = msg.getData().getString("changelog", "");
                        JobParameters jobParameters = msg.getData().getParcelable("params");

                        boolean AppActive = false;
                        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {

                            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
                            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
                                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                    for (String activeProcess : processInfo.pkgList) {
                                        if (activeProcess.equals(getApplicationContext().getPackageName())) {
                                            AppActive = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (update_exist) {
                            SharedPreferences updates = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if (!AppActive || !updates.getBoolean("show_update_window", true)) {
                                Logging.message("Showing Update Details Notification", null);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    NotificationChannel chanel = new NotificationChannel("chanel", "ApklisUpdate", NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(chanel);
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "chanel")
                                        .setSmallIcon(R.drawable.ic_baseline_system_update_24)
                                        .setContentTitle(getApplicationContext().getApplicationInfo().loadLabel(getApplicationContext().getPackageManager()).toString())
                                        .setContentText("Ya está disponible la v" + version_name + " en Apklis")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                notificationManagerCompat.notify(0, builder.build());
                            } else {
                                Logging.message("Launching Update Intent", null);
                                Intent intent = new Intent("apklis_update");
                                intent.putExtra("update_exist", update_exist);
                                intent.putExtra("version_name", version_name);
                                intent.putExtra("new_version_size", new_version_size);
                                intent.putExtra("changelog", changelog);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        }

                        jobFinished(jobParameters, false);
                    }
                    super.handleMessage(msg);

                }
            };
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.error(null, null, ex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onStartJob(JobParameters params) {
        try{
            String APP_PACKAGE = params.getExtras().getString("APP_PACKAGE","");
            NetWorkThread netWorkThread = new NetWorkThread(APP_PACKAGE, params);
            new Thread(netWorkThread).start();
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.error(null, null, ex);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public String parse(String api_apklis_json, String option){
        String string_value = "";
        try {
            int vn_index = api_apklis_json.indexOf(option);
            String value_count = String.valueOf(api_apklis_json.charAt(vn_index + option.length()));
            for (int i = option.length() + 1; !value_count.equals(","); i++) {
                string_value = string_value + value_count;
                value_count = String.valueOf(api_apklis_json.charAt(vn_index + i));
            }
            if (string_value.contains("\"")) {
                string_value = string_value.replaceAll("\"", "");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.error(null, null, ex);
        }
        return string_value;
    }

    class NetWorkThread extends Thread {
        String APP_PACKAGE;
        JobParameters jobParameters;

        NetWorkThread(String APP_PACKAGE, JobParameters jobParameters) {
            this.APP_PACKAGE = APP_PACKAGE;
            this.jobParameters = jobParameters;
        }

        @Override
        public void run() {

            String api_apklis_json = "";
            boolean update_exist = false;
            int apklis_version_code = 0;
            String apklis_version_name = "";
            String new_version_size = "";
            String changelog = "";

            PackageInfo pinfo = null;
            try {
                pinfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Logging.error(null, null, e);
            }
            int this_version_code = pinfo.versionCode;

            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            URL url = null;
            try {
                url = new URL("https://api.apklis.cu/v1/application/?package_name=" + APP_PACKAGE);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Logging.error(null, null, e);
            }

            try {
                BufferedReader api_apklis = new BufferedReader(new InputStreamReader(url.openStream()));

                String inputLine;
                while ((inputLine = api_apklis.readLine()) != null) {
                    api_apklis_json = api_apklis_json + inputLine;
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                Logging.error(null, null, e);
            } catch (SocketException se) {
                se.printStackTrace();
                Logging.error(null, null, se);
            } catch (IOException e) {
                e.printStackTrace();
                Logging.error(null, null, e);
            }

            if (!api_apklis_json.equals("")) {
                String apklis_version_code_tem = "";
                String version_code = "\"version_code\":";
                int cd_Index = api_apklis_json.indexOf(version_code);
                if (cd_Index != -1) {
                    String code = String.valueOf(api_apklis_json.charAt(cd_Index + version_code.length()));
                    for (int i = version_code.length(); !code.equals(","); i++) {
                        apklis_version_code_tem = apklis_version_code_tem + code;
                        code = String.valueOf(api_apklis_json.charAt(cd_Index + i + 1));
                    }
                } else apklis_version_code_tem = "";
                if (!apklis_version_code_tem.equals("")) {
                    apklis_version_code = Integer.parseInt(apklis_version_code_tem);


                    if (apklis_version_code > this_version_code) {
                        update_exist = true;

                        int vn_index = api_apklis_json.indexOf("\"version_name\":");
                        String vname = String.valueOf(api_apklis_json.charAt(vn_index + 16));
                        for (int i = 17; !vname.equals("\""); i++) {
                            apklis_version_name = apklis_version_name + vname;
                            vname = String.valueOf(api_apklis_json.charAt(vn_index + i));
                        }
                    }
                }
                // tamanno del APK ultima version
                try {
                    new_version_size = parse(api_apklis_json, "\"human_readable_size\":");
                    Log.e("Main", new_version_size);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("Main", ex.getMessage());
                    Logging.error(null, null, ex);
                }
                // la descripcion de la ultima version
                try {
                    changelog = parse(api_apklis_json, "\"changelog\":");
                    if (changelog.length() > 0){
                        changelog = changelog.replaceAll("<p>", "• ");
                        changelog = changelog.replaceAll("</p>", "\n");
                    }
                    Log.e("Main", changelog);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logging.error(null, null, ex);
                }
                // la descripcion de la ultima version
                String downloadCount;
                try {
                    changelog = parse(api_apklis_json, "\"changelog\":");
                    if (changelog.length() > 0){
                        changelog = changelog.replaceAll("<p>", "• ");
                        changelog = changelog.replaceAll("</p>", "\n");
                    }
                    Log.e("Main", changelog);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logging.error(null, null, ex);
                }
            }

            Message message = new Message();

            message.what = 1;
            Bundle bundle = new Bundle();

            bundle.putParcelable("params", jobParameters);
            bundle.putBoolean("update_exist", update_exist);
            bundle.putString("version_name", apklis_version_name);
            bundle.putString("new_version_size", new_version_size);
            bundle.putString("changelog", changelog);
            message.setData(bundle);
            ApklisUpdateServiceHandler.sendMessage(message);
        }
    }
}