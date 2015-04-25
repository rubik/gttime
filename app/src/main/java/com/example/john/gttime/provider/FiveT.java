package com.example.john.gttime.provider;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.john.gttime.model.ApiResult;
import com.example.john.gttime.model.StopResult;
import com.example.john.gttime.model.StopTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class FiveT {

    private static final String BASE_URL = "http://www.5t.torino.it/5t/trasporto/arrival-times-byline.jsp?action=getTransitsByLine&shortName=";

    public static void getStopData(final String stopNumber, RequestQueue rq, final StopDataClientListener listener) {
        final ArrayList<StopResult> stopResults = new ArrayList<>();
        final ApiResult result = new ApiResult(stopResults);
        String url = makeUrl(stopNumber);
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseResponse(s, result);
                listener.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        rq.add(req);
    }

    private static void parseResponse(String response, ApiResult result) {
        try {
            Document doc = Jsoup.parse(response);
            result.setStopName(doc.select("#infow span").first().text());
            Elements rows = doc.select("table td");
            StopResult stop = new StopResult(rows.first().text());
            for (Element row : rows.subList(1, rows.size())) {
                if (row.hasAttr("class") && row.attr("class").equals("line")) {
                    result.getStopResults().add(stop);
                    stop = new StopResult(row.text());
                } else {
                    String text = row.text();
                    if (text.endsWith("*")) {
                        stop.addTime(new StopTime(text.substring(0, text.length() - 1), true));
                    } else stop.addTime(new StopTime(text, false));
                }
            }
            result.getStopResults().add(stop);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static String makeUrl(String stopNumber) {
        return BASE_URL + stopNumber;
    }

    public static interface StopDataClientListener {
        public void onResponse(ApiResult stopResult);
    }
}
