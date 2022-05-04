package com.marlon.portalusuario.PUNotifications;

import android.annotation.SuppressLint;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.marlon.portalusuario.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

@Entity(tableName = "notifications")
public class PUNotification implements Comparable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String text;
    private String image;
    private long date;
    @Ignore
    private Util util;

    @Ignore
    public PUNotification(){}

    public PUNotification(String title, String text, String image, long date) {
        this.title = title;
        this.text = text;
        this.image = image;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @SuppressLint("SimpleDateFormat")
    public String dateToString(){
        return Util.date2String(Util.long2Date(getDate()));
    }

    @Override
    public int compareTo(Object o) {
        PUNotification pun = (PUNotification) o;
        return Util.long2Date(pun.getDate()).compareTo(Util.long2Date(getDate()));
    }
}
