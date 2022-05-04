package com.marlon.portalusuario.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.marlon.portalusuario.Nauta;
import com.marlon.portalusuario.PUNotifications.PUNotification;
import com.marlon.portalusuario.Wifi;
import com.marlon.portalusuario.une.Une;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private Context context;

    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DBConstants.DATABASE_NAME, factory, DBConstants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // CREATING NOTIFICATION TABLE
            String queryCreateNotificationTable = "CREATE TABLE " + DBConstants.NOTIFICATION_TABLE + "(" +
                    DBConstants.NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBConstants.NOTIFICATION_TITLE + " TEXT, " +
                    DBConstants.NOTIFICATION_TEXT + " TEXT, " +
                    DBConstants.NOTIFICATION_DATE + " TEXT, " +
                    DBConstants.NOTIFICATION_IMAGE + " TEXT " +
                    ");";
            db.execSQL(queryCreateNotificationTable);
            // CREATING WIFI TABLE
            String queryCreateWifiTable = "CREATE TABLE " + DBConstants.WIFI_TABLE + "(" +
                    DBConstants.ACCOUNT_WIFI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBConstants.ACCOUNT_WIFIETECSA + " TEXT, " +
                    DBConstants.ACCOUNT_WIFIETECSA_PASSWORD + " TEXT, " +
                    DBConstants.ACCOUNT_WIFIETECSA_LAST_TIME_CONNECTED + " TEXT, " +
                    DBConstants.ACCOUNT_WIFIETECSA_LEFT_TIME + " TEXT " +
                    ");";
            db.execSQL(queryCreateWifiTable);
            // CREATING NAUTA TABLE
            String queryCreateNAUTATable = "CREATE TABLE " + DBConstants.NAUTA_TABLE + "(" +
                    DBConstants.ACCOUNT_NAUTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBConstants.ACCOUNT_NAUTA_EMAIL + " TEXT, " +
                    DBConstants.ACCOUNT_NAUTA_EMAIL_PASSWORD + " TEXT " +
                    ");";
            db.execSQL(queryCreateNAUTATable);
            // CREATING UNE TABLE
            String queryCreateUNETable = "CREATE TABLE " + DBConstants.UNE_TABLE + "(" +
                    DBConstants.UNE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBConstants.UNE_DATE + " TEXT, " +
                    DBConstants.UNE_CURRENT_REGISTER + " INTEGER, " +
                    DBConstants.UNE_LAST_REGISTER + " INTEGER, " +
                    DBConstants.UNE_TOTAL_CONSUMPTION + " REAL, " +
                    DBConstants.UNE_TOTAL_TO_PAY + " REAL " +
                    ");";
            db.execSQL(queryCreateUNETable);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error al crear tablas en Base de Datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.NOTIFICATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.NAUTA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.UNE_TABLE);
        onCreate(db);
    }

    @SuppressLint("SimpleDateFormat")
    public boolean addNotification(PUNotification puNotification) {
        try {
            Log.i("DataBase", "ADDING NOTIFICATION");
            SQLiteDatabase db = getWritableDatabase();
            // ADD NOT
            // SAVE IMAGE
            String image = puNotification.getImage();
            if (image != null) {

            }
            // NOTIFICATION VALUES
            ContentValues notificationValues = new ContentValues();
            notificationValues.put(DBConstants.NOTIFICATION_TITLE, puNotification.getTitle());
            notificationValues.put(DBConstants.NOTIFICATION_TEXT, puNotification.getText());
            notificationValues.put(DBConstants.NOTIFICATION_DATE, new SimpleDateFormat("dd/MM/yyyy hh:mm aa").format(Calendar.getInstance().getTime()));
            notificationValues.put(DBConstants.NOTIFICATION_IMAGE, puNotification.getImage());
            // INSERT INTO DB
            db.insert(DBConstants.NOTIFICATION_TABLE, null, notificationValues);
            Log.e("DataBase", "NOTIFICATION ADDED!");

            return true;
        } catch (Exception ex) {
            Log.e("DataBase", "ERROR WHILE TRYING TO ADD NOTIFICATION");
            ex.printStackTrace();
            //Toast.makeText(context, "Error al insertar mensaje", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public List<PUNotification> selectAllNotifications() {
        // LIST
        List<PUNotification> notificationList = new ArrayList<>();
        // SQL OBJECT
        SQLiteDatabase db = getWritableDatabase();
        // SQL QUERY
        try {
            String query = "SELECT * FROM " + DBConstants.NOTIFICATION_TABLE + ";";
            Cursor cursorPUNotifications = db.rawQuery(query, null);
            if (cursorPUNotifications.getCount() > 0) {

                cursorPUNotifications.moveToFirst();
                while (!cursorPUNotifications.isAfterLast()) {
                    // NOTIFICATION INSTANCE
                    PUNotification notification = null;
                    // ATTRIBUTES
                    int id = cursorPUNotifications.getInt(0);
                    // TITLE
                    String title = cursorPUNotifications.getString(1);
                    // TEXT
                    String text = cursorPUNotifications.getString(2);
                    // DATE
                    String date = cursorPUNotifications.getString(3);
                    Log.e("DATE", date);
                    GregorianCalendar realDate = new GregorianCalendar();
                    if (date != null || !date.isEmpty()) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                        Date d = spf.parse(date);
                        realDate.setTime(d);
                    }
                    // IMAGE
                    String image = cursorPUNotifications.getString(4);
                    //
                    notificationList.add(new PUNotification(id, title, text, image, realDate));
                    cursorPUNotifications.moveToNext();
                }
            }
            cursorPUNotifications.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error cargando mensajes", Toast.LENGTH_SHORT).show();
        }
        return notificationList;
    }

    public PUNotification selectPUNotification(final int id) {
        SQLiteDatabase db = getWritableDatabase();
        // NOTIFICATION INSTANCE
        PUNotification notification = null;
        try {
            String query = "SELECT * FROM " + DBConstants.NOTIFICATION_TABLE + " WHERE id=" + id + ";";
            Cursor cursorPUNotifications = db.rawQuery(query, null);
            if (cursorPUNotifications != null) {
                cursorPUNotifications.moveToFirst();
                // ATTRIBUTES
                // TITLE
                String title = cursorPUNotifications.getString(1);
                // TEXT
                String text = cursorPUNotifications.getString(2);
                // DATE
                String date = cursorPUNotifications.getString(3);
                GregorianCalendar realDate = new GregorianCalendar();
                if (date != null || !date.isEmpty()) {
                    String[] splittedDate = date.split("/");
                    int day = Integer.parseInt(splittedDate[0]);
                    int month = Integer.parseInt(splittedDate[1]);
                    int year = Integer.parseInt(splittedDate[2]);
                    realDate.set(year, month, day);
                }
                // IMAGE
                String image = cursorPUNotifications.getString(4);
                //
                notification = new PUNotification(id, title, text, image, realDate);
            }
            cursorPUNotifications.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error cargando el mensaje", Toast.LENGTH_SHORT).show();
        }
        db.close();
        return notification;
    }

    public PUNotification deleteAllPUNotification() {
        SQLiteDatabase db = getWritableDatabase();
        // NOTIFICATION INSTANCE
        PUNotification notification = null;
        try {
            String query = "DELETE FROM " + DBConstants.NOTIFICATION_TABLE + ";";
            db.execSQL(query);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error eliminando mensajes", Toast.LENGTH_SHORT).show();
        }
        db.close();
        return notification;
    }

    @SuppressLint("SimpleDateFormat")
    public boolean addUne(Une une) {
        SQLiteDatabase db = getWritableDatabase();
        // ADD NOT
        try{
            // NOTIFICATION VALUES
            ContentValues notificationValues = new ContentValues();
            notificationValues.put(DBConstants.UNE_DATE, new SimpleDateFormat("dd/MM/yyyy hh:mm aa").format(Calendar.getInstance().getTime()));
            notificationValues.put(DBConstants.UNE_CURRENT_REGISTER, une.getCurrentRegister());
            notificationValues.put(DBConstants.UNE_LAST_REGISTER, une.getLastRegister());
            notificationValues.put(DBConstants.UNE_TOTAL_CONSUMPTION, une.getTotalConsumption());
            notificationValues.put(DBConstants.UNE_TOTAL_TO_PAY, une.getTotalToPay());
            // INSERT INTO DB
            db.insert(DBConstants.UNE_TABLE, null, notificationValues);

            Toast.makeText(context, "Agregado registro de electricidad", Toast.LENGTH_LONG).show();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(context, "Error al insertar registro", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public List<Une> selectAllUneRegisters() {
        // LIST
        List<Une> uneRegisterList = new ArrayList<>();
        // SQL OBJECT
        SQLiteDatabase db = getWritableDatabase();
        // SQL QUERY
        try {
            String query = "SELECT * FROM " + DBConstants.UNE_TABLE + ";";
            Cursor cursorUne = db.rawQuery(query, null);
            if (cursorUne.getCount() > 0) {

                cursorUne.moveToFirst();
                while (!cursorUne.isAfterLast()) {
                    // NOTIFICATION INSTANCE
                    Une une = null;
                    // ATTRIBUTES
                    int id = cursorUne.getInt(0);
                    // TITLE
                    String date = cursorUne.getString(1);
                    GregorianCalendar realDate = new GregorianCalendar();
                    if (date != null || !date.isEmpty()) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                        Date d = spf.parse(date);
                        realDate.setTime(d);
                    }
                    // VALUES
                    double lastRegister = cursorUne.getDouble(2);
                    double currentRegister = cursorUne.getDouble(3);
                    double totalConsumption = cursorUne.getDouble(4);
                    double totalToPay = cursorUne.getDouble(5);
                    //
                    uneRegisterList.add(new Une(realDate, lastRegister, currentRegister, totalConsumption, totalToPay));
                    cursorUne.moveToNext();
                }
            }
            cursorUne.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error cargando registros", Toast.LENGTH_SHORT).show();
        }
        return uneRegisterList;
    }
    public double [] resumenUne() {
        // LIST
        double [] values = new double[4];
        // SQL OBJECT
        SQLiteDatabase db = getWritableDatabase();

        // SQL QUERY
        try {
            String query1 = "SELECT AVG("+ DBConstants.UNE_TOTAL_CONSUMPTION +") FROM " + DBConstants.UNE_TABLE + ";";
            String query2 = "SELECT AVG("+ DBConstants.UNE_TOTAL_TO_PAY + ") FROM " + DBConstants.UNE_TABLE + ";";
            String query3 = "SELECT SUM("+ DBConstants.UNE_TOTAL_CONSUMPTION +") FROM " + DBConstants.UNE_TABLE + ";";
            String query4 = "SELECT SUM("+ DBConstants.UNE_TOTAL_TO_PAY +") FROM " + DBConstants.UNE_TABLE + ";";
            //
            Cursor cursor1 = db.rawQuery(query1, null);
            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                values[0] = cursor1.getDouble(0);
                }
            cursor1.close();
            //
            Cursor cursor2 = db.rawQuery(query2, null);
            if (cursor2.getCount() > 0) {
                cursor2.moveToFirst();
                values[1] = cursor2.getDouble(0);
            }
            cursor2.close();
            //
            Cursor cursor3 = db.rawQuery(query3, null);
            if (cursor3.getCount() > 0) {
                cursor3.moveToFirst();
                values[2] = cursor3.getDouble(0);
            }
            cursor3.close();
            //
            Cursor cursor4 = db.rawQuery(query4, null);
            if (cursor4.getCount() > 0) {
                cursor4.moveToFirst();
                values[3] = cursor4.getDouble(0);
            }
            cursor4.close();
            //
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error durante consulta SQL", Toast.LENGTH_SHORT).show();
        }
        return values;
    }
    @SuppressLint("SimpleDateFormat")
    public boolean addWifiEtecsaUser(Wifi wifi) {
        SQLiteDatabase db = getWritableDatabase();
        // ADD NOT
        try{
            // NOTIFICATION VALUES
            ContentValues notificationValues = new ContentValues();
            notificationValues.put(DBConstants.ACCOUNT_WIFIETECSA, wifi.getWifiUser());
            notificationValues.put(DBConstants.ACCOUNT_WIFIETECSA_PASSWORD, wifi.getWifiPassw());
            notificationValues.put(DBConstants.ACCOUNT_WIFIETECSA_LAST_TIME_CONNECTED, new SimpleDateFormat("dd/MM/yyyy hh:mm aa").format(wifi.getLastTime().getInstance().getTime()));
            notificationValues.put(DBConstants.ACCOUNT_WIFIETECSA_LEFT_TIME, wifi.getLeftTime());
            // INSERT INTO DB
            db.insert(DBConstants.WIFI_TABLE, null, notificationValues);

            Toast.makeText(context, "Agregado usuario Wifi", Toast.LENGTH_LONG).show();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(context, "Error al insertar usuario", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public List<Wifi> selectAllWifiEtecsaUsers() {
        // LIST
        List<Wifi> wifiUsers = new ArrayList<>();
        // SQL OBJECT
        SQLiteDatabase db = getWritableDatabase();
        // SQL QUERY
        try {
            String query = "SELECT * FROM " + DBConstants.WIFI_TABLE + ";";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    // NOTIFICATION INSTANCE
                    Wifi wifi = null;
                    // ATTRIBUTES
                    int id = cursor.getInt(0);
                    //
                    String wifiUser = cursor.getString(1);
                    String wifiPassword = cursor.getString(2);
                    String leftTime = cursor.getString(4);
                    //
                    String date = cursor.getString(3);
                    GregorianCalendar realDate = new GregorianCalendar();
                    if (date != null || !date.isEmpty()) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                        Date d = spf.parse(date);
                        realDate.setTime(d);
                    }

                    //
                    wifiUsers.add(new Wifi(wifiUser, wifiPassword, realDate, leftTime));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error cargando usuarios WiFi", Toast.LENGTH_SHORT).show();
        }
        return wifiUsers;
    }
    @SuppressLint("SimpleDateFormat")
    public boolean addNautaUser(Nauta nauta) {
        SQLiteDatabase db = getWritableDatabase();
        // ADD NOT
        try{
            // NOTIFICATION VALUES
            ContentValues notificationValues = new ContentValues();
            notificationValues.put(DBConstants.ACCOUNT_WIFIETECSA, nauta.getNautaUser());
            notificationValues.put(DBConstants.ACCOUNT_WIFIETECSA_PASSWORD, nauta.getNautaPassw());
            // INSERT INTO DB
            db.insert(DBConstants.NAUTA_TABLE, null, notificationValues);

            Toast.makeText(context, "Agregado usuario Nauta", Toast.LENGTH_LONG).show();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(context, "Error al insertar usuario Nauta", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public List<Nauta> selectAllNautaUsers() {
        // LIST
        List<Nauta> usersNauta = new ArrayList<>();
        // SQL OBJECT
        SQLiteDatabase db = getWritableDatabase();
        // SQL QUERY
        try {
            String query = "SELECT * FROM " + DBConstants.NAUTA_TABLE + ";";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    // ATTRIBUTES
                    int id = cursor.getInt(0);
                    //
                    String nautaUser = cursor.getString(1);
                    String nautaPassword = cursor.getString(2);
                                        //
                    usersNauta.add(new Nauta(nautaUser, nautaPassword));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Error cargando usuarios Nauta", Toast.LENGTH_SHORT).show();
        }
        return usersNauta;
    }
}