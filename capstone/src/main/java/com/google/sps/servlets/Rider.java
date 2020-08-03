package com.google.sps.servlets;

public final class Rider {
    private final String rider;
    private final String day;

    public Rider(String rider, String day) {
        this.rider = rider;
        this.day = day;
    }

    public String getRider() {
        return rider;
    }
    
    public String getDay() {
        return day;
    }
}  