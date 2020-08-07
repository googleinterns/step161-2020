package com.google.sps.data;

public final class Rider {
  private final String rider;
  private final String day;
  public static Long driverId;

  public Rider(String rider, String day, Long driverId) {
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

  public void setId(Long newId) {
    driverId = newId;
  }
}  