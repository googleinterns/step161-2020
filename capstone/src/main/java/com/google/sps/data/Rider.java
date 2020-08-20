package com.google.sps.data;

public final class Rider {
  private final String rider;
  private final String day;
  private final String location;
  public static long driverId;

  public Rider(String rider, String day, Long driverId, String location) {
    this.rider = rider;
    this.day = day;
    this.driverId = driverId;
    this.location = location;
  }

  public Rider(String rider, String day, Long driverId) {
    this.rider = rider;
    this.day = day;
    this.driverId = driverId;
    this.location = "";
  }

  public String getRider() {
    return rider;
  }
    
  public String getDay() {
    return day;
  }

  public long getDriver(){
    return driverId;
  }

  public String getLocation() {
    return location;
  }

  public void setId(Long newId) {
    driverId = newId;
  }
}  