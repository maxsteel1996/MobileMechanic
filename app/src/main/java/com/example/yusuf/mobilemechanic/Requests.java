package com.example.yusuf.mobilemechanic;

import com.backendless.geo.GeoPoint;

/**
 * Created by ASHUTOSH on 3/13/2016.
 */
public class Requests {
    String mechanicUsername;
    String requesterUsername;
    GeoPoint mylocation;

    public GeoPoint getMylocation() {
        return mylocation;
    }

    public void setMylocation(GeoPoint mylocation) {
        this.mylocation = mylocation;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public String getDriverUsername() {
        return mechanicUsername;
    }

    public void setDriverUsername(String driverUsername) {
        this.mechanicUsername = driverUsername;
    }
}
