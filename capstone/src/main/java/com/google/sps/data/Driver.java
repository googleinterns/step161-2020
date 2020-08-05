package com.google.sps.data;

public final class Driver {
    private final String first;
    private final String last;
    private final String day;
    private final String times;
    private long seats;
    private final long timestamp;
    private final long id;

    public Driver(String first, String last, String day, String times, long seats, long timestamp, long id) {
        this.first = first;
        this.last = last;
        this.day = day;
        this.times = times;
        this.seats = seats;
        this.timestamp = timestamp;
        this.id = id;
    }

    public long getSeats() {
    return seats;
    }

    public void setSeats(long newSeats) {
    seats = newSeats;
    }
}
