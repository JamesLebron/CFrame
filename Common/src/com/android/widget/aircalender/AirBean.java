package com.android.widget.aircalender;

/**
 * Created by xudong on 2015/4/28.
 */
public  class AirBean {
    private Boolean hasTicket;
    private String surplusTickets;
    private String price;

    public Boolean getHasTicket() {
        return hasTicket;
    }

    public void setHasTicket(Boolean hasTicket) {
        this.hasTicket = hasTicket;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSurplusTickets() {
        return surplusTickets;
    }

    public void setSurplusTickets(String surplusTickets) {
        this.surplusTickets = surplusTickets;
    }
}
