package com.google.sps.data;

public final class Driver {

  private final String first;
  private final String last;
  private final String day;
  private final String times;
  private final long seats;
  private final long timestamp;

  public Driver(String first, String last, String day, String times, long seats, long timestamp) {
    this.first = first;
    this.last = last;
    this.day = day;
    this.times = times;
    this.seats = seats;
    this.timestamp = timestamp;
  }
}
