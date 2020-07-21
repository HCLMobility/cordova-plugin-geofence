package com.cowbell.cordova.geofence;

import android.app.Notification;

import com.google.android.gms.location.Geofence;
import com.google.gson.annotations.Expose;

public class GeoNotification {
    @Expose public String id;
    @Expose public double latitude;
    @Expose public double longitude;
    @Expose public int radius;
    @Expose public int transitionType;


    public GeoNotification() {
    }

    public String toJson() {
        return Gson.get().toJson(this);
    }

    public static GeoNotification fromJson(String json) {
        if (json == null) return null;
        return Gson.get().fromJson(json, GeoNotification.class);
    }

    public Geofence toGeofence() {
        return new Geofence.Builder()
                .setCircularRegion(latitude, longitude, radius)
                .setRequestId(id)
                .setTransitionTypes(transitionType)
                .setLoiteringDelay(30000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }
}
