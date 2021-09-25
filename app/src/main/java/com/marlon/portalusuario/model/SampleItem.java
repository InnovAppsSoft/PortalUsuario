package com.marlon.portalusuario.model;

public class SampleItem {
    private String mName = "";
    private int mDays = 30;
    private int mWatts = 0;
    private int mCategoryId = -1;
    private int mItemId = -1;

    public String getName(){
        return mName;
    }

    public int getDays(){
        return mDays;
    }

    public int getWatts(){
        return mWatts;
    }

    public int getCategoryId(){
        return mCategoryId;
    }

    public int getItemId(){
        return mItemId;
    }

    public void setName(String name){
        mName = name;
    }

    public void setWatts(int watts){
        mWatts = watts;
    }

    public void setCategoryId(int id){
        mCategoryId = id;
    }

    public void setItemId(int id){
        mItemId = id;
    }

}
