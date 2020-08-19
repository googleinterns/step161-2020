package com.google.sps.data;

public final class Rider {
  private final String rider;
  private final String day;
  public static String email;

  public Rider(String rider, String day, String email) {
    this.rider = rider;
    this.day = day;
    this.email = email;
  }

  public String getRider() {
    return rider;
  }
    
  public String getDay() {
    return day;
  }

}  