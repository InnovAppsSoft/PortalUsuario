package com.marlon.portalusuario.model;

public class Rate {

    public static final String NAME_KEY = "name";
    public static final String KWH_KEY = "kwh";
    public static final String COST_KEY = "cost";
    public static final String INDEX_KEY = "index";

    private String mName;
    private int mKwh;
    private double mCost;
    private int mIndex;
    private boolean mIsLastRate;

    public void setName(String name) { mName = name; }

    public void setKwh(int kwh) { mKwh = kwh; }

    public void setCost(double cost) { mCost = cost; }

    public void setIndex(int index) {mIndex = index;}

    public void setLastRate(boolean value) {mIsLastRate = value;}

    public String getName() { return mName; }

    public int getKwh() { return mKwh; }

    public double getCost() { return mCost; }

    public boolean getIsLastRate() { return mIsLastRate; }

    public String getKwhStr() {
        if(mKwh == -1){
            if(mIsLastRate){
                return "-";
            } else {
                return "Error";
            }

        } else {
            return String.valueOf(mKwh);
        }
    }

    public int getIndex() { return mIndex; }

}
