package com.google.sps.data;

public final class Driver {
  private final String first;
  private final String day;
  private final String times;
  private long seats;
  private final String email;

  public Driver(String first, String day, String times, long seats, String email) {
    this.first = first;
    this.day = day;
    this.times = times;
    this.seats = seats;
    this.email = email;
  }

  public long getSeats() {
    return seats;
  }

  public void setSeats(long newSeats) {
    seats = newSeats;
  }
}
