package com.marlon.portalusuario.une;

import android.annotation.SuppressLint;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.marlon.portalusuario.PUNotifications.PUNotification;
import com.marlon.portalusuario.util.Util;

import java.text.SimpleDateFormat;

@Entity(tableName = "une")
public class Une implements Comparable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long date;
    private double lastRegister;
    private double currentRegister;
    private double totalConsumption;
    private double totalToPay;

    @Ignore
    public Une() {
    }

    public Une(final long date, final double lastRegister, final double currentRegister, final double totalConsumption, final double totalToPay) {
        this.date = date;
        this.lastRegister = lastRegister;
        this.currentRegister = currentRegister;
        this.totalConsumption = totalConsumption;
        this.totalToPay = totalToPay;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(final long date) {
        this.date = date;
    }

    public double getLastRegister() {
        return this.lastRegister;
    }

    public void setLastRegister(final double lastRegister) {
        this.lastRegister = lastRegister;
    }

    public double getCurrentRegister() {
        return this.currentRegister;
    }

    public void setCurrentRegister(final double currentRegister) {
        this.currentRegister = currentRegister;
    }

    public double getTotalConsumption() {
        return this.totalConsumption;
    }

    public void setTotalConsumption(final double totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public double getTotalToPay() {
        return this.totalToPay;
    }

    public void setTotalToPay(final double totalToPay) {
        this.totalToPay = totalToPay;
    }

    @SuppressLint("SimpleDateFormat")
    public String dateToString() {
        return Util.date2String(Util.long2Date(date));
    }

    @Override
    public int compareTo(Object o) {
        Une une = (Une) o;
        return Util.long2Date(getDate()).compareTo(Util.long2Date(une.getDate()));
    }
}
