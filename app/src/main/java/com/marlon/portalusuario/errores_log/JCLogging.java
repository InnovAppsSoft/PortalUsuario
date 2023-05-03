// Jalexcode - Logger Util //
package com.marlon.portalusuario.errores_log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class JCLogging {
    private static String log_title = "PortalUsuarioLog";
    @SuppressLint("SimpleDateFormat")
    private static final Format date = new SimpleDateFormat("dd-MM-yyyy | HH:mm:ss.SSS");
    private static final String logger_name = JCLogging.class.getName();
    private static PrintWriter writer;
    private final static boolean print_on_logcat = true;
    private final static boolean print_on_file = true;
    private static Context context;

    public JCLogging(Context context){
        this.context = context;
        setWriter();
    }

    public static boolean createFile(String path) {
        try {
            File logFile = getFile();
            if (!logFile.exists()) {
                return false;
            }
            new FileWriter(logFile).close();
            return true;
        } catch (IOException ex) {
            error(null, null, ex);
            return false;
        }
    }

    static String getDirectory() {
//        try {
//            if (externalStorageAvilable()) {
//                return context.getExternalFilesDir(null);
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        return context.getFilesDir();
        if (context != null) {
            return context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath();
        }else{
            return "";
        }
    }

    private static boolean externalStorageAvilable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static List<String> readFromFile(File file) throws IOException {
        List<String> arrayList = new ArrayList<String>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                arrayList.add(readLine);
            } else {
                bufferedReader.close();
                return arrayList;
            }
        }
    }

    public static String readAllFromFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String log = "";
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                log += readLine;
            } else {
                bufferedReader.close();
                return log;
            }
        }
    }

    public static File getFile(){
        return new File(getDirectory(), "log.txt");
    }

    public static void clearLog() {
        try{
            FileWriter fw = new FileWriter(getFile());
            fw.write("");
        }catch (IOException e){
            e.printStackTrace();
            error(null, null, e);
        }
    }

    private static void setWriter() {
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(getFile(), true)), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean fileExists() {
        try {
            File file = getFile();
            if (!file.exists()) {
                return false;
            }
            new FileWriter(file).close();
            return true;
        } catch (IOException ex) {
            error(null, null, ex);
            return false;
        }
    }

    public static void error(String msg, Object obj, Throwable throwable) {
        throwMessage('E',msg, obj, throwable);
    }

    public static void message(String msg, Object obj) {
        throwMessage('I', msg, obj, null);
    }

    public static void warning(String msg, Object obj) {
        throwMessage('W', msg, obj, null);
    }

    private static void throwMessage(char type, String msg, Object obj, Throwable th) {
        if ((msg == null || obj == null) && (print_on_file || print_on_logcat)) {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            int length = stackTrace.length;
            for (int i = 0; i < length; i++){
                StackTraceElement stackTraceElement = stackTrace[i];
                String className = stackTraceElement.getClassName();
                if (!logger_name.equals(className)) {
                    if (msg == null) {
                        msg = className.substring(className.lastIndexOf(46) + 1);
                    }
                    if (obj == null) {
                        obj = stackTraceElement.getMethodName() + "()";
                    }
                }
            }
        }
        if (print_on_file) {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                if (pref.getBoolean("save_logs", true)) {
                    String format_string = "%s | %c | %s: %s\n";
                    writer.format(format_string, date.format(new Date()), type, msg, obj);
                    if (type == 'E' && th != null) {
                        th.printStackTrace(writer);
                    }
                }
            }catch (final Exception e){
                e.printStackTrace();
            }
        }
        if (print_on_logcat) {
            String str2 = msg + ": " + obj;
            if (type != 'E') {
                if (type == 'I') {
                    Log.i(log_title, str2);
                } else if (type == 'W') {
                    Log.w(log_title, str2);
                }
            } else if (th == null) {
                Log.e(log_title, str2);
            } else {
                Log.e(log_title, str2, th);
            }
        }
//        if (context != null) {
//            Toast.makeText(context, "Ha ocurrido un error. Revise el Registro de actividades", Toast.LENGTH_SHORT).show();
//        }
    }
}
