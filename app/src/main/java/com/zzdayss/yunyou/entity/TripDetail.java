package com.zzdayss.yunyou.entity;

/**
 * @Author Siying.Li
 * @Date 2024/11/3 17:41
 * @Version 1.0
 */
public  class TripDetail {
    private String date;
    private String position;

    public TripDetail(String date, String position) {
        this.date = date;
        this.position = position;
    }

    public String getDate() { return date; }
    public String getPosition() { return position; }
}