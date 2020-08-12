package com.google.sps.data;

public final class Driver {
  private final String first;
  private final String day;
  private final String times;
  private long seats;
  private final long id;

  public Driver(String first, String day, String times, long seats, long id) {
    this.first = first;
    this.day = day;
    this.times = times;
    this.seats = seats;
    this.id = id;
  }

  public long getSeats() {
    return seats;
  }

  public void setSeats(long newSeats) {
    seats = newSeats;
  }
}
