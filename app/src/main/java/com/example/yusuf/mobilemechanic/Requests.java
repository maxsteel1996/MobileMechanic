package com.example.yusuf.mobilemechanic;

import com.backendless.geo.GeoPoint;

/**
 * Created by ASHUTOSH on 3/13/2016.
 */
public class Requests {
    String mechanicUsername;
    String requesterUsername;
    GeoPoint mylocation;
    String objectId;
    public String getObjectId() {

        return objectId;
    }

    public void setObjectId( String objectId ) {
        this.objectId = objectId;
    }



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

    public String getMechanicUsername() {
        return mechanicUsername;
    }

    public void setMechanicUsername(String mechanicUsername) {
        this.mechanicUsername = mechanicUsername;
    }
}
