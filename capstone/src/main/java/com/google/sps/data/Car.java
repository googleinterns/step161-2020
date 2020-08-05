package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;



public final class Car {
    private final List<Rider> riders;
    private final String day;
    private final Long driverId;

    public Car(List<Rider> riders, String day, Long driverId) {
        this.riders = riders;
        this.day = day;
        this.driverId = driverId;
    }

    public List<Rider> getRiders() {
        return riders;
    }
    

    public String getDay() {
        return day;
    }

    public void addRider(Rider rider, Driver driver) {
        riders.add(rider);
        driver.setSeats(driver.getSeats() - 1);
    }

}  