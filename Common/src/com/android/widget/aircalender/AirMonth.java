package com.android.widget.aircalender;

import java.util.Date;
import java.util.List;

/**
 * Created by xudong on 2015/4/28.
 */
public  class AirMonth {
    private Date yearAndMonth;
    private List<AirBean> airBeans;

    public Date getYearAndMonth() {
        return yearAndMonth;
    }

    public void setYearAndMonth(Date yearAndMonth) {
        this.yearAndMonth = yearAndMonth;
    }

    public List<AirBean> getAirBeans() {
        return airBeans;
    }

    public void setAirBeans(List<AirBean> airBeans) {
        this.airBeans = airBeans;
    }
}
