package com.example.john.gttime.model;


import java.util.ArrayList;

public class ApiResult {
    private String mStopName;
    private ArrayList<StopResult> mStopResults;

    public ApiResult(ArrayList<StopResult> stopResults) {
        mStopName = "";
        mStopResults = stopResults;
    }

    public ApiResult(String stopName, ArrayList<StopResult> stopResults) {
        mStopName = stopName;
        mStopResults = stopResults;
    }

    public String getStopName() {
        return mStopName;
    }

    public void setStopName(String stopName) {
        mStopName = stopName;
    }

    public ArrayList<StopResult> getStopResults() {
        return mStopResults;
    }
}
