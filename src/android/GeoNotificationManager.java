package com.cowbell.cordova.geofence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.cordova.CallbackContext;

import java.util.ArrayList;
import java.util.List;

public class GeoNotificationManager {
    private Context context;
    private Logger logger;

    private GeoNotificationStore geoNotificationStore;
    private GeofencingClient geofencingClient = null;
    private GeofenceHelper geofenceHelper = null;

    public GeoNotificationManager(Context context) {
        this.context = context;
        logger = Logger.getLogger();
        geoNotificationStore = new GeoNotificationStore(context);
        geofencingClient = LocationServices.getGeofencingClient(context);
        geofenceHelper = new GeofenceHelper(context);

        if (areGoogleServicesAvailable()) {
            logger.log(Log.DEBUG, "Google play services available");
        } else {
            logger.log(Log.WARN, "Google play services not available. Geofence plugin will not work correctly.");
        }
    }

    public void loadFromStorageAndInitializeGeofences() {
        List<GeoNotification> geoNotifications = geoNotificationStore.getAll();
        for (GeoNotification geo : geoNotifications) {
            addGeoNotification(geo, null);
        }

    }

    private boolean areGoogleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(context);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    public void addGeoNotifications(List<GeoNotification> geoNotifications,
                                    final CallbackContext callback) {
        for (GeoNotification geo : geoNotifications) {
            geoNotificationStore.setGeoNotification(geo);
            addGeoNotification(geo, callback);
        }

    }

    @SuppressLint("MissingPermission")
    public void addGeoNotification(GeoNotification geo,
                                   final CallbackContext callback) {
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geo.toGeofence());

        geofencingClient.addGeofences(geofencingRequest, geofenceHelper.getPendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (callback != null) callback.success();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callback != null) callback.error(geofenceHelper.getErrorString(e));
                    }
                });


    }

    public void removeGeoNotifications(List<String> ids, final CallbackContext callback) {
        geofencingClient.removeGeofences(geofenceHelper.getPendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.success();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.error(geofenceHelper.getErrorString(e));
                    }
                });

        for (String id : ids) {
            geoNotificationStore.remove(id);
        }
    }

    public void removeAllGeoNotifications(final CallbackContext callback) {
        List<GeoNotification> geoNotifications = geoNotificationStore.getAll();
        List<String> geoNotificationsIds = new ArrayList<String>();
        for (GeoNotification geo : geoNotifications) {
            geoNotificationsIds.add(geo.id);
        }
        removeGeoNotifications(geoNotificationsIds, callback);
    }


}
