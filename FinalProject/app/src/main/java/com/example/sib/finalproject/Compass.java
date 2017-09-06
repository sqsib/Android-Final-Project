package com.example.sib.finalproject;

import android.location.Location;

/**
 * Created by Shuo on 3/22/2017.
 */

public class Compass {
    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    double angle;
    Location currentLocation;

    public Compass() {

    }

    public double getFriendAngle(Location friendLocation) {
        if (currentLocation != null) {
            if (currentLocation != null) {
                double mapAngle = currentLocation.bearingTo(friendLocation);
                if (mapAngle < 0) {
                    mapAngle += 360;
                }
                return mapAngle - angle;
            }
        }
        return 0;
    }

    public float getFriendDistance(Location friendLocation) {
        if (currentLocation != null && friendLocation != null) {
            return currentLocation.distanceTo(friendLocation);
        }
        return 0;
    }
}
