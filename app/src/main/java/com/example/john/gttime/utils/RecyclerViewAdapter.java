package com.example.john.gttime.utils;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.john.gttime.R;
import com.example.john.gttime.model.StopResult;
import com.example.john.gttime.model.StopTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<StopResult> mDataset;
    private Context mContext;
    private static final List<String> NOT_BUSES = Arrays.asList("3", "4", "6", "7",
            "9", "9B", "10", "15", "16CS", "16CD", "18");

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public ImageView mIcon;
        public TextView mTransportNumber;
        public TextView mTimes;
        public ViewHolder(CardView v) {
            super(v);
            mCardView = v;
            mIcon = (ImageView) v.findViewById(R.id.result_item_ic_transport_type);
            mTransportNumber = (TextView) v.findViewById(R.id.result_item_text_transport_number);
            mTimes = (TextView) v.findViewById(R.id.result_item_text_times);
        }
    }

    public RecyclerViewAdapter(ArrayList<StopResult> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StopResult element = mDataset.get(position);
        int drawableId = isNotBus(element.getTransportNumber())
                ? R.drawable.ic_directions_train_grey
                : R.drawable.ic_directions_bus_grey;

        holder.mIcon.setImageDrawable(
                mContext.getResources().getDrawable(drawableId, mContext.getTheme())
        );
        holder.mTransportNumber.setText(element.getTransportNumber());
        StringBuilder times = new StringBuilder();
        ArrayList<Integer> positions = new ArrayList<Integer>();
        int i = 0;
        for (StopTime time : element.getTimes()) {
            times.append(time.toString() + " ");
            if (time.isRealTime()) positions.add(i++);
        }
        String completeSring = times.toString();
        Spannable timesToSpan = new SpannableString(completeSring.substring(0, completeSring.length() - 1));
        for (int pos : positions) {
            timesToSpan.setSpan(new ForegroundColorSpan(
                            mContext.getResources().getColor(R.color.colorHighlight)), pos * 6, pos * 6 + 5,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
            );
        }
        holder.mTimes.setText(timesToSpan);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private static Boolean isNotBus(String transportNumber) {
        return NOT_BUSES.contains(transportNumber);
    }
}