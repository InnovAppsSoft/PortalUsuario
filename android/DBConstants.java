package com.marlon.portalusuario.database;

public class DBConstants {
    private static DBConstants instance = null;
    /**
     * DATABASE's name
     */
    public static final String DATABASE_NAME = "portalUsuario.db";
    /**
     * Tables name
     */
    public static final String NOTIFICATION_TABLE = "notification";
    public static final String NAUTA_TABLE = "nauta";
    public static final String WIFI_TABLE = "wifi";
    public static final String UNE_TABLE = "une";

    /**
     * Version of the database. Only used to import from assets.
     */
    public static final int DATABASE_VERSION = 3;

    /**
     * COLUMNS NAME
     */
    // NOTIFICATION
    public static String NOTIFICATION_ID = "id";
    public static String NOTIFICATION_TITLE = "title";
    public static String NOTIFICATION_TEXT = "text";
    public static String NOTIFICATION_DATE = "date";
    public static String NOTIFICATION_IMAGE = "image";
    // NAUTA
    public static String ACCOUNT_NAUTA_ID = "id";
    public static String ACCOUNT_NAUTA_EMAIL = "email";
    public static String ACCOUNT_NAUTA_EMAIL_PASSWORD = "email_password";
    // WIFI
    public static String ACCOUNT_WIFI_ID = "id";
    public static String ACCOUNT_WIFIETECSA = "wifi";
    public static String ACCOUNT_WIFIETECSA_PASSWORD = "wifi_password";
    public static String ACCOUNT_WIFIETECSA_LAST_TIME_CONNECTED = "last_time_connected";
    public static String ACCOUNT_WIFIETECSA_LEFT_TIME = "left_time";
    // UNE
    public static String UNE_ID = "id";
    public static String UNE_DATE = "date";
    public static String UNE_LAST_REGISTER = "last_register";
    public static String UNE_CURRENT_REGISTER = "current_register";
    public static String UNE_TOTAL_CONSUMPTION = "total_consumption";
    public static String UNE_TOTAL_TO_PAY = "total_to_pay";
}
