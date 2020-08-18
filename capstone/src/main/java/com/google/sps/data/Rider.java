package com.google.sps.data;

public final class Rider {
  private final String rider;
  private final String day;
  public static String driverId;

  public Rider(String rider, String day, String driverId) {
    this.rider = rider;
    this.day = day;
    this.driverId = driverId;
  }

  public String getRider() {
    return rider;
  }
    
  public String getDay() {
    return day;
  }

  public void setId(String newId) {
    driverId = newId;
  }
}  