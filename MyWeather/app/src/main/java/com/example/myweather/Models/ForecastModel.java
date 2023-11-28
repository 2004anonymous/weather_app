package com.example.myweather.Models;

public class ForecastModel {
    String iconUrl, time, condition;

    public ForecastModel(String iconUrl, String time, String condition) {
        this.iconUrl = iconUrl;
        this.time = time;
        this.condition = condition;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
