package com.android.widget.aircalender;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xudong on 2015/4/25.
 */
public class AirSimpleAirAdapter extends BaseAdapter {
    private List<AirMonth> mAirMonths;
    private Context mContext;

    public AirSimpleAirAdapter(Context context, List<AirMonth> airMonths) {
        this.mAirMonths = airMonths;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mAirMonths == null ? 0 : mAirMonths.size();
    }

    @Override
    public Object getItem(int position) {
        return mAirMonths == null ? null : mAirMonths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = new SimpleMonthView(mContext);
        SimpleMonthView airMonthView = (SimpleMonthView) convertView;
        airMonthView.init(mAirMonths.get(position));
//        if (convertView == null)
//            convertView = new AirMonthView(mContext);
//        AirMonthView airMonthView = (AirMonthView) convertView;
//        airMonthView.init(mAirMonths.get(position));
        return convertView;
    }

    public List<AirMonth> getmAirMonths() {
        return mAirMonths;
    }

    public void setmAirMonths(List<AirMonth> mAirMonths) {
        this.mAirMonths = mAirMonths;
    }
}
