package com.android.widget.aircalender;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.List;

/**
 * Created by xudong on 2015/4/25.
 */
public class AirCalenderView extends ListView {
    private List<AirMonth> mAirMonths;
    private AirSimpleAirAdapter mAirAdapter;

    public AirCalenderView(Context context) {
        super(context);
    }

    public AirCalenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AirCalenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public AirCalenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void setmAirMonths(List<AirMonth> mAirMonths) {
        this.mAirMonths = mAirMonths;
        if (mAirAdapter == null) {
            mAirAdapter = new AirSimpleAirAdapter(getContext(), mAirMonths);
            this.setAdapter(mAirAdapter);
        } else {
            mAirAdapter.setmAirMonths(mAirMonths);
            mAirAdapter.notifyDataSetChanged();
        }
    }
}
