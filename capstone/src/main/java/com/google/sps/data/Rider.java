package com.google.sps.data;

public final class Rider {
  private final String rider;
  private final String day;
  private final String email;
  private final String address;

  public Rider(String rider, String day, String email, String address) {
    this.rider = rider;
    this.day = day;
    this.email = email;
    this.address = address;
  }

  public String getRider() {
    return rider;
  }
    
  public String getDay() {
    return day;
  }

}  
