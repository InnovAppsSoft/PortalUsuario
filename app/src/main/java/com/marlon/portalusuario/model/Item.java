package com.marlon.portalusuario.model;

public class Item {
    private String mName = "Item";
    private int mDays = 0;
    private int mTime = 0;
    private int mWatts = 0;
    private int mQuantity = 1;
    private int mPosition = 0;
    private float mPercentage = 0;

    public static final String NAME_KEY = "name";
    public static final String DAYS_KEY = "days";
    public static final String TIME_KEY = "time";
    public static final String WATTS_KEY = "watts";
    public static final String QUANTITY_KEY = "quantity";

    public double getConsumption(){
        double result = 0;
        if(mWatts > 100000){
            result = (mWatts / 1000) * mTime * mDays * mQuantity / (60);
        }
        else {
            double temp = mWatts * mTime * mDays * mQuantity;
            result = temp / (60000);
        }

        //Log.d("Item " + mName + " consumption " + result);
        return result;
    }

    public String getName(){
        return mName;
    }

    public int getDays(){
        return mDays;
    }

    public int getTime(){
        return mTime;
    }

    public String getTimeStr(){
        int hour = mTime / 60;
        int minute = mTime % 60;
        String result = hour + ":" + String.format("%02d", minute);
        return result;
    }

    public int getWatts(){
        return mWatts;
    }

    public String getWattsStr(){
        String result;
        double watts = 0;
        if(mWatts > 1000000){
            watts = mWatts / 1000000;
            result = Double.toString(watts) + " M";
        }
        else if(mWatts > 10000){
            watts = mWatts / 1000;
            result = Double.toString(watts) + " k";
        }
        else{
            result = Integer.toString(mWatts);
        }
        return result;
    }

    public int getQuantity(){
        return mQuantity;
    }

    public int getPosition(){
        return mPosition;
    }

    public float getPercentage(){
        return mPercentage;
    }

    public void setName(String name){
        mName = name;
    }

    public void setDays(int days){
        mDays = days;
    }

    public void setTime(int hours){
        mTime = hours;
    }

    public void setWatts(int watts){
        mWatts = watts;
    }

    public void setQuantity(int quantity){
        mQuantity = quantity;
    }

    public void setPosition(int pos){
        mPosition = pos;
    }

    public void setPercentage(float percentage){
        mPercentage = percentage;
    }
}
