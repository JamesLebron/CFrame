package com.android.widget.aircalender;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.R;
import com.android.util.Logs;
import com.android.widget.datetimepicker.datetimepicker.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xudong on 2015/4/28.
 */
public class SimpleMonthView extends RelativeLayout implements View.OnClickListener {
    private View layout;

    private TextView mYearText;
    private TextView mMonthText;

    private int mDayOfWeekStart;//本月第一天是周几
    private int mYear;
    private int mMonth;
    private List<AirBean> mAirBeans;
    private int mDaysOfMonth;
    private OnChooseDayChange mOnChooseDayChange;
    private static CalendarDay calendarDay = new CalendarDay();

    private final Calendar mCalendar = Calendar.getInstance();

    private static List<Integer> lines;
    private static List<Integer> days;

    public void setOnChooseDayChange(OnChooseDayChange mOnChooseDayChange) {
        this.mOnChooseDayChange = mOnChooseDayChange;
    }

    public SimpleMonthView(Context context) {
        super(context);
        init();
    }

    public SimpleMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化函数
     */
    void init() {
        layout = View.inflate(getContext(), R.layout.calender_view_item, null);
        this.addView(layout);
        mYearText = getViewById(layout, R.id.title_year);
        mMonthText = getViewById(layout, R.id.title_month);
        if (lines == null) {
            lines = new ArrayList<>();//初始化weeks
            lines.add(R.id.line1);
            lines.add(R.id.line2);
            lines.add(R.id.line3);
            lines.add(R.id.line4);
            lines.add(R.id.line5);
            lines.add(R.id.line6);
            days = new ArrayList<>();//初始化weekday
            days.add(R.id.day1);
            days.add(R.id.day2);
            days.add(R.id.day3);
            days.add(R.id.day4);
            days.add(R.id.day5);
            days.add(R.id.day6);
            days.add(R.id.day7);
        }

    }

    private static View last;//上一个被选中

    /**
     * 初始化数据
     * @param mAirMonth
     */
    public void init(AirMonth mAirMonth) {
        mCalendar.setTime(mAirMonth.getYearAndMonth());
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mYearText.setText(mYear + "年");
        mMonthText.setText(mMonth + 1 + "月");
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        mDaysOfMonth = Utils.getDaysInMonth(mMonth, mYear);

        mAirBeans = mAirMonth.getAirBeans();
        int size = mAirBeans.size();
        for (int i = 1; i <= 6; i++) {
            View week = getViewById(layout, lines.get(i - 1));
            for (int j = 1; j <= 7; j++) {
                int start = (i - 1) * 7 + j - mDayOfWeekStart;//start ：当前日期
                View d = getViewById(week, days.get(j - 1));
                if (start > 0 && start <= size) {
                    AirBean airBean = mAirBeans.get(start - 1);
                    if (airBean.getHasTicket()) {
                        change(d, start + "", airBean.getSurplusTickets(), airBean.getPrice(), R.color.white);
                        d.setTag(start);
                        if (calendarDay.day == start && calendarDay.year == mYear && calendarDay.month == mMonth) {//判断是否是今日需要显示的日期
                            changeChooseDay(d);
                            if (mOnChooseDayChange != null) {
                                mOnChooseDayChange.onChooseDayChange(calendarDay.year, calendarDay.month, calendarDay.day);
                            }
                        }
                        d.setOnClickListener(this);
                    } else
                        change(d, start + "", "", "", R.color.day_no_bg);
                } else
                    change(d, "", "", "", R.color.day_no_bg);
            }
        }

        //决定最后一行是否显示
        if (mDaysOfMonth + mDayOfWeekStart <= 35) {
            findViewById(R.id.line6).setVisibility(View.GONE);
        }
    }

    /**
     * 更改选中日期背景以及字体颜色等
     * @param day
     * @param ttitle
     * @param tsurplus
     * @param tprice
     * @param color
     */
    private void change(View day, String ttitle, String tsurplus, String tprice, int color) {
        TextView title = getViewById(day, R.id.day_num);
        TextView surplus = getViewById(day, R.id.day_surplus);
        TextView price = getViewById(day, R.id.day_price);
        title.setText(ttitle);
        surplus.setText(tsurplus);
        price.setText(tprice);
        day.setBackgroundColor(getResources().getColor(color));
    }


    private <T extends View> T getViewById(View v, int id) {
        return (T) v.findViewById(id);
    }

    /**
     * 日期选择监听器
     */
    public interface OnChooseDayChange {
        void onChooseDayChange(int year, int month, int day);
    }

    @Override
    public void onClick(View v) {
        int day = (Integer) v.getTag();
        if (day > 0 && day <= mDaysOfMonth) {
            if (day - 1 < mAirBeans.size()) {
                AirBean airBean = mAirBeans.get(day - 1);
                if (airBean.getHasTicket()) {
                    calendarDay.setDay(mYear, mMonth, day);
                    changeChooseDay(v);
                    if (mOnChooseDayChange != null) {
                        mOnChooseDayChange.onChooseDayChange(calendarDay.year, calendarDay.month, calendarDay.day);
                    }
                    Logs.w("onClick", calendarDay.toString());
                }
            }
        }
    }

    /**
     * 更改选择
     * @param v
     */
    private void changeChooseDay(View v) {
        if (last != null) {
            last.setBackgroundColor(getResources().getColor(R.color.white));
            TextView day_num = getViewById(last, R.id.day_num);
            day_num.setTextColor(Color.parseColor("#444444"));

            TextView day_surplus = getViewById(last, R.id.day_surplus);
            day_surplus.setTextColor(Color.parseColor("#FD8E3B"));

            TextView day_price = getViewById(last, R.id.day_price);
            day_price.setTextColor(Color.parseColor("#999999"));

        }
        if (mYear == calendarDay.year && mMonth == calendarDay.month) {
            v.setBackgroundColor(getResources().getColor(R.color.cxd_green));
            TextView day_num = getViewById(v, R.id.day_num);
            day_num.setTextColor(getResources().getColor(R.color.white));

            TextView day_surplus = getViewById(v, R.id.day_surplus);
            day_surplus.setTextColor(getResources().getColor(R.color.white));

            TextView day_price = getViewById(v, R.id.day_price);
            day_price.setTextColor(getResources().getColor(R.color.white));

            last = v;
        }
    }
}
