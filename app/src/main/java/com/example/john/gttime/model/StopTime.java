package com.example.john.gttime.model;


public class StopTime {
    private String mTime;
    private Boolean mRealTime;

    public StopTime(String time, Boolean realTime) {
        mTime = time;
        mRealTime = realTime;
    }

    public String getTime() {
        return mTime;
    }

    public Boolean isRealTime() {
        return mRealTime;
    }

    @Override
    public String toString() {
        return mTime;
    }
}
