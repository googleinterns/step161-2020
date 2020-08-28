package com.google.sps.data;

public final class Driver {
  private final String first;
  private final String day;
  private final String times;
  private long seats;
  private final String email;
  private final String license;
  private final String pollingAddress;
  private final String address;

  public Driver(
      String first, String day, String times, long seats, String email, String license, String pollingAddress, String address) {
    this.first = first;
    this.day = day;
    this.times = times;
    this.seats = seats;
    this.email = email;
    this.license = license;
    this.pollingAddress = pollingAddress;
    this.address = address;
  }

  public long getSeats() {
    return seats;
  }

  public void setSeats(long newSeats) {
    seats = newSeats;
  }
}
