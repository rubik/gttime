package com.example.john.gttime.model;

import android.text.TextUtils;

import java.util.ArrayList;


public class StopResult {
    private String mTransportNumber;
    private ArrayList<StopTime> mTimes;

    public StopResult(String transportNumber) {
        mTransportNumber = transportNumber;
        mTimes = new ArrayList<StopTime>();
    }

    public StopResult(String transportNumber, ArrayList<StopTime> times) {
        mTransportNumber = transportNumber;
        mTimes = times;
    }

    public String getTransportNumber() {
        return mTransportNumber;
    }

    public ArrayList<StopTime> getTimes() {
        return mTimes;
    }

    public void addTime(StopTime time) {
        mTimes.add(time);
    }

    @Override
    public String toString() {
        return mTransportNumber + " " + TextUtils.join(" ", mTimes);
    }
}