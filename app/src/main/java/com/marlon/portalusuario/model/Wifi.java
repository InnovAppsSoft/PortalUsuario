package com.marlon.portalusuario.model;

import java.util.GregorianCalendar;

public class Wifi {
    private int id;
    private String wifiUser;
    private String wifiPassw;
    private GregorianCalendar lastTime;
    private String leftTime;

    public Wifi(){}

    public Wifi(final String wifiUser, final String wifiPassw, final GregorianCalendar lastTime, final String leftTime) {
        this.wifiUser = wifiUser;
        this.wifiPassw = wifiPassw;
        this.lastTime = lastTime;
        this.leftTime = leftTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getWifiUser() {
        return this.wifiUser;
    }

    public void setWifiUser(final String wifiUser) {
        this.wifiUser = wifiUser;
    }

    public String getWifiPassw() {
        return this.wifiPassw;
    }

    public void setWifiPassw(final String wifiPassw) {
        this.wifiPassw = wifiPassw;
    }

    public GregorianCalendar getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(final GregorianCalendar lastTime) {
        this.lastTime = lastTime;
    }

    public String getLeftTime() {
        return this.leftTime;
    }

    public void setLeftTime(final String leftTime) {
        this.leftTime = leftTime;
    }
}
